package com.bekircaglar.qarko.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.navigation.Login
import com.bekircaglar.qarko.navigation.Register
import com.bekircaglar.qarko.presentation.auth.SignUpPrompt
import com.bekircaglar.qarko.presentation.auth.components.LoginOptionButton
import com.bekircaglar.qarko.presentation.cart.component.GenericTabRow
import com.bekircaglar.qarko.presentation.common.components.QButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.components.QTextField
import com.bekircaglar.qarko.presentation.common.components.TextCenterDivider
import com.bekircaglar.qarko.util.QarkoTypography
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.apple_logo_black
import qarko.composeapp.generated.resources.facebook_logo
import qarko.composeapp.generated.resources.google_logo
import qarko.composeapp.generated.resources.ic_email_filled
import qarko.composeapp.generated.resources.ic_lock_filled


@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable

fun LoginScreen(navController: NavController) {

    var selectedTabIndex by remember { mutableStateOf(0) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = white,
                    titleContentColor = black,
                    navigationIconContentColor = black
                ),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) {

        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(white)
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.google_logo),
                    modifier = Modifier.size(72.dp),
                    contentDescription = "Logo"
                )
            }

            Spacer(modifier = Modifier.size(24.dp))

            QText(
                text = "Hesabına Giriş Yap",
                textAlign = TextAlign.Center,
                textStyle = QarkoTypography.headlineLarge
            )

            GenericTabRow(
                tabTitles = listOf("E-posta", "Telefon Numarası"),
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index ->
                    selectedTabIndex = index
                },
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )

            when (selectedTabIndex) {
                0 -> { // E-posta
                    QTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "E-posta",
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_email_filled),
                                contentDescription = "Email Icon",
                                tint = gray,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        ),
                        modifier = Modifier.focusable(true)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    QTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Şifre",
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityChange = { passwordVisible = it },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_lock_filled),
                                contentDescription = "Şifre",
                                tint = gray,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

                    )
                }

                1 -> { // Telefon
                    QTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = "+90 000 000 00 00",
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(modifier = Modifier.size(14.dp))
                                Text(
                                    text = "🇹🇷",
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Phone Icon",
                                    tint = gray,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
            Spacer(modifier = Modifier.size(48.dp))

            QButton(
                buttonText = "Giriş Yap",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )

            TextCenterDivider(text = "Veya", modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoginOptionButton(
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.facebook_logo),
                            contentDescription = "Facebook Logo",
                            modifier = Modifier.size(24.dp)
                        )

                    },
                    onClick = {},
                    modifier = Modifier.width(80.dp)
                )

                LoginOptionButton(
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.google_logo),
                            contentDescription = "Facebook Logo",
                            modifier = Modifier.size(24.dp)
                        )

                    },
                    onClick = {},
                    modifier = Modifier.width(80.dp)
                )

                LoginOptionButton(
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.apple_logo_black),
                            contentDescription = "Facebook Logo",
                            colorFilter = ColorFilter.tint(black),
                            modifier = Modifier.size(24.dp)
                        )

                    },
                    onClick = {},
                    modifier = Modifier.width(80.dp)
                )
            }

            Spacer(modifier = Modifier.size(32.dp))

            SignUpPrompt {
                navController.navigate(Register) {
                    popUpTo<Login> { inclusive = true }
                }
            }

        }


    }

}