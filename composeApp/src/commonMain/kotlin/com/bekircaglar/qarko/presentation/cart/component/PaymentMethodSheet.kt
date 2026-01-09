package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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