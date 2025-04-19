package cn.altawk.nbt

import cn.altawk.nbt.tag.NbtTag
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder

/**
 * NbtDecoder
 *
 * @author TheFloodDragon
 * @since 2025/3/15 11:04
 */
public interface NbtDecoder : Decoder, CompositeDecoder {

    /**
     * An instance of the current [NbtFormat].
     */
    public val nbt: NbtFormat

    /**
     * Decode a [NbtTag].
     */
    public fun decodeNbtTag(): NbtTag

    /**
     * Decode a [ByteArray].
     */
    public fun decodeByteArray(): ByteArray

    /**
     * Decode a [IntArray].
     */
    public fun decodeIntArray(): IntArray

    /**
     * Decode a [LongArray].
     */
    public fun decodeLongArray(): LongArray

}