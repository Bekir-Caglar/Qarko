package com.bekircaglar.qarko.presentation.auth.register

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.navigation.Screen
import com.bekircaglar.qarko.presentation.auth.SignInPrompt
import com.bekircaglar.qarko.presentation.auth.components.LoginOptionButton
import com.bekircaglar.qarko.presentation.common.components.QButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.components.QTextField
import com.bekircaglar.qarko.presentation.common.components.TextCenterDivider
import com.bekircaglar.qarko.util.QarkoTypography
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.apple_logo_black
import qarko.composeapp.generated.resources.email
import qarko.composeapp.generated.resources.facebook_logo
import qarko.composeapp.generated.resources.google_logo
import qarko.composeapp.generated.resources.lock_filled
import qarko.composeapp.generated.resources.profile_filled


@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun RegisterScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
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
        val verticalScrollState = rememberScrollState()
        val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(white)
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(state = verticalScrollState, enabled = true),
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
                text = "Yeni Hesap Oluştur",
                textAlign = TextAlign.Center,
                textStyle = QarkoTypography.headlineLarge
            )

            Spacer(modifier = Modifier.size(24.dp))

            QTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "İsim ve Soyisim",
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.profile_filled),
                        contentDescription = "Profil",
                        tint = gray,
                        modifier = Modifier.size(22.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                ),

            )

            Spacer(modifier = Modifier.size(12.dp))

            QTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "E-posta",
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.email),
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
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                ),

            )

            Spacer(modifier = Modifier.size(12.dp))

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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
            )

            Spacer(modifier = Modifier.size(12.dp))

            QTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Şifre",
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = it },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.lock_filled),
                        contentDescription = "Şifre",
                        tint = gray,
                        modifier = Modifier.size(22.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

            )

            Spacer(modifier = Modifier.size(32.dp))

            QButton(
                buttonText = "Kayıt Ol",
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

            SignInPrompt {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }

        }


    }

}