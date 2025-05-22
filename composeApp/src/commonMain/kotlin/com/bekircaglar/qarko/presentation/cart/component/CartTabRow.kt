package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CartTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .height(48.dp)
            .background(
                color = gray.copy(alpha = 0.2f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
    ) {
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> -width } + fadeIn() with
                            slideOutHorizontally { width -> width } + fadeOut()
                } else {
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                }
            },
            label = "TabSlide"
        ) { tabIndex ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { onTabSelected(0) },
                    selectedContentColor = primary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(3.dp)
                        .background(
                            if (tabIndex == 0) primary else Color.Transparent,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(13.dp)
                        )
                        .padding(end = 4.dp)
                        .height(48.dp),
                ) {
                    Text(
                        text = "Kart ile öde",
                        fontSize = 16.sp,
                        color = if (tabIndex == 0) white else gray
                    )
                }
                Tab(
                    selected = tabIndex == 1,
                    onClick = { onTabSelected(1) },
                    selectedContentColor = primary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(3.dp)
                        .background(
                            if (tabIndex == 1) primary else Color.Transparent,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(13.dp)
                        )
                        .padding(start = 4.dp)
                        .height(48.dp),
                ) {
                    Text(
                        text = "Kasada öde",
                        fontSize = 16.sp,
                        color = if (tabIndex == 1) white else gray
                    )
                }
            }
        }
    }
}