package com.bekircaglar.qarko.presentation.tenant.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lightGray
import com.bekircaglar.qarko.presentation.common.components.QSwitch
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.LocalThemeController
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.facebook_logo
import qarko.composeapp.generated.resources.ic_angle_right
import qarko.composeapp.generated.resources.ic_clock
import qarko.composeapp.generated.resources.ic_exit
import qarko.composeapp.generated.resources.ic_food
import qarko.composeapp.generated.resources.ic_language
import qarko.composeapp.generated.resources.ic_location_marker
import qarko.composeapp.generated.resources.ic_moon
import qarko.composeapp.generated.resources.ic_sun
import qarko.composeapp.generated.resources.ifsokak_logo
import qarko.composeapp.generated.resources.insta_logo
import qarko.composeapp.generated.resources.x_logo

@Composable
fun DrawerContent(
    onChangeLanguage: () -> Unit,
    onChangeTheme: (Boolean) -> Unit,
    onExitMenu: () -> Unit,
    onLogout: () -> Unit
) {

    val themeController = LocalThemeController.current
    val isDarkTheme by remember { mutableStateOf(themeController.isDarkTheme) }


    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.7f),
        drawerShape = RoundedCornerShape(0.dp),
        drawerContainerColor = white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ifsokak_logo),
                        contentDescription = "IF Sokak Logo",
                        colorFilter = ColorFilter.tint(Color(0xFFf4244a)),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        QText(
                            text = "IF Sokak",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = black
                        )
                        QText(
                            text = "Fast Food & Sokak Lezzetleri",
                            fontSize = 12.sp,
                            color = gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Adres
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_location_marker),
                        contentDescription = "Adres",
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    QText(
                        text = "Çayyolu Mahallesi, Ankara Caddesi\nNo: 123/A, Çankaya/Ankara",
                        fontSize = 13.sp,
                        color = black,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Çalışma Saatleri
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clock),
                        contentDescription = "Çalışma Saatleri",
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        QText(
                            text = "Çalışma Saatleri",
                            fontSize = 13.sp,
                            color = black,
                            fontWeight = Bold
                        )
                        QText(
                            text = "Pazartesi - Pazar: 09:00 - 23:00",
                            fontSize = 12.sp,
                            color = gray
                        )
                        QText(
                            text = "Şu anda: Açık",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hizmet Alanları
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_food),
                        contentDescription = "Hizmet Alanları",
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        QText(
                            text = "Kategoriler",
                            fontSize = 13.sp,
                            color = black,
                            fontWeight = Bold
                        )
                        QText(
                            text = "• Pizza • Burger • Alkol ",
                            fontSize = 12.sp,
                            color = gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = lightGray)

                Spacer(modifier = Modifier.height(8.dp))

                // Sosyal Medya
                Column {
                    QText(
                        text = "Sosyal Medya Hesapları",
                        fontSize = 14.sp,
                        color = black,
                        fontWeight = Bold,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconButton(
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.insta_logo),
                                contentDescription = "Instagram",
                                modifier = Modifier.size(24.dp).padding(2.dp)
                            )
                        }

                        IconButton(
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.x_logo),
                                contentDescription = "X-Twitter",
                                colorFilter = ColorFilter.tint(black),
                                modifier = Modifier.size(24.dp).padding(2.dp)
                            )
                        }


                        IconButton(
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.facebook_logo),
                                contentDescription = "Facebook",
                                modifier = Modifier.size(24.dp)
                            )
                        }


                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = lightGray)

            Spacer(modifier = Modifier.height(16.dp))


            val flagEmoji = when ("tr") {
                "tr" -> "🇹🇷"
                "en" -> "🇬🇧"
                else -> "🇹🇷"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_language),
                    contentDescription = "Settings Icon",
                    tint = black,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                QText(text = "Dil Değiştir", fontSize = 16.sp, color = black)

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.clickable {
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        flagEmoji,
                        fontSize = 24.sp,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Right Arrow",
                        modifier = Modifier.size(24.dp),
                        tint = black
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(if (isDarkTheme) Res.drawable.ic_moon else Res.drawable.ic_sun),
                    contentDescription = "Settings Icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                QText(
                    text = "Koyu Tema",
                    fontSize = 16.sp,
                    color = black
                )

                Spacer(modifier = Modifier.weight(1f))

                QSwitch(
                    scale = 1f,
                    isChecked = isDarkTheme,
                    onCheckedChange = {
                        themeController.toggleTheme()
                    },
                    checkedTrackColor = primary,
                    enabled = true,
                    uncheckedTrackColor = gray
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = lightGray)

            Spacer(modifier = Modifier.weight(1f))


            SettingsMenuItem(
                label = "İşletme Menüsünden Çık",
                iconInt = Res.drawable.ic_exit,
                iconColor = MaterialTheme.colorScheme.onSurface,
                textColor = MaterialTheme.colorScheme.onSurface,
                trailingIconInt = Res.drawable.ic_angle_right,
                onClick = onLogout
            )

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}


