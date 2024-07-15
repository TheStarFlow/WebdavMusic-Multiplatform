package zzs.webdav.music.utils

import okio.ByteString.Companion.encode
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.ISO_8859_1


fun credentials(
    username: String, password: String, charset: Charset = ISO_8859_1
): String {
    val usernameAndPassword = "$username:$password"
    val encoded = usernameAndPassword.encode(charset).base64()
    return "Basic $encoded"
}