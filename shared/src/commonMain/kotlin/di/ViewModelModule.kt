package di

import org.koin.dsl.module
import viewmodel.ChatViewModel
import viewmodel.HomeViewModel

val provideviewModelModule = module {
    single {
        HomeViewModel(get())
    }
    single {
        ChatViewModel(get())
    }
}