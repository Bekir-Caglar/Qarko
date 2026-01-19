package com.bekircaglar.qarko.presentation.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.data.model.Campaign
import com.bekircaglar.qarko.data.model.CampaignType
import com.bekircaglar.qarko.navigation.FoodDetail
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.darkPrimary
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.presentation.common.theme.white
import com.bekircaglar.qarko.data.manager.TenantSession
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignSelectScreen(
    navController: NavController,
    viewModel: CheckoutViewModel
) {
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()
    var isCampaignCodeExpanded by remember { mutableStateOf(false) }
    var campaignCode by remember { mutableStateOf("") }

    // Ekran her açıldığında veya geri gelindiğinde kampanyaları yeniden doğrula
    LaunchedEffect(Unit) {
        viewModel.loadCampaigns()
    }

    Scaffold(
        containerColor = surfaceGray,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = white,
                ),
                title = {
                    QText(
                        text = "Kampanya Seç",
                        fontSize = 18.sp,
                        color = black,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    BackButton { navController.popBackStack() }
                }
            )
        },
        bottomBar = {
            if (!uiState.isCampaignsLoading) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = white
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primary
                            )
                        ) {
                            Text(
                                text = "Tamam",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isCampaignsLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Kampanya Kodun Var Mı?
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(12.dp))
                        .background(white, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCampaignCodeExpanded = !isCampaignCodeExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kampanya Kodun Var Mı?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkPrimary
                        )
                        Icon(
                            imageVector = if (isCampaignCodeExpanded) FeatherIcons.ChevronUp else FeatherIcons.ChevronDown,
                            contentDescription = null,
                            tint = gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedVisibility(visible = isCampaignCodeExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = campaignCode,
                                    onValueChange = { campaignCode = it },
                                    placeholder = {
                                        Text(
                                            text = "Kampanya kodu girin",
                                            color = gray,
                                            fontSize = 14.sp
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = primary,
                                        unfocusedBorderColor = gray.copy(alpha = 0.7f),
                                        focusedContainerColor = lighterGray.copy(alpha = 0.2f),
                                        unfocusedContainerColor = lighterGray.copy(alpha = 0.2f)
                                    ),
                                    singleLine = true,
                                    isError = uiState.promoCodeError != null
                                )
                                Button(
                                    onClick = { viewModel.applyPromoCode(campaignCode) },
                                    enabled = campaignCode.isNotEmpty() && !uiState.isPromoCodeLoading,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    if (uiState.isPromoCodeLoading) {
                                        CircularProgressIndicator(color = white, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(
                                            text = "Uygula",
                                            color = white,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                            if (uiState.promoCodeError != null) {
                                Text(
                                    text = uiState.promoCodeError!!,
                                    color = primary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Kullanılabilir Kampanyalar
                if (uiState.availableCampaigns.isNotEmpty()) {
                    CampaignSection(
                        title = "Kullanabileceğin Kampanyalar",
                        campaigns = uiState.availableCampaigns,
                        selectedCampaignId = uiState.selectedCampaign?.id,
                        onCampaignSelect = { campaign -> viewModel.selectCampaign(campaign) },
                        navController = navController
                    )
                }

                // Koşulu Sağlamak Gereken Kampanyalar
                if (uiState.otherCampaigns.isNotEmpty()) {
                    CampaignSection(
                        title = "Koşulu Sağlamak Gereken Kampanyalar",
                        campaignsWithReason = uiState.otherCampaigns,
                        selectedCampaignId = null,
                        onCampaignSelect = { /* Seçilemez */ },
                        isDisabled = true,
                        navController = navController
                    )
                }

                if (uiState.availableCampaigns.isEmpty() && uiState.otherCampaigns.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Şu an aktif kampanya bulunmuyor.",
                            color = gray,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CampaignSection(
    title: String,
    campaigns: List<Campaign> = emptyList(),
    campaignsWithReason: List<Pair<Campaign, String>> = emptyList(),
    selectedCampaignId: String?,
    onCampaignSelect: (Campaign) -> Unit,
    isDisabled: Boolean = false,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .background(white, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = darkPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (campaigns.isNotEmpty()) {
            campaigns.forEachIndexed { index, campaign ->
                val conditionText = if (campaign.conditions.minOrderAmount != null && campaign.conditions.minOrderAmount!! > 0) {
                    "₺${campaign.conditions.minOrderAmount!!.toInt()} ve üzeri siparişlerde geçerli"
                } else null

                CampaignItemRow(
                    campaign = campaign,
                    isSelected = selectedCampaignId == campaign.id,
                    onSelect = { if (!isDisabled) onCampaignSelect(campaign) },
                    isDisabled = isDisabled,
                    conditionText = conditionText,
                    onReasonClick = { 
                        if (campaign.type == CampaignType.FREE_ITEM && campaign.conditions.freeItemId != null) {
                            val allMenuItems = TenantSession.menuItems
                            val item = allMenuItems.find { it.id == campaign.conditions.freeItemId }
                            if (item != null) {
                                navController.navigate(FoodDetail.fromFoodItem(item))
                            }
                        }
                    }
                )

                if (index < campaigns.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = gray.copy(alpha = 0.1f)
                    )
                }
            }
        } else {
            campaignsWithReason.forEachIndexed { index, pair ->
                val (campaign, reason) = pair
                CampaignItemRow(
                    campaign = campaign,
                    isSelected = false,
                    onSelect = { },
                    isDisabled = isDisabled,
                    conditionText = reason,
                    onReasonClick = {
                        if (reason.contains("Hediye ürünü sepetinize eklemelisiniz") && campaign.conditions.freeItemId != null) {
                            val allMenuItems = TenantSession.menuItems
                            val item = allMenuItems.find { it.id == campaign.conditions.freeItemId }
                            if (item != null) {
                                navController.navigate(FoodDetail.fromFoodItem(item))
                            }
                        }
                    }
                )

                if (index < campaignsWithReason.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = gray.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CampaignItemRow(
    campaign: Campaign,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isDisabled: Boolean = false,
    conditionText: String? = null,
    onReasonClick: () -> Unit = {}
) {
    val discountText = when (campaign.type) {
        CampaignType.PERCENTAGE_DISCOUNT -> "%${campaign.discountValue.toInt()}"
        CampaignType.FIXED_DISCOUNT -> "₺${campaign.discountValue.toInt()}"
        CampaignType.BUY_X_GET_Y -> "${campaign.conditions.buyQuantity} Al ${campaign.conditions.getQuantity} Öde"
        CampaignType.FREE_ITEM -> "Bedava Ürün"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) primary.copy(alpha = 0.1f) else lighterGray.copy(alpha = 0.2f))
            .clickable(enabled = !isDisabled, onClick = onSelect)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = discountText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDisabled) gray else primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = campaign.title.ifEmpty { campaign.name },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDisabled) gray else black
                )
                Text(
                    text = campaign.description,
                    fontSize = 12.sp,
                    color = gray
                )
                if (conditionText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val isClickableReason = conditionText.contains("Hediye ürünü sepetinize eklemelisiniz")
                    
                    Text(
                        text = conditionText,
                        fontSize = 11.sp,
                        color = if (isClickableReason) primary else (if (isDisabled) gray else primary),
                        fontWeight = if (isClickableReason) FontWeight.Bold else FontWeight.Medium,
                        textDecoration = if (isClickableReason) TextDecoration.Underline else null,
                        modifier = Modifier.clickable(enabled = isClickableReason) { onReasonClick() }
                    )
                }
            }
        }

        if (!isDisabled) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = primary,
                    unselectedColor = gray
                )
            )
        }
    }
}
