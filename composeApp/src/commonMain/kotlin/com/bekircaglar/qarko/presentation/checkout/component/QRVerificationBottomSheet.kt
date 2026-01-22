package com.bekircaglar.qarko.presentation.checkout.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.presentation.common.components.QButton
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.AlertTriangle
import compose.icons.feathericons.Camera

/**
 * Güvenlik doğrulaması için QR yeniden okutma Bottom Sheet
 * NEW kullanıcılar + Kasada ödeme seçeneği için kullanılır
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRVerificationBottomSheet(
    onDismiss: () -> Unit,
    onScanQR: () -> Unit,
    tableNumber: String?,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Uyarı İkonu
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        primary.copy(alpha = 0.1f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FeatherIcons.AlertTriangle,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Başlık
            Text(
                text = "Masa Doğrulaması Gerekli",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = darkBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Açıklama
            Text(
                text = "Güvenliğiniz için siparişinizi onaylamadan önce masanızdaki QR kodu tekrar taratmanız gerekmektedir.",
                style = MaterialTheme.typography.bodyMedium,
                color = gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Masa Bilgisi
            if (tableNumber != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            lighterGray.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📍 Beklenen Masa:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tableNumber,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = darkBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // QR Tara Butonu
            QButton(
                onClick = onScanQR,
                buttonText = "QR Kodu Tara",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // İptal Butonu
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Vazgeç",
                    color = gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Neden Bilgisi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        primary.copy(alpha = 0.08f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "ℹ️",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bu doğrulama, ilk siparişinizde masada olduğunuzu onaylamak için gereklidir. Sonraki siparişlerinizde bu adım atlanacaktır.",
                    style = MaterialTheme.typography.labelSmall,
                    color = darkBlue.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
