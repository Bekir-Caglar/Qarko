package com.bekircaglar.qarko.presentation.common.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.Award
import compose.icons.feathericons.Gift
import compose.icons.feathericons.Star
import compose.icons.feathericons.Zap
import kotlinx.coroutines.delay

/**
 * Giriş yapılmadığında gösterilen premium ve profesyonel ekran
 */
@Composable
fun LoginRequiredContent(
    title: String,
    description: String,
    featureList: List<String> = emptyList(),
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceGray)
    ) {
        // Scroll edilebilir içerik alanı
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Başlık ve Açıklama
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = darkBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Özellik Kartları (Premium Görünüm) with Animation
            if (featureList.isNotEmpty()) {
                Text(
                    text = "AVANTAJLARINIZ",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = primary.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    featureList.forEachIndexed { index, feature ->
                        PremiumFeatureItem(
                            text = feature,
                            icon = when (index % 4) {
                                0 -> FeatherIcons.Star
                                1 -> FeatherIcons.Gift
                                2 -> FeatherIcons.Zap
                                else -> FeatherIcons.Award
                            },
                            delayMillis = index * 200 // Her öğe için artan gecikme (sıralı efekt)
                        )
                    }
                }
            }
            
            // Butonun altında kalan alan için boşluk (BottomBar + Button yüksekliği kadar)
            Spacer(modifier = Modifier.height(120.dp))
        }

        // Sabit Buton Alanı (En altta)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            surfaceGray.copy(alpha = 0f),
                            surfaceGray.copy(alpha = 0.9f),
                            surfaceGray
                        )
                    )
                )
                .padding(24.dp)
        ) {
            QButton(
                onClick = onLoginClick,
                buttonText = "Hemen Giriş Yap",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

@Composable
private fun PremiumFeatureItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    delayMillis: Int
) {
    // Animasyon durumu
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong()) // Başlangıç gecikmesi
        while (true) {
            // Büyüme
            scale.animateTo(
                targetValue = 1.02f, // Çok hafif büyüme
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
            // Küçülme
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
            // Bir sonraki döngü öncesi biraz bekleme (nefes alma hissi)
            delay(2000) 
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value) // Animasyon uygulanıyor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = darkBlue,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = FeatherIcons.Star,
                contentDescription = null,
                tint = primary.copy(alpha = 0.3f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

