package com.bekircaglar.qarko.presentation.tenant.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun SettingsMenuItem(
    label: String,
    iconInt: DrawableResource? = null,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingIconInt: DrawableResource? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 8.dp)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconInt != null)
            Icon(
                painter = painterResource(iconInt),
                contentDescription = "Settings Icon",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

        Spacer(modifier = Modifier.width(12.dp))

        Text(label, fontSize = 16.sp, color = textColor)

        Spacer(modifier = Modifier.weight(1f))


        if (trailingIconInt != null)
            Icon(
                painter = painterResource(trailingIconInt),
                contentDescription = "Right Arrow",
                tint = iconColor,
                modifier = Modifier
                    .size(14.dp)
            )

    }
}