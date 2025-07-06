package com.bekircaglar.qarko.presentation.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bekircaglar.qarko.lighterGray


@Composable
fun TextCenterDivider(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            color = lighterGray,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )

        QText(text = text)

        HorizontalDivider(
            color = lighterGray,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
    }
}