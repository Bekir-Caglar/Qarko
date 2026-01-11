package com.bekircaglar.qarko.presentation.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.darkPrimary
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.presentation.common.theme.white
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp
import compose.icons.feathericons.Check

data class CampaignItem(
    val id: String,
    val title: String,
    val description: String,
    val discountAmount: String,
    val isAvailable: Boolean = true,
    val conditionText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignSelectScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    var selectedCampaignId by remember { mutableStateOf<String?>(null) }
    var isCampaignCodeExpanded by remember { mutableStateOf(false) }
    var campaignCode by remember { mutableStateOf("") }

    // Kullanılabilir kampanyalar
    val availableCampaigns = listOf(
        CampaignItem(
            id = "1",
            title = "İlk Sipariş İndirimi",
            description = "İlk siparişinize özel %20 indirim",
            discountAmount = "20%",
            isAvailable = true
        ),
        CampaignItem(
            id = "2",
            title = "Hafta Sonu Keyfi",
            description = "Hafta sonu siparişlerinde 25₺ indirim",
            discountAmount = "25₺",
            isAvailable = true
        )
    )

    // Koşulu sağlamak gereken kampanyalar
    val unavailableCampaigns = listOf(
        CampaignItem(
            id = "3",
            title = "100₺ Üzeri Kargo Bedava",
            description = "Minimum 100₺ sipariş tutarına ulaşın",
            discountAmount = "Kargo Bedava",
            isAvailable = false,
            conditionText = "Sepet tutarınız: 75₺ (25₺ kaldı)"
        ),
        CampaignItem(
            id = "4",
            title = "3 Al 2 Öde",
            description = "Aynı kategoriden 3 ürün alın, en ucuzu bedava",
            discountAmount = "1 Ürün Bedava",
            isAvailable = false,
            conditionText = "Sepetinizde 2 ürün var (1 ürün daha ekleyin)"
        )
    )

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
        }
    ) { paddingValues ->
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
                                singleLine = true
                            )
                            Button(
                                onClick = { /* TODO: Apply code */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Text(
                                    text = "Uygula",
                                    color = white,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            // Kullanılabilir Kampanyalar
            CampaignSection(
                title = "Kullanabileceğin Kampanyalar",
                campaigns = availableCampaigns,
                selectedCampaignId = selectedCampaignId,
                onCampaignSelect = { id -> selectedCampaignId = id }
            )

            // Koşulu Sağlamak Gereken Kampanyalar
            CampaignSection(
                title = "Koşulu Sağlamak Gereken Kampanyalar",
                campaigns = unavailableCampaigns,
                selectedCampaignId = selectedCampaignId,
                onCampaignSelect = { /* Seçilemez */ },
                isDisabled = true
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Apply Button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary
                ),
                enabled = selectedCampaignId != null || campaignCode.isNotEmpty()
            ) {
                Text(
                    text = if (selectedCampaignId != null) "Kampanyayı Uygula" else "Kodu Uygula",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = white
                )
            }
        }
    }
}

@Composable
private fun CampaignSection(
    title: String,
    campaigns: List<CampaignItem>,
    selectedCampaignId: String?,
    onCampaignSelect: (String) -> Unit,
    isDisabled: Boolean = false
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

        campaigns.forEachIndexed { index, campaign ->
            CampaignItemRow(
                campaign = campaign,
                isSelected = selectedCampaignId == campaign.id,
                onSelect = { if (!isDisabled) onCampaignSelect(campaign.id) },
                isDisabled = isDisabled
            )

            if (index < campaigns.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = gray.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun CampaignItemRow(
    campaign: CampaignItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isDisabled: Boolean = false
) {
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
                    text = campaign.discountAmount,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDisabled) gray else primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = campaign.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDisabled) gray else black
                )
                Text(
                    text = campaign.description,
                    fontSize = 12.sp,
                    color = gray
                )
                if (campaign.conditionText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = campaign.conditionText,
                        fontSize = 11.sp,
                        color = primary,
                        fontWeight = FontWeight.Medium
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

