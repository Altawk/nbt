package cn.altawk.nbt.tag

import kotlinx.serialization.Serializable

/**
 * NbtInt
 *
 * @author TheFloodDragon
 * @since 2025/1/5 16:43
 */
@Serializable(NbtIntSerializer::class)
public class NbtInt(
    /**
     * The content of the tag.
     */
    public override val content: Int
) : NbtTag {

    /**
     * Clone the tag.
     */
    public override fun clone(): NbtInt = NbtInt(content)

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String = content.toString()

    /**
     * Check if the tag equals another.
     */
    override fun equals(other: Any?): Boolean = this === other || (other is NbtInt && this.content == other.content)

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int = content.hashCode()

    /**
     * The type of the tag.
     */
    public override val type: NbtType get() = NbtType.INT

}