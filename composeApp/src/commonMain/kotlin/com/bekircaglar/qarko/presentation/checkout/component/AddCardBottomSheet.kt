package com.bekircaglar.qarko.presentation.checkout.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.data.model.CardBrand
import com.bekircaglar.qarko.data.model.SavedCard
import com.bekircaglar.qarko.presentation.common.components.QButton
import com.bekircaglar.qarko.presentation.common.components.QTextField
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.CreditCard
import kotlinx.datetime.Clock

/**
 * Kart numarası için VisualTransformation
 * 1234567890123456 -> 1234 5678 9012 3456
 */
class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(16)
        var out = ""
        
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 4) return offset
                if (offset <= 8) return offset + 1
                if (offset <= 12) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }
        
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

/**
 * Son kullanma tarihi için VisualTransformation
 * 1224 -> 12/24
 */
class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(4)
        var out = ""
        
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 && trimmed.length > 2) out += "/"
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                return offset + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                return offset - 1
            }
        }
        
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

/**
 * Kart ekleme Bottom Sheet bileşeni
 * DEV MODE: Şimdilik sadece Firebase'e kaydedilir, token sistemi sonra eklenecek
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardBottomSheet(
    onDismiss: () -> Unit,
    onSaveCard: (SavedCard) -> Unit,
    isLoading: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    // Raw değerler (sadece rakamlar)
    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    
    // Kart markasını BIN'den tahmin et
    val detectedBrand = remember(cardNumber) {
        detectCardBrand(cardNumber)
    }
    
    val isFormValid = cardNumber.length == 16 &&
            cardHolderName.isNotBlank() &&
            cardName.isNotBlank() &&
            expiryDate.length == 4 &&
            cvv.length >= 3

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Başlık
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = FeatherIcons.CreditCard,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Yeni Kart Ekle",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = darkBlue
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Kart Numarası
            Text(
                text = "Kart Numarası",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = darkBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            QTextField(
                value = cardNumber,
                onValueChange = { newValue ->
                    val digitsOnly = newValue.filter { it.isDigit() }
                    if (digitsOnly.length <= 16) {
                        cardNumber = digitsOnly
                    }
                },
                placeholder = "1234 5678 9012 3456",
                visualTransformation = CardNumberVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            
            // Kart markası göstergesi
            if (detectedBrand != CardBrand.UNKNOWN) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (detectedBrand) {
                        CardBrand.VISA -> "💳 VISA"
                        CardBrand.MASTERCARD -> "💳 Mastercard"
                        CardBrand.AMEX -> "💳 American Express"
                        CardBrand.TROY -> "💳 Troy"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Kart Sahibi Adı
            Text(
                text = "Kart Üzerindeki İsim",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = darkBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            QTextField(
                value = cardHolderName,
                onValueChange = { cardHolderName = it.uppercase() },
                placeholder = "AD SOYAD",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Son Kullanma ve CVV - Yan yana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Son Kullanma",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = darkBlue,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    QTextField(
                        value = expiryDate,
                        onValueChange = { newValue ->
                            val digitsOnly = newValue.filter { it.isDigit() }
                            if (digitsOnly.length <= 4) {
                                expiryDate = digitsOnly
                            }
                        },
                        placeholder = "AA/YY",
                        visualTransformation = ExpiryDateVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Right) }
                        )
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CVV",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = darkBlue,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    QTextField(
                        value = cvv,
                        onValueChange = { newValue ->
                            val digitsOnly = newValue.filter { it.isDigit() }
                            val maxLength = if (detectedBrand == CardBrand.AMEX) 4 else 3
                            if (digitsOnly.length <= maxLength) {
                                cvv = digitsOnly
                            }
                        },
                        placeholder = "123",
                        isPassword = false,
                        passwordVisible = false,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Kart İsmi (Opsiyonel)
            Text(
                text = "Kart İsmi",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = darkBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            QTextField(
                value = cardName,
                onValueChange = { cardName = it },
                placeholder = "örn: Maaş kartım",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Varsayılan Kart Toggle - Custom Design
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDefault) primary.copy(alpha = 0.1f) else lighterGray.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Varsayılan Kart",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = darkBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isDefault) "Bu kart ödemelerde öncelikli kullanılacak" else "Ödemelerde bu kartı varsayılan olarak kullan",
                        style = MaterialTheme.typography.labelSmall,
                        color = gray
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Custom Switch
                Switch(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = white,
                        checkedTrackColor = primary,
                        uncheckedThumbColor = white,
                        uncheckedTrackColor = lightGray,
                        uncheckedBorderColor = lightGray
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Kaydet Butonu
            QButton(
                onClick = {
                    if (isFormValid) {
                        val (month, year) = parseExpiry(expiryDate)
                        
                        val savedCard = SavedCard(
                            id = "", // Firebase'de oluşturulacak
                            userId = "", // ViewModel'de doldurulacak
                            cardName = cardName,
                            cardBrand = detectedBrand,
                            lastFourDigits = cardNumber.takeLast(4),
                            firstSixDigits = cardNumber.take(6),
                            expiryMonth = month,
                            expiryYear = year,
                            cardHolderName = cardHolderName,
                            isDefault = isDefault,
                            createdAt = Clock.System.now().toEpochMilliseconds(),
                            paymentToken = "" // DEV: Token sonra eklenecek
                        )
                        
                        onSaveCard(savedCard)
                    }
                },
                buttonText = if (isLoading) "Kaydediliyor..." else "Kartı Kaydet",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid && !isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Güvenlik Notu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        primary.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔒",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kart bilgileriniz 256-bit SSL ile şifrelenerek güvenle saklanır.",
                    style = MaterialTheme.typography.labelSmall,
                    color = darkBlue,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// Son kullanma tarihini parse et (raw digits: "1224" -> month=12, year=2024)
private fun parseExpiry(expiry: String): Pair<Int, Int> {
    return if (expiry.length == 4) {
        val month = expiry.take(2).toIntOrNull() ?: 0
        val year = (expiry.drop(2).toIntOrNull() ?: 0) + 2000
        Pair(month, year)
    } else {
        Pair(0, 0)
    }
}

// BIN numarasından kart markasını tahmin et
private fun detectCardBrand(cardNumber: String): CardBrand {
    if (cardNumber.isEmpty()) return CardBrand.UNKNOWN
    
    return when {
        // VISA: 4 ile başlar
        cardNumber.startsWith("4") -> CardBrand.VISA
        
        // Mastercard: 51-55 veya 2221-2720 ile başlar
        cardNumber.length >= 2 && cardNumber.substring(0, 2).toIntOrNull()?.let { it in 51..55 } == true -> CardBrand.MASTERCARD
        cardNumber.length >= 4 && cardNumber.substring(0, 4).toIntOrNull()?.let { it in 2221..2720 } == true -> CardBrand.MASTERCARD
        
        // AMEX: 34 veya 37 ile başlar
        cardNumber.startsWith("34") || cardNumber.startsWith("37") -> CardBrand.AMEX
        
        // Troy: 9792 ile başlar (Türkiye)
        cardNumber.startsWith("9792") -> CardBrand.TROY
        
        else -> CardBrand.UNKNOWN
    }
}
