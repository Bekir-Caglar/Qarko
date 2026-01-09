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
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.white

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GenericTabRow(
    tabTitles: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedTabColor: Color = primary,
    unselectedTabColor: Color = Color.Transparent,
    selectedTextColor: Color = white,
    unselectedTextColor: Color = gray,
    backgroundColor: Color = gray.copy(alpha = 0.2f),
    cornerRadius: Int = 16,
    horizontalPadding: Int = 32
) {
    Row(
        modifier = modifier
            .padding(horizontal = horizontalPadding.dp)
            .height(48.dp)
            .background(
                color = backgroundColor,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius.dp)
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
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { onTabSelected(index) },
                        selectedContentColor = selectedTabColor,
                        modifier = Modifier
                            .weight(1f)
                            .padding(3.dp)
                            .background(
                                if (tabIndex == index) selectedTabColor else unselectedTabColor,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape((cornerRadius - 3).dp)
                            )
                            .padding(
                                start = if (index == 0) 0.dp else 4.dp,
                                end = if (index == tabTitles.lastIndex) 0.dp else 4.dp
                            )
                            .height(48.dp),
                    ) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            color = if (tabIndex == index) selectedTextColor else unselectedTextColor
                        )
                    }
                }
            }
        }
    }
}