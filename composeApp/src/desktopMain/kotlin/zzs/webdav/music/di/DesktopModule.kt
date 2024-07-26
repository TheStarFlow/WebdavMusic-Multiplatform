package zzs.webdav.music.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module
import zzs.webdav.music.data.db.RoomBuilderFactory
import zzs.webdav.music.data.db.createDataStore
import zzs.webdav.music.data.db.dataStoreFileName
import zzs.webdav.music.utils.logInfo
import java.io.File

actual fun platformModule() = module {
    single<DataStore<Preferences>> {
        createDataStore(
            producePath = {
                val path = File(System.getProperty("user.dir"), "DataStore").resolve(
                    dataStoreFileName
                ).absolutePath
                path
            }
        )
    }
    single { RoomBuilderFactory() }

}