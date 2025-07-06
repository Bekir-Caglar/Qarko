package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.presentation.common.components.QButton

@Composable
fun OrderButtonComponent(
    buttonText: String,
    isButtonEnabled: Boolean = true,
    onButtonClick: () -> Unit,
    topContent: @Composable () -> Unit,
    showWarning: Boolean = false,
    warningText: String = ""
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        topContent()

        QButton(
            onClick = onButtonClick,
            buttonText = buttonText,
            enabled = isButtonEnabled,
        )

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = showWarning,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) + slideInVertically(
                    initialOffsetY = { it }
                ) togetherWith (
                        fadeOut(animationSpec = tween(300)) + slideOutVertically(
                            targetOffsetY = { it }
                        ))
            },
            label = "TableWarning"
        ) { visible ->
            if (visible) {
                Text(
                    text = warningText,
                    fontSize = 12.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}