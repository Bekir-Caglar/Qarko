package com.bekircaglar.qarko.presentation.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.data.manager.HistoryManager
import com.bekircaglar.qarko.data.repository.TenantRepository
import com.bekircaglar.qarko.domain.usecase.tenant.LoadTenantFromQRUseCase
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.darkPrimary
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.navigation.TenantMenu
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.darkBlue
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.util.QarkoTypography
import com.bekircaglar.qarko.presentation.common.theme.white
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock
import compose.icons.feathericons.Maximize
import compose.icons.feathericons.ChevronRight
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_qr


@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    val tenantRepository = remember { TenantRepository() }
    val loadTenantUseCase = remember { LoadTenantFromQRUseCase(tenantRepository) }
    
    val recentMenus = remember { HistoryManager.getRecentMenus() }
    var showHistorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/qr_scan.json").decodeToString()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {},
                actions = {}
            )
        },
        containerColor = surfaceGray
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Ana içerik
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = white),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberLottiePainter(
                                composition = composition,
                                iterations = Compottie.IterateForever,
                            ),
                            modifier = Modifier.size(250.dp),
                            contentDescription = "Lottie animation"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.offset(y = (-32).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        QText(
                            text = "Masana Özel Menüyü Keşfet",
                            textStyle = QarkoTypography.titleLarge,
                            color = darkPrimary,
                            textAlign = TextAlign.Center,
                        )

                        QText(
                            text = "Masadaki QR kodu okutarak menüyü gör ve sipariş oluştur!",
                            fontSize = 16.sp,
                            color = black,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // QR Kod Butonu
            Button(
                onClick = {
                    navController.navigate(QRScan)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_qr),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    QText(
                        text = "Yeni QR Okut",
                        textStyle = QarkoTypography.titleMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Görüntülenen Menüler Butonu
            if (recentMenus.isNotEmpty()) {
                OutlinedButton(
                    onClick = { showHistorySheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = FeatherIcons.Clock,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        QText(
                            text = "Son Görüntülenen Menüler",
                            textStyle = QarkoTypography.titleMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // History Bottom Sheet
        if (showHistorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showHistorySheet = false },
                sheetState = sheetState,
                containerColor = white,
                dragHandle = { BottomSheetDefaults.DragHandle(color = gray.copy(alpha = 0.5f)) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 48.dp)
                ) {
                    QText(
                        text = "Son Görüntülenen Menüler",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(recentMenus) { menu ->
                            Surface(
                                onClick = {
                                    scope.launch {
                                        sheetState.hide()
                                        showHistorySheet = false
                                        loadTenantUseCase.loadDirect(menu.tenant.slug, menu.tableId)
                                            .onSuccess {
                                                navController.navigate(TenantMenu)
                                            }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = lighterGray,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = menu.tenant.logo,
                                        contentDescription = menu.tenant.name,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(white)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        QText(
                                            text = menu.tenant.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = darkBlue
                                        )
                                        QText(
                                            text = "Menüye tekrar göz at",
                                            fontSize = 12.sp,
                                            color = gray
                                        )
                                    }
                                    
                                    Icon(
                                        imageVector = FeatherIcons.ChevronRight,
                                        contentDescription = null,
                                        tint = gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
