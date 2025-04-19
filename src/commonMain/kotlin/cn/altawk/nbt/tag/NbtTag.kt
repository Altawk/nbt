package cn.altawk.nbt.tag

import kotlinx.serialization.Serializable

/**
 * NbtTag
 *
 * @author TheFloodDragon
 * @since 2025/1/5 16:46
 */
@Serializable(NbtTagSerializer::class)
public sealed interface NbtTag {

    /**
     * The type of the tag.
     */
    public val type: NbtType

    /**
     * The content of the tag.
     */
    public val content: Any

    /**
     * Clone the tag.
     */
    public fun clone(): NbtTag

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String

    /**
     * Check if the tag is equal to another object.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int

}