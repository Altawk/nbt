package cn.altawk.nbt.tag

import kotlinx.serialization.Serializable

/**
 * NbtIntArray
 *
 * @author TheFloodDragon
 * @since 2025/1/5 16:43
 */
@Serializable(NbtIntArraySerializer::class)
public class NbtIntArray(
    /**
     * The content of the tag.
     */
    public override val content: IntArray
) : NbtTag {

    /**
     * Get the data at the specified index.
     */
    public operator fun get(index: Int): Int = content[index]

    /**
     * The size of the tag.
     */
    public val size: Int get() = content.size

    /**
     * Clone the tag.
     */
    public override fun clone(): NbtIntArray = NbtIntArray(content.clone())

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String = content.joinToString(separator = ",", prefix = "[I;", postfix = "]")

    /**
     * Check if the tag is equal to another object.
     */
    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtIntArray && this.content.contentEquals(other.content))

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int = content.hashCode()

    /**
     * The type of the tag.
     */
    public override val type: NbtType get() = NbtType.INT_ARRAY

}