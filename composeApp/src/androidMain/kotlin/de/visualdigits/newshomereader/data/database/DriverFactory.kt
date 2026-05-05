package de.visualdigits.newshomereader.data.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import de.visualdigits.newshomereader.SettingsDatabase

actual class DriverFactory(
    private val context: Context
) {
    private val dbName = "settings.db"

    actual fun createDriver(basePath: String): SqlDriver {
        // todo - remember to remove this later
//        context.deleteDatabase(dbName)

        return AndroidSqliteDriver(
            schema = SettingsDatabase.Schema,
            context = context,
            name = dbName,
            callback = object : AndroidSqliteDriver.Callback(SettingsDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }
}
