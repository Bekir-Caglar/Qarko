package com.bekircaglar.qarko.presentation.campaign.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkPrimary
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.util.QarkoTypography
import com.bekircaglar.qarko.white

@Composable
fun CampaignItem(
    campaignTitle: String,
    campaignDescription: String,
    campaignImageUrl: String? = null,
    onClick: () -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .background(white, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        AsyncImage(
            model = campaignImageUrl,
            contentDescription = campaignTitle,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        QText(
            text = campaignTitle,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            fontSize = 18.sp,
            color = darkPrimary
        )

        QText(
            text = campaignDescription,
            textStyle = QarkoTypography.bodyMedium,
            color = black
        )

        Spacer(modifier = Modifier.height(8.dp))

    }

    
}