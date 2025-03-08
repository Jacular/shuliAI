package com.shuli.cc.app

import app.cash.sqldelight.db.SqlDriver

class Database {
     class DriverFactory {
         fun createDriver(): SqlDriver {
            return NativeSqliteDriver(
                schema = ChatDatabase.Schema,
                name = "chat.db"
            )
        }
    }
}
