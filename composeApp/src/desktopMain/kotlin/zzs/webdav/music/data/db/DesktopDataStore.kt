package zzs.webdav.music.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File


actual class RoomBuilderFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )
    }
}