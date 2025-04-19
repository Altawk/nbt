package cn.altawk.nbt.tag

import kotlinx.serialization.Serializable

/**
 * NbtDouble
 *
 * @author TheFloodDragon
 * @since 2025/1/5 16:43
 */
@Serializable(NbtDoubleSerializer::class)
public class NbtDouble(
    /**
     * The content of the tag.
     */
    public override val content: Double
) : NbtTag {

    /**
     * Clone the tag.
     */
    public override fun clone(): NbtDouble = NbtDouble(content)

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String = "${content}d"

    /**
     * Check if the tag equals to another.
     */
    override fun equals(other: Any?): Boolean = this === other || (other is NbtDouble && this.content == other.content)

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int = content.hashCode()

    /**
     * The type of the tag.
     */
    public override val type: NbtType get() = NbtType.DOUBLE

}