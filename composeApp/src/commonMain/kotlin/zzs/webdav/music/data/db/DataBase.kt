package zzs.webdav.music.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

// shared/src/commonMain/kotlin/Database.kt

fun getRoomDatabase(
    factory: RoomBuilderFactory
): AppDatabase {
    val builder = factory.create()
    return builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .setDriver(BundledSQLiteDriver())
        .build()
}

expect class RoomBuilderFactory {
    fun create(): RoomDatabase.Builder<AppDatabase>
}