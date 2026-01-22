package com.bekircaglar.qarko.presentation.checkout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.getPlatformName
import com.bekircaglar.qarko.data.model.QRScanResult
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.AlertCircle
import compose.icons.feathericons.XCircle
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions
import qarko.composeapp.generated.resources.Res

/**
 * Sadece masa doğrulaması için QR tarama ekranı
 * TenantSession'ı etkilemez, sadece masa ID kontrolü yapar
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun QRVerificationScreen(
    navController: NavController,
    expectedTableId: String
) {
    var verificationResult by remember { mutableStateOf<VerificationResult?>(null) }
    var isScanning by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val isPlatformAndroid by remember { mutableStateOf(getPlatformName() == "ANDROID") }

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/qr_border.json").decodeToString()
        )
    }

    // QR kod işleme fonksiyonu
    fun processQRCode(scannedUrl: String) {
        isLoading = true
        
        println("QRVerification: Scanned URL = $scannedUrl")
        println("QRVerification: Expected Table ID = $expectedTableId")
        
        // QR kodunu parse et
        val scanResult = QRScanResult.fromUrl(scannedUrl)
        
        println("QRVerification: Parsed tableId = ${scanResult.tableId}")
        println("QRVerification: isValid = ${scanResult.isValid}")
        
        if (scanResult.isValid && scanResult.tableId != null) {
            if (scanResult.tableId == expectedTableId) {
                println("QRVerification: SUCCESS - Tables match!")
                verificationResult = VerificationResult.Success
                // Başarılı sonucu kaydet ve geri dön
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("qr_verification_result", true)
                navController.popBackStack()
            } else {
                println("QRVerification: WRONG TABLE - Expected: $expectedTableId, Got: ${scanResult.tableId}")
                verificationResult = VerificationResult.WrongTable(scanResult.tableId)
            }
        } else {
            println("QRVerification: INVALID QR - ${scanResult.errorMessage}")
            verificationResult = VerificationResult.InvalidQR(scanResult.errorMessage ?: "Geçersiz QR kod")
        }
        
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize().background(black)) {
        // Scanner
        if (isScanning && !isLoading && verificationResult == null) {
            ScannerWithPermissions(
                onScanned = { result ->
                    isScanning = false
                    processQRCode(result)
                    true
                },
                types = listOf(CodeType.QR)
            )
        }

        // Karartma ve kesim alanı
        Canvas(modifier = Modifier.fillMaxSize()) {
            val squareSize = 250.dp.toPx()
            val left = (size.width - squareSize) / 2
            val top = (size.height - squareSize) / 2

            drawRect(
                color = Color.Black.copy(alpha = 0.7f)
            )

            drawRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(squareSize, squareSize),
                blendMode = BlendMode.Clear
            )
        }

        // QR animasyonu
        if (verificationResult == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberLottiePainter(
                        composition = composition,
                        iterations = Int.MAX_VALUE,
                    ),
                    contentDescription = "QR Tarama Çerçevesi",
                    modifier = Modifier.size(300.dp)
                )
            }
        }

        // Geri butonu
        IconButton(
            onClick = { 
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("qr_verification_result", false)
                navController.popBackStack() 
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

        // Alt bilgi kartı - Normal durum
        if (verificationResult == null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Masa doğrulanıyor...",
                            fontSize = 14.sp,
                            color = gray,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "🔒 Masa Doğrulama",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Siparişinizi onaylamak için masanızdaki QR kodu tarayın",
                            fontSize = 14.sp,
                            color = gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Hata durumları
        verificationResult?.let { result ->
            when (result) {
                is VerificationResult.WrongTable -> {
                    ErrorContent(
                        icon = FeatherIcons.XCircle,
                        title = "Yanlış Masa!",
                        message = "Bu QR kod farklı bir masaya ait. Lütfen siparişinizi verdiğiniz masanın QR kodunu okutun.",
                        buttonText = "Tekrar Dene",
                        onRetry = {
                            verificationResult = null
                            isScanning = true
                        },
                        onCancel = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("qr_verification_result", false)
                            navController.popBackStack()
                        }
                    )
                }
                is VerificationResult.InvalidQR -> {
                    ErrorContent(
                        icon = FeatherIcons.AlertCircle,
                        title = "Geçersiz QR Kod",
                        message = result.message,
                        buttonText = "Tekrar Dene",
                        onRetry = {
                            verificationResult = null
                            isScanning = true
                        },
                        onCancel = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("qr_verification_result", false)
                            navController.popBackStack()
                        }
                    )
                }
                VerificationResult.Success -> {
                    // Bu ekrana gelmez, direkt geri döner
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
    buttonText: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // İkon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color.Red.copy(alpha = 0.1f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = message,
                fontSize = 14.sp,
                color = gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = white
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Vazgeç",
                    fontSize = 16.sp,
                    color = gray
                )
            }
        }
    }
}

private sealed class VerificationResult {
    object Success : VerificationResult()
    data class WrongTable(val scannedTableId: String) : VerificationResult()
    data class InvalidQR(val message: String) : VerificationResult()
}
