package com.bekircaglar.qarko.presentation.checkout.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check

@Composable
fun OrderSuccessDialog(
    onDismiss: () -> Unit,
    onGoToOrders: () -> Unit
) {
    // Animasyon için
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Dialog içeriği
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .scale(scale.value),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = white),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Success Icon
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF4CAF50),
                                        Color(0xFF2E7D32)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Check,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = white
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Başlık
                    Text(
                        text = "Siparişiniz Alındı! 🎉",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Açıklama
                    Text(
                        text = "Sipariş durumunu \"Siparişlerim\" ekranından anlık olarak izleyebilirsiniz.",
                        fontSize = 14.sp,
                        color = gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Siparişlerime Git butonu
                    Button(
                        onClick = onGoToOrders,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primary
                        )
                    ) {
                        Text(
                            text = "Siparişlerime Git",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = white
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Menüye dön butonu
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Menüye Dön",
                            fontSize = 16.sp,
                            color = gray
                        )
                    }
                }
            }
        }
    }
}
