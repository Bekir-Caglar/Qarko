package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bekircaglar.qarko.presentation.common.theme.darkBlue
import com.bekircaglar.qarko.presentation.common.theme.darkPrimary
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lightBlue
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_qr

@Composable
fun TableEntryCard(
    tableNumber: String?,
    isTableActive: Boolean,
    restaurantName: String? = null,
    onQrScanClick: () -> Unit,
    onChangeTableClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightBlue
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol taraf - QR Icon ve Masa Bilgisi
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_qr),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(24.dp),
                    tint = if (isTableActive) primary else gray
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    if (isTableActive && tableNumber != null) {
                        Text(
                            text = "Masa $tableNumber",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                        restaurantName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        Text(
                            text = "Masa Seçilmedi",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = gray
                        )
                        Text(
                            text = "Sipariş vermek için QR okutun",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Sağ taraf - Aksiyon butonu
            if (isTableActive) {
                OutlinedButton(
                    onClick = onChangeTableClick,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = darkBlue
                    ),
                    border = BorderStroke(1.dp, darkBlue)
                ) {
                    Text(
                        text = "Değiştir",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Button(
                    onClick = onQrScanClick,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = darkPrimary
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_qr),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = white
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "QR Okut",
                        style = MaterialTheme.typography.bodySmall,
                        color = white
                    )
                }
            }
        }
    }
}

