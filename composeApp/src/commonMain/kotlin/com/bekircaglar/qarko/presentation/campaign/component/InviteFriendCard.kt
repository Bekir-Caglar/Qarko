package com.bekircaglar.qarko.presentation.campaign.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.util.QarkoTypography
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_email_filled

@Composable
fun InviteFriendCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Column {
            // Üst mor başlık alanı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primary)
                    .padding(8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource (Res.drawable.ic_email_filled), // kendi ikonunu koy
                        contentDescription = "Invite Icon",
                        tint = white,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    QText(
                        text = "Arkadaşını getir, ₺200 kazan",
                        color = white,
                        textStyle = QarkoTypography.titleMedium,
                    )
                }
            }

            // Alt açıklama alanı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QText(
                    text = "Her yeni ilk siparişte ikinize de hediye.\nDavet kodu paylaş, birlikte kazanın!",
                    textStyle = QarkoTypography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Arrow",
                    tint = primary,
                    modifier = Modifier
                        .size(30.dp)
                        .background(lighterGray, shape = RoundedCornerShape(12.dp))
                        .padding(4.dp)
                )
            }
        }
    }
}