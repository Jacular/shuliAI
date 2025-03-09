package com.shuli.cc.app.data.local

/***
 * 数据库初始化
 */
class DatabaseProvider(
    driverFactory: DriverFactory
) {
    private val database: ChatDatabase by lazy {
        ChatDatabase(driverFactory.createDriver())
    }

    val chatDao: ChatDao by lazy {
        ChatDao(database.chatQueries)
    }
}