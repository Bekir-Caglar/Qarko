package com.bekircaglar.qarko.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.domain.repository.IAuthRepository
import com.bekircaglar.qarko.navigation.TenantMenu
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import com.bekircaglar.qarko.util.QarkoTypography
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val authRepository: IAuthRepository = koinInject()
    val scope = rememberCoroutineScope()
    val user = UserManager.currentUser
    
    var showSheet by remember { mutableStateOf(false) }
    var sheetContent by remember { mutableStateOf<SettingsSheetContent?>(null) }
    val sheetState = rememberModalBottomSheetState()

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = white,
            dragHandle = { BottomSheetDefaults.DragHandle(color = lightGray) }
        ) {
            sheetContent?.let { content ->
                SettingsBottomSheetContent(
                    content = content,
                    user = user,
                    onClose = { scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false } }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { QText("Ayarlar", textStyle = QarkoTypography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = FeatherIcons.ArrowLeft, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = surfaceGray)
            )
        },
        containerColor = surfaceGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profil Bilgileri Bölümü
            SettingsSection(title = "Profil Bilgileri") {
                SettingsItem(
                    icon = FeatherIcons.User,
                    title = "Ad Soyad",
                    subtitle = user?.displayName ?: "Ayşe Yılmaz",
                    onClick = { 
                        sheetContent = SettingsSheetContent.UpdateName
                        showSheet = true
                    }
                )
                SettingsItem(
                    icon = FeatherIcons.Phone,
                    title = "Telefon Numarası",
                    subtitle = user?.phoneNumber ?: "+90 555 123 4567",
                    onClick = { 
                        sheetContent = SettingsSheetContent.UpdatePhone
                        showSheet = true
                    }
                )
            }

            // Ödeme ve Güvenlik
            SettingsSection(title = "Ödeme ve Güvenlik") {
                SettingsItem(
                    icon = FeatherIcons.CreditCard,
                    title = "Kayıtlı Kartlarım",
                    subtitle = "Kartlarını düzenle veya ekle",
                    onClick = { 
                        sheetContent = SettingsSheetContent.PaymentMethods
                        showSheet = true
                    }
                )
            }

            // Destek ve Bilgi
            SettingsSection(title = "Destek ve Bilgi") {
                SettingsItem(
                    icon = FeatherIcons.Headphones,
                    title = "Canlı Destek",
                    onClick = { 
                        sheetContent = SettingsSheetContent.LiveSupport
                        showSheet = true
                    }
                )
                SettingsItem(
                    icon = FeatherIcons.MessageCircle,
                    title = "Sıkça Sorulan Sorular",
                    onClick = { 
                        sheetContent = SettingsSheetContent.FAQ
                        showSheet = true
                    }
                )
                SettingsItem(
                    icon = FeatherIcons.Info,
                    title = "Uygulama Hakkında",
                    onClick = { 
                        sheetContent = SettingsSheetContent.About
                        showSheet = true
                    }
                )
                SettingsItem(
                    icon = FeatherIcons.FileText,
                    title = "Kullanım ve Gizlilik Kuralları",
                    onClick = { 
                        sheetContent = SettingsSheetContent.Privacy
                        showSheet = true
                    }
                )
            }

            // Hesap Yönetimi
            SettingsSection(title = "Hesap Yönetimi") {
                SettingsItem(
                    icon = FeatherIcons.LogOut,
                    title = "Çıkış Yap",
                    titleColor = Color.Red,
                    iconTint = Color.Red,
                    showArrow = false,
                    onClick = {
                        scope.launch {
                            authRepository.logout()
                            UserManager.logout()
                            navController.navigate(TenantMenu) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
                SettingsItem(
                    icon = FeatherIcons.Trash2,
                    title = "Hesabımı Sil",
                    titleColor = Color.Red,
                    iconTint = Color.Red,
                    showArrow = false,
                    onClick = { 
                        sheetContent = SettingsSheetContent.DeleteAccount
                        showSheet = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

enum class SettingsSheetContent {
    UpdateName, UpdatePhone, PaymentMethods, LiveSupport, FAQ, About, Privacy, DeleteAccount
}

@Composable
fun SettingsBottomSheetContent(
    content: SettingsSheetContent,
    user: com.bekircaglar.qarko.data.model.User?,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val title = when (content) {
            SettingsSheetContent.UpdateName -> "Ad Soyad Güncelle"
            SettingsSheetContent.UpdatePhone -> "Telefon Numarası Güncelle"
            SettingsSheetContent.PaymentMethods -> "Ödeme Yöntemlerim"
            SettingsSheetContent.LiveSupport -> "Canlı Destek"
            SettingsSheetContent.FAQ -> "Sıkça Sorulan Sorular"
            SettingsSheetContent.About -> "Uygulama Hakkında"
            SettingsSheetContent.Privacy -> "Gizlilik Politikası"
            SettingsSheetContent.DeleteAccount -> "Hesabımı Sil"
        }

        QText(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(24.dp))

        when (content) {
            SettingsSheetContent.UpdateName -> {
                var name by remember { mutableStateOf(user?.displayName ?: "") }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ad Soyad") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    QText("Güncelle", color = white, fontWeight = FontWeight.Bold)
                }
            }
            SettingsSheetContent.UpdatePhone -> {
                var phone by remember { mutableStateOf(user?.phoneNumber ?: "") }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefon Numarası") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    QText("Güncelle", color = white, fontWeight = FontWeight.Bold)
                }
            }
            SettingsSheetContent.PaymentMethods -> {
                QText("Kayıtlı herhangi bir kartınız bulunmamaktadır.", color = gray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    QText("+ Yeni Kart Ekle", color = white, fontWeight = FontWeight.Bold)
                }
            }
            SettingsSheetContent.LiveSupport -> {
                Icon(FeatherIcons.Headphones, null, modifier = Modifier.size(64.dp), tint = primary)
                Spacer(modifier = Modifier.height(16.dp))
                QText("Müşteri temsilcimiz şu an meşgul.", color = gray)
            }
            SettingsSheetContent.FAQ -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FAQItem("Nasıl sipariş verebilirim?", "QR kodu taratarak menüye ulaşabilir ve siparişinizi oluşturabilirsiniz.")
                    FAQItem("Ödeme yöntemleri nelerdir?", "Kredi kartı, banka kartı ve kasada ödeme seçeneklerimiz mevcuttur.")
                }
            }
            SettingsSheetContent.About -> {
                QText("Qarko v1.0.0", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                QText("Restoran deneyiminizi dijitalleştiren akıllı çözüm.", color = gray)
            }
            SettingsSheetContent.Privacy -> {
                QText("Verileriniz KVKK kapsamında korunmaktadır. Detaylı bilgi için web sitemizi ziyaret edebilirsiniz.", color = gray)
            }
            SettingsSheetContent.DeleteAccount -> {
                QText("Hesabınızı silmek istediğinize emin misiniz? Bu işlem geri alınamaz.", color = gray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    QText("Hesabımı Kalıcı Olarak Sil", color = white, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        QText(text = question, fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 15.sp)
        QText(text = answer, color = gray, fontSize = 14.sp)
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        QText(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = gray,
            modifier = Modifier.padding(start = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = white),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = darkBlue,
    iconTint: Color = primary,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                QText(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = titleColor
                )
                if (subtitle != null) {
                    QText(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = gray
                    )
                }
            }

            if (showArrow) {
                Icon(
                    imageVector = FeatherIcons.ChevronRight,
                    contentDescription = null,
                    tint = gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
