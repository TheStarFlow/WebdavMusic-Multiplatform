package zzs.webdav.music.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import zzs.webdav.music.bean.ServerDesc


@Database(entities = [ServerDesc::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getServerDao(): ServerDao
}
