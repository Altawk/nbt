package cn.altawk.nbt.tag

import kotlinx.serialization.Serializable

/**
 * NbtList
 *
 * @since 2025/1/5 16:43
 */
@Serializable(NbtListSerializer::class)
public class NbtList(
    /**
     * The content of the tag, for compatibility.
     */
    public override val content: MutableList<NbtTag>
) : NbtTag, MutableList<NbtTag> by content {

    /**
     * Create an empty list.
     */
    public constructor() : this(ArrayList())

    /**
     * Create a list with a size.
     */
    public constructor(size: Int) : this(ArrayList(size))

    /**
     * The type of elements in the list.
     */
    public val elementType: NbtType = if (content.isEmpty()) NbtType.END else content.first().type

    /**
     * Clone the tag.
     */
    override fun clone(): NbtList {
        val newList = NbtList(this.size)
        for (element in this.content) {
            newList.add(element.clone())
        }
        return newList
    }

    /**
     * Get the string representation of the tag.
     */
    override fun toString(): String = content.joinToString(prefix = "[", postfix = "]", separator = ",")

    /**
     * Check if the tag equals another.
     */
    override fun equals(other: Any?): Boolean = this === other || (other is NbtList && content == other.content)

    /**
     * Get the hash code of the tag.
     */
    override fun hashCode(): Int = content.hashCode()

    /**
     * The type of the tag.
     */
    override val type: NbtType get() = NbtType.LIST

    public companion object {

        @JvmStatic
        public fun of(list: Collection<NbtTag>): NbtList = NbtList(list.toMutableList())

    }

}