package com.bekircaglar.qarko.presentation.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.util.QarkoTypography
import com.bekircaglar.qarko.presentation.common.theme.white


@Composable
fun LoginOptionButton(
    onClick: () -> Unit,
    text: String? = null,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke((1.5).dp, lighterGray),
        colors = buttonColors(containerColor = white, contentColor = black)
    ) {
        if (icon != null) {
            icon()
        }
        if (text != null) {
            QText(
                text = text,
                modifier = Modifier.padding(start = 8.dp),
                textStyle = QarkoTypography.titleSmall
            )
        }
    }
}