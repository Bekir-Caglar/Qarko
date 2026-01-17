package com.bekircaglar.qarko.presentation.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bekircaglar.qarko.presentation.common.theme.black
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import qarko.composeapp.generated.resources.Res
import kotlin.time.Duration

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AddToCartDialog(onDismiss: () -> Unit) {
    val animationSpeed = 2f
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/add_cart.json").decodeToString()
        )
    }

    LaunchedEffect(composition) {
        composition?.let {
            // Animasyon süresini hıza bölerek gerçek süreyi hesaplıyoruz
            // Duration.div(Float) bulunmadığı için Double'a çeviriyoruz
            val actualDuration = it.duration / animationSpeed.toDouble()
            delay(actualDuration)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.White, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                QText(
                    text = "Sepete Eklendi",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Image(
                    painter = rememberLottiePainter(
                        composition = composition,
                        speed = animationSpeed
                    ),
                    contentDescription = "Success",
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}
