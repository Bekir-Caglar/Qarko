package com.bekircaglar.qarko.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.data.model.IngredientModel

@Composable
fun IngredientItem(ingredient: IngredientModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Blue) // Replace with your desired color
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = ingredient.name,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}