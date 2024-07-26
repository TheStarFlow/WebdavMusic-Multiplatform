package zzs.webdav.music.ui.folder

import com.github.sardine.DavResource
import kotlinx.coroutines.Dispatchers
import zzs.webdav.music.base.BaseViewModel
import zzs.webdav.music.base.Loading
import zzs.webdav.music.bean.FileDesc
import zzs.webdav.music.bean.FileFormat
import zzs.webdav.music.bean.LrcFormat
import zzs.webdav.music.bean.MediaFormat
import zzs.webdav.music.bean.ServerDesc
import zzs.webdav.music.bean.Song
import zzs.webdav.music.data.repository.WebdavRepository
import java.util.Locale


class FolderViewModel constructor(private val repository: WebdavRepository) :
    BaseViewModel<FolderUIState, FolderIntent>() {


    fun fetchServerDavResList(serverDesc: ServerDesc, targetPath: String?) {
        withState { oldState ->
            if (oldState.currDavList is Loading){
                return@withState
            }
            if (oldState.currTargetPath.isNotEmpty()){
                serverDesc.targetPath = oldState.currTargetPath
            }
            suspend {
                //对dav文件描述做一定的转换
                val davList = repository.fetchDavResList(serverDesc,targetPath)
                val lrcDavResMap = mutableMapOf<String, DavResource>()
                val directoryArray = mutableListOf<DavResource>()
                val mediaArray = mutableListOf<DavResource>()
                val mediaFormatCache = mutableMapOf<String, FileFormat>()
                val otherDavResource = mutableListOf<DavResource>()
                //文件分类
                for (dav in davList) {
                    //获取目录
                    if (dav.isDirectory) {
                        directoryArray.add(dav)
                        continue
                    }
                    //取出歌词描述
                    if (dav.displayName.endsWith(LrcFormat.LRC.name.lowercase(Locale.ROOT))) {
                        lrcDavResMap[dav.displayName] = dav
                        continue
                    }
                    //flac
                    if (dav.displayName.endsWith(FileFormat.FLAC.name.lowercase(Locale.ROOT))
                    ) {
                        mediaFormatCache[dav.path] = FileFormat.FLAC
                        mediaArray.add(dav)
                        continue
                    }
                    //mp3
                    if (dav.displayName.endsWith(FileFormat.MP3.name.lowercase(Locale.ROOT))) {
                        mediaFormatCache[dav.path] = FileFormat.MP3
                        mediaArray.add(dav)
                        continue
                    }
                    //其他文件
                    otherDavResource.add(dav)
                }
                val directoryList: List<FileDesc> = directoryArray.map {
                    FileDesc(
                        it.displayName,
                        FileFormat.DIRECTORY,
                        it.path
                    )
                }
                val regex = Regex("\\.(\\w+)$")
                val mediaList = mediaArray.map {
                    val lrcDisplayName = regex.replaceFirst(it.displayName, ".lrc")
                    val lrcDav = lrcDavResMap[lrcDisplayName]
                    var lrcPath: String? = null
                    if (lrcDav != null) {
                        lrcPath = "http://${serverDesc.ip}:${serverDesc.port}${lrcDav.path}"
                    }
                    Song(it.displayName, mediaFormatCache[it.path]!!, it.href.rawPath, lrcPath)
                }
                val otherFileDesc = otherDavResource.map {
                    FileDesc(name = it.displayName, FileFormat.UNKNOW, it.path)
                }
                val mutableList = directoryList.toMutableList()
                mutableList.addAll(mediaList)
                mutableList.addAll(otherFileDesc)
                mutableList
            }.execute(dispatcher = Dispatchers.IO) { result ->
              //  logInfo("result Thread:${Thread.currentThread().name}_ $result")
                copy(
                    currDavList = result,

                    currMediaDavList = result()?.takeWhile {
                        it.format in MediaFormat
                    } ?: emptyList(),
                    currTargetPath =  serverDesc.targetPath?:""//targetPath?: ""
                )
            }
        }
    }

    fun updateFocusIndex(index: Int) {
        setState {
            copy(focusIndex = index)
        }
    }


    override suspend fun handleIntent(intent: FolderIntent) {

    }

}




