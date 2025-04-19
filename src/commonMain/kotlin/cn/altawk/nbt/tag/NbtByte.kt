package cn.altawk.nbt.tag

import cn.altawk.nbt.internal.Tokens
import kotlinx.serialization.Serializable

/**
 * NbtByte
 *
 * @author TheFloodDragon
 * @since 2025/1/5 16:43
 */
@Serializable(NbtByteSerializer::class)
public class NbtByte(
    /**
     * The content of the tag.
     */
    public override val content: Byte
) : NbtTag {

    /**
     * Construct representing a [Boolean]: `false = 0b`, `true = 1b`.
     */
    public constructor(value: Boolean) : this(if (value) 1 else 0)

    /**
     * Convert to its [Boolean] representation: `0b = false`, `1b = true`.
     */
    public fun toBoolean(): Boolean? {
        return when (content) {
            0.toByte() -> false
            1.toByte() -> true
            else -> null
        }
    }

    /**
     * Convert to its [Boolean] representation: `0b = false`, `1b = true`.
     *
     * In order to match Minecraft's lenient behavior, all other values convert to `false`.
     */
    public fun toBooleanStrict(): Boolean {
        return content == 1.toByte()
    }

    /**
     * Clone the tag.
     */
    override fun clone(): NbtByte = NbtByte(content)

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String = content.toString() + Tokens.TYPE_BYTE

    /**
     * Check if the tag is equal to another object.
     */
    override fun equals(other: Any?): Boolean = this === other || (other is NbtByte && this.content == other.content)

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int = content.hashCode()

    /**
     * The type of the tag.
     */
    override val type: NbtType get() = NbtType.BYTE

}