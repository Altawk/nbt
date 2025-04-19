package cn.altawk.nbt.tag

import kotlinx.serialization.Serializable

/**
 * NbtFloat
 *
 * @author TheFloodDragon
 * @since 2025/1/5 16:43
 */
@Serializable(NbtFloatSerializer::class)
public class NbtFloat(
    /**
     * The content of the tag.
     */
    public override val content: Float
) : NbtTag {

    /**
     * Clone the tag.
     */
    public override fun clone(): NbtFloat = NbtFloat(content)

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String = "${content}f"

    /**
     * Check if the tag equals another.
     */
    override fun equals(other: Any?): Boolean = this === other || (other is NbtFloat && this.content == other.content)

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int = content.hashCode()

    /**
     * The type of the tag.
     */
    public override val type: NbtType get() = NbtType.FLOAT

}