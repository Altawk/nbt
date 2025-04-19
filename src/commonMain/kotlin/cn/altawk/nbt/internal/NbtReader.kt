package cn.altawk.nbt.internal

import cn.altawk.nbt.tag.NbtTag

/**
 * NbtReader
 *
 * @author TheFloodDragon
 * @since 2025/3/14 23:33
 */
internal interface NbtReader {

    /**
     * Read a tag.
     */
    fun readTag(): NbtTag

    /**
     * Begin a compound.
     */
    fun beginCompound()

    /**
     * Begin a compound entry.
     */
    fun beginCompoundEntry(): String

    /**
     * End a compound.
     */
    fun endCompound()

    /**
     * Begin a list.
     */
    fun beginList(): Int

    /**
     * Begin a list entry.
     */
    fun beginListEntry(): Boolean

    /**
     * End a list.
     */
    fun endList()

    /**
     * Begin a byte array.
     */
    fun beginByteArray(): Int

    /**
     * Begin a byte array entry.
     */
    fun beginByteArrayEntry(): Boolean

    /**
     * End a byte array.
     */
    fun endByteArray()

    /**
     * Begin an int array.
     */
    fun beginIntArray(): Int

    /**
     * Begin an int array entry.
     */
    fun beginIntArrayEntry(): Boolean

    /**
     * End an int array.
     */
    fun endIntArray()

    /**
     * Begin a long array.
     */
    fun beginLongArray(): Int

    /**
     * Begin a long array entry.
     */
    fun beginLongArrayEntry(): Boolean

    /**
     * End a long array.
     */
    fun endLongArray()

    /**
     * Read a byte.
     */
    fun readByte(): Byte

    /**
     * Read a short.
     */
    fun readShort(): Short

    /**
     * Read an int.
     */
    fun readInt(): Int

    /**
     * Read a long.
     */
    fun readLong(): Long

    /**
     * Read a float.
     */
    fun readFloat(): Float

    /**
     * Read a double.
     */
    fun readDouble(): Double

    /**
     * Read a string.
     */
    fun readString(): String

    companion object {

        /**
         * Unknown size.
         */
        const val UNKNOWN_SIZE = -1

        /**
         * EOF.
         */
        const val EOF: String = "\u0000"

    }

}

/**
 * Read a byte array.
 */
internal fun NbtReader.readByteArray(): ByteArray {
    val size = beginByteArray()
    val result = if (size == NbtReader.UNKNOWN_SIZE) {
        buildList { while (beginByteArrayEntry()) add(readByte()) }.toByteArray()
    } else {
        ByteArray(size).also { array -> repeat(size) { array[it] = readByte() } }
    }
    endByteArray()
    return result
}

/**
 * Read an int array.
 */
internal fun NbtReader.readIntArray(): IntArray {
    val size = beginIntArray()
    val result = if (size == NbtReader.UNKNOWN_SIZE) {
        buildList { while (beginIntArrayEntry()) add(readInt()) }.toIntArray()
    } else {
        IntArray(size).also { array -> repeat(size) { array[it] = readInt() } }
    }
    endIntArray()
    return result
}

/**
 * Read a long array.
 */
internal fun NbtReader.readLongArray(): LongArray {
    val size = beginLongArray()
    val result = if (size == NbtReader.UNKNOWN_SIZE) {
        buildList { while (beginLongArrayEntry()) add(readLong()) }.toLongArray()
    } else {
        LongArray(size).also { array -> repeat(size) { array[it] = readLong() } }
    }
    endLongArray()
    return result
}
