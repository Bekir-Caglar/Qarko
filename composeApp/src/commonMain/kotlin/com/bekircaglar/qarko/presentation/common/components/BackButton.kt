package com.bekircaglar.qarko.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.arrow_left

@Composable
fun BackButton(iconColor: Color = white, backgroundColor: Color = white, modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .size(36.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.arrow_left),
            contentDescription = "Geri",
            tint = iconColor,
            modifier = Modifier.padding(10.dp)
        )
    }
}