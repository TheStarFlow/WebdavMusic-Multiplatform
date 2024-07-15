package zzs.webdav.music.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.sardine.SardineFactory
import org.koin.core.module.Module
import org.koin.dsl.module
import zzs.webdav.music.data.db.AppDatabase
import zzs.webdav.music.data.db.ServerDao
import zzs.webdav.music.data.db.getRoomDatabase
import zzs.webdav.music.data.repository.NetWorkWebdavRepository
import zzs.webdav.music.data.repository.WebdavRepository
import zzs.webdav.music.ui.main.MainViewModel

expect fun platformModule(): Module

fun commonModule() = module {

    single { SardineFactory.begin() }
    single<AppDatabase> { getRoomDatabase(factory = get()) }
    single<ServerDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.getServerDao()
    }
    single<WebdavRepository> {
        NetWorkWebdavRepository(
            sardine = get(),
            serverDao = get(),
            dataStore = get(),
        )
    }

    single {
        MainViewModel(repository = get())
    }
}