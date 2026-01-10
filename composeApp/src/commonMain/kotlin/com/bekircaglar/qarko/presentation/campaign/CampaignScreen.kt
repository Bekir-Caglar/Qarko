package com.bekircaglar.qarko.presentation.campaign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.lightGray
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.presentation.campaign.component.CampaignItem
import com.bekircaglar.qarko.presentation.campaign.component.InviteFriendCard
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.util.QarkoTypography
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_qr

// Dummy kampanya data class'ı
data class Campaign(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val discount: String,
    val validUntil: String
)

// Dummy kampanya listesi
val dummyCampaigns = listOf(
    Campaign(
        id = "1",
        title = "Yemek Festivali",
        description = "Tüm yemekler için geçerli %25 indirim! Lezzet dolu anlar sizi bekliyor.",
        imageUrl = "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=800&q=80",
        discount = "%25",
        validUntil = "31 Aralık 2024"
    ),
    Campaign(
        id = "2",
        title = "Kahvaltı Keyfi",
        description = "Hafta sonu kahvaltı menülerinde %20 indirim fırsatını kaçırmayın!",
        imageUrl = "https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?w=800&q=80",
        discount = "%20",
        validUntil = "15 Şubat 2025"
    ),
    Campaign(
        id = "3",
        title = "Pizza Şenliği",
        description = "En sevilen pizza çeşitlerinde büyük indirim! 2 pizza al, 1 pizza öde.",
        imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&q=80",
        discount = "2+1",
        validUntil = "28 Şubat 2025"
    ),
    Campaign(
        id = "4",
        title = "Burger Zamanı",
        description = "Ev yapımı burger çeşitlerimizde %30 indirim! Lezzet dolu anlar sizi bekliyor.",
        imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&q=80",
        discount = "%30",
        validUntil = "10 Mart 2025"
    ),
    Campaign(
        id = "5",
        title = "Tatlı Dünyası",
        description = "Ev yapımı tatlılarımızda %15 indirim! Şekerpare, baklava ve daha fazlası.",
        imageUrl = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&q=80",
        discount = "%15",
        validUntil = "20 Mart 2025"
    ),
    Campaign(
        id = "6",
        title = "Salata Bar",
        description = "Taze ve sağlıklı salata çeşitlerimizde %10 indirim! Sağlıklı beslenmenin tadını çıkarın.",
        imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&q=80",
        discount = "%10",
        validUntil = "5 Nisan 2025"
    ),
    Campaign(
        id = "7",
        title = "Akşam Yemeği",
        description = "19:00-22:00 saatleri arası tüm ana yemeklerde %20 indirim!",
        imageUrl = "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=800&q=80",
        discount = "%20",
        validUntil = "30 Nisan 2025"
    ),
    Campaign(
        id = "8",
        title = "Deniz Ürünleri",
        description = "Taze deniz ürünleri menümüzde %25 indirim! Denizin lezzetini keşfedin.",
        imageUrl = "https://images.unsplash.com/photo-1544943910-4c1dc44aab44?w=800&q=80",
        discount = "%25",
        validUntil = "15 Mayıs 2025"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignScreen(navController: NavController) {

    Box(){
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        colors =  TopAppBarDefaults.topAppBarColors(
                            containerColor = white,
                            titleContentColor = black
                        ),
                        title = {
                            QText(
                                text = "Kampanyalar",
                                textStyle = QarkoTypography.titleLarge,
                            )
                        }
                    )
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = lightGray)
                }

            },
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        ?: "",
                )
            },
            containerColor = surfaceGray
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {

                // Kampanya kodu ekleme alanı


                // Kampanya listesi
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    item {

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, shape = RoundedCornerShape(12.dp))
                                .background(white)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            FloatingActionButton(
                                onClick = {
                                    // Kampanya kodu ekleme işlemi
                                },
                                containerColor = white,
                                shape = RoundedCornerShape(12.dp),
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = 2.dp,
                                    pressedElevation = 0.dp
                                ),
                                modifier = Modifier.width(56.dp).height(46.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Campaign",
                                    modifier = Modifier.size(24.dp),
                                    tint = primary
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            QText(
                                text = "Kampanya Kodu Ekle",
                                textStyle = QarkoTypography.titleMedium,
                                fontWeight = Bold,
                                color = primary,
                            )

                        }

                        Spacer(modifier = Modifier.height(16.dp))


                        InviteFriendCard(){

                        }
                    }

                    items(dummyCampaigns) { campaign ->

                        Spacer(modifier = Modifier.height(8.dp))

                        CampaignItem(
                            campaignTitle = campaign.title,
                            campaignDescription = campaign.description,
                            campaignImageUrl = campaign.imageUrl,
                            onClick = {
                                // Kampanya detayına git
                                // navController.navigate("campaign_detail/${campaign.id}")
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

            }

        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
                .padding(bottom = 24.dp)
        ) {
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