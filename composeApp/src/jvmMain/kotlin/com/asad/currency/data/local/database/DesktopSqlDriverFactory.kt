package com.asad.currency.data.local.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class SqlDriverFactory actual constructor(context: Any?) {
    actual fun getSqlDriver(): SqlDriver {
        return JdbcSqliteDriver(
            "jdbc:sqlite:CurrencyDatabase.db"
        )
    }
}