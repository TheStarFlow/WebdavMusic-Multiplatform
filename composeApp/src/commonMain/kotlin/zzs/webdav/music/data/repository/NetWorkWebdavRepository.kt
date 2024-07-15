package zzs.webdav.music.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.sardine.DavResource
import com.github.sardine.Sardine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import org.apache.commons.logging.Log
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import zzs.webdav.music.bean.PlayMode
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.bean.generateKey
import zzs.webdav.music.bean.isValidHttpUrl
import zzs.webdav.music.data.db.ServerDao
import zzs.webdav.music.utils.LrcUtils
import zzs.webdav.music.utils.credentials

class NetWorkWebdavRepository(
    private val sardine: Sardine,
    private val serverDao: ServerDao,
    private val dataStore: DataStore<Preferences>,
) : WebdavRepository {

    companion object {
        val KEY_PLAY_MODE = intPreferencesKey("key_play_mode")
        val BING_PIC_KEY = stringPreferencesKey("key_bing_pic")
        val CURR_SERVER = stringPreferencesKey("curr_server")
    }


    override suspend fun setCurrServerKey(serverDesc: ServerDesc) {
        dataStore.edit { setting ->
            setting[CURR_SERVER] = serverDesc.generateKey() ?: ""
        }

    }


    override suspend fun fetchDavResList(serverDesc: ServerDesc): List<DavResource> {
        sardine.setCredentials(serverDesc.user, serverDesc.password)
        val basicPath =
            if (isValidHttpUrl(serverDesc.wholeUrl))
                serverDesc.wholeUrl
            else
                "http://${serverDesc.ip}:${serverDesc.port}"
        var path = basicPath
        if (serverDesc.targetPath?.isNotBlank() == true && !isValidHttpUrl(serverDesc.wholeUrl)) {
            path = basicPath + serverDesc.targetPath
        }
        return sardine.list(path).drop(1)
    }

    override suspend fun fetchCacheServer(): ServerDesc? {
        val serverList = serverDao.loadAll()
        when {
            serverList.isEmpty() -> return null
            serverList.size == 1 -> {
                val first = serverList.first()
                dataStore.edit {
                    it[CURR_SERVER] = first.generateKey()?:""
                }
                return first
            }

            else -> {
                val keyFlow = dataStore.data.map {
                    it[CURR_SERVER]
                }
                val key = keyFlow.first()?:""
                val result = serverList.find {
                    it.generateKey() == key
                }
                return result
            }
        }
    }

    override suspend fun fetchCacheServerList() = serverDao.loadAll()


    override suspend fun insertServer(serverDesc: ServerDesc) {
        serverDao.insertServer(serverDesc)
    }

    override suspend fun updateServer(serverDesc: ServerDesc) {
        serverDao.updateServer(serverDesc)
    }

    override suspend fun deleteServer(serverDesc: ServerDesc) {
        serverDao.deleteServer(serverDesc)
    }

    override suspend fun getLrcContent(serverDesc: ServerDesc, path: String): String {
        val credential: String = credentials(serverDesc.user, serverDesc.password)
        val ins = sardine.get(path, mapOf("Authorization" to credential))
        return LrcUtils.readLyricContent(ins) ?: ""
    }

    override fun getPlayMode(): Flow<Int> {
        return dataStore.data.map {
            it[KEY_PLAY_MODE] ?: PlayMode.LIST.ordinal
        }
    }

    override suspend fun setPlayMode(mode: Int) {
        dataStore.edit {
            it[KEY_PLAY_MODE] = mode
        }
    }

    override fun getBingPictureFromNet(): Flow<String> =
        dataStore.data.map { it[BING_PIC_KEY] ?: "" }

}