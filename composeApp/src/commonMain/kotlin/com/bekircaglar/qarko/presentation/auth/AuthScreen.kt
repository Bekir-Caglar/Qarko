package com.bekircaglar.qarko.presentation.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.navigation.Login
import com.bekircaglar.qarko.navigation.Register
import com.bekircaglar.qarko.presentation.auth.components.LoginOptionButton
import com.bekircaglar.qarko.presentation.common.components.QButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.components.TextCenterDivider
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.util.QarkoTypography
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.apple_logo_black
import qarko.composeapp.generated.resources.facebook_logo
import qarko.composeapp.generated.resources.google_logo
import qarko.composeapp.generated.resources.login_ils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController
) {
    Scaffold(
        bottomBar = {
//            AppBottomBar(
//                navController = navController,
//                currentRoute = Screen.Profile.route,
//            )
        },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = white,
                    navigationIconContentColor = black
                )
            )
        },
        containerColor = white
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.login_ils),
                contentDescription = "App Logo",
                modifier = Modifier.size(250.dp).offset(y = (-50).dp)
            )

            QText(
                text = "Tüm Özellikleri Keşfedin",
                textStyle = QarkoTypography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-50).dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginOptionButton(
                    text = "Facebook ile Giriş Yap",
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.facebook_logo),
                            contentDescription = "logo",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.size(12.dp))

                LoginOptionButton(
                    text = "Google ile Giriş Yap",
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.google_logo),
                            contentDescription = "logo",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.size(12.dp))

                LoginOptionButton(
                    text = "Apple ile Giriş Yap",
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.apple_logo_black),
                            contentDescription = "logo",
                            colorFilter = ColorFilter.tint(black),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextCenterDivider(text = "Veya", modifier = Modifier.offset(y = (-20).dp).padding(vertical = 16.dp))

            QButton(
                buttonText = "Giriş Yap",
                onClick = { navController.navigate(Login) },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)

            )

            SignUpPrompt {
                navController.navigate(Register)
            }

        }
    }
}


@Composable
fun SignUpPrompt(onSignUpClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        // Gri metin
        withStyle(style = SpanStyle(color = gray)) {
            append("Hesabınız yok mu? ")
        }

        // Mavi ve kalın tıklanabilir metin
        pushStringAnnotation(tag = "SIGN_UP", annotation = "sign_up")
        withStyle(style = SpanStyle(color = primary, fontWeight = FontWeight.Bold)) {
            append("Kayıt ol")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "SIGN_UP", start = offset, end = offset)
                .firstOrNull()?.let {
                    onSignUpClick()
                }
        },
        style = QarkoTypography.bodyMedium.copy(color = gray),
    )
}

@Composable
fun SignInPrompt(onSignInClick: () -> Unit) {

    val anototatedText = buildAnnotatedString {
        // Gri metin
        withStyle(style = SpanStyle(color = gray)) {
            append("Zaten hesabınız var mı? ")
        }

        // Mavi ve kalın tıklanabilir metin
        pushStringAnnotation(tag = "SIGN_IN", annotation = "sign_in")
        withStyle(style = SpanStyle(color = primary, fontWeight = FontWeight.Bold)) {
            append("Giriş yap")
        }
        pop()
    }

    ClickableText(
        text = anototatedText,
        onClick = { offset ->
            anototatedText.getStringAnnotations(tag = "SIGN_IN", start = offset, end = offset)
                .firstOrNull()?.let {
                    onSignInClick()
                }
        },
        style = QarkoTypography.bodyMedium.copy(color = gray),
    )


}
