package com.bekircaglar.qarko.di

import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.FavoritesManager
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.manager.HistoryManager
import com.bekircaglar.qarko.data.repository.FoodRepository
import com.bekircaglar.qarko.data.repository.TenantRepository
import com.bekircaglar.qarko.domain.repository.IFoodRepository
import com.bekircaglar.qarko.domain.repository.ITenantRepository
import com.bekircaglar.qarko.domain.usecase.food.AddToCartUseCase
import com.bekircaglar.qarko.domain.usecase.food.GetFoodDetailsUseCase
import com.bekircaglar.qarko.domain.usecase.tenant.GetTenantMenuUseCase
import com.bekircaglar.qarko.domain.usecase.tenant.LoadTenantFromQRUseCase
import com.bekircaglar.qarko.presentation.food_detail.FoodDetailViewModel
import com.bekircaglar.qarko.presentation.profile.ProfileViewModel
import com.bekircaglar.qarko.presentation.tenant.TenantMenuViewModel
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf

val managerModule = module {
    single { UserManager }
    single { CartManager }
    single { FavoritesManager }
    single { TenantSession }
    single { HistoryManager }
    // Tip açıkça belirtilerek inference hatası giderildi
    single<Settings> { Settings() }
}

val repositoryModule = module {
    singleOf(::FoodRepository) bind IFoodRepository::class
    singleOf(::TenantRepository) bind ITenantRepository::class
}

val useCaseModule = module {
    factoryOf(::GetTenantMenuUseCase)
    factoryOf(::LoadTenantFromQRUseCase)
    factoryOf(::GetFoodDetailsUseCase)
    factoryOf(::AddToCartUseCase)
}

val viewModelModule = module {
    viewModelOf(::TenantMenuViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::FoodDetailViewModel)
}

val appModule = module {
    includes(managerModule, repositoryModule, useCaseModule, viewModelModule)
}
