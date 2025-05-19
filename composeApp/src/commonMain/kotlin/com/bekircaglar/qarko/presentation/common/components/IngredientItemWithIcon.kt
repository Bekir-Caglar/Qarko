// filepath: /Qarko/Qarko/composeApp/src/commonMain/kotlin/com/bekircaglar/qarko/presentation/common/components/IngredientItemWithIcon.kt
package com.bekircaglar.qarko.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.darkGray
import com.bekircaglar.qarko.data.model.IngredientWithIcon

@Composable
fun IngredientItemWithIcon(ingredient: IngredientWithIcon) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(8.dp)
            .clickable { /* Handle click event */ }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ingredient.icon,
                contentDescription = ingredient.name,
                tint = primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = ingredient.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = darkGray,
            textAlign = TextAlign.Center
        )
    }
}