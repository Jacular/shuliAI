package di

import dao.PlatformSettings
import network.NetworkRepository
import org.koin.dsl.module

val provideRepositoryModule = module {
    single<NetworkRepository> { NetworkRepository(get(), get()) }
}