package cn.altawk.nbt.exception

import cn.altawk.nbt.NbtPath
import kotlinx.serialization.SerializationException

/**
 * NbtExceptions
 *
 * @author TheFloodDragon
 * @since 2025/3/15 12:31
 */

public sealed class NbtException(
    message: String,
    path: NbtPath? = null,
    cause: Throwable? = null,
) : SerializationException(message, cause) {

    public var path: NbtPath? = path
        internal set

}

public open class NbtEncodingException(
    message: String,
    path: NbtPath? = null,
    cause: Throwable? = null,
) : NbtException(message, path, cause) {

    override val message: String = if (path != null) "Error while encoding '$path': $message" else message

}

public open class NbtDecodingException(
    message: String,
    path: NbtPath? = null,
    cause: Throwable? = null,
) : NbtException(message, path, cause) {

    override val message: String = if (path != null) "Error while decoding '$path': $message" else message

}

internal fun <R> catching(path: NbtPath?, block: () -> R): R =
    try {
        block()
    } catch (ex: NbtException) {
        ex.path = path
        throw ex
    }
