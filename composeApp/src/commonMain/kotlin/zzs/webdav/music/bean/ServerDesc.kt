package zzs.webdav.music.bean

import androidx.room.Entity


@Entity(primaryKeys = ["ip", "port"])
data class ServerDesc(
    val name: String,
    val ip: String,
    val port: String, val user: String, val password: String, var targetPath: String? = null,
    var wholeUrl: String? = null
) {

    override fun toString(): String {
        return "ServerDesc(name='$name', ip='$ip', port='$port', user='$user', password='$password', targetPath=$targetPath)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServerDesc

        if (name != other.name) return false
        if (ip != other.ip) return false
        if (port != other.port) return false
        if (user != other.user) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + ip.hashCode()
        result = 31 * result + port.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }


}

fun isValidHttpUrl(url: String?): Boolean {
    url ?: return false
    return  (url.startsWith("http://") || url.startsWith("https://"))
}


fun ServerDesc.isValidate(): Boolean {
    return isValidHttpUrl(wholeUrl) || !(name.isBlank()
            || ip.isEmpty() ||
            port.isEmpty() || user.isEmpty() || password.isEmpty())
}

fun ServerDesc.generateKey() = if (isValidHttpUrl(wholeUrl)) wholeUrl else "${ip}-${port}"

