package cn.altawk.nbt.internal

/**
 * NbtWriter
 *
 * @author TheFloodDragon
 * @since 2025/1/25 11:21
 */
internal interface NbtWriter {

    /**
     * Begin a compound.
     */
    fun beginCompound()

    /**
     * Begin a compound entry.
     */
    fun beginCompoundEntry(name: String)

    /**
     * End a compound.
     */
    fun endCompound()

    /**
     * Begin a list.
     */
    fun beginList(size: Int)

    /**
     * Begin a list entry.
     */
    fun beginListEntry()

    /**
     * End a list.
     */
    fun endList()

    /**
     * Begin a byte array.
     */
    fun beginByteArray(size: Int)

    /**
     * Begin a byte array entry.
     */
    fun beginByteArrayEntry()

    /**
     * End a byte array.
     */
    fun endByteArray()

    /**
     * Begin an int array.
     */
    fun beginIntArray(size: Int)

    /**
     * Begin an int array entry.
     */
    fun beginIntArrayEntry()

    /**
     * End an int array.
     */
    fun endIntArray()

    /**
     * Begin a long array.
     */
    fun beginLongArray(size: Int)

    /**
     * Begin a long array entry.
     */
    fun beginLongArrayEntry()

    /**
     * End a long array.
     */
    fun endLongArray()

    /**
     * Write a byte.
     */
    fun writeByte(value: Byte)

    /**
     * Write a short.
     */
    fun writeShort(value: Short)

    /**
     * Write an int.
     */
    fun writeInt(value: Int)

    /**
     * Write a long.
     */
    fun writeLong(value: Long)

    /**
     * Write a float.
     */
    fun writeFloat(value: Float)

    /**
     * Write a double.
     */
    fun writeDouble(value: Double)

    /**
     * Write a string.
     */
    fun writeString(value: String)

}

/**
 * Write a byte array.
 */
internal fun NbtWriter.writeByteArray(value: ByteArray) {
    beginByteArray(value.size)
    for (b in value) {
        beginByteArrayEntry()
        writeByte(b)
    }
    endByteArray()
}

/**
 * Write an int array.
 */
internal fun NbtWriter.writeIntArray(value: IntArray) {
    beginIntArray(value.size)
    for (i in value) {
        beginIntArrayEntry()
        writeInt(i)
    }
    endIntArray()
}


/**
 * Write a long array.
 */
internal fun NbtWriter.writeLongArray(value: LongArray) {
    beginLongArray(value.size)
    for (l in value) {
        beginIntArrayEntry()
        writeLong(l)
    }
    endLongArray()
}