package zzs.webdav.music.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.get
import org.koin.dsl.module
import zzs.webdav.music.data.db.RoomBuilderFactory
import zzs.webdav.music.data.db.createDataStore
import zzs.webdav.music.data.db.dataStoreFileName

actual fun platformModule() = module {

    single<DataStore<Preferences>> {
        createDataStore {
            androidContext().filesDir.resolve(dataStoreFileName).absolutePath
        }
    }
    single<RoomBuilderFactory> { RoomBuilderFactory(context = androidContext())  }
}