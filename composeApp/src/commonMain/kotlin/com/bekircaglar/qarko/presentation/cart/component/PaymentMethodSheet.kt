package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.darkPrimary
import com.bekircaglar.qarko.data.model.Allergen
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lightGray
import com.bekircaglar.qarko.navigation.Screen
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.tenant.component.FoodItemCard
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white
import com.bekircaglar.qarko.yellow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.arrow_left
import qarko.composeapp.generated.resources.clock
import qarko.composeapp.generated.resources.favourite
import qarko.composeapp.generated.resources.location
import qarko.composeapp.generated.resources.more_horizontal
import kotlin.text.get


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodSheet(
    onDismiss: () -> Unit,
    onSave: (CardDetails) -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var saveCard by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Kart Bilgilerinizi Girin",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = cardNumber,
            onValueChange = {
                if (it.length <= 16) cardNumber = it
            },
            label = { Text("Kart Numarası") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = cardHolder,
            onValueChange = { cardHolder = it },
            label = { Text("Kart Üzerindeki İsim") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = expiryDate,
                onValueChange = {
                    if (it.length <= 5) {
                        expiryDate = it
                        if (it.length == 2 && !it.contains("/")) {
                            expiryDate = "$it/"
                        }
                    }
                },
                label = { Text("Son Kullanma Tarihi (AA/YY)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = cvv,
                onValueChange = { if (it.length <= 3) cvv = it },
                label = { Text("CVV") },
                modifier = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = saveCard,
                onCheckedChange = { saveCard = it }
            )
            Text("Kart bilgilerimi kaydet")
        }

        Button(
            onClick = {
                onSave(CardDetails(cardNumber, cardHolder, expiryDate, cvv, saveCard))
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = cardNumber.length == 16 && cardHolder.isNotEmpty() &&
                    expiryDate.length == 5 && cvv.length == 3
        ) {
            Text("Kaydet", fontSize = 16.sp)
        }
    }
}

data class CardDetails(
    val cardNumber: String,
    val cardHolder: String,
    val expiryDate: String,
    val cvv: String,
    val saveCard: Boolean
)