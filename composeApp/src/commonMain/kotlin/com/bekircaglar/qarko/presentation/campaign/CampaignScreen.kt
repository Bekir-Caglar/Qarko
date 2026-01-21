package com.bekircaglar.qarko.presentation.campaign

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.data.model.Campaign
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.Auth
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.navigation.Register
import com.bekircaglar.qarko.presentation.campaign.component.CampaignCard
import com.bekircaglar.qarko.presentation.campaign.component.InviteFriendCard
import com.bekircaglar.qarko.presentation.common.components.LoginRequiredContent
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_qr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignScreen(
    navController: NavHostController
) {
    val viewModel: CampaignViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    
    // Auth kontrolü
    val isLoggedIn = UserManager.isLoggedIn

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = surfaceGray,
            topBar = {
                TopAppBar(
                    title = { Text("Kampanyalar") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = white,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            },
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Kullanıcı giriş yapmamışsa LoginRequiredContent göster
                if (!isLoggedIn) {
                   LoginRequiredContent(
                       title = "Kampanyaları Keşfedin",
                       description = "Özel indirimler, fırsatlar ve kampanyalardan yararlanmak için giriş yapın.",
                       featureList = listOf(
                           "Sadakat puanları kazanın",
                           "Özel indirim kuponları",
                           "Erken erişim fırsatları",
                           "Arkadaş davetiyle ekstra ödüller"
                       ),
                       onLoginClick = { navController.navigate(Auth) },
                   )
                } else {
                    when (val state = uiState) {
                        is CampaignUiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        is CampaignUiState.Success -> {
                            CampaignList(
                                campaigns = state.campaigns,
                                onInviteClick = { /* Davet ekranına yönlendirme eklenebilir */ }
                            )
                        }
                        is CampaignUiState.Empty -> {
                            CampaignList(
                                campaigns = emptyList(),
                                onInviteClick = { /* Davet ekranına yönlendirme eklenebilir */ }
                            )
                        }
                        is CampaignUiState.Error -> {
                            ErrorView(
                                message = state.message,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }

        // Central FAB for QR Scan (Matches other screens)
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

@Composable
private fun CampaignList(
    campaigns: List<Campaign>,
    onInviteClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            InviteFriendCard(onClick = onInviteClick)
        }
        
        items(
            items = campaigns,
            key = { it.id }
        ) { campaign ->
            CampaignCard(campaign = campaign)
        }

        // Add spacer at bottom to prevent FAB from overlapping the last item
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ErrorView(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
