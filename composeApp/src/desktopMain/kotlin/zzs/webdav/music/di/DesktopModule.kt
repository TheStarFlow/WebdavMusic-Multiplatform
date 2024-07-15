package zzs.webdav.music.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module
import zzs.webdav.music.data.db.RoomBuilderFactory
import zzs.webdav.music.data.db.createDataStore

actual fun platformModule() = module {
    single<DataStore<Preferences>> {
        createDataStore(
            producePath = {
                System.getProperty("user.dir")
            }
        )
    }
    single { RoomBuilderFactory() }
}