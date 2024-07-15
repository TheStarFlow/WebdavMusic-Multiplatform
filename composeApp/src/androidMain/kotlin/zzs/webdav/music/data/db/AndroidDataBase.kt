package zzs.webdav.music.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.compose.koinInject


actual class RoomBuilderFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("my_room.db")
        return Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}