package com.bekircaglar.qarko.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.domain.repository.IAuthRepository
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.navigation.Settings
import com.bekircaglar.qarko.navigation.TenantMenu
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import com.bekircaglar.qarko.util.QarkoTypography
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_qr

@Composable
fun ProfileScreen(navController: NavController) {
    val user = UserManager.currentUser
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val authRepository: IAuthRepository = koinInject()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(surfaceGray)) {
        Scaffold(
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    currentRoute = navBackStackEntry?.destination?.route ?: ""
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Üst Kısım (Gradient Arka Plan)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(primary.copy(alpha = 0.8f), primary)
                            )
                        )
                ) {
                    // Ayarlar İkonu (Sağ Üst)
                    IconButton(
                        onClick = { navController.navigate(Settings) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(white.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = white,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Profil Resmi
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(white.copy(alpha = 0.3f))
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(white),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = primary.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Kullanıcı Adı ve Mail
                        QText(
                            text = user?.displayName ?: "Kullanıcı Adı",
                            color = white,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        QText(
                            text = user?.email ?: "",
                            color = white.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }

                // İstatistik Kartları
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-40).dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(modifier = Modifier.weight(1f), title = "Sipariş", value = "24")
                    StatCard(modifier = Modifier.weight(1f), title = "Kupon", value = "3")
                    StatCard(modifier = Modifier.weight(1f), title = "Toplam", value = "650₺")
                }

                // Sadakat Puanı Kartı
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            QText(text = "⭐", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            QText(
                                text = "Sadakat Puanı",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = darkBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(primary.copy(alpha = 0.7f), primary)
                                    )
                                )
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                QText(text = "Toplam Puanınız", color = white, fontSize = 14.sp)
                                QText(text = "650 Puan", color = white, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { 0.65f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = primary,
                            trackColor = lightGray.copy(alpha = 0.5f),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        QText(
                            text = "Platinum seviyeye 350 puan kaldı!",
                            color = gray,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bilgi Satırları
                        InfoRow(label = "Telefon", value = user?.phoneNumber ?: "+90 555 123 4567")
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = lightGray.copy(alpha = 0.5f))
                        InfoRow(label = "Üyelik Tarihi", value = "15 Mart 2024")
                    }
                }

            }
        }
        // QR FAB
        Box(modifier = Modifier.align(Alignment.BottomCenter).zIndex(2f).padding(bottom = 24.dp)) {
            FloatingActionButton(
                onClick = { navController.navigate(QRScan) },
                containerColor = primary,
                contentColor = white,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_qr),
                    contentDescription = "QR",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            QText(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primary)
            QText(text = title, fontSize = 12.sp, color = gray)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        QText(text = label, color = gray, fontSize = 14.sp)
        QText(text = value, fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 14.sp)
    }
}
