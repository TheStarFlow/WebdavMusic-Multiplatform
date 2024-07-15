package zzs.webdav.music.data.repository

import com.github.sardine.DavResource
import zzs.webdav.music.bean.ServerDesc
import kotlinx.coroutines.flow.Flow


interface WebdavRepository {

    suspend fun setCurrServerKey(serverDesc: ServerDesc)

    /**
     * 获取当前页面的 webdav 资源列表
     *
     * */
    suspend fun fetchDavResList(serverDesc: ServerDesc): List<DavResource>

    /**
     * 获取当前正在使用的服务器
     *
     * */

    suspend fun fetchCacheServer():ServerDesc?

    /**
     * 获取缓存的服务器列表
     *
     * */

    suspend fun fetchCacheServerList(): List<ServerDesc>

    /**
     * 添加服务器
     * */

    suspend fun insertServer(serverDesc: ServerDesc)


    /**
     * 更新服务器
     * */

    suspend fun updateServer(serverDesc: ServerDesc)

    /**
     * 删除服务器
     *
     * */

    suspend fun deleteServer(serverDesc: ServerDesc)

    /**
     * 获取歌词字符
     *
     * */

    suspend fun getLrcContent(serverDesc: ServerDesc, path: String): String

    /**
     * 播放模式
     *
     * */

    fun getPlayMode(): Flow<Int>

    /**
     * 设置播放模式
     * */

    suspend fun setPlayMode(mode: Int)

    fun getBingPictureFromNet(): Flow<String>

//    fun searchSingerImage(name: String): Bitmap?
//
//    fun searchMusicImage(
//        album: String,
//        name: String,
//        singer: String,
//        songHttpPath: String,
//        user: String,
//        password: String
//    ): Flow<Bitmap?>

}