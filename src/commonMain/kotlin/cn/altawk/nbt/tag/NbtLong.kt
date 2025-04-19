package cn.altawk.nbt.tag

import kotlinx.serialization.Serializable

/**
 * NbtLong
 *
 * @author TheFloodDragon
 * @since 2025/1/5 16:43
 */
@Serializable(NbtLongSerializer::class)
public class NbtLong(
    /**
     * The content of the tag.
     */
    public override val content: Long
) : NbtTag {

    /**
     * Clone the tag.
     */
    public override fun clone(): NbtLong = NbtLong(content)

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String = "${content}L"

    /**
     * Check if the tag equals another.
     */
    override fun equals(other: Any?): Boolean = this === other || (other is NbtLong && this.content == other.content)

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int = content.hashCode()

    /**
     * The type of the tag.
     */
    public override val type: NbtType get() = NbtType.LONG

}