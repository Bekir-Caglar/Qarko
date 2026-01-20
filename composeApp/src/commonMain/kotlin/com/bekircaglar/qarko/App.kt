package com.bekircaglar.qarko

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.di.appModule
import com.bekircaglar.qarko.domain.repository.IAuthRepository
import com.bekircaglar.qarko.navigation.AppNavHost
import com.bekircaglar.qarko.presentation.common.theme.QarkoTheme
import com.bekircaglar.qarko.presentation.common.theme.QarkoThemeProvider
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

@Composable
@Preview
fun App() {
    KoinContext {
        val authRepository: IAuthRepository = koinInject()

        // Uygulama başladığında mevcut oturumu kontrol et
        LaunchedEffect(Unit) {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let {
                UserManager.updateUser(it)
            }
        }

        QarkoTheme {
            val navHost = rememberNavController()
            QarkoThemeProvider {
                AppNavHost(
                    navController = navHost,
                )
            }
        }
    }
}

/**
 * Koin'i başlatan ana fonksiyon.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(appModule)
    }
}
