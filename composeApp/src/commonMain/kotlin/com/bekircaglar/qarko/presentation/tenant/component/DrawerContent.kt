package com.bekircaglar.qarko.presentation.tenant.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.forToday
import com.bekircaglar.qarko.presentation.common.components.QSwitch
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.*

@Composable
fun DrawerContent(
    onChangeLanguage: () -> Unit,
    onChangeTheme: (Boolean) -> Unit,
    onExitMenu: () -> Unit,
) {
    val themeController = LocalThemeController.current
    val isDarkTheme by remember { mutableStateOf(themeController.isDarkTheme) }
    var isLanguageMenuExpanded by remember { mutableStateOf(false) }

    // Firebase'den gelen veya güncellenen aktif tenant bilgisi
    val currentTenant = TenantSession.currentTenant
    val currentTable = TenantSession.currentTable

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.85f),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        drawerContainerColor = white
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = primary.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, lightGray.copy(alpha = 0.5f))
                ) {
                    if (!currentTenant?.logo.isNullOrEmpty())
                        AsyncImage(
                            model = currentTenant.logo,
                            contentDescription = "Logo",
                            modifier = Modifier.padding(8.dp)
                        )
                    else
                        Image(
                            painter = painterResource(Res.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.padding(12.dp)
                        )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    QText(
                        text = currentTenant?.name ?: "Yükleniyor...",
                        fontSize = 18.sp,
                        fontWeight = Bold,
                        color = darkBlue
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (currentTable != null) {
                        Surface(
                            color = primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            QText(
                                text = currentTable.name,
                                fontSize = 12.sp,
                                color = primary,
                                fontWeight = Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        QText(
                            text = currentTenant?.description ?: "İşletme Bilgisi",
                            fontSize = 12.sp,
                            color = gray,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Business Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(lighterGray)
                    .padding(16.dp)
            ) {
                QText(text = "İşletme Bilgileri", fontSize = 14.sp, fontWeight = Bold, color = gray, modifier = Modifier.padding(bottom = 12.dp))

                InfoItem(
                    icon = FeatherIcons.MapPin,
                    text = currentTenant?.address?.let { "${it.street}, ${it.district}/${it.city}" } ?: "Adres bilgisi yükleniyor..."
                )

                val todayHours = currentTenant?.workingHours?.forToday()
                val openNow = todayHours?.isOpenNow() == true
                val infoText = todayHours?.formattedHours()?.let { if (openNow) "$it (Açık)" else "$it (Kapalı)" } ?: "Çalışma saati yok"
                val infoColor = if (openNow) Color(0xFF4CAF50) else Color.Red

                InfoItem(
                    icon = FeatherIcons.Clock,
                    text = infoText,
                    textColor = infoColor
                )

                if (!currentTenant?.contact?.phone.isNullOrEmpty()) {
                    InfoItem(
                        icon = FeatherIcons.Phone,
                        text = currentTenant!!.contact.phone
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = lightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))

            // Preferences Section
            QText(text = "Ayarlar", fontSize = 14.sp, fontWeight = Bold, color = gray)
            Spacer(modifier = Modifier.height(12.dp))

            // Language Preference
            PreferenceItem(
                icon = FeatherIcons.Globe,
                label = "Dil Değiştir",
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        QText(text = "🇹🇷", fontSize = 18.sp)
                        Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(20.dp), gray)
                    }
                },
                onClick = { isLanguageMenuExpanded = true }
            )

            // Theme Preference
            PreferenceItem(
                icon = if (isDarkTheme) FeatherIcons.Moon else FeatherIcons.Sun,
                label = "Koyu Tema",
                trailing = {
                    QSwitch(
                        scale = 0.8f,
                        isChecked = isDarkTheme,
                        onCheckedChange = { themeController.toggleTheme() },
                        checkedTrackColor = primary,
                        uncheckedTrackColor = lightGray
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Footer Section - Exit
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExitMenu() },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF44336).copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(FeatherIcons.LogOut, null, Modifier.size(20.dp), Color(0xFFF44336))
                    Spacer(modifier = Modifier.width(12.dp))
                    QText(text = "Menüden Çıkış Yap", color = Color(0xFFF44336), fontWeight = Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            QText(
                text = "Qarko v1.0.0",
                fontSize = 10.sp,
                color = gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: Color = darkBlue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, null, Modifier.size(16.dp), primary)
        Spacer(modifier = Modifier.width(12.dp))
        QText(text = text, fontSize = 13.sp, color = textColor, lineHeight = 18.sp)
    }
}

@Composable
fun PreferenceItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    trailing: @Composable () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(lightGray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(18.dp), darkBlue)
        }
        Spacer(modifier = Modifier.width(16.dp))
        QText(text = label, fontSize = 15.sp, color = darkBlue, fontWeight = Bold)
        Spacer(modifier = Modifier.weight(1f))
        trailing()
    }
}
