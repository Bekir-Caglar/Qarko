package com.bekircaglar.qarko.presentation.tenant.component

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.Minus
import compose.icons.feathericons.Plus
import kotlinx.datetime.Month

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FoodItemCard(
    item: FoodItem,
    quantity: Int = 0,
    onClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onRemoveFromCartClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = white,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            Surface(
                modifier = Modifier.size(110.dp),
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 2.dp
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Food Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp), // Match image height
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Name and Info
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    QText(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = darkBlue,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    QText(
                        text = item.info,
                        color = gray,
                        fontSize = 14.sp,
                        maxLines = 2,
                        lineHeight = 18.sp
                    )
                }

                // Bottom section: Price and Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QText(
                        text = item.price,
                        color = primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp
                    )

                    // Stepper Control: [+] [Sayı] [-]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // Plus Button (Small & Compact)
                        Surface(
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                            color = primary,
                            shadowElevation = 4.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onAddToCartClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = FeatherIcons.Plus,
                                    contentDescription = "Ekle",
                                    tint = white,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Sağa doğru açılan Miktar ve Eksi butonu
                        AnimatedVisibility(
                            visible = quantity > 0,
                            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Quantity Text
                                QText(
                                    text = quantity.toString(),
                                    color = darkBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )

                                // Minus Button (Small & Compact)
                                Surface(
                                    modifier = Modifier.size(30.dp),
                                    shape = CircleShape,
                                    color = primary,
                                    shadowElevation = 4.dp
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable { onRemoveFromCartClick() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = FeatherIcons.Minus,
                                            contentDescription = "Azalt",
                                            tint = white,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
