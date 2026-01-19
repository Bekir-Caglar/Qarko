# Mobil Uygulama Kampanya ve Promosyon Mantığı

Bu doküman, mobil uygulamada (React Native / Flutter / Native) sepet aşamasında kampanyaların nasıl işleneceğini, kuralların nasıl kontrol edileceğini ve indirimlerin nasıl hesaplanacağını detaylandırır.

## 1. Veri Yapısı (Data Model)

Backend'den gelen `Campaign` objesi aşağıdaki kritik alanları içerir. Mobil uygulama bu alanları parse ederek mantığı çalıştırmalıdır.

```typescript
interface Campaign {
    id: string;
    type: 'PERCENTAGE_DISCOUNT' | 'FIXED_DISCOUNT' | 'BUY_X_GET_Y' | 'FREE_ITEM';
    discountValue: number; // Yüzde veya Tutar
    conditions: {
        minOrderAmount: number;     // Sepet alt limiti
        requiresCode: boolean;      // Kod gerekli mi?
        code?: string;              // Kampanya kodu
        maxTotalUsage: number;      // Genel kullanım limiti
        maxUsagePerUser: number;    // Kişi başı kullanım limiti
        
        // Buy X Get Y Mantığı İçin
        buyQuantity: number;        // Kaç adet alınmalı (X)
        getQuantity: number;        // Kaç adet hediye (Y)
        
        // Hediye Ürün Mantığı İçin
        freeItemId?: string;        // Hediye edilecek ürün ID'si
        
        // Kampanya Kapsamı (Scope) - Backend'de bu alanlar dolu gelir
        scope: 'ALL' | 'CATEGORY' | 'ITEM'; 
        applicableItems: string[];      // Geçerli Ürün ID'leri
        applicableCategories: string[]; // Geçerli Kategori ID'leri
    };
    startDate: string;
    endDate: string;
}
```

## 2. Genel Doğrulama (Validation Pipeline)

Sepete "Uygula" denildiğinde veya otomatik hesaplamada sırasıyla şu kontroller yapılmalıdır:

### A. Aktiflik Kontrolü
1.  `isActive` true olmalı.
2.  Tarih kontrolü: `startDate <= bugünün_tarihi <= endDate`.

### B. Kullanıcı Limit Kontrolü (Client & Server)
1.  **Genel Limit:** Eğer `usage.totalUsed >= conditions.maxTotalUsage` ise kampanya pasif görünmeli (Backend bu veriyi göndermeli).
2.  **Kişi Başı Limit:** Kullanıcının bu kampanyayı daha önce kaç kez kullandığı (`userUsageCount`) kontrol edilmeli. Eğer `userUsageCount >= conditions.maxUsagePerUser` ise kullanıcıya "Kullanım hakkınız doldu" uyarısı verilmeli.

### C. Sepet Koşulları
1.  **Min Tutar:** `cartTotal >= conditions.minOrderAmount`.
2.  **Kupon Kodu:** Eğer `requiresCode` true ise, kullanıcının girdiği input ile `conditions.code` birebir eşleşmeli.
### D. Çakışma Yönetimi (Conflict Resolution) - TEKİL KAMPANYA KURALI
1.  **Temel Prensip:** Sepete aynı anda **SADECE 1 (BİR)** kampanya uygulanabilir. Kampanyalar asla birleştirilemez.
2.  **Otomatik Seçim:** Eğer sepete uygulanabilir birden fazla kampanya varsa:
    *   Sistem hepsinin indirim tutarını ayrı ayrı hesaplar.
    *   Müşteriye **EN YÜKSEK İNDİRİMİ** sağlayan tek bir kampanya otomatik olarak seçilir ve uygulanır.
    *   Diğer kampanyalar göz ardı edilir.
    *   *Örnek:* Kampanya A (50₺ indirim) ve Kampanya B (40₺ indirim) varsa, sadece Kampanya A uygulanır.

## 3. Kampanya Türüne Göre Hesaplama Algoritmaları

Doğrulamayı geçen kampanya için `calculatedDiscount` (hesaplanan indirim) değeri aşağıdaki mantıklara göre bulunur.

### 3.1. Yüzdesel İndirim (PERCENTAGE_DISCOUNT)
*Mantık:* Kapsamdaki ürünlerin toplam tutarına %X indirim uygular.

1.  **Hedef Ürünleri Filtrele:** Sepetteki ürünlerden, kampanya `scope`'una uyanları bul.
    *   `ALL`: Tüm sepet.
    *   `CATEGORY`: `item.categoryId` değeri `applicableCategories` içinde olanlar.
    *   `ITEM`: `item.id` değeri `applicableItems` içinde olanlar.
2.  **Matrah Hesapla:** Filtrelenen ürünlerin toplam fiyatını (`TargetTotal`) hesapla.
3.  **İndirim:** `Discount = TargetTotal * (discountValue / 100)`.

### 3.2. Sabit Tutar İndirimi (FIXED_DISCOUNT)
*Mantık:* Kapsamdaki ürünlerin toplam tutarından Sabit X TL düşer.

1.  **Hedef Ürünleri Filtrele:** (Yukarıdaki ile aynı scope mantığı).
2.  **Matrah Hesapla:** Filtrelenen ürünlerin toplam fiyatını (`TargetTotal`) hesapla.
3.  **İndirim:**
    *   Eğer `TargetTotal < discountValue`: `Discount = TargetTotal` (İndirim ürün tutarını aşamaz, eksi bakiye olmaz).
    *   Eğer `TargetTotal >= discountValue`: `Discount = discountValue`.

### 3.3. X Al Y Öde (BUY_X_GET_Y)
*Mantık:* Kapsamdaki ürünlerden (X+Y) adet alındığında, Y tanesi (en ucuz olanlar) bedava olur.
*Örnek:* "2 Alana 1 Hediye" -> `buyQuantity: 2`, `getQuantity: 1`. Her 3'lü sette 1'i bedava.

1.  **Havuz Oluştur:** Scope'a (Kategori/Ürün) uyan sepetteki tüm ürünleri topla.
    *   *Önemli:* Sepet satırlarını (line items) tekil adetlere dök. Örn: 2 adet 'Latte' varsa, listeye 2 tane 'Latte' objesi ekle.
2.  **Sırala:** Havuzdaki ürünleri **Fiyatlarına Göre Artan (Ucuzdan Pahalıya)** sırala.
3.  **Paket Hesabı:**
    *   `PaketBoyutu = buyQuantity + getQuantity` (Örn: 2+1=3).
    *   `BedavaHakSayısı = Math.floor(HavuzdakiToplamAdet / PaketBoyutu) * getQuantity`.
4.  **İndirim:** Sıralı listenin en başındaki (en ucuz) `BedavaHakSayısı` kadar ürünün fiyatını topla.
    *   `Discount = Sum(First N items prices)`.

### 3.4. Hediye Ürün (FREE_ITEM)
*Mantık:* Sepet koşulları sağlanıyorsa, belirtilen ürün (`freeItemId`) ücretsiz verilir.

1.  **Sepet Kontrolü:** Sepette `freeItemId` ID'sine sahip ürün var mı?
2.  **İşlem:**
    *   **Var:** O sepetteki ilgili ürün satırından 1 adedinin fiyatını düş. `Discount = ItemPrice`.
    *   **Yok:**
        *   *Opsiyon A (Otomatik Ekleme):* Sepete otomatik olarak 1 adet o üründen, fiyatı 0 veya indirimli olarak ekle.
        *   *Opsiyon B (Uyarı):* Kullanıcıya "Sepetinize X ekleyin, bizden hediye!" mesajı göster. (Genelde Opsiyon A tercih edilir ama stok kontrolü gerekir).

## 4. Sepet Özeti & Sonuç

Sepet hesaplaması sonunda kullanıcıya şu döküm gösterilmelidir:

*   **Ara Toplam:** İndirimsiz Ürünler Toplamı.
*   **Kampanya İndirimi:** (-) Hesaplanan `Discount` değeri.
*   **Genel Toplam:** Ara Toplam - İndirim.

*Not: Çakışma durumunda (D maddesi) her zaman **Müşteri İçin En Avantajlı (En Yüksek İndirim)** senaryo otomatik seçilmelidir.*

## 5. Güvenlik ve Race Condition Yönetimi (Firebase)

Firebase (Firestore) yapısında "Yarış Durumu"nu (Race Condition) önlemek ve kampanya limitlerinin (%)100 güvenli çalışmasını sağlamak için **Transaction** yapısını kullanmak zorundayız.

### Sorun Nedir?
Eğer 100 kişi aynı anda "Son 1 Kampanya Hakkı" için istek atarsa, standart okuma-yazma işlemiyle 100'ü birden "Limit var" cevabı alabilir ve limit -99'a düşebilir (veya 100 kişi de indirim alır).

### Çözüm: Firestore Transaction Protokolü
Mobil uygulama siparişi tamamlarken (veya Cloud Function) kampanya kullanımını şu adımlarla işlemelidir:

```typescript
// Firebase Admin SDK veya Client SDK Örneği

async function redeemCampaign(campaignId, userId) {
    return await firestore.runTransaction(async (transaction) => {
        const campaignRef = firestore.collection('campaigns').doc(campaignId);
        const campaignDoc = await transaction.get(campaignRef);

        if (!campaignDoc.exists) {
            throw "Kampanya bulunamadı!";
        }

        const data = campaignDoc.data();
        const currentTotal = data.usage?.totalUsed || 0;
        const maxTotal = data.conditions?.maxTotalUsage || 0;

        // 1. KRİTİK KONTROL: Anlık Limit Kontrolü
        if (maxTotal > 0 && currentTotal >= maxTotal) {
            throw "Kampanya kullanım limiti doldu!"; // İşlem burada iptal edilir ve veritabanına yazılmaz.
        }

        // 2. İŞLEM: Sayacı Güvenli Artırma
        // Bu işlem sırasında başka biri araya girerse, Firebase transaction'ı otomatik tekrar dener.
        transaction.update(campaignRef, {
            'usage.totalUsed': currentTotal + 1
        });
        
        // Kullanıcı geçmişine de ekleme yapılabilir
        // transaction.set(userHistoryRef, { ... });
    });
}
```

Bu yöntemle:
1.  **Atomik İşlem:** Okuma ve Yazma tek bir paket halindedir. Ya hepsi yapılır ya hiçbiri yapılmaz.
2.  **Tutarlılık:** Eğer işlem sırasında veri değişirse (başka biri kampanya kullandıysa), işlem en baştan tekrar çalışır ve güncel veriyi okuyup tekrar limiti kontrol eder.

## 6. Firestore Security Rules (Sunucu Tarafı Koruma)

ÇOK ÖNEMLİ: API Key'iniz public olduğu için, güvenliği sadece mobil uygulama koduna bırakamazsınız. Kötü niyetli bir kişi tarayıcı konsolundan bile veritabanına yazmayı deneyebilir. Bunu engellemek için aşağıdaki **Firestore Güvenlik Kuralları**'nı Firebase panelinize eklemelisiniz:

```javascript
match /tenants/{tenantId}/campaigns/{campaignId} {
  // 1. Okuma İzni: Herkes (veya sadece giriş yapmış kullanıcılar) kampanyaları görebilir
  allow read: if true;

  // 2. Güncelleme İzni (Kritik Nokta)
  allow update: if request.auth != null && (
      // Senaryo A: Yönetici (Admin) ise her alanı değiştirebilir
      get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin' 
      
      ||
      
      // Senaryo B: Müşteri (Mobil/Web Kullanıcısı) ise SADECE kullanım sayacını 1 artırabilir
      (
        // Kural 1: Sadece 'usage' alanı değişiyorsa izin ver. (İndirim oranını, tarihini vs. değiştiremesin)
        request.resource.data.diff(resource.data).affectedKeys().hasOnly(['usage'])
        &&
        // Kural 2: Sayaç sadece ve sadece +1 artıyorsa izin ver.
        request.resource.data.usage.totalUsed == resource.data.usage.totalUsed + 1
        &&
        // Kural 3: Limit aşılmamışsa izin ver. (Limit dolduysa yazma işlemini reddet)
        (
             resource.data.conditions.maxTotalUsage == 0 ||
             resource.data.usage.totalUsed < resource.data.conditions.maxTotalUsage
        )
      )
  );
}
```

Bu kurallar sayesinde:
1.  Kimse admin paneli dışında indirim oranını, süresini vb. değiştiremez.
2.  Müşteriler (veya hackerlar) kampanya limitini tek seferde 1'den fazla artıramaz.
3.  Limit dolduğunda veritabanı otomatik olarak kilitleme yapar ve yazmayı reddeder.

