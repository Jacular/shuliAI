package com.shuli.cc.app.di

import com.shuli.cc.app.data.local.ChatDatabase
import org.koin.dsl.module

val appModule = module {
    // 数据库
    single<DriverFactory> { DriverFactory() }
    single<ChatDatabase> { ChatDatabase(get<DriverFactory>().createDriver()) }
    single<ChatDao> { get<ChatDatabase>().chatQueries }

    // 网络
    single { ApiClient() }

    // Repository
    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get()) }

    // ViewModel
    viewModel { ChatViewModel() }
}

fun initKoin() = initKoin {
    modules(appModule)
}