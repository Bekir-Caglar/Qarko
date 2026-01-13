# Qarko İşletme Admin Paneli - Geliştirme Prompt'u

## Proje Bilgileri

Bu döküman, Qarko mobil uygulamasının işletme tarafı yönetim panelini geliştirmek için kullanılacak detaylı bir prompt içermektedir.

---

## Ana Prompt

Aşağıdaki prompt'u AI asistanına (Claude, GPT-4, vb.) vererek projeyi başlatabilirsin:

---

### PROMPT BAŞLANGIÇ

Sen deneyimli bir full-stack web geliştiricisisin. Qarko adlı bir restoran/kafe sipariş yönetim sistemi için işletme admin paneli geliştirmeni istiyorum.

#### Teknoloji Stack

- React 18+ (Vite ile)
- TypeScript
- Tailwind CSS
- Firebase (Firestore, Auth, Storage)
- React Router v6
- React Query (TanStack Query)
- Zustand (state management)
- React Hook Form + Zod (form validation)
- Lucide React (icons)
- Recharts (grafikler için)
- date-fns (tarih işlemleri)

#### Tema ve Renkler

Qarko markasının renk paleti:

```css
:root {
  --primary: #51C4D3;
  --primary-dark: #126E82;
  --primary-light: #D4F1F4;
  --secondary: #FB7433;
  --background: #FFFFFF;
  --surface: #F5F5F5;
  --surface-gray: #f5f5f5;
  --text-primary: #000000;
  --text-secondary: #4B4B4B;
  --text-gray: #979797;
  --success: #2E7D32;
  --warning: #FFB800;
  --error: #D32F2F;
  --border: #E0DFDF;
  --border-light: #ECECEC;
}
```

Tailwind config'e eklenecek:

```javascript
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#51C4D3',
          dark: '#126E82',
          light: '#D4F1F4',
        },
        secondary: '#FB7433',
        surface: '#F5F5F5',
        'text-primary': '#000000',
        'text-secondary': '#4B4B4B',
        'text-gray': '#979797',
        success: '#2E7D32',
        warning: '#FFB800',
        error: '#D32F2F',
      },
    },
  },
}
```

#### Firebase Yapısı

Firestore veritabanı yapısı:

##### 1. Tenants Collection (İşletmeler)

```
tenants/{tenantId}
  - id: string
  - name: string
  - slug: string
  - logo: string (URL)
  - coverImage: string (URL)
  - description: string
  - contact: { phone, email, website }
  - address: { street, district, city, country, coordinates }
  - workingHours: { monday: {open, close, isOpen}, ... }
  - features: { hasAlcohol, hasHookah, hasPizza, hasBurger, ... }
  - status: "ACTIVE" | "INACTIVE" | "SUSPENDED"
  - isOpen: boolean
  - theme: { primaryColor, secondaryColor }
  - createdAt, updatedAt
```

##### 2. Menu Subcollection

```
tenants/{tenantId}/menu/{categoryId}
  - id: string
  - name: string (kategori adı: "Burgerler", "Pizzalar", vb.)
  - emoji: string
  - sortOrder: number
  - isActive: boolean

tenants/{tenantId}/menu/{categoryId}/items/{itemId}
  - id: string
  - name: string
  - description: string
  - imageUrl: string
  - price: number
  - discountedPrice: number | null
  - foodType: string
  - ingredients: array
  - allergens: array
  - customizationGroups: array
  - isActive: boolean
  - isFeatured: boolean
  - stock: { isAvailable, quantity }
```

##### 3. Orders Collection

```
orders/{orderId}
  - id: string
  - orderNumber: string ("ORD-2026-001234")
  - tenantId: string
  - userId: string
  - table: { id, name, section }
  - type: "DINE_IN" | "TAKEAWAY" | "DELIVERY"
  - items: array
  - pricing: { subtotal, discount, tip, total }
  - payment: { method, status, transactionId }
  - status: "PENDING" | "CONFIRMED" | "PREPARING" | "READY" | "SERVED" | "COMPLETED" | "CANCELLED"
  - statusHistory: array
  - timing: { placedAt, confirmedAt, prepStartedAt, readyAt, ... }
  - notes: { customerNote, kitchenNote }
  - createdAt, updatedAt
```

##### 4. Tables Subcollection

```
tenants/{tenantId}/tables/{tableId}
  - id: string
  - name: string ("Masa 3")
  - section: string ("İç Mekan", "Bahçe", "Teras")
  - capacity: number
  - status: "AVAILABLE" | "OCCUPIED" | "RESERVED" | "CLEANING"
  - qrCode: { url, imageUrl }
  - isActive: boolean
```

##### 5. Campaigns Subcollection

```
tenants/{tenantId}/campaigns/{campaignId}
  - id: string
  - name: string
  - title: string
  - description: string
  - type: "PERCENTAGE_DISCOUNT" | "FIXED_DISCOUNT" | "BUY_X_GET_Y" | "FREE_ITEM"
  - discountValue: number
  - conditions: { minOrderAmount, applicableCategories, ... }
  - validity: { startDate, endDate, isActive }
  - usage: { totalUsed, totalDiscountGiven }
```

#### Admin Panel Sayfaları ve Özellikleri

##### 1. Login Sayfası (/login)

- E-posta ve şifre ile giriş
- "Şifremi Unuttum" linki
- Qarko logosu ve marka renkleri
- Responsive tasarım

##### 2. Dashboard (/dashboard)

Ana sayfa, özet istatistikler:

```
┌─────────────────────────────────────────────────────────────────┐
│  📊 Dashboard                                    [Bugün ▼]      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ Bugünkü  │ │ Aktif    │ │ Bekleyen │ │ Toplam   │          │
│  │ Ciro     │ │ Sipariş  │ │ Sipariş  │ │ Müşteri  │          │
│  │ ₺4,250   │ │ 12       │ │ 3        │ │ 45       │          │
│  │ ↑ %12    │ │          │ │          │ │ ↑ %8     │          │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
│                                                                 │
│  ┌─────────────────────────────────┐ ┌─────────────────────┐   │
│  │ 📈 Haftalık Satış Grafiği      │ │ 🔔 Son Siparişler   │   │
│  │                                 │ │                     │   │
│  │   [Line Chart]                  │ │  Masa 3 - ₺125     │   │
│  │                                 │ │  Masa 7 - ₺89      │   │
│  │                                 │ │  Masa 1 - ₺210     │   │
│  └─────────────────────────────────┘ └─────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────┐ ┌─────────────────────┐   │
│  │ 🍔 En Çok Satanlar             │ │ 📍 Masa Durumları   │   │
│  │                                 │ │                     │   │
│  │  1. Classic Burger (45)         │ │  [Masa Grid View]   │   │
│  │  2. Margarita Pizza (38)        │ │                     │   │
│  │  3. Limonata (52)               │ │                     │   │
│  └─────────────────────────────────┘ └─────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

##### 3. Siparişler Sayfası (/orders)

Canlı sipariş yönetimi:

```
┌─────────────────────────────────────────────────────────────────┐
│  🍽️ Siparişler                          [Yeni Sipariş 🔔]      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [Tümü] [Bekleyen] [Hazırlanıyor] [Hazır] [Tamamlanan]         │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 🟡 BEKLEYEN                                              │   │
│  │                                                          │   │
│  │ ┌────────────────────────────────────────────────────┐  │   │
│  │ │ #ORD-001234          📍 Masa 3          15:45      │  │   │
│  │ │ ──────────────────────────────────────────────────  │  │   │
│  │ │ 2x Classic Burger                          ₺300    │  │   │
│  │ │    • Orta pişmiş, BBQ sos, +Bacon                  │  │   │
│  │ │ 1x Limonata (Büyük)                        ₺45     │  │   │
│  │ │ ──────────────────────────────────────────────────  │  │   │
│  │ │ 💬 "Hızlı olursa sevinirim"                        │  │   │
│  │ │ ──────────────────────────────────────────────────  │  │   │
│  │ │ TOPLAM: ₺345                                       │  │   │
│  │ │                                                     │  │   │
│  │ │ [✓ Onayla]  [✗ Reddet]  [📞 Müşteriyi Ara]        │  │   │
│  │ └────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 🔵 HAZIRLANIYOR                                         │   │
│  │ ...                                                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

Sipariş kartı özellikleri:
- Sipariş numarası ve masa bilgisi
- Ürün listesi (özelleştirmeler dahil)
- Müşteri notu
- Toplam tutar
- Durum değiştirme butonları
- Sipariş zamanı ve geçen süre
- Sesli bildirim (yeni sipariş)

##### 4. Menü Yönetimi (/menu)

Kategoriler ve ürünler:

```
┌─────────────────────────────────────────────────────────────────┐
│  🍽️ Menü Yönetimi                        [+ Yeni Kategori]     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐                                               │
│  │ Kategoriler │                                               │
│  │             │  ┌─────────────────────────────────────────┐  │
│  │ 🍔 Burgerler│  │ Burgerler (8 ürün)      [+ Yeni Ürün]  │  │
│  │ 🍕 Pizzalar │  │                                         │  │
│  │ 🥤 İçecekler│  │ ┌─────────────────────────────────────┐ │  │
│  │ 🍰 Tatlılar │  │ │ [img] Classic Burger                │ │  │
│  │ ☕ Sıcak    │  │ │       ₺150  ✓ Aktif  ⭐ Öne Çıkan   │ │  │
│  │             │  │ │       [Düzenle] [Sil] [Gizle]       │ │  │
│  │ [Sıralamayı │  │ └─────────────────────────────────────┘ │  │
│  │  Kaydet]    │  │                                         │  │
│  └─────────────┘  │ ┌─────────────────────────────────────┐ │  │
│                   │ │ [img] Cheese Burger                 │ │  │
│                   │ │       ₺140  ✓ Aktif                 │ │  │
│                   │ └─────────────────────────────────────┘ │  │
│                   └─────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

Ürün ekleme/düzenleme modal:

```
┌─────────────────────────────────────────────────────────────────┐
│  Ürün Ekle / Düzenle                                    [X]    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐  Ürün Adı                                     │
│  │             │  [Classic Burger________________________]     │
│  │   [Resim    │                                               │
│  │    Yükle]   │  Açıklama                                     │
│  │             │  [180gr dana eti, cheddar, marul, domates]   │
│  └─────────────┘                                               │
│                                                                 │
│  Kategori              Fiyat              İndirimli Fiyat      │
│  [Burgerler ▼]        [₺150____]         [₺______ ]           │
│                                                                 │
│  Hazırlık Süresi       Kalori                                  │
│  [15-20 dk___]        [650_____]                               │
│                                                                 │
│  ═══════════════════════════════════════════════════════════   │
│  Malzemeler                                      [+ Ekle]      │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ 🥩 Dana Eti (Ana)  │ 🧀 Cheddar  │ 🥬 Marul  │ ✕      │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ═══════════════════════════════════════════════════════════   │
│  Özelleştirme Grupları                           [+ Ekle]      │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Pişirme Derecesi (Tek Seçim, Zorunlu)                  │   │
│  │ ○ Az Pişmiş (+₺0)  ● Orta (+₺0)  ○ Çok Pişmiş (+₺0)   │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Ekstralar (Çoklu Seçim)                                │   │
│  │ ☑ Bacon (+₺25)  ☐ Yumurta (+₺15)  ☐ Ekstra Peynir (+₺20)│  │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ═══════════════════════════════════════════════════════════   │
│  Alerjenler                                                    │
│  ☑ Gluten  ☑ Süt  ☐ Yumurta  ☐ Fıstık  ☐ Kabuklu Deniz Ürünü │
│                                                                 │
│  ═══════════════════════════════════════════════════════════   │
│  Durum                                                         │
│  ☑ Aktif     ☐ Öne Çıkan     ☐ Yeni Ürün                      │
│  ☑ Stokta Var                                                  │
│                                                                 │
│                              [İptal]  [💾 Kaydet]              │
└─────────────────────────────────────────────────────────────────┘
```

##### 5. Masa Yönetimi (/tables)

```
┌─────────────────────────────────────────────────────────────────┐
│  📍 Masa Yönetimi                           [+ Yeni Masa]      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Bölümler: [Tümü] [İç Mekan] [Bahçe] [Teras] [VIP]            │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                      İÇ MEKAN                            │   │
│  │                                                          │   │
│  │   ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐          │   │
│  │   │  1  │  │  2  │  │  3  │  │  4  │  │  5  │          │   │
│  │   │ 🟢  │  │ 🟡  │  │ 🔵  │  │ 🟢  │  │ 🟢  │          │   │
│  │   │ 4👤 │  │ 2👤 │  │ 4👤 │  │ 2👤 │  │ 6👤 │          │   │
│  │   └─────┘  └─────┘  └─────┘  └─────┘  └─────┘          │   │
│  │                                                          │   │
│  │   ┌─────┐  ┌─────┐  ┌─────┐                             │   │
│  │   │  6  │  │  7  │  │  8  │                             │   │
│  │   │ 🔴  │  │ 🟢  │  │ ⚫  │                             │   │
│  │   │ 4👤 │  │ 4👤 │  │ 2👤 │                             │   │
│  │   └─────┘  └─────┘  └─────┘                             │   │
│  │                                                          │   │
│  │  🟢 Müsait  🟡 Dolu  🔵 Sipariş Var  🔴 Ödeme Bekliyor  │   │
│  │  ⚫ Temizleniyor  ⚪ Rezerve                             │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Seçili Masa: Masa 3                                           │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Durum: Sipariş Var (🔵)                                  │   │
│  │ Aktif Sipariş: #ORD-001234                              │   │
│  │ Tutar: ₺345                                              │   │
│  │ Süre: 25 dk                                              │   │
│  │                                                          │   │
│  │ [📋 Siparişi Gör] [🧾 Hesap Kes] [📱 QR Kodu İndir]     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

##### 6. Kampanyalar (/campaigns)

```
┌─────────────────────────────────────────────────────────────────┐
│  🎁 Kampanyalar                            [+ Yeni Kampanya]   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [Aktif] [Taslak] [Sona Ermiş] [Tümü]                          │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 🟢 AKTİF                                                 │   │
│  │                                                          │   │
│  │ ┌────────────────────────────────────────────────────┐  │   │
│  │ │ 🌞 Yaz Kampanyası                                   │  │   │
│  │ │ İçeceklerde %20 İndirim                            │  │   │
│  │ │                                                     │  │   │
│  │ │ 📅 01.06.2026 - 31.08.2026                         │  │   │
│  │ │ 📊 Kullanım: 245 / 1000                            │  │   │
│  │ │ 💰 Toplam İndirim: ₺4,500                          │  │   │
│  │ │                                                     │  │   │
│  │ │ [Düzenle] [Durdur] [İstatistikler]                 │  │   │
│  │ └────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

##### 7. Raporlar (/reports)

```
┌─────────────────────────────────────────────────────────────────┐
│  📊 Raporlar                                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Tarih Aralığı: [01.01.2026] - [12.01.2026]  [Uygula]          │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 💰 SATIŞ ÖZETİ                                          │   │
│  │                                                          │   │
│  │ Toplam Ciro:        ₺125,450                            │   │
│  │ Sipariş Sayısı:     1,245                               │   │
│  │ Ortalama Sepet:     ₺100.76                             │   │
│  │ İptal Edilen:       23 (₺2,150)                         │   │
│  │                                                          │   │
│  │ [📥 Excel İndir] [📄 PDF İndir]                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 📈 GÜNLÜK SATIŞ GRAFİĞİ                                 │   │
│  │                                                          │   │
│  │  ₺15k │        ╭─╮                                      │   │
│  │       │     ╭──╯ ╰──╮                                   │   │
│  │  ₺10k │  ╭──╯       ╰──╮    ╭──╮                       │   │
│  │       │──╯             ╰────╯  ╰──                      │   │
│  │  ₺5k  │                                                 │   │
│  │       └────────────────────────────────────             │   │
│  │        Pzt  Sal  Çar  Per  Cum  Cmt  Paz               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌──────────────────────┐  ┌──────────────────────┐           │
│  │ 🏆 EN ÇOK SATANLAR   │  │ 📍 MASA PERFORMANSI  │           │
│  │                      │  │                      │           │
│  │ 1. Classic Burger    │  │ Masa 3: ₺12,450     │           │
│  │    425 adet - ₺63,750│  │ Masa 1: ₺11,200     │           │
│  │                      │  │ Masa 7: ₺10,800     │           │
│  │ 2. Margarita Pizza   │  │                      │           │
│  │    312 adet - ₺46,800│  │                      │           │
│  └──────────────────────┘  └──────────────────────┘           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

##### 8. Ayarlar (/settings)

```
┌─────────────────────────────────────────────────────────────────┐
│  ⚙️ Ayarlar                                                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐                                               │
│  │ • Genel     │  ┌─────────────────────────────────────────┐  │
│  │   İşletme   │  │ İŞLETME BİLGİLERİ                       │  │
│  │   Çalışma   │  │                                         │  │
│  │   Sipariş   │  │ İşletme Adı                             │  │
│  │   Ödeme     │  │ [IF Sokak Lezzetleri_______________]   │  │
│  │   Bildirim  │  │                                         │  │
│  │   Personel  │  │ Logo  ┌──────┐  Kapak  ┌──────────┐    │  │
│  └─────────────┘  │       │ [img]│         │  [img]   │    │  │
│                   │       └──────┘         └──────────┘    │  │
│                   │       [Değiştir]       [Değiştir]      │  │
│                   │                                         │  │
│                   │ Açıklama                                │  │
│                   │ [Sokak lezzetlerinin en iyisi_______]  │  │
│                   │                                         │  │
│                   │ Telefon              E-posta            │  │
│                   │ [+90 532 123 4567]  [info@ifsokak.com] │  │
│                   │                                         │  │
│                   │ Adres                                   │  │
│                   │ [Atatürk Cad. No:123, Kadıköy/İstanbul]│  │
│                   │                                         │  │
│                   │                        [💾 Kaydet]      │  │
│                   └─────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

Çalışma Saatleri sekmesi:

```
┌─────────────────────────────────────────────────────────────────┐
│  ÇALIŞMA SAATLERİ                                              │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Gün          Açık    Açılış     Kapanış      Durum    │   │
│  │ ──────────────────────────────────────────────────────  │   │
│  │ Pazartesi    [✓]     [10:00]    [23:00]      🟢 Açık  │   │
│  │ Salı         [✓]     [10:00]    [23:00]      🟢 Açık  │   │
│  │ Çarşamba     [✓]     [10:00]    [23:00]      🟢 Açık  │   │
│  │ Perşembe     [✓]     [10:00]    [23:00]      🟢 Açık  │   │
│  │ Cuma         [✓]     [10:00]    [00:00]      🟢 Açık  │   │
│  │ Cumartesi    [✓]     [11:00]    [00:00]      🟢 Açık  │   │
│  │ Pazar        [✓]     [11:00]    [22:00]      🟢 Açık  │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Anlık Durum: [🟢 Açık ▼]  ← Manuel aç/kapat                   │
│                                                                 │
│                                           [💾 Kaydet]          │
└─────────────────────────────────────────────────────────────────┘
```

Sipariş Ayarları sekmesi:

```
┌─────────────────────────────────────────────────────────────────┐
│  SİPARİŞ AYARLARI                                              │
│                                                                 │
│  Sipariş Türleri                                               │
│  ☑ Restoranda Yeme (Dine-in)                                   │
│  ☑ Paket Servis (Takeaway)                                     │
│  ☐ Kurye ile Teslimat (Delivery)                               │
│                                                                 │
│  ═══════════════════════════════════════════════════════════   │
│                                                                 │
│  Ödeme Yöntemleri                                              │
│  ☑ Kredi Kartı                                                 │
│  ☑ Nakit (Kasada Öde)                                          │
│                                                                 │
│  Kasada Öde Ayarları                                           │
│  ├── Telefon doğrulama zorunlu:  [✓]                          │
│  ├── Yeni kullanıcı limiti:      [₺200____]                   │
│  ├── Güvenilir kullanıcı limiti: [₺500____]                   │
│  └── VIP kullanıcı limiti:       [Limitsiz]                   │
│                                                                 │
│  ═══════════════════════════════════════════════════════════   │
│                                                                 │
│  Minimum Sipariş Tutarı:  [₺50_____]                          │
│  Tahmini Hazırlık Süresi: [15-20 dk]                          │
│                                                                 │
│  Sipariş Onayı                                                 │
│  ○ Otomatik onayla                                             │
│  ● Manuel onay gerektir                                        │
│                                                                 │
│                                           [💾 Kaydet]          │
└─────────────────────────────────────────────────────────────────┘
```

#### Layout Yapısı

```
┌─────────────────────────────────────────────────────────────────┐
│  ┌─────────┐  Qarko Admin           🔔 3    👤 Ahmet Y.  [Çıkış]│
│  │  LOGO   │  IF Sokak Lezzetleri   Bildirim                    │
│  └─────────┘                                                    │
├─────────────┬───────────────────────────────────────────────────┤
│             │                                                   │
│  📊 Dashboard│                                                  │
│  🍽️ Siparişler│              MAIN CONTENT AREA                 │
│  📋 Menü     │                                                  │
│  📍 Masalar  │                                                  │
│  🎁 Kampanyalar│                                                │
│  📊 Raporlar │                                                  │
│  ⚙️ Ayarlar  │                                                  │
│             │                                                   │
│             │                                                   │
│─────────────│                                                   │
│  🟢 Açık    │                                                   │
│  [Kapat ▼]  │                                                   │
│             │                                                   │
└─────────────┴───────────────────────────────────────────────────┘
```

#### Önemli Özellikler

1. Real-time Updates
   - Firestore onSnapshot kullanarak siparişleri canlı dinle
   - Yeni sipariş geldiğinde ses çal ve bildirim göster
   - Masa durumları otomatik güncelle

2. Responsive Design
   - Tablet ve desktop için optimize
   - Sidebar collapse edilebilir
   - Mobilde hamburger menü

3. Bildirimler
   - Browser notification API
   - Ses bildirimi (yeni sipariş)
   - Toast mesajları

4. QR Kod Yönetimi
   - Her masa için QR kod oluştur
   - QR kodları toplu indir (PDF)
   - QR kod önizleme

5. Performans
   - Lazy loading
   - Pagination
   - Infinite scroll (siparişler)

#### Dosya Yapısı

```
src/
├── components/
│   ├── ui/                    # Temel UI bileşenleri
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Modal.tsx
│   │   ├── Card.tsx
│   │   ├── Badge.tsx
│   │   ├── Toast.tsx
│   │   └── ...
│   ├── layout/
│   │   ├── Sidebar.tsx
│   │   ├── Header.tsx
│   │   ├── Layout.tsx
│   │   └── MobileNav.tsx
│   ├── orders/
│   │   ├── OrderCard.tsx
│   │   ├── OrderList.tsx
│   │   ├── OrderDetails.tsx
│   │   └── OrderStatusBadge.tsx
│   ├── menu/
│   │   ├── CategoryList.tsx
│   │   ├── MenuItemCard.tsx
│   │   ├── MenuItemForm.tsx
│   │   └── CustomizationBuilder.tsx
│   ├── tables/
│   │   ├── TableGrid.tsx
│   │   ├── TableCard.tsx
│   │   └── QRCodeModal.tsx
│   └── dashboard/
│       ├── StatCard.tsx
│       ├── SalesChart.tsx
│       └── RecentOrders.tsx
├── pages/
│   ├── Login.tsx
│   ├── Dashboard.tsx
│   ├── Orders.tsx
│   ├── Menu.tsx
│   ├── Tables.tsx
│   ├── Campaigns.tsx
│   ├── Reports.tsx
│   └── Settings.tsx
├── hooks/
│   ├── useOrders.ts
│   ├── useMenu.ts
│   ├── useTables.ts
│   ├── useAuth.ts
│   └── useNotification.ts
├── services/
│   ├── firebase.ts
│   ├── orderService.ts
│   ├── menuService.ts
│   └── tableService.ts
├── store/
│   ├── authStore.ts
│   ├── tenantStore.ts
│   └── notificationStore.ts
├── types/
│   ├── order.ts
│   ├── menu.ts
│   ├── table.ts
│   └── tenant.ts
├── utils/
│   ├── formatters.ts
│   ├── validators.ts
│   └── qrGenerator.ts
├── App.tsx
├── main.tsx
└── index.css
```

### PROMPT SONU

---

## Ek Bilgiler

### Kullanılacak Kütüphaneler

```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "@tanstack/react-query": "^5.0.0",
    "zustand": "^4.4.0",
    "firebase": "^10.7.0",
    "react-hook-form": "^7.48.0",
    "@hookform/resolvers": "^3.3.0",
    "zod": "^3.22.0",
    "lucide-react": "^0.294.0",
    "recharts": "^2.10.0",
    "date-fns": "^2.30.0",
    "react-hot-toast": "^2.4.0",
    "qrcode.react": "^3.1.0",
    "html2canvas": "^1.4.0",
    "jspdf": "^2.5.0",
    "xlsx": "^0.18.0"
  },
  "devDependencies": {
    "typescript": "^5.3.0",
    "vite": "^5.0.0",
    "@types/react": "^18.2.0",
    "tailwindcss": "^3.3.0",
    "autoprefixer": "^10.4.0",
    "postcss": "^8.4.0"
  }
}
```

### Başlangıç Komutları

```bash
# Proje oluştur
npm create vite@latest qarko-admin -- --template react-ts

# Klasöre gir
cd qarko-admin

# Bağımlılıkları yükle
npm install

# Tailwind kurulumu
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p

# Firebase ve diğer paketler
npm install firebase react-router-dom @tanstack/react-query zustand
npm install react-hook-form @hookform/resolvers zod
npm install lucide-react recharts date-fns react-hot-toast
npm install qrcode.react html2canvas jspdf xlsx

# Geliştirme sunucusu
npm run dev
```

### Firebase Kurulumu

1. Firebase Console'da proje oluştur
2. Web uygulaması ekle
3. Firestore veritabanı oluştur
4. Authentication ayarla (Email/Password)
5. Storage oluştur
6. Config bilgilerini .env dosyasına ekle

```env
VITE_FIREBASE_API_KEY=xxx
VITE_FIREBASE_AUTH_DOMAIN=xxx.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=xxx
VITE_FIREBASE_STORAGE_BUCKET=xxx.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=xxx
VITE_FIREBASE_APP_ID=xxx
```

---

## Sonraki Adımlar

Bu prompt'u kullanarak admin paneli geliştirmeye başlayabilirsin. Önerilen sıralama:

1. Proje kurulumu ve temel yapı
2. Firebase bağlantısı ve auth
3. Layout ve routing
4. Dashboard sayfası
5. Siparişler sayfası (en kritik)
6. Menü yönetimi
7. Masa yönetimi
8. Kampanyalar
9. Raporlar
10. Ayarlar

Her adımda incremental olarak ilerle ve test et.

