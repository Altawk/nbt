package cn.altawk.nbt.exception

import kotlinx.serialization.SerializationException

/**
 * NbtExceptions
 *
 * @author TheFloodDragon
 * @since 2025/3/15 12:31
 */

public sealed class NbtException(
    message: String,
    cause: Throwable? = null,
) : SerializationException(message, cause)

public open class NbtEncodingException(
    message: String,
    cause: Throwable? = null,
) : NbtException(message, cause) {
    override val message: String = "Error while encoding: $message"
}

public open class NbtDecodingException(
    message: String,
    cause: Throwable? = null,
) : NbtException(message, cause) {
    override val message: String = "Error while decoding: $message"
}
