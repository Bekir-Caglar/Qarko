package com.bekircaglar.qarko.presentation.welcome

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions
import qarko.composeapp.generated.resources.Res

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.navigation.NavController
import com.bekircaglar.getPlatformName
import com.bekircaglar.qarko.App
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.QRScanResult
import com.bekircaglar.qarko.data.repository.TenantRepository
import com.bekircaglar.qarko.domain.usecase.tenant.LoadTenantFromQRUseCase
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.navigation.TenantMenu
import com.bekircaglar.qarko.navigation.Welcome
import com.bekircaglar.qarko.presentation.common.components.QText
import kotlinx.coroutines.launch

@OptIn(ExperimentalResourceApi::class)
@Composable
fun QRScanScreen(navController: NavController) {
    var isScanning by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val tenantRepository = remember { TenantRepository() }
    val loadTenantUseCase = remember { LoadTenantFromQRUseCase(tenantRepository) }

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/qr_border.json").decodeToString()
        )
    }

    val isPlatformAndroid by remember { mutableStateOf(getPlatformName() == "ANDROID") }

    // QR kod işleme fonksiyonu
    fun processQRCode(scannedUrl: String) {
        scope.launch {
            isLoading = true
            errorMessage = null

            val result = loadTenantUseCase(scannedUrl)

            result.onSuccess {
                isLoading = false
                navController.navigate(TenantMenu) {
                    popUpTo<QRScan> { inclusive = true }
                }
            }.onFailure { exception ->
                isLoading = false
                errorMessage = exception.message ?: "QR kod işlenemedi"
                isScanning = true // Tekrar taramaya izin ver
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isScanning && !isLoading) {
            ScannerWithPermissions(
                onScanned = { result ->
                    isScanning = false
                    processQRCode(result)
                    true
                },
                types = listOf(CodeType.QR)
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val squareSize = 250.dp.toPx() // QR alanının boyutu
            val left = (size.width - squareSize) / 2
            val top = (size.height - squareSize) / 2

            drawRect(
                color = Color.Black.copy(alpha = 0.7f)
            )

            // Ortadaki alanı temizle
            drawRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(squareSize, squareSize),
                blendMode = BlendMode.Clear
            )
        }

        // QR animasyonu (ortadaki alan)
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberLottiePainter(
                    composition = composition,
                    iterations = Int.MAX_VALUE,
                ),
                contentDescription = "QR Tarama Çerçevesi",
                modifier = Modifier.size(300.dp), // kare boyutu ile aynı olmalı
            )
        }

        // Geri butonu
        IconButton(
            onClick = {
                navController.navigate(Welcome) {
                    popUpTo<QRScan> { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp)
                .then(
                    if (isPlatformAndroid) Modifier else Modifier.padding(top = 32.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Geri",
                tint = Color.White
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable() {
                    // Test için örnek QR URL'i ile devam et
                    processQRCode("https://qarko.app/menu/beko-yeri?tableId=ic4Db9JrLcbhkHXrAJYM")
                }
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QText(
                        text = "Menü yükleniyor...",
                        textAlign = TextAlign.Center
                    )
                } else if (errorMessage != null) {
                    QText(
                        text = errorMessage!!,
                        textAlign = TextAlign.Center,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QText(
                        text = "Tekrar taramak için dokunun",
                        textAlign = TextAlign.Center
                    )
                } else {
                    QText(
                        text = "QR kodu çerçeve içine getirin",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
