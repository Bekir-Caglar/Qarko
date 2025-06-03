package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.darkGreen
import com.bekircaglar.qarko.darkPrimary
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lightBlue
import com.bekircaglar.qarko.lightGray
import com.bekircaglar.qarko.lighterGray
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.credit_card
import qarko.composeapp.generated.resources.qr

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
                    painter = painterResource(Res.drawable.qr),
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
                        painter = painterResource(Res.drawable.qr),
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

