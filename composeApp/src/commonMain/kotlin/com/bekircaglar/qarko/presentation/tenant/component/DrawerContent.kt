package com.bekircaglar.qarko.presentation.tenant.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onLogout: () -> Unit
) {
    val themeController = LocalThemeController.current
    val isDarkTheme by remember { mutableStateOf(themeController.isDarkTheme) }
    var isLanguageMenuExpanded by remember { mutableStateOf(false) }

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.75f),
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
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = lightGray.copy(alpha = 0.2f)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ifsokak_logo),
                        contentDescription = "Logo",
                        colorFilter = ColorFilter.tint(Color(0xFFf4244a)),
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    QText(
                        text = "IF Sokak",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = black
                    )
                    QText(
                        text = "Lezzet Dururağı",
                        fontSize = 12.sp,
                        color = gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            InfoItem(
                icon = FeatherIcons.MapPin,
                text = "Çayyolu, Ankara Caddesi No:123, Çankaya/Ankara"
            )
            InfoItem(
                icon = FeatherIcons.Clock,
                text = "09:00 - 23:00 (Açık)",
                textColor = Color(0xFF4CAF50)
            )
            InfoItem(
                icon = FeatherIcons.Coffee,
                text = "Pizza, Burger, İçecek"
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = lightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))

            // Preferences Section
            QText(text = "Ayarlar", fontSize = 14.sp, fontWeight = Bold, color = gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Language Preference with Dropdown
            Box {
                PreferenceItem(
                    icon = FeatherIcons.Globe,
                    label = "Dil Değiştir",
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            QText(text = "🇹🇷", fontSize = 20.sp)
                            Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(20.dp), gray)
                        }
                    },
                    onClick = { isLanguageMenuExpanded = true }
                )

                DropdownMenu(
                    expanded = isLanguageMenuExpanded,
                    onDismissRequest = { isLanguageMenuExpanded = false },
                    modifier = Modifier.background(white)
                ) {
                    DropdownMenuItem(
                        text = { QText("Türkçe 🇹🇷") },
                        onClick = {
                            isLanguageMenuExpanded = false
                            onChangeLanguage()
                        }
                    )
                    DropdownMenuItem(
                        text = { QText("English 🇬🇧") },
                        onClick = {
                            isLanguageMenuExpanded = false
                            onChangeLanguage()
                        }
                    )
                }
            }

            // Theme Preference
            PreferenceItem(
                icon = if (isDarkTheme) FeatherIcons.Moon else FeatherIcons.Sun,
                label = "Koyu Tema",
                trailing = {
                    QSwitch(
                        scale = 1.2f, // Reduced size
                        isChecked = isDarkTheme,
                        onCheckedChange = { themeController.toggleTheme() },
                        checkedTrackColor = primary,
                        uncheckedTrackColor = gray
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout/Exit Section
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onLogout,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336).copy(alpha = 0.1f) )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(FeatherIcons.LogOut, null, Modifier.size(20.dp), Color(0xFFF44336))
                    Spacer(modifier = Modifier.width(12.dp))
                    QText(text = "Menüden Çık", color = Color(0xFFF44336), fontWeight = Bold)
                }
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: Color = black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(18.dp), primary)
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
        Icon(icon, null, Modifier.size(22.dp), black)
        Spacer(modifier = Modifier.width(16.dp))
        QText(text = label, fontSize = 16.sp, color = black)
        Spacer(modifier = Modifier.weight(1f))
        trailing()
    }
}
