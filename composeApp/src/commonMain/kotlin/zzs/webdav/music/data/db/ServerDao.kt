package zzs.webdav.music.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import zzs.webdav.music.bean.ServerDesc

@Dao
interface ServerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerDesc)

    @Delete
    suspend fun deleteServer(server: ServerDesc)


    @Query("DELETE FROM serverdesc")
    suspend fun deleteAll()


    @Query("SELECT * FROM serverdesc WHERE ip = :ip and port = :port")
    suspend fun queryServer(ip: String, port: String): ServerDesc


    @Query("SELECT * FROM serverdesc")
    suspend fun loadAll(): List<ServerDesc>



    @Update
    suspend fun updateServer(server: ServerDesc)

}