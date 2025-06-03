package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.darkGreen
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lightGray
import com.bekircaglar.qarko.lighterGray
import com.bekircaglar.qarko.navigation.Screen
import com.bekircaglar.qarko.presentation.cart.component.CardDetails
import com.bekircaglar.qarko.presentation.cart.component.CardPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.CartItem
import com.bekircaglar.qarko.presentation.cart.component.CartTabRow
import com.bekircaglar.qarko.presentation.cart.component.CashPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodRow
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodSheet
import com.bekircaglar.qarko.presentation.cart.component.PaymentSummaryComponent
import com.bekircaglar.qarko.presentation.cart.component.TableEntryCard
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.arrow_left
import qarko.composeapp.generated.resources.delete

@Composable
fun OrderButtonComponent(
    buttonText: String,
    isButtonEnabled: Boolean,
    onButtonClick: () -> Unit,
    topContent: @Composable () -> Unit,
    showWarning: Boolean,
    warningText: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Butonun üstündeki içerik (örneğin PaymentMethodRow)
        topContent()

        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primary),
            enabled = isButtonEnabled,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = buttonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

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