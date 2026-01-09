package com.bekircaglar.qarko.presentation.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.util.QarkoTypography

@Composable
fun QButton(
    buttonText: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primary),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp)
    ) {
        QText(
            text = buttonText,
            textStyle = QarkoTypography.titleMedium
        )
    }
}