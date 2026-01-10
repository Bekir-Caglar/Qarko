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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.navigation.TenantMenu
import com.bekircaglar.qarko.navigation.Welcome
import com.bekircaglar.qarko.presentation.common.components.QText

@OptIn(ExperimentalResourceApi::class)
@Composable
fun QRScanScreen(navController: NavController) {
    var isScanning by remember { mutableStateOf(true) }
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/qr_border.json").decodeToString()
        )
    }

    val isPlatformAndroid by remember { mutableStateOf(getPlatformName() == "ANDROID") }



    Box(modifier = Modifier.fillMaxSize()) {
        if (isScanning) {
            ScannerWithPermissions(
                onScanned = { result ->
                    navController.navigate(TenantMenu) {
                        popUpTo<QRScan> { inclusive = true }
                    }
                    isScanning = false
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
                    navController.navigate(TenantMenu) {
                        popUpTo<QRScan> { inclusive = true }
                    }
                }
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            QText(
                text = "QR kodu çerçeve içine getirin",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
