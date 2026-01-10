package com.bekircaglar.qarko.presentation.search

import SearchTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.white
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_qr

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun SearchScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/search_not_found.json").decodeToString()
        )
    }

    Box() {
        Scaffold(
            topBar = {
                Column(
                    Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            SearchTextField(
                                text = searchText,
                                onValueChange = {
                                    searchText = it
                                },
                                placeholder = "Search",
                                modifier = Modifier.padding(all = 4.dp)
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                    )
                }
            },
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        ?: "",
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Filter", "Sort", "Category", "Date")) { chipText ->
                        FilterChip(
                            onClick = { /* Handle chip click */ },
                            label = { Text(chipText) },
                            selected = false
                        )
                    }
                }
                
                /*
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberLottiePainter(
                            composition = composition,
                            iterations = Compottie.IterateForever,
                        ),
                        modifier = Modifier.size(250.dp),
                        contentDescription = "Lottie animation"
                    )
                }
                */

            }


        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
                .padding(bottom = 24.dp)
        ) {
            FloatingActionButton(
                onClick = { navController.navigate(QRScan) },
                containerColor = primary,
                contentColor = white,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_qr),
                    contentDescription = "QR",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
