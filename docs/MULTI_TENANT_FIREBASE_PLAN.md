# 🏪 Qarko Multi-Tenant Firebase Entegrasyon Planı

## 📋 İçindekiler

1. [Genel Bakış](#genel-bakış)
2. [Auth Stratejisi](#auth-stratejisi)
3. [Firebase Yapılandırması](#firebase-yapılandırması)
4. [Veritabanı Şeması](#veritabanı-şeması)
5. [QR Kod Sistemi](#qr-kod-sistemi)
6. [Uygulama Mimarisi](#uygulama-mimarisi)
7. [Veri Modelleri](#veri-modelleri)
8. [Repository Katmanı](#repository-katmanı)
9. [State Management](#state-management)
10. [Ekran Güncellemeleri](#ekran-güncellemeleri)
11. [Güvenlik Kuralları](#güvenlik-kuralları)
12. [Uygulama Adımları](#uygulama-adımları)

---

## 🎯 Genel Bakış

### Amaç
Qarko uygulamasını birden fazla işletmenin kullanabileceği multi-tenant bir sisteme dönüştürmek. Her işletme kendi QR kodu ile müşterilerine menü, sipariş ve kampanya hizmetleri sunabilecek.

### Akış Diyagramı

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   QR Kod Tara   │────▶│  İşletme ID Al  │────▶│ Firebase'den    │
│   (Masa/İşletme)│     │  (tenantId)     │     │ Veri Çek        │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                                        │
                                                        ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Sipariş Ver    │◀────│   Menü Göster   │◀────│ İşletme Bilgisi │
│                 │     │                 │     │ Menü, Kampanya  │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

---

## 🔐 Auth Stratejisi

### Temel Felsefe: "Geç Giriş" (Lazy Authentication)

Kullanıcıyı en baştan giriş yapmaya zorlamak yerine, **kritik aksiyonlarda** giriş istemek. Bu sayede:
- Kullanıcı deneyimi akıcı kalır
- Drop-off oranı düşer
- Güvenlik sağlanır

### Auth Gereksinim Matrisi

| Aksiyon | Auth Gerekli mi? | Gerekçe |
|---------|------------------|---------|
| QR kod okutma | ❌ Hayır | Herkese açık |
| Menüyü görüntüleme | ❌ Hayır | Keşif aşaması |
| Ürün detaylarını görme | ❌ Hayır | Bilgi edinme |
| Sepete ekleme | ❌ Hayır | Sepet local tutulabilir |
| Fiyatları görme | ❌ Hayır | Şeffaflık |
| **Sipariş verme** | ✅ **EVET** | Fake sipariş önleme |
| **Kampanya kullanma** | ✅ **EVET** | Kötüye kullanım önleme |
| Geçmiş siparişler | ✅ Evet | Kişisel veri |
| Favorilere ekleme | ✅ Evet | Kişisel veri |
| Kayıtlı kart kullanma | ✅ Evet | Ödeme güvenliği |
| Puan kazanma | ✅ Evet | Sadakat programı |

### Kullanıcı Akışı

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         KULLANICI AKIŞI                                   │
└──────────────────────────────────────────────────────────────────────────┘

    ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
    │  QR Okut    │────▶│  Menü Gör   │────▶│ Sepete Ekle │
    │  (Auth ❌)  │     │  (Auth ❌)  │     │  (Auth ❌)  │
    └─────────────┘     └─────────────┘     └─────────────┘
                                                   │
                                                   ▼
                                          ┌─────────────────┐
                                          │  Ödemeye Geç    │
                                          │  (Auth ❌)      │
                                          └─────────────────┘
                                                   │
                        ┌──────────────────────────┼──────────────────────────┐
                        │                          │                          │
                        ▼                          ▼                          ▼
              ┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
              │  Kampanya Var   │      │  Sipariş Ver    │      │  Kasada Öde     │
              │  (Auth ✅)      │      │  (Auth ✅)      │      │  (Auth ⚠️)      │
              └─────────────────┘      └─────────────────┘      └─────────────────┘
                        │                          │                          │
                        │                          │                          │
                        ▼                          ▼                          ▼
              ┌─────────────────────────────────────────────────────────────────┐
              │                                                                 │
              │                    GİRİŞ / KAYIT EKRANI                        │
              │                                                                 │
              │   ┌─────────────────────────────────────────────────────────┐  │
              │   │                                                         │  │
              │   │   📱 Telefon ile Giriş (OTP)     ← ÖNERİLEN            │  │
              │   │   📧 E-posta ile Giriş                                  │  │
              │   │   🍎 Apple ile Giriş                                    │  │
              │   │   🔵 Google ile Giriş                                   │  │
              │   │                                                         │  │
              │   └─────────────────────────────────────────────────────────┘  │
              │                                                                 │
              │   ⚠️ Kasada Öde seçeneğinde telefon doğrulama zorunlu         │
              │                                                                 │
              └─────────────────────────────────────────────────────────────────┘
```

### Fake Sipariş Önleme Stratejileri

#### 1. Telefon Doğrulama (En Etkili)

```kotlin
// Sipariş vermek için telefon doğrulama ZORUNLU
data class UserVerification(
    val phoneNumber: String,
    val isVerified: Boolean,
    val verifiedAt: Instant?,
    val verificationMethod: VerificationMethod
)

enum class VerificationMethod {
    SMS_OTP,           // SMS ile tek kullanımlık kod
    WHATSAPP_OTP,      // WhatsApp ile doğrulama
    MISSED_CALL        // Çağrı ile doğrulama (daha ucuz)
}
```

**Neden Telefon?**
- Her kişinin genelde 1-2 telefonu var
- Fake numara almak zor ve maliyetli
- SMS maliyeti işletme için düşük (~0.05₺/SMS)
- Hızlı ve kolay UX

#### 2. Kart ile Ödeme Zorunluluğu (Alternatif)

```
Kasada Öde seçeneği için:
├── Telefon doğrulama ZORUNLU
├── VEYA kayıtlı kart ile ön provizyon (1₺ çekilir, iade edilir)
└── VEYA minimum sipariş geçmişi (en az 1 başarılı sipariş)
```

#### 3. Risk Skoru Sistemi

```kotlin
// Her sipariş için risk skoru hesapla
data class OrderRiskAssessment(
    val score: Int,  // 0-100 arası, yüksek = riskli
    val factors: List<RiskFactor>,
    val action: RiskAction
)

enum class RiskFactor {
    NEW_USER,                    // +20 puan
    UNVERIFIED_PHONE,           // +30 puan
    HIGH_ORDER_VALUE,           // +15 puan (>500₺)
    MULTIPLE_ORDERS_SHORT_TIME, // +25 puan
    SUSPICIOUS_LOCATION,        // +20 puan
    FIRST_ORDER_AT_TENANT,      // +10 puan
    CASH_PAYMENT,               // +15 puan
    LATE_NIGHT_ORDER            // +10 puan (00:00-06:00)
}

enum class RiskAction {
    ALLOW,                  // score < 30
    REQUIRE_PHONE_VERIFY,   // score 30-60
    REQUIRE_CARD_PAYMENT,   // score 60-80
    MANUAL_REVIEW,          // score 80-90
    BLOCK                   // score > 90
}
```

#### 4. İşletme Tarafı Koruma

```kotlin
// İşletme ayarları
data class TenantOrderSettings(
    val requirePhoneVerification: Boolean = true,
    val allowCashPayment: Boolean = true,
    val cashPaymentRequiresVerification: Boolean = true,
    val minOrderForCashPayment: Double = 0.0,
    val maxCashOrderValue: Double = 500.0,
    val blockNewUsersForCash: Boolean = false,
    val autoRejectHighRiskOrders: Boolean = false,
    val manualApprovalThreshold: Int = 70
)
```

### Kasada Öde - Özel Durum

"Kasada Öde" seçeneği en riskli seçenek çünkü:
- Para alınmadan sipariş veriliyor
- Müşteri gelmeyebilir (fake sipariş)
- İşletme zarar eder

**Çözüm: Kademeli Güven Sistemi**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    KASADA ÖDE - GÜVENLİK KADEMELERİ                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  KADEME 1: Yeni Kullanıcı (0 sipariş)                                  │
│  ├── Telefon doğrulama ZORUNLU                                         │
│  ├── Maksimum sipariş tutarı: 200₺                                     │
│  └── İşletme onayı gerekebilir                                         │
│                                                                         │
│  KADEME 2: Güvenilir Kullanıcı (1-5 başarılı sipariş)                  │
│  ├── Telefon doğrulama yapılmış                                        │
│  ├── Maksimum sipariş tutarı: 500₺                                     │
│  └── Otomatik onay                                                     │
│                                                                         │
│  KADEME 3: VIP Kullanıcı (5+ başarılı sipariş)                         │
│  ├── Limit yok                                                         │
│  ├── Otomatik onay                                                     │
│  └── Özel avantajlar                                                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Auth UI Akışı

```kotlin
// Sipariş verme butonuna basıldığında
@Composable
fun CheckoutScreen() {
    val user by UserManager.currentUser.collectAsState()
    val isPhoneVerified = user?.isPhoneVerified ?: false
    
    fun onPlaceOrder() {
        when {
            // Kullanıcı giriş yapmamış
            user == null -> {
                showAuthBottomSheet(
                    reason = AuthReason.PLACE_ORDER,
                    message = "Sipariş vermek için giriş yapın"
                )
            }
            
            // Telefon doğrulanmamış
            !isPhoneVerified -> {
                showPhoneVerificationSheet(
                    reason = "Siparişinizi onaylamak için telefonunuzu doğrulayın"
                )
            }
            
            // Her şey tamam, sipariş ver
            else -> {
                placeOrder()
            }
        }
    }
}

// Auth Bottom Sheet
@Composable
fun AuthBottomSheet(
    reason: AuthReason,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // İkon
            Icon(
                imageVector = when (reason) {
                    AuthReason.PLACE_ORDER -> Icons.Default.ShoppingCart
                    AuthReason.USE_CAMPAIGN -> Icons.Default.LocalOffer
                    AuthReason.VIEW_HISTORY -> Icons.Default.History
                    AuthReason.ADD_FAVORITE -> Icons.Default.Favorite
                },
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Başlık
            Text(
                text = when (reason) {
                    AuthReason.PLACE_ORDER -> "Sipariş vermek için giriş yapın"
                    AuthReason.USE_CAMPAIGN -> "Kampanya kullanmak için giriş yapın"
                    AuthReason.VIEW_HISTORY -> "Siparişlerinizi görmek için giriş yapın"
                    AuthReason.ADD_FAVORITE -> "Favorilere eklemek için giriş yapın"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Açıklama
            Text(
                text = "Giriş yaparak siparişlerinizi takip edebilir, kampanyalardan yararlanabilirsiniz.",
                fontSize = 14.sp,
                color = gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Telefon ile giriş (Birincil)
            Button(
                onClick = { /* Telefon giriş */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Telefon ile Giriş")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Diğer seçenekler
            OutlinedButton(
                onClick = { /* Google giriş */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Google icon
                Text("Google ile Giriş")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { /* Apple giriş */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Apple icon
                Text("Apple ile Giriş")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Gizlilik notu
            Text(
                text = "Giriş yaparak Kullanım Koşulları ve Gizlilik Politikası'nı kabul etmiş olursunuz.",
                fontSize = 11.sp,
                color = gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

enum class AuthReason {
    PLACE_ORDER,
    USE_CAMPAIGN,
    VIEW_HISTORY,
    ADD_FAVORITE
}
```

### Firebase Auth Yapılandırması

```kotlin
// Desteklenen auth yöntemleri
object AuthProviders {
    const val PHONE = "phone"           // SMS OTP
    const val GOOGLE = "google.com"     // Google Sign-In
    const val APPLE = "apple.com"       // Apple Sign-In
    const val EMAIL = "password"        // E-posta/Şifre (opsiyonel)
}

// Auth servisi
class AuthService(
    private val firebaseAuth: FirebaseAuth
) {
    // Telefon ile giriş
    suspend fun signInWithPhone(
        phoneNumber: String,
        verificationCode: String
    ): Result<FirebaseUser>
    
    // Google ile giriş
    suspend fun signInWithGoogle(
        idToken: String
    ): Result<FirebaseUser>
    
    // Apple ile giriş
    suspend fun signInWithApple(
        idToken: String,
        nonce: String
    ): Result<FirebaseUser>
    
    // Mevcut kullanıcı
    fun getCurrentUser(): FirebaseUser?
    
    // Çıkış
    suspend fun signOut()
}
```

### Veritabanı Güncellemesi - User Document

```json
{
  "id": "user_abc123",
  "firebaseUid": "firebase_uid_123",
  
  "auth": {
    "providers": ["phone", "google.com"],
    "primaryProvider": "phone",
    "phoneNumber": "+905321234567",
    "email": "ahmet@example.com",
    "isPhoneVerified": true,
    "isEmailVerified": false,
    "phoneVerifiedAt": "2026-01-10T14:30:00Z",
    "lastSignInAt": "2026-01-12T10:00:00Z"
  },
  
  "trustScore": {
    "level": "TRUSTED",
    "score": 85,
    "totalOrders": 12,
    "successfulOrders": 12,
    "cancelledOrders": 0,
    "noShowOrders": 0,
    "totalSpent": 1450.00,
    "memberSince": "2025-06-15T10:00:00Z",
    "lastOrderAt": "2026-01-10T19:30:00Z"
  },
  
  "restrictions": {
    "canUseCashPayment": true,
    "maxCashOrderValue": 500.00,
    "requiresManualApproval": false,
    "isBlocked": false,
    "blockReason": null
  },
  
  "profile": {
    "firstName": "Ahmet",
    "lastName": "Yılmaz",
    "displayName": "Ahmet Y.",
    "photoUrl": null
  }
}
```

### Özet: Önerilen Strateji

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         ÖNERİLEN STRATEJİ                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1️⃣  Menü gezme, sepet oluşturma → AUTH GEREKMİYOR                     │
│                                                                         │
│  2️⃣  Sipariş verme → TELEFON DOĞRULAMA ZORUNLU                         │
│      • SMS OTP en güvenilir yöntem                                      │
│      • Bir kez doğrulanınca tekrar istenmez                            │
│                                                                         │
│  3️⃣  Kampanya kullanma → GİRİŞ ZORUNLU                                 │
│      • Kötüye kullanımı önler                                          │
│      • Kullanım takibi yapılabilir                                     │
│                                                                         │
│  4️⃣  Kasada öde → TELEFON DOĞRULAMA + KADEME SİSTEMİ                   │
│      • Yeni kullanıcılar için limit                                    │
│      • Güven kazandıkça limit artar                                    │
│                                                                         │
│  5️⃣  Kart ile ödeme → TELEFON DOĞRULAMA                                │
│      • Ekstra güvenlik için 3D Secure                                  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🔥 Firebase Yapılandırması

### Kullanılacak Firebase Servisleri

| Servis | Kullanım Amacı |
|--------|----------------|
| **Firestore** | Ana veritabanı (işletmeler, menüler, siparişler) |
| **Authentication** | Kullanıcı kimlik doğrulama |
| **Storage** | Resim ve medya dosyaları |
| **Cloud Functions** | Bildirimler, sipariş işleme |
| **Analytics** | Kullanım istatistikleri |

### Proje Yapısı

```
qarko-firebase/
├── firestore/
│   ├── tenants/              # İşletmeler
│   ├── users/                # Kullanıcılar
│   ├── orders/               # Siparişler
│   └── carts/                # Sepetler
├── storage/
│   ├── tenants/{tenantId}/   # İşletme görselleri
│   └── products/{productId}/ # Ürün görselleri
└── functions/
    ├── notifications/
    └── orderProcessing/
```

---

## 📊 Veritabanı Şeması

### 1. Tenants (İşletmeler) Collection

```
firestore/
└── tenants/
    └── {tenantId}/
        ├── info                    # İşletme temel bilgileri
        ├── menu/                   # Menü kategorileri ve ürünler
        │   └── {categoryId}/
        │       └── items/
        │           └── {itemId}
        ├── campaigns/              # Kampanyalar
        │   └── {campaignId}
        ├── tables/                 # Masa bilgileri
        │   └── {tableId}
        └── settings/               # İşletme ayarları
            └── config
```

#### 1.1 Tenant Info Document

```json
{
  "id": "tenant_123",
  "name": "IF Sokak Lezzetleri",
  "slug": "if-sokak",
  "logo": "https://storage.googleapis.com/qarko/tenants/tenant_123/logo.png",
  "coverImage": "https://storage.googleapis.com/qarko/tenants/tenant_123/cover.png",
  "description": "Sokak lezzetlerinin en iyisi",
  "shortDescription": "Burger, Pizza, Döner",
  
  "contact": {
    "phone": "+90 532 123 4567",
    "email": "info@ifsokak.com",
    "website": "https://ifsokak.com"
  },
  
  "address": {
    "street": "Atatürk Caddesi No: 123",
    "district": "Kadıköy",
    "city": "İstanbul",
    "country": "Türkiye",
    "postalCode": "34710",
    "coordinates": {
      "latitude": 40.9876,
      "longitude": 29.1234
    }
  },
  
  "workingHours": {
    "monday": { "open": "10:00", "close": "23:00", "isOpen": true },
    "tuesday": { "open": "10:00", "close": "23:00", "isOpen": true },
    "wednesday": { "open": "10:00", "close": "23:00", "isOpen": true },
    "thursday": { "open": "10:00", "close": "23:00", "isOpen": true },
    "friday": { "open": "10:00", "close": "00:00", "isOpen": true },
    "saturday": { "open": "11:00", "close": "00:00", "isOpen": true },
    "sunday": { "open": "11:00", "close": "22:00", "isOpen": true }
  },
  
  "features": {
    "hasAlcohol": true,
    "hasHookah": true,
    "hasPizza": true,
    "hasBurger": true,
    "hasKebab": true,
    "hasSeafood": false,
    "hasBreakfast": true,
    "hasDessert": true,
    "hasVeganOptions": true,
    "hasGlutenFreeOptions": false,
    "acceptsCreditCard": true,
    "acceptsCash": true,
    "hasWifi": true,
    "hasParking": false,
    "hasOutdoorSeating": true,
    "hasLiveMusic": false,
    "petsAllowed": false,
    "wheelchairAccessible": true
  },
  
  "serviceTypes": ["DINE_IN", "TAKEAWAY", "DELIVERY"],
  
  "priceRange": "₺₺",  // ₺, ₺₺, ₺₺₺, ₺₺₺₺
  
  "rating": {
    "average": 4.5,
    "count": 1250
  },
  
  "theme": {
    "primaryColor": "#51C4D3",
    "secondaryColor": "#126E82",
    "accentColor": "#FB7433"
  },
  
  "socialMedia": {
    "instagram": "ifsokak",
    "facebook": "ifsokak",
    "twitter": "ifsokak",
    "tiktok": "ifsokak"
  },
  
  "status": "ACTIVE",  // ACTIVE, INACTIVE, SUSPENDED, PENDING
  "isOpen": true,
  "isBusy": false,
  "estimatedWaitTime": 15,  // dakika
  
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2024-06-20T15:30:00Z"
}
```

#### 1.2 Menu Category Document

```json
{
  "id": "cat_burgers",
  "tenantId": "tenant_123",
  "name": "Burgerler",
  "description": "El yapımı özel burgerlerimiz",
  "emoji": "🍔",
  "imageUrl": "https://storage.googleapis.com/qarko/categories/burgers.png",
  "sortOrder": 1,
  "isActive": true,
  "itemCount": 12,
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2024-06-20T15:30:00Z"
}
```

#### 1.3 Menu Item Document

```json
{
  "id": "item_classic_burger",
  "tenantId": "tenant_123",
  "categoryId": "cat_burgers",
  "name": "Classic Burger",
  "description": "180gr dana eti, cheddar, marul, domates, turşu, özel sos",
  "imageUrl": "https://storage.googleapis.com/qarko/products/classic_burger.png",
  "price": 150.00,
  "discountedPrice": null,
  "discountPercent": 0,
  
  "foodType": "BURGER",
  
  "ingredients": [
    { "id": "ing_1", "name": "Dana Eti", "emoji": "🥩", "isMain": true },
    { "id": "ing_2", "name": "Cheddar", "emoji": "🧀", "isMain": true },
    { "id": "ing_3", "name": "Marul", "emoji": "🥬", "isMain": false },
    { "id": "ing_4", "name": "Domates", "emoji": "🍅", "isMain": false },
    { "id": "ing_5", "name": "Turşu", "emoji": "🥒", "isMain": false }
  ],
  
  "allergens": ["GLUTEN", "DAIRY", "SESAME"],
  
  "nutritionInfo": {
    "calories": 650,
    "protein": 35,
    "carbs": 45,
    "fat": 38,
    "fiber": 3
  },
  
  "customizationGroups": [
    {
      "id": "cg_cooking",
      "name": "Pişirme Derecesi",
      "type": "SINGLE_SELECT",
      "isRequired": true,
      "options": [
        { "id": "opt_rare", "name": "Az Pişmiş", "price": 0, "isDefault": false },
        { "id": "opt_medium", "name": "Orta", "price": 0, "isDefault": true },
        { "id": "opt_well", "name": "Çok Pişmiş", "price": 0, "isDefault": false }
      ]
    },
    {
      "id": "cg_extras",
      "name": "Ekstralar",
      "type": "MULTI_SELECT",
      "isRequired": false,
      "maxSelections": 5,
      "options": [
        { "id": "opt_bacon", "name": "Bacon", "price": 25, "isDefault": false },
        { "id": "opt_egg", "name": "Yumurta", "price": 15, "isDefault": false },
        { "id": "opt_jalapeno", "name": "Jalapeno", "price": 10, "isDefault": false },
        { "id": "opt_extra_cheese", "name": "Ekstra Peynir", "price": 20, "isDefault": false }
      ]
    },
    {
      "id": "cg_sauce",
      "name": "Sos Seçimi",
      "type": "SINGLE_SELECT",
      "isRequired": false,
      "options": [
        { "id": "opt_house", "name": "Özel Sos", "price": 0, "isDefault": true },
        { "id": "opt_bbq", "name": "BBQ Sos", "price": 0, "isDefault": false },
        { "id": "opt_mayo", "name": "Mayonez", "price": 0, "isDefault": false },
        { "id": "opt_no_sauce", "name": "Sossuz", "price": 0, "isDefault": false }
      ]
    }
  ],
  
  "removableItems": [
    { "id": "rem_onion", "name": "Soğan", "emoji": "🧅" },
    { "id": "rem_pickle", "name": "Turşu", "emoji": "🥒" },
    { "id": "rem_tomato", "name": "Domates", "emoji": "🍅" }
  ],
  
  "tags": ["POPULAR", "CHEF_RECOMMENDED"],
  "badges": ["NEW", "BESTSELLER"],
  
  "prepTime": "15-20",  // dakika
  "servingSize": "1 porsiyon",
  
  "rating": {
    "average": 4.7,
    "count": 234
  },
  
  "stock": {
    "isAvailable": true,
    "quantity": null,  // null = sınırsız
    "lowStockThreshold": 10
  },
  
  "sortOrder": 1,
  "isActive": true,
  "isFeatured": true,
  "isNew": false,
  
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2024-06-20T15:30:00Z"
}
```

#### 1.4 Campaign Document

```json
{
  "id": "camp_summer_2024",
  "tenantId": "tenant_123",
  "name": "Yaz Kampanyası",
  "title": "🌞 Yaz Fırsatları",
  "description": "Tüm içeceklerde %20 indirim",
  "shortDescription": "İçeceklerde %20 indirim",
  "imageUrl": "https://storage.googleapis.com/qarko/campaigns/summer.png",
  "bannerUrl": "https://storage.googleapis.com/qarko/campaigns/summer_banner.png",
  
  "type": "PERCENTAGE_DISCOUNT",  // PERCENTAGE_DISCOUNT, FIXED_DISCOUNT, BUY_X_GET_Y, FREE_ITEM, FREE_DELIVERY
  
  "discountValue": 20,  // yüzde veya sabit tutar
  "maxDiscountAmount": 50,  // maksimum indirim tutarı
  
  "conditions": {
    "minOrderAmount": 100,
    "minItemCount": 1,
    "applicableCategories": ["cat_drinks", "cat_hot_drinks"],
    "applicableItems": [],  // boş = tüm ürünler
    "excludedItems": [],
    "applicableDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
    "applicableHours": { "start": "14:00", "end": "18:00" },
    "maxUsagePerUser": 3,
    "maxTotalUsage": 1000,
    "requiresCode": false,
    "code": null,
    "isFirstOrderOnly": false,
    "isNewUserOnly": false
  },
  
  "validity": {
    "startDate": "2024-06-01T00:00:00Z",
    "endDate": "2024-08-31T23:59:59Z",
    "isActive": true
  },
  
  "usage": {
    "totalUsed": 245,
    "totalDiscountGiven": 4500.00
  },
  
  "priority": 1,  // düşük sayı = yüksek öncelik
  "isStackable": false,  // diğer kampanyalarla birleştirilebilir mi
  "isAutoApply": true,  // otomatik uygulanır mı
  
  "createdAt": "2024-05-25T10:00:00Z",
  "updatedAt": "2024-06-01T00:00:00Z"
}
```

#### 1.5 Table Document

```json
{
  "id": "3",
  "tenantId": "if-sokak",
  "tableNumber": 3,
  "name": "Masa 3",
  "section": "İç Mekan",
  "floor": 1,
  "capacity": 4,
  
  "qrCode": {
    "url": "https://qarko.app/q/if-sokak/3",
    "imageUrl": "https://storage.googleapis.com/qarko/qrcodes/if-sokak_3.png",
    "generatedAt": "2024-01-15T10:00:00Z"
  },
  
  "status": "AVAILABLE",
  "currentSession": {
    "sessionId": null,
    "startedAt": null,
    "customerCount": null,
    "activeOrderIds": []
  },
  
  "location": {
    "description": "Pencere kenarı",
    "isOutdoor": false,
    "isPrivate": false,
    "isAccessible": true,
    "hasOutlet": true,
    "nearWindow": true,
    "nearKitchen": false
  },
  
  "settings": {
    "minSpend": null,
    "reservationAllowed": true,
    "maxReservationDuration": 120
  },
  
  "stats": {
    "totalOrders": 156,
    "totalRevenue": 12450.00,
    "averageOrderValue": 79.80,
    "averageSessionDuration": 45
  },
  
  "isActive": true,
  "sortOrder": 3,
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2024-06-20T15:30:00Z"
}
```

#### Masa Durumları (TableStatus)

| Durum | Açıklama | Renk |
|-------|----------|------|
| `AVAILABLE` | Müsait, sipariş bekliyor | 🟢 Yeşil |
| `OCCUPIED` | Dolu, müşteri var | 🟡 Sarı |
| `ORDERING` | Sipariş veriliyor | 🔵 Mavi |
| `WAITING_FOOD` | Yemek bekleniyor | 🟠 Turuncu |
| `SERVED` | Servis yapıldı | 🟣 Mor |
| `PAYMENT_PENDING` | Ödeme bekleniyor | 🔴 Kırmızı |
| `RESERVED` | Rezerve edilmiş | ⚪ Gri |
| `CLEANING` | Temizleniyor | ⚫ Siyah |

#### Masa Bölümleri (Section) Örnekleri

```json
{
  "sections": [
    { "id": "ic-mekan", "name": "İç Mekan", "emoji": "🏠", "sortOrder": 1 },
    { "id": "bahce", "name": "Bahçe", "emoji": "🌳", "sortOrder": 2 },
    { "id": "teras", "name": "Teras", "emoji": "☀️", "sortOrder": 3 },
    { "id": "vip", "name": "VIP", "emoji": "⭐", "sortOrder": 4 },
    { "id": "bar", "name": "Bar", "emoji": "🍸", "sortOrder": 5 }
  ]
}
```

### 2. Users Collection

```json
{
  "id": "user_abc123",
  "firebaseUid": "firebase_uid_123",
  
  "profile": {
    "firstName": "Ahmet",
    "lastName": "Yılmaz",
    "displayName": "Ahmet Y.",
    "email": "ahmet@example.com",
    "phone": "+90 532 123 4567",
    "photoUrl": "https://storage.googleapis.com/qarko/users/user_abc123.png",
    "dateOfBirth": "1990-05-15",
    "gender": "MALE"
  },
  
  "preferences": {
    "language": "tr",
    "currency": "TRY",
    "theme": "LIGHT",
    "notificationsEnabled": true,
    "emailNotifications": true,
    "smsNotifications": false
  },
  
  "dietaryPreferences": {
    "isVegetarian": false,
    "isVegan": false,
    "isGlutenFree": false,
    "allergens": ["NUTS"],
    "dislikes": ["Soğan", "Kişniş"]
  },
  
  "savedAddresses": [
    {
      "id": "addr_1",
      "title": "Ev",
      "address": "Atatürk Mah. 123 Sok. No: 45 D: 3",
      "district": "Kadıköy",
      "city": "İstanbul",
      "coordinates": { "latitude": 40.9876, "longitude": 29.1234 },
      "isDefault": true
    }
  ],
  
  "savedPaymentMethods": [
    {
      "id": "pm_1",
      "type": "CREDIT_CARD",
      "cardBrand": "MASTERCARD",
      "lastFourDigits": "4567",
      "cardHolderName": "AHMET YILMAZ",
      "expiryMonth": 12,
      "expiryYear": 2026,
      "isDefault": true
    }
  ],
  
  "stats": {
    "totalOrders": 45,
    "totalSpent": 3450.00,
    "averageOrderValue": 76.67,
    "favoriteCategory": "Burgerler",
    "favoriteTenant": "tenant_123"
  },
  
  "membership": {
    "tier": "GOLD",  // BRONZE, SILVER, GOLD, PLATINUM
    "points": 2500,
    "lifetimePoints": 5000,
    "tierExpiresAt": "2025-12-31T23:59:59Z"
  },
  
  "createdAt": "2023-06-15T10:00:00Z",
  "updatedAt": "2024-06-20T15:30:00Z",
  "lastActiveAt": "2024-06-20T15:30:00Z"
}
```

### 3. Orders Collection

```json
{
  "id": "order_xyz789",
  "orderNumber": "ORD-2026-001234",
  "tenantId": "if-sokak",
  "tenantName": "IF Sokak Lezzetleri",
  "userId": "user_abc123",
  
  "table": {
    "id": "3",
    "name": "Masa 3",
    "section": "İç Mekan",
    "floor": 1
  },
  
  "sessionId": "session_456",
  
  "type": "DINE_IN",  // DINE_IN, TAKEAWAY, DELIVERY
  
  "items": [
    {
      "id": "orderitem_1",
      "menuItemId": "item_classic_burger",
      "name": "Classic Burger",
      "imageUrl": "https://...",
      "quantity": 2,
      "unitPrice": 150.00,
      "totalPrice": 350.00,
      "customizations": {
        "selectedOptions": {
          "cg_cooking": "opt_medium",
          "cg_sauce": "opt_bbq"
        },
        "extras": ["opt_bacon", "opt_extra_cheese"],
        "removedItems": ["rem_onion"],
        "specialNote": "Ekstra sos lütfen"
      },
      "addedExtrasPrice": 45.00
    },
    {
      "id": "orderitem_2",
      "menuItemId": "item_cola",
      "name": "Cola",
      "imageUrl": "https://...",
      "quantity": 2,
      "unitPrice": 20.00,
      "totalPrice": 40.00,
      "customizations": {
        "selectedOptions": {
          "cg_size": "opt_medium"
        },
        "extras": [],
        "removedItems": [],
        "specialNote": null
      },
      "addedExtrasPrice": 0
    }
  ],
  
  "pricing": {
    "subtotal": 390.00,
    "extrasTotal": 45.00,
    "deliveryFee": 0,
    "serviceFee": 0,
    "discount": {
      "campaignId": "camp_summer_2024",
      "campaignName": "Yaz Kampanyası",
      "discountAmount": 8.00,
      "discountType": "PERCENTAGE",
      "discountValue": 20
    },
    "tip": 20.00,
    "tax": 35.10,
    "total": 447.10
  },
  
  "payment": {
    "method": "CREDIT_CARD",  // CREDIT_CARD, CASH, WALLET
    "status": "PAID",  // PENDING, PAID, FAILED, REFUNDED
    "transactionId": "txn_123456",
    "paidAt": "2024-06-20T15:35:00Z",
    "cardLastFour": "4567",
    "cardBrand": "MASTERCARD"
  },
  
  "status": "COMPLETED",  // PENDING, CONFIRMED, PREPARING, READY, SERVED, COMPLETED, CANCELLED
  "statusHistory": [
    { "status": "PENDING", "timestamp": "2024-06-20T15:30:00Z" },
    { "status": "CONFIRMED", "timestamp": "2024-06-20T15:31:00Z" },
    { "status": "PREPARING", "timestamp": "2024-06-20T15:32:00Z" },
    { "status": "READY", "timestamp": "2024-06-20T15:45:00Z" },
    { "status": "SERVED", "timestamp": "2024-06-20T15:47:00Z" },
    { "status": "COMPLETED", "timestamp": "2024-06-20T16:30:00Z" }
  ],
  
  "timing": {
    "placedAt": "2024-06-20T15:30:00Z",
    "confirmedAt": "2024-06-20T15:31:00Z",
    "prepStartedAt": "2024-06-20T15:32:00Z",
    "readyAt": "2024-06-20T15:45:00Z",
    "servedAt": "2024-06-20T15:47:00Z",
    "completedAt": "2024-06-20T16:30:00Z",
    "estimatedPrepTime": 15,
    "actualPrepTime": 13
  },
  
  "notes": {
    "customerNote": "Hızlı olursa sevinirim",
    "kitchenNote": null,
    "deliveryNote": null
  },
  
  "rating": {
    "overall": 5,
    "food": 5,
    "service": 5,
    "comment": "Harika burger!",
    "ratedAt": "2024-06-20T16:35:00Z"
  },
  
  "createdAt": "2024-06-20T15:30:00Z",
  "updatedAt": "2024-06-20T16:35:00Z"
}
```

### 4. Carts Collection (Tamamlanmamış Sepetler)

```json
{
  "id": "cart_abc123",
  "tenantId": "if-sokak",
  "tenantName": "IF Sokak Lezzetleri",
  "userId": "user_abc123",
  
  "table": {
    "id": "3",
    "name": "Masa 3",
    "section": "İç Mekan"
  },
  
  "sessionId": "session_456",
  
  "items": [
    {
      "id": "cartitem_1",
      "menuItemId": "item_classic_burger",
      "name": "Classic Burger",
      "imageUrl": "https://...",
      "quantity": 1,
      "unitPrice": 150.00,
      "totalPrice": 175.00,
      "customizations": {
        "selectedOptions": {
          "cg_cooking": "opt_medium"
        },
        "extras": ["opt_bacon"],
        "removedItems": [],
        "specialNote": null
      },
      "addedExtrasPrice": 25.00,
      "addedAt": "2024-06-20T15:25:00Z"
    }
  ],
  
  "pricing": {
    "subtotal": 175.00,
    "extrasTotal": 25.00,
    "estimatedTotal": 175.00
  },
  
  "appliedCampaign": null,
  "promoCode": null,
  
  "isAbandoned": false,
  "abandonedAt": null,
  "reminderSentAt": null,
  
  "expiresAt": "2024-06-20T17:25:00Z",  // 2 saat sonra expire
  
  "createdAt": "2024-06-20T15:25:00Z",
  "updatedAt": "2024-06-20T15:25:00Z"
}
```

### 5. Favorites Collection

```json
{
  "id": "fav_user123_tenant456",
  "userId": "user_abc123",
  "tenantId": "tenant_123",
  
  "favoriteItems": [
    {
      "itemId": "item_classic_burger",
      "addedAt": "2024-05-15T10:00:00Z"
    },
    {
      "itemId": "item_margherita",
      "addedAt": "2024-06-01T14:30:00Z"
    }
  ],
  
  "isTenantFavorite": true,  // işletme favori mi
  "tenantFavoriteAddedAt": "2024-04-20T09:00:00Z",
  
  "updatedAt": "2024-06-01T14:30:00Z"
}
```

---

## 📱 QR Kod Sistemi

### 🎯 Temel Mantık

Her masada bir QR kod bulunur. Müşteri QR kodu okuttuğunda:
1. **İşletme ID** ve **Masa ID** birlikte alınır
2. Sipariş verildiğinde "Masa 3'ten sipariş geldi" şeklinde işletmeye bildirim gider
3. İşletme hangi masadan hangi sipariş geldiğini görebilir

### QR Kod Formatı

```
https://qarko.app/q/{tenantId}/{tableId}
```

#### Örnekler:
```
https://qarko.app/q/if-sokak/3         → IF Sokak - Masa 3
https://qarko.app/q/if-sokak/12        → IF Sokak - Masa 12
https://qarko.app/q/pizza-house/VIP-1  → Pizza House - VIP Masa 1
https://qarko.app/q/cafe-nero/bahce-5  → Cafe Nero - Bahçe Masa 5
```

### QR Kod Akış Diyagramı

```
┌──────────────────────────────────────────────────────────────────────┐
│                        MÜŞTERİ TARAFI                                 │
├──────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌─────────────┐    ┌──────────────┐    ┌─────────────────────────┐  │
│  │  QR Kod     │───▶│  Parse Et    │───▶│  TenantManager.load()   │  │
│  │  Okut      │    │  tenantId +  │    │  - İşletme bilgisi      │  │
│  │             │    │  tableId     │    │  - Menü                 │  │
│  └─────────────┘    └──────────────┘    │  - Masa bilgisi         │  │
│                                          └─────────────────────────┘  │
│                                                     │                 │
│                                                     ▼                 │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │                      MENÜ EKRANI                                 │ │
│  │  ┌─────────────────────────────────────────────────────────┐    │ │
│  │  │  🏪 IF Sokak Lezzetleri          📍 Masa 3              │    │ │
│  │  └─────────────────────────────────────────────────────────┘    │ │
│  │                                                                  │ │
│  │  [Burgerler] [Pizzalar] [İçecekler] [Tatlılar]                  │ │
│  │                                                                  │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                     │                 │
│                                                     ▼                 │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │                    SİPARİŞ VER                                   │ │
│  │                                                                  │ │
│  │  Order {                                                         │ │
│  │    tenantId: "if-sokak",                                        │ │
│  │    tableId: "3",              ◄── Masa bilgisi otomatik eklenir │ │
│  │    tableName: "Masa 3",                                         │ │
│  │    items: [...],                                                │ │
│  │    ...                                                          │ │
│  │  }                                                              │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                     │                 │
└─────────────────────────────────────────────────────│─────────────────┘
                                                      │
                                                      ▼
┌──────────────────────────────────────────────────────────────────────┐
│                        İŞLETME TARAFI                                 │
├──────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │                    SİPARİŞ PANELİ                                │ │
│  │                                                                  │ │
│  │  🔔 Yeni Sipariş!                                               │ │
│  │  ┌─────────────────────────────────────────────────────────┐   │ │
│  │  │  📍 MASA 3                              15:45           │   │ │
│  │  │  ─────────────────────────────────────────────────────  │   │ │
│  │  │  2x Classic Burger          ₺300                        │   │ │
│  │  │  1x Limonata                ₺35                         │   │ │
│  │  │  ─────────────────────────────────────────────────────  │   │ │
│  │  │  TOPLAM: ₺335                                           │   │ │
│  │  │                                                          │   │ │
│  │  │  [✓ Onayla]  [✗ Reddet]                                 │   │ │
│  │  └─────────────────────────────────────────────────────────┘   │ │
│  │                                                                  │ │
│  │  Aktif Siparişler:                                              │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │ │
│  │  │ Masa 1   │ │ Masa 3   │ │ Masa 7   │ │ Masa 12  │           │ │
│  │  │ Hazır ✓  │ │ Hazır.   │ │ Bekliyor │ │ Hazır ✓  │           │ │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                       │
└──────────────────────────────────────────────────────────────────────┘
```

### QR Kod Parse Logic

```kotlin
// domain/model/QrCodeData.kt
data class QrCodeData(
    val tenantId: String,
    val tableId: String,
    val tableName: String? = null  // Firebase'den çekilecek
)

// util/QrCodeParser.kt
object QrCodeParser {
    
    private val QR_PATTERN = Regex("""https://qarko\.app/q/([^/]+)/([^/]+)""")
    
    /**
     * QR kod URL'ini parse eder
     * @param url QR koddan okunan URL
     * @return QrCodeData veya null (geçersiz format)
     * 
     * Örnek: https://qarko.app/q/if-sokak/3
     *        tenantId = "if-sokak"
     *        tableId = "3"
     */
    fun parse(url: String): QrCodeData? {
        val match = QR_PATTERN.find(url) ?: return null
        
        val (tenantId, tableId) = match.destructured
        
        return QrCodeData(
            tenantId = tenantId,
            tableId = tableId
        )
    }
    
    /**
     * QR kod URL'i oluşturur (Admin panel için)
     */
    fun generateQrUrl(tenantId: String, tableId: String): String {
        return "https://qarko.app/q/$tenantId/$tableId"
    }
}
```

### QR Okuma Sonrası Akış

```kotlin
// presentation/welcome/QRScanScreen.kt
@Composable
fun QRScanScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    fun onQrCodeScanned(qrCodeUrl: String) {
        scope.launch {
            isLoading = true
            error = null
            
            // 1. QR kodu parse et
            val qrData = QrCodeParser.parse(qrCodeUrl)
            
            if (qrData == null) {
                error = "Geçersiz QR kod"
                isLoading = false
                return@launch
            }
            
            try {
                // 2. İşletme ve masa bilgilerini yükle
                TenantManager.loadTenant(
                    tenantId = qrData.tenantId,
                    tableId = qrData.tableId
                )
                
                // 3. Kullanıcının bu işletmedeki sepetini yükle
                UserManager.currentUser?.id?.let { userId ->
                    CartManager.loadCart(userId, qrData.tenantId)
                }
                
                // 4. Menü ekranına git
                navController.navigate(TenantMenu) {
                    popUpTo(QRScan) { inclusive = true }
                }
                
            } catch (e: Exception) {
                error = when (e) {
                    is TenantNotFoundException -> "İşletme bulunamadı"
                    is TableNotFoundException -> "Masa bulunamadı"
                    else -> "Bir hata oluştu: ${e.message}"
                }
            } finally {
                isLoading = false
            }
        }
    }
    
    // UI components...
}
```

### Sipariş Oluşturma (Masa Bilgisi ile)

```kotlin
// domain/usecase/PlaceOrderUseCase.kt
class PlaceOrderUseCase(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(
        cart: Cart,
        paymentMethod: PaymentMethod,
        tip: Double = 0.0,
        customerNote: String? = null
    ): Result<Order> {
        
        // Mevcut tenant ve masa bilgisini al
        val tenant = TenantManager.currentTenant.value
            ?: return Result.failure(Exception("İşletme bilgisi bulunamadı"))
        
        val table = TenantManager.currentTable.value
            ?: return Result.failure(Exception("Masa bilgisi bulunamadı"))
        
        val user = UserManager.currentUser
        
        // Sipariş oluştur
        val order = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = generateOrderNumber(),
            tenantId = tenant.id,
            tenantName = tenant.name,
            userId = user?.id,
            
            // 🎯 MASA BİLGİSİ - QR koddan alındı
            tableId = table.id,
            tableName = table.name,        // "Masa 3"
            tableSection = table.section,  // "İç Mekan" veya "Bahçe"
            
            type = OrderType.DINE_IN,
            items = cart.items.map { it.toOrderItem() },
            pricing = calculatePricing(cart, tip),
            payment = PaymentInfo(
                method = paymentMethod,
                status = PaymentStatus.PENDING
            ),
            status = OrderStatus.PENDING,
            notes = OrderNotes(customerNote = customerNote),
            createdAt = Clock.System.now()
        )
        
        // Firebase'e kaydet
        return orderRepository.placeOrder(order)
    }
}
```

### Firebase'e Kaydedilen Sipariş Örneği

```json
{
  "id": "order_abc123",
  "orderNumber": "ORD-2026-001234",
  "tenantId": "if-sokak",
  "tenantName": "IF Sokak Lezzetleri",
  "userId": "user_xyz789",
  
  "tableId": "3",
  "tableName": "Masa 3",
  "tableSection": "İç Mekan",
  
  "type": "DINE_IN",
  
  "items": [
    {
      "id": "item_1",
      "menuItemId": "classic_burger",
      "name": "Classic Burger",
      "quantity": 2,
      "unitPrice": 150.00,
      "totalPrice": 300.00
    },
    {
      "id": "item_2",
      "menuItemId": "limonata",
      "name": "Limonata",
      "quantity": 1,
      "unitPrice": 35.00,
      "totalPrice": 35.00
    }
  ],
  
  "pricing": {
    "subtotal": 335.00,
    "discount": 0,
    "tip": 0,
    "total": 335.00
  },
  
  "status": "PENDING",
  "statusHistory": [
    {
      "status": "PENDING",
      "timestamp": "2026-01-12T15:45:00Z",
      "note": "Masa 3'ten yeni sipariş"
    }
  ],
  
  "createdAt": "2026-01-12T15:45:00Z"
}
```

### Masa Durumu Takibi

```kotlin
// data/manager/TenantManager.kt
object TenantManager {
    // ... existing code ...
    
    private val _currentTable = MutableStateFlow<Table?>(null)
    val currentTable: StateFlow<Table?> = _currentTable.asStateFlow()
    
    // Masa bilgisini içeren açıklama
    val tableDisplayName: String
        get() = _currentTable.value?.let { table ->
            if (table.section != null) {
                "${table.section} - ${table.name}"  // "Bahçe - Masa 5"
            } else {
                table.name  // "Masa 3"
            }
        } ?: "Masa seçilmedi"
    
    suspend fun loadTenant(tenantId: String, tableId: String) {
        isLoading.value = true
        error.value = null
        
        try {
            // 1. İşletme bilgilerini yükle
            val tenant = tenantRepository.getTenantById(tenantId).getOrThrow()
            _currentTenant.value = tenant
            
            // 2. Masa bilgisini yükle
            val table = tenantRepository.getTable(tenantId, tableId).getOrThrow()
            _currentTable.value = table
            
            // 3. Menüyü yükle
            val menu = tenantRepository.getTenantMenu(tenantId).getOrThrow()
            _menu.value = menu
            
            // 4. Kampanyaları yükle
            val campaigns = tenantRepository.getTenantCampaigns(tenantId).getOrThrow()
            _campaigns.value = campaigns
            
            // 5. Masa durumunu güncelle (occupied olarak işaretle)
            tenantRepository.updateTableStatus(tenantId, tableId, TableStatus.OCCUPIED)
            
        } catch (e: Exception) {
            error.value = e.message
            throw e
        } finally {
            isLoading.value = false
        }
    }
}
```

### UI'da Masa Gösterimi

```kotlin
// presentation/tenant/TenantMenuScreen.kt
@Composable
fun TenantMenuScreen(navController: NavController) {
    val tenant by TenantManager.currentTenant.collectAsState()
    val table by TenantManager.currentTable.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = tenant?.name ?: "",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // 🎯 Masa bilgisi gösterimi
                        table?.let {
                            Text(
                                text = "📍 ${it.name}",
                                fontSize = 12.sp,
                                color = gray
                            )
                        }
                    }
                }
            )
        }
    ) {
        // Menu content...
    }
}

// presentation/cart/CartScreen.kt - Sepette masa bilgisi
@Composable
fun CartScreen(navController: NavController) {
    val table by TenantManager.currentTable.collectAsState()
    
    // ... existing code ...
    
    // Sepet header'ında masa gösterimi
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceGray)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Sepetim",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        // Masa chip'i
        table?.let {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = primary.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📍", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = primary
                    )
                }
            }
        }
    }
}
```

---

## 🏗️ Uygulama Mimarisi

### Katmanlı Mimari

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Screens   │  │  ViewModels │  │  State Management   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                      DOMAIN LAYER                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Use Cases  │  │   Models    │  │    Repositories     │  │
│  │             │  │             │  │    (Interfaces)     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                       DATA LAYER                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Firebase   │  │   Local     │  │    Repository       │  │
│  │  Services   │  │   Cache     │  │  Implementations    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Paket Yapısı

```
com.bekircaglar.qarko/
├── data/
│   ├── firebase/
│   │   ├── FirebaseService.kt
│   │   ├── TenantFirebaseService.kt
│   │   ├── MenuFirebaseService.kt
│   │   ├── OrderFirebaseService.kt
│   │   ├── CartFirebaseService.kt
│   │   └── UserFirebaseService.kt
│   ├── repository/
│   │   ├── TenantRepositoryImpl.kt
│   │   ├── MenuRepositoryImpl.kt
│   │   ├── OrderRepositoryImpl.kt
│   │   ├── CartRepositoryImpl.kt
│   │   └── UserRepositoryImpl.kt
│   ├── model/
│   │   ├── firebase/           # Firebase DTO'ları
│   │   │   ├── TenantDto.kt
│   │   │   ├── MenuItemDto.kt
│   │   │   └── ...
│   │   └── mapper/             # DTO -> Domain Mapper
│   │       ├── TenantMapper.kt
│   │       └── ...
│   └── local/
│       ├── preferences/
│       │   └── AppPreferences.kt
│       └── cache/
│           └── TenantCache.kt
│
├── domain/
│   ├── model/
│   │   ├── Tenant.kt
│   │   ├── MenuItem.kt
│   │   ├── Order.kt
│   │   ├── Cart.kt
│   │   ├── Campaign.kt
│   │   └── User.kt
│   ├── repository/
│   │   ├── TenantRepository.kt
│   │   ├── MenuRepository.kt
│   │   ├── OrderRepository.kt
│   │   ├── CartRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── tenant/
│       │   ├── GetTenantByIdUseCase.kt
│       │   ├── GetTenantMenuUseCase.kt
│       │   └── ...
│       ├── order/
│       │   ├── PlaceOrderUseCase.kt
│       │   ├── GetOrderHistoryUseCase.kt
│       │   └── ...
│       └── cart/
│           ├── AddToCartUseCase.kt
│           ├── SyncCartUseCase.kt
│           └── ...
│
├── presentation/
│   ├── tenant/
│   │   ├── TenantMenuScreen.kt
│   │   └── TenantMenuViewModel.kt
│   ├── cart/
│   │   ├── CartScreen.kt
│   │   └── CartViewModel.kt
│   ├── order/
│   │   ├── OrdersScreen.kt
│   │   └── OrdersViewModel.kt
│   └── ...
│
└── di/
    ├── AppModule.kt
    ├── FirebaseModule.kt
    ├── RepositoryModule.kt
    └── UseCaseModule.kt
```

---

## 📦 Veri Modelleri (Kotlin)

### Tenant Model

```kotlin
// domain/model/Tenant.kt
data class Tenant(
    val id: String,
    val name: String,
    val slug: String,
    val logo: String,
    val coverImage: String?,
    val description: String,
    val shortDescription: String?,
    val contact: TenantContact,
    val address: TenantAddress,
    val workingHours: Map<DayOfWeek, WorkingHour>,
    val features: TenantFeatures,
    val serviceTypes: List<ServiceType>,
    val priceRange: PriceRange,
    val rating: Rating,
    val theme: TenantTheme?,
    val socialMedia: SocialMedia?,
    val status: TenantStatus,
    val isOpen: Boolean,
    val isBusy: Boolean,
    val estimatedWaitTime: Int?
)

data class TenantContact(
    val phone: String?,
    val email: String?,
    val website: String?
)

data class TenantAddress(
    val street: String,
    val district: String,
    val city: String,
    val country: String,
    val postalCode: String?,
    val coordinates: Coordinates?
)

data class WorkingHour(
    val open: String,
    val close: String,
    val isOpen: Boolean
)

data class TenantFeatures(
    val hasAlcohol: Boolean,
    val hasHookah: Boolean,
    val hasPizza: Boolean,
    val hasBurger: Boolean,
    val hasKebab: Boolean,
    val hasSeafood: Boolean,
    val hasBreakfast: Boolean,
    val hasDessert: Boolean,
    val hasVeganOptions: Boolean,
    val hasGlutenFreeOptions: Boolean,
    val acceptsCreditCard: Boolean,
    val acceptsCash: Boolean,
    val hasWifi: Boolean,
    val hasParking: Boolean,
    val hasOutdoorSeating: Boolean,
    val hasLiveMusic: Boolean,
    val petsAllowed: Boolean,
    val wheelchairAccessible: Boolean
)

enum class ServiceType {
    DINE_IN, TAKEAWAY, DELIVERY
}

enum class PriceRange {
    BUDGET,      // ₺
    MODERATE,    // ₺₺
    EXPENSIVE,   // ₺₺₺
    LUXURY       // ₺₺₺₺
}

enum class TenantStatus {
    ACTIVE, INACTIVE, SUSPENDED, PENDING
}
```

### Menu Models

```kotlin
// domain/model/MenuCategory.kt
data class MenuCategory(
    val id: String,
    val tenantId: String,
    val name: String,
    val description: String?,
    val emoji: String?,
    val imageUrl: String?,
    val sortOrder: Int,
    val isActive: Boolean,
    val itemCount: Int
)

// domain/model/MenuItem.kt
data class MenuItem(
    val id: String,
    val tenantId: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val discountedPrice: Double?,
    val discountPercent: Int,
    val foodType: FoodType,
    val ingredients: List<Ingredient>,
    val allergens: List<Allergen>,
    val nutritionInfo: NutritionInfo?,
    val customizationGroups: List<CustomizationGroup>,
    val removableItems: List<RemovableItem>,
    val tags: List<String>,
    val badges: List<String>,
    val prepTime: String,
    val servingSize: String?,
    val rating: Rating,
    val stock: StockInfo,
    val sortOrder: Int,
    val isActive: Boolean,
    val isFeatured: Boolean,
    val isNew: Boolean
)

data class StockInfo(
    val isAvailable: Boolean,
    val quantity: Int?,
    val lowStockThreshold: Int?
)
```

### Order & Cart Models

```kotlin
// domain/model/Order.kt
data class Order(
    val id: String,
    val orderNumber: String,
    val tenantId: String,
    val userId: String,
    val tableId: String?,
    val type: OrderType,
    val items: List<OrderItem>,
    val pricing: OrderPricing,
    val payment: PaymentInfo,
    val status: OrderStatus,
    val statusHistory: List<StatusHistoryItem>,
    val timing: OrderTiming,
    val notes: OrderNotes,
    val rating: OrderRating?,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class OrderType {
    DINE_IN, TAKEAWAY, DELIVERY
}

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    SERVED,
    COMPLETED,
    CANCELLED
}

// domain/model/Cart.kt
data class Cart(
    val id: String,
    val tenantId: String,
    val userId: String?,
    val tableId: String?,
    val items: List<CartItem>,
    val pricing: CartPricing,
    val appliedCampaign: Campaign?,
    val promoCode: String?,
    val isAbandoned: Boolean,
    val expiresAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

---

## 🔌 Repository Katmanı

### Tenant Repository

```kotlin
// domain/repository/TenantRepository.kt
interface TenantRepository {
    suspend fun getTenantById(tenantId: String): Result<Tenant>
    suspend fun getTenantBySlug(slug: String): Result<Tenant>
    suspend fun getTenantMenu(tenantId: String): Result<List<MenuCategory>>
    suspend fun getMenuItems(tenantId: String, categoryId: String): Result<List<MenuItem>>
    suspend fun getMenuItem(tenantId: String, itemId: String): Result<MenuItem>
    suspend fun getTenantCampaigns(tenantId: String): Result<List<Campaign>>
    suspend fun getTenantTables(tenantId: String): Result<List<Table>>
    fun observeTenantStatus(tenantId: String): Flow<TenantStatus>
}

// data/repository/TenantRepositoryImpl.kt
class TenantRepositoryImpl(
    private val firebaseService: TenantFirebaseService,
    private val cache: TenantCache
) : TenantRepository {
    
    override suspend fun getTenantById(tenantId: String): Result<Tenant> {
        // Önce cache'e bak
        cache.getTenant(tenantId)?.let { return Result.success(it) }
        
        // Firebase'den çek
        return firebaseService.getTenant(tenantId)
            .map { dto -> dto.toDomain() }
            .onSuccess { tenant -> cache.saveTenant(tenant) }
    }
    
    // ... diğer metodlar
}
```

### Order Repository

```kotlin
// domain/repository/OrderRepository.kt
interface OrderRepository {
    suspend fun placeOrder(order: Order): Result<Order>
    suspend fun getOrder(orderId: String): Result<Order>
    suspend fun getUserOrders(userId: String, tenantId: String?): Result<List<Order>>
    suspend fun cancelOrder(orderId: String, reason: String): Result<Unit>
    suspend fun rateOrder(orderId: String, rating: OrderRating): Result<Unit>
    fun observeOrderStatus(orderId: String): Flow<OrderStatus>
}
```

### Cart Repository

```kotlin
// domain/repository/CartRepository.kt
interface CartRepository {
    suspend fun getCart(userId: String, tenantId: String): Result<Cart?>
    suspend fun createCart(cart: Cart): Result<Cart>
    suspend fun updateCart(cart: Cart): Result<Cart>
    suspend fun addItemToCart(cartId: String, item: CartItem): Result<Cart>
    suspend fun removeItemFromCart(cartId: String, itemId: String): Result<Cart>
    suspend fun updateItemQuantity(cartId: String, itemId: String, quantity: Int): Result<Cart>
    suspend fun applyCampaign(cartId: String, campaignId: String): Result<Cart>
    suspend fun clearCart(cartId: String): Result<Unit>
    suspend fun syncLocalCart(localCart: Cart): Result<Cart>
    fun observeCart(userId: String, tenantId: String): Flow<Cart?>
}
```

---

## 🔄 State Management

### TenantManager (Singleton)

```kotlin
// data/manager/TenantManager.kt
object TenantManager {
    private val _currentTenant = MutableStateFlow<Tenant?>(null)
    val currentTenant: StateFlow<Tenant?> = _currentTenant.asStateFlow()
    
    private val _currentTable = MutableStateFlow<Table?>(null)
    val currentTable: StateFlow<Table?> = _currentTable.asStateFlow()
    
    private val _menu = MutableStateFlow<List<MenuCategory>>(emptyList())
    val menu: StateFlow<List<MenuCategory>> = _menu.asStateFlow()
    
    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val campaigns: StateFlow<List<Campaign>> = _campaigns.asStateFlow()
    
    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)
    
    suspend fun loadTenant(tenantId: String, tableId: String? = null) {
        isLoading.value = true
        error.value = null
        
        try {
            // Tenant bilgilerini yükle
            val tenant = tenantRepository.getTenantById(tenantId).getOrThrow()
            _currentTenant.value = tenant
            
            // Menüyü yükle
            val menu = tenantRepository.getTenantMenu(tenantId).getOrThrow()
            _menu.value = menu
            
            // Kampanyaları yükle
            val campaigns = tenantRepository.getTenantCampaigns(tenantId).getOrThrow()
            _campaigns.value = campaigns
            
            // Masa bilgisi varsa yükle
            tableId?.let { id ->
                val table = tenantRepository.getTable(tenantId, id).getOrNull()
                _currentTable.value = table
            }
        } catch (e: Exception) {
            error.value = e.message
        } finally {
            isLoading.value = false
        }
    }
    
    fun clearTenant() {
        _currentTenant.value = null
        _currentTable.value = null
        _menu.value = emptyList()
        _campaigns.value = emptyList()
    }
}
```

### CartManager Güncellemesi

```kotlin
// data/manager/CartManager.kt
object CartManager {
    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart.asStateFlow()
    
    val cartItems: List<CartItem>
        get() = _cart.value?.items ?: emptyList()
    
    val totalPrice: Double
        get() = _cart.value?.pricing?.estimatedTotal ?: 0.0
    
    val itemCount: Int
        get() = cartItems.sumOf { it.quantity }
    
    private var cartRepository: CartRepository? = null
    
    fun initialize(repository: CartRepository) {
        cartRepository = repository
    }
    
    suspend fun loadCart(userId: String, tenantId: String) {
        val existingCart = cartRepository?.getCart(userId, tenantId)?.getOrNull()
        _cart.value = existingCart
    }
    
    suspend fun addToCart(
        menuItem: MenuItem,
        quantity: Int,
        customizations: ItemCustomizations,
        totalPrice: Double
    ) {
        val currentCart = _cart.value
        val tenantId = TenantManager.currentTenant.value?.id ?: return
        
        val cartItem = CartItem(
            id = UUID.randomUUID().toString(),
            menuItemId = menuItem.id,
            name = menuItem.name,
            imageUrl = menuItem.imageUrl,
            quantity = quantity,
            unitPrice = menuItem.price,
            totalPrice = totalPrice,
            customizations = customizations,
            addedExtrasPrice = totalPrice - (menuItem.price * quantity),
            addedAt = Clock.System.now()
        )
        
        if (currentCart == null) {
            // Yeni sepet oluştur
            val newCart = Cart(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                userId = UserManager.currentUser?.id,
                tableId = TenantManager.currentTable.value?.id,
                items = listOf(cartItem),
                pricing = calculatePricing(listOf(cartItem)),
                // ... diğer alanlar
            )
            _cart.value = newCart
            cartRepository?.createCart(newCart)
        } else {
            // Mevcut sepete ekle
            val updatedItems = currentCart.items + cartItem
            val updatedCart = currentCart.copy(
                items = updatedItems,
                pricing = calculatePricing(updatedItems),
                updatedAt = Clock.System.now()
            )
            _cart.value = updatedCart
            cartRepository?.updateCart(updatedCart)
        }
    }
    
    suspend fun syncCart() {
        val localCart = _cart.value ?: return
        val syncedCart = cartRepository?.syncLocalCart(localCart)?.getOrNull()
        syncedCart?.let { _cart.value = it }
    }
    
    // ... diğer metodlar
}
```

---

## 🖥️ Ekran Güncellemeleri

### QRScanScreen Güncellemesi

```kotlin
@Composable
fun QRScanScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // QR kod tarama sonucu
    fun onQrCodeScanned(qrCode: String) {
        scope.launch {
            isLoading = true
            error = null
            
            try {
                val qrData = parseQrCode(qrCode)
                
                // Tenant'ı yükle
                TenantManager.loadTenant(qrData.tenantId, qrData.tableId)
                
                // Sepeti yükle (kullanıcı giriş yapmışsa)
                UserManager.currentUser?.id?.let { userId ->
                    CartManager.loadCart(userId, qrData.tenantId)
                }
                
                // Menü ekranına git
                navController.navigate(TenantMenu)
                
            } catch (e: Exception) {
                error = "İşletme bulunamadı"
            } finally {
                isLoading = false
            }
        }
    }
    
    // UI...
}
```

### TenantMenuScreen Güncellemesi

```kotlin
@Composable
fun TenantMenuScreen(navController: NavController) {
    val tenant by TenantManager.currentTenant.collectAsState()
    val menu by TenantManager.menu.collectAsState()
    val campaigns by TenantManager.campaigns.collectAsState()
    val isLoading by TenantManager.isLoading.collectAsState()
    val error by TenantManager.error.collectAsState()
    
    if (tenant == null) {
        // Tenant yüklenmemiş, QR ekranına yönlendir
        LaunchedEffect(Unit) {
            navController.navigate(QRScan) {
                popUpTo(TenantMenu) { inclusive = true }
            }
        }
        return
    }
    
    Scaffold(
        topBar = {
            TenantTopBar(
                tenantName = tenant!!.name,
                tenantLogo = tenant!!.logo,
                isOpen = tenant!!.isOpen
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            // Tenant header
            item {
                TenantHeader(tenant = tenant!!)
            }
            
            // Kampanyalar
            if (campaigns.isNotEmpty()) {
                item {
                    CampaignBanner(campaigns = campaigns)
                }
            }
            
            // Menü kategorileri
            menu.forEach { category ->
                item {
                    CategoryHeader(category = category)
                }
                
                items(category.items) { item ->
                    MenuItemCard(
                        item = item,
                        onClick = { navController.navigate(FoodDetail.fromMenuItem(item)) }
                    )
                }
            }
        }
    }
}
```

---

## 🔒 Güvenlik Kuralları

### Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper functions
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function isOwner(userId) {
      return isAuthenticated() && request.auth.uid == userId;
    }
    
    function isTenantAdmin(tenantId) {
      return isAuthenticated() && 
        exists(/databases/$(database)/documents/tenantAdmins/$(tenantId)/admins/$(request.auth.uid));
    }
    
    // Tenants - herkes okuyabilir, sadece admin yazabilir
    match /tenants/{tenantId} {
      allow read: if true;
      allow write: if isTenantAdmin(tenantId);
      
      // Menu subcollection
      match /menu/{categoryId} {
        allow read: if true;
        allow write: if isTenantAdmin(tenantId);
        
        match /items/{itemId} {
          allow read: if true;
          allow write: if isTenantAdmin(tenantId);
        }
      }
      
      // Campaigns subcollection
      match /campaigns/{campaignId} {
        allow read: if true;
        allow write: if isTenantAdmin(tenantId);
      }
      
      // Tables subcollection
      match /tables/{tableId} {
        allow read: if true;
        allow write: if isTenantAdmin(tenantId);
      }
    }
    
    // Users - sadece kendi verisine erişebilir
    match /users/{userId} {
      allow read, write: if isOwner(userId);
    }
    
    // Orders
    match /orders/{orderId} {
      allow read: if isAuthenticated() && 
        (resource.data.userId == request.auth.uid || 
         isTenantAdmin(resource.data.tenantId));
      allow create: if isAuthenticated() && 
        request.resource.data.userId == request.auth.uid;
      allow update: if isTenantAdmin(resource.data.tenantId);
      allow delete: if false;
    }
    
    // Carts
    match /carts/{cartId} {
      allow read, write: if isAuthenticated() && 
        resource.data.userId == request.auth.uid;
      allow create: if isAuthenticated() && 
        request.resource.data.userId == request.auth.uid;
    }
    
    // Favorites
    match /favorites/{favId} {
      allow read, write: if isAuthenticated() && 
        resource.data.userId == request.auth.uid;
    }
  }
}
```

---

## 📋 Uygulama Adımları

### Faz 1: Firebase Kurulumu (1-2 gün)

- [ ] Firebase projesi oluştur
- [ ] Firestore veritabanı oluştur
- [ ] Firebase Storage yapılandır
- [ ] Firebase Authentication yapılandır
- [ ] Security rules yaz
- [ ] KMP projesine Firebase SDK ekle
  - [ ] `gitlive-firebase` veya resmi SDK kullan
  - [ ] Platform-specific implementations

### Faz 2: Veri Modelleri (2-3 gün)

- [ ] Domain model sınıflarını oluştur
- [ ] Firebase DTO sınıflarını oluştur
- [ ] Mapper fonksiyonları yaz
- [ ] Serialization/Deserialization test et

### Faz 3: Repository Katmanı (3-4 gün)

- [ ] Repository interface'lerini tanımla
- [ ] Firebase service sınıflarını oluştur
- [ ] Repository implementasyonlarını yaz
- [ ] Cache mekanizması ekle
- [ ] Error handling ekle

### Faz 4: State Management (2-3 gün)

- [ ] TenantManager'ı oluştur
- [ ] CartManager'ı güncelle
- [ ] UserManager oluştur
- [ ] OrderManager oluştur
- [ ] Flow/StateFlow entegrasyonu

### Faz 5: QR Kod Sistemi (1-2 gün)

- [ ] QR kod parser yaz
- [ ] Deep link handling ekle
- [ ] QRScanScreen'i güncelle

### Faz 6: Ekran Güncellemeleri (5-7 gün)

- [ ] TenantMenuScreen'i Firebase'e bağla
- [ ] CartScreen'i güncelle
- [ ] CheckoutScreen'i güncelle
- [ ] OrdersScreen'i güncelle
- [ ] ProfileScreen'i güncelle
- [ ] CampaignScreen'i güncelle

### Faz 7: Test & Optimizasyon (3-4 gün)

- [ ] Unit testler yaz
- [ ] Integration testler yaz
- [ ] Performance optimizasyonu
- [ ] Offline support ekle
- [ ] Error handling iyileştir

### Faz 8: Admin Panel (Opsiyonel, 5-7 gün)

- [ ] Web tabanlı admin panel
- [ ] Menü yönetimi
- [ ] Sipariş takibi
- [ ] Kampanya yönetimi
- [ ] Raporlama

---

## 📊 Örnek Firebase Console Yapısı

```
Firestore Database
├── tenants
│   ├── if-sokak
│   │   ├── info (document)
│   │   ├── menu (subcollection)
│   │   │   ├── burgers
│   │   │   │   └── items (subcollection)
│   │   │   ├── pizzas
│   │   │   └── drinks
│   │   ├── campaigns (subcollection)
│   │   └── tables (subcollection)
│   └── pizza-house
│       └── ...
│
├── users
│   ├── user_abc123
│   └── user_def456
│
├── orders
│   ├── order_xyz789
│   └── order_uvw012
│
├── carts
│   ├── cart_abc123
│   └── cart_def456
│
└── favorites
    ├── fav_user123_ifsokak
    └── fav_user456_pizzahouse
```

---

## 🎯 Sonuç

Bu plan, Qarko uygulamasını multi-tenant bir sisteme dönüştürmek için gerekli tüm adımları içermektedir. Firebase kullanarak ölçeklenebilir, güvenli ve performanslı bir altyapı kurulacaktır.

### Tahmini Toplam Süre: 3-4 Hafta

### Öncelikli Hedefler:
1. ✅ Temel Firebase entegrasyonu
2. ✅ QR kod ile işletme yükleme
3. ✅ Menü ve sipariş sistemi
4. ✅ Sepet senkronizasyonu
5. ✅ Geçmiş siparişler

### Gelecek Geliştirmeler:
- Push notifications
- Real-time sipariş takibi
- Loyalty programı
- Admin panel
- Analytics dashboard

