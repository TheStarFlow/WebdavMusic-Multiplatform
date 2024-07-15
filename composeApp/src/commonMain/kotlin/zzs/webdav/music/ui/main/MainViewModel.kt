package zzs.webdav.music.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zzs.webdav.music.base.BaseViewModel
import zzs.webdav.music.base.Loading
import zzs.webdav.music.base.Success
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.data.repository.WebdavRepository
import zzs.webdav.music.utils.logInfo

class MainViewModel(private val repository: WebdavRepository) :
    BaseViewModel<MainState, MainIntent>() {


    init {
        sendUiIntent(MainIntent.FetchServer)
    }

    override fun initUiState(): MainState {
        return MainState()
    }

    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.FetchServer -> {
                suspend {
                    repository.fetchCacheServer()
                }.execute(Dispatchers.IO) {
                    logInfo(it.toString())
                    if (it is Success) {
                        copy(currServer = it)
                    } else {
                        this
                    }
                }
            }

            else -> {

            }
        }
    }

    fun modifyServer(
        serverDesc: ServerDesc,
        insert: Boolean,
        updateCurr: Boolean,
        oriServer: ServerDesc? = null
    ) {
        if (updateCurr) {
            setState {
                copy(currServer = Loading())
            }
        }
        if (insert) {
            suspend {
                repository.insertServer(serverDesc)
            }.execute(Dispatchers.IO) {
                if (updateCurr) {
                    viewModelScope.launch {
                        repository.setCurrServerKey(serverDesc)
                    }
                    copy(currServer = Success(serverDesc))
                } else {
                    this
                }
            }
        } else {
            suspend {
                if (oriServer != null) {
                    repository.deleteServer(oriServer)
                }
                repository.insertServer(serverDesc)
            }.execute(Dispatchers.IO) {
                if (it is Success || !updateCurr) {
                    viewModelScope.launch {
                        repository.setCurrServerKey(serverDesc)
                    }
                    copy(currServer = Success(serverDesc))
                } else {
                    this
                }

            }
        }
    }
}