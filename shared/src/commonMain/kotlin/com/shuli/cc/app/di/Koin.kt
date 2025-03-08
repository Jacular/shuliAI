package com.shuli.cc.app.di

import com.shuli.cc.app.data.local.ChatDatabase
import org.koin.dsl.module

class Koin {
    val appModule = module {
        single<DriverFactory> {
            // 各平台实现见上文
            DriverFactory()
        }

        single<ChatDatabase> {
            ChatDatabase(get<DriverFactory>().createDriver())
        }

        single<ChatRepository> {
            ChatRepositoryImpl(get(), get(), get())
        }

        single { ContextManager(get()) }

        viewModel { params ->
            ChatViewModel(get(), get())
        }
    }
}