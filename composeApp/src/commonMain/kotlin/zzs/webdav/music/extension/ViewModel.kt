package zzs.webdav.music.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.java.KoinJavaComponent.get
import zzs.webdav.music.data.repository.WebdavRepository
import zzs.webdav.music.ui.main.MainViewModel
import kotlin.reflect.KClass

val sDefaultFactory = WebdavViewModelFactory()


class WebdavViewModelFactory : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        val repository: WebdavRepository = get(WebdavRepository::class.java)
        when {
            modelClass == MainViewModel::class -> {
                return MainViewModel(repository = repository) as T
            }
        }
        return super.create(modelClass, extras)
    }

}