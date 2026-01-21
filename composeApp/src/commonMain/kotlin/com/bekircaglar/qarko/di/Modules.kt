package com.bekircaglar.qarko.di

import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.FavoritesManager
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.manager.HistoryManager
import com.bekircaglar.qarko.data.repository.AuthRepository
import com.bekircaglar.qarko.data.repository.CampaignRepository
import com.bekircaglar.qarko.data.repository.FoodRepository
import com.bekircaglar.qarko.data.repository.OrderRepository
import com.bekircaglar.qarko.data.repository.TenantRepository
import com.bekircaglar.qarko.domain.repository.IAuthRepository
import com.bekircaglar.qarko.domain.repository.ICampaignRepository
import com.bekircaglar.qarko.domain.repository.IFoodRepository
import com.bekircaglar.qarko.domain.repository.IOrderRepository
import com.bekircaglar.qarko.domain.repository.ITenantRepository
import com.bekircaglar.qarko.domain.usecase.food.AddToCartUseCase
import com.bekircaglar.qarko.domain.usecase.food.GetFoodDetailsUseCase
import com.bekircaglar.qarko.domain.usecase.order.CreateOrderUseCase
import com.bekircaglar.qarko.domain.usecase.tenant.GetTenantMenuUseCase
import com.bekircaglar.qarko.domain.usecase.tenant.LoadTenantFromQRUseCase
import com.bekircaglar.qarko.presentation.auth.login.LoginViewModel
import com.bekircaglar.qarko.presentation.auth.register.RegisterViewModel
import com.bekircaglar.qarko.presentation.campaign.CampaignViewModel
import com.bekircaglar.qarko.presentation.cart.CartViewModel
import com.bekircaglar.qarko.presentation.checkout.CheckoutViewModel
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
    single<Settings> { Settings() }
}

val repositoryModule = module {
    singleOf(::AuthRepository) bind IAuthRepository::class
    singleOf(::FoodRepository) bind IFoodRepository::class
    singleOf(::TenantRepository) bind ITenantRepository::class
    singleOf(::OrderRepository) bind IOrderRepository::class
    singleOf(::CampaignRepository) bind ICampaignRepository::class
}

val useCaseModule = module {
    factoryOf(::GetTenantMenuUseCase)
    factoryOf(::LoadTenantFromQRUseCase)
    factoryOf(::GetFoodDetailsUseCase)
    factoryOf(::AddToCartUseCase)
    factoryOf(::CreateOrderUseCase)
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::TenantMenuViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::FoodDetailViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::CampaignViewModel)
}

val appModule = module {
    includes(managerModule, repositoryModule, useCaseModule, viewModelModule)
}
