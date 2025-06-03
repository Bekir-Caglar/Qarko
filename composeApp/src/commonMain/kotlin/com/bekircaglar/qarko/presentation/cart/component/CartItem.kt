package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.darkGreen
import com.bekircaglar.qarko.darkPrimary
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lightBlue
import com.bekircaglar.qarko.lighterGray
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.add
import qarko.composeapp.generated.resources.delete
import qarko.composeapp.generated.resources.minus

@Composable
fun CartItem(
    imageUrl: String,
    name: String,
    description: String,
    price: Double,
    quantity: Int,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemove: () -> Unit
) {
    var isNoteVisible by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                modifier = Modifier
                    .height(70.dp)
                    .width(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = black
                )

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = gray
                )

                Spacer(modifier = Modifier.size(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₺$price",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )

                    OutlinedButton(
                        onClick = { isNoteVisible = !isNoteVisible },
                        modifier = Modifier.height(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors().copy(
                            containerColor = white,
                            contentColor = primary
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Not Ekle",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (note.isNotEmpty()) "Not Düzenle" else "Not Ekle",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                IconButton(
                    onClick = onDecreaseQuantity,
                    modifier = Modifier
                        .size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(if (quantity > 1) Res.drawable.minus else Res.drawable.delete),
                        contentDescription = if (quantity > 1) "Azalt" else "Sil",
                        tint = black,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = black
                )

                IconButton(
                    onClick = onIncreaseQuantity,
                    modifier = Modifier
                        .size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = "Artır",
                        tint = black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isNoteVisible,
            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Sipariş notu ekleyin...") },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedContainerColor = white,
                        unfocusedTextColor = black,
                        focusedIndicatorColor = darkPrimary,
                        unfocusedIndicatorColor = darkPrimary,
                        cursorColor = primary,
                        focusedLabelColor = darkPrimary,
                        unfocusedLabelColor = gray,
                        textSelectionColors = TextSelectionColors(
                            handleColor = primary,
                            backgroundColor = lightBlue
                        )
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { isNoteVisible = false }
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

            }
        }
    }
}