package com.bekircaglar.qarko.presentation.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.presentation.common.components.QButton
import com.bekircaglar.qarko.presentation.common.theme.*
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.apple_logo_black
import qarko.composeapp.generated.resources.google_logo

/**
 * Sepet ekranında auth gerektiren işlemler için açılan Bottom Sheet.
 * Kullanıcıyı bağlamdan koparmadan hızlı giriş seçenekleri sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBottomSheet(
    onDismiss: () -> Unit,
    onLoginWithEmail: () -> Unit,
    onLoginWithGoogle: () -> Unit = {},
    onLoginWithApple: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .padding(bottom = 8.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(2.dp),
                    color = lightGray
                ) {}
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Emoji veya İkon
            Text(
                text = "🍕",
                fontSize = 48.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Başlık
            Text(
                text = "Lezzetler Hazırlanmak Üzere!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = darkBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Açıklama
            Text(
                text = "Sipariş vermek ve puan kazanmak için hızlıca giriş yap.",
                style = MaterialTheme.typography.bodyMedium,
                color = gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Ana Giriş Butonu (E-posta ile)
            QButton(
                onClick = onLoginWithEmail,
                buttonText = "Giriş Yap / Kayıt Ol",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ayırıcı
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = lightGray
                )
                Text(
                    text = "  veya  ",
                    style = MaterialTheme.typography.labelMedium,
                    color = gray
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = lightGray
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sosyal Giriş Butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google Butonu
                OutlinedButton(
                    onClick = onLoginWithGoogle,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = darkBlue
                    )
                ) {
                    Image(
                        painter = painterResource(Res.drawable.google_logo),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                
                // Apple Butonu
                OutlinedButton(
                    onClick = onLoginWithApple,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = darkBlue
                    )
                ) {
                    Image(
                        painter = painterResource(Res.drawable.apple_logo_black),
                        contentDescription = "Apple",
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(black)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Apple",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Alt Not
            Text(
                text = "Giriş yaparak Kullanım Koşulları'nı ve Gizlilik Politikası'nı kabul etmiş olursunuz.",
                style = MaterialTheme.typography.labelSmall,
                color = gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
