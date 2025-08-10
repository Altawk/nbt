package cn.altawk.nbt

import cn.altawk.nbt.tag.NbtTag
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder

/**
 * NbtEncoder
 *
 * @author TheFloodDragon
 * @since 2025/3/15 11:04
 */
public interface NbtEncoder : Encoder, CompositeEncoder {

    /**
     * An instance of the current [NbtFormat].
     */
    public val nbt: NbtFormat

    /**
     * Encode a [NbtTag].
     */
    public fun encodeNbtTag(value: NbtTag)

    /**
     * Encode a [ByteArray].
     */
    public fun encodeByteArray(value: ByteArray)

    /**
     * Encode a [IntArray].
     */
    public fun encodeIntArray(value: IntArray)

    /**
     * Encode a [LongArray].
     */
    public fun encodeLongArray(value: LongArray)

}