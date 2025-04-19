package cn.altawk.nbt.tag

import kotlinx.serialization.Serializable

/**
 * NbtShort
 *
 * @author TheFloodDragon
 * @since 2025/1/5 16:43
 */
@Serializable(NbtShortSerializer::class)
public class NbtShort(
    /**
     * The content of the tag.
     */
    public override val content: Short
) : NbtTag {

    /**
     * Clone the tag.
     */
    public override fun clone(): NbtShort = NbtShort(content)

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String = "${content}s"

    /**
     * Check if the tag equals another.
     */
    override fun equals(other: Any?): Boolean = this === other || (other is NbtShort && this.content == other.content)

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int = content.hashCode()

    /**
     * The type of the tag.
     */
    public override val type: NbtType get() = NbtType.SHORT

}