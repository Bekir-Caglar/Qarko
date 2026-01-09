package com.bekircaglar.qarko.presentation.tenant.component
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.presentation.common.theme.darkBlue
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_plus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodItemCard(
    item: FoodItem,
    onClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(120.dp)
            .background(white, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                QText(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = darkBlue,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
                Spacer(Modifier.height(4.dp))
                QText(
                    text = item.info,
                    color = gray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(Modifier.weight(1f))
                QText(
                    text = item.price,
                    color = darkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Box {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .zIndex(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(8.dp),
                                clip = true
                            )
                            .background(
                                Color(0xFFd1eef0),
                                RoundedCornerShape(8.dp)
                            )
                            .size(28.dp)
                            .clip(shape = RoundedCornerShape(8.dp))
                            .combinedClickable(onClick = onAddToCartClick)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_plus),
                            contentDescription = "Sepete Ekle",
                            tint = primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(84.dp)
                        .padding(4.dp)
                        .shadow(
                            2.dp,
                            shape = RoundedCornerShape(16.dp),
                        )
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}