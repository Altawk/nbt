package cn.altawk.nbt.internal

import cn.altawk.nbt.tag.*

/**
 * TreeNbtWriter
 *
 * @author TheFloodDragon
 * @since 2025/3/14 22:51
 */
internal class TreeNbtWriter(tagConsumer: (NbtTag) -> Unit) : NbtWriter {
    private var writer: TagWriter = RootWriter(tagConsumer)

    override fun beginCompound(): Unit = writer.beginCompound()
    override fun beginCompoundEntry(name: String): Unit = writer.beginCompoundEntry(name)
    override fun endCompound(): Unit = writer.endCompound()
    override fun beginList(size: Int): Unit = writer.beginList(size)
    override fun beginListEntry(): Unit = writer.beginListEntry()
    override fun endList(): Unit = writer.endList()
    override fun beginByteArray(size: Int): Unit = writer.beginByteArray(size)
    override fun beginByteArrayEntry(): Unit = writer.beginByteArrayEntry()
    override fun endByteArray(): Unit = writer.endByteArray()
    override fun beginIntArray(size: Int): Unit = writer.beginIntArray(size)
    override fun beginIntArrayEntry(): Unit = writer.beginIntArrayEntry()
    override fun endIntArray(): Unit = writer.endIntArray()
    override fun beginLongArray(size: Int): Unit = writer.beginLongArray(size)
    override fun beginLongArrayEntry(): Unit = writer.beginLongArrayEntry()
    override fun endLongArray(): Unit = writer.endLongArray()
    override fun writeByte(value: Byte): Unit = writer.writeByte(value)
    override fun writeShort(value: Short): Unit = writer.writeShort(value)
    override fun writeInt(value: Int): Unit = writer.writeInt(value)
    override fun writeLong(value: Long): Unit = writer.writeLong(value)
    override fun writeFloat(value: Float): Unit = writer.writeFloat(value)
    override fun writeDouble(value: Double): Unit = writer.writeDouble(value)
    override fun writeString(value: String): Unit = writer.writeString(value)

    private open inner class TagWriter : NbtWriter {

        open fun consumeTag(tag: NbtTag): Unit = error("${this::class} does not support consumeTag()")

        override fun beginCompound() {
            writer = CompoundWriter(this)
        }

        override fun beginList(size: Int) {
            writer = ListWriter(this, size)
        }

        override fun beginByteArray(size: Int) {
            writer = ByteArrayWriter(this, size)
        }

        override fun beginIntArray(size: Int) {
            writer = IntArrayWriter(this, size)
        }

        override fun beginLongArray(size: Int) {
            writer = LongArrayWriter(this, size)
        }

        override fun beginCompoundEntry(name: String): Unit = error("${this::class} does not support beginCompoundEntry()")
        override fun endCompound(): Unit = error("${this::class} does not support endCompound()")

        override fun beginListEntry(): Unit = error("${this::class} does not support beginListEntry()")
        override fun endList(): Unit = error("${this::class} does not support endList()")

        override fun beginByteArrayEntry(): Unit = error("${this::class} does not support beginByteArrayEntry()")
        override fun endByteArray(): Unit = error("${this::class} does not support endByteArray()")

        override fun beginIntArrayEntry(): Unit = error("${this::class} does not support beginIntArrayEntry()")
        override fun endIntArray(): Unit = error("${this::class} does not support endIntArray()")

        override fun beginLongArrayEntry(): Unit = error("${this::class} does not support beginLongArrayEntry()")
        override fun endLongArray(): Unit = error("${this::class} does not support endLongArray()")

        override fun writeByte(value: Byte): Unit = consumeTag(NbtByte(value))
        override fun writeShort(value: Short): Unit = consumeTag(NbtShort(value))
        override fun writeInt(value: Int): Unit = consumeTag(NbtInt(value))
        override fun writeLong(value: Long): Unit = consumeTag(NbtLong(value))
        override fun writeFloat(value: Float): Unit = consumeTag(NbtFloat(value))
        override fun writeDouble(value: Double): Unit = consumeTag(NbtDouble(value))
        override fun writeString(value: String): Unit = consumeTag(NbtString(value))

    }

    private inner class RootWriter(private val tagConsumer: (NbtTag) -> Unit) : TagWriter() {
        override fun consumeTag(tag: NbtTag): Unit = tagConsumer(tag)
    }

    private inner class CompoundWriter(private val parent: TagWriter) : TagWriter() {
        private val builder = LinkedHashMap<String, NbtTag>()
        private lateinit var entryName: String

        override fun consumeTag(tag: NbtTag) {
            builder[entryName] = tag
        }

        override fun beginCompoundEntry(name: String) {
            entryName = name
        }

        override fun endCompound() {
            writer = parent
            parent.consumeTag(NbtCompound(builder))
        }
    }

    private inner class ListWriter(private val parent: TagWriter, size: Int) : TagWriter() {
        private val builder = ArrayList<NbtTag>(size)

        override fun consumeTag(tag: NbtTag) {
            builder.add(tag)
        }

        override fun beginListEntry(): Unit = Unit

        override fun endList() {
            writer = parent
            parent.consumeTag(NbtList(builder))
        }
    }

    private inner class ByteArrayWriter(private val parent: TagWriter, size: Int) : TagWriter() {
        private val array = ByteArray(size)
        private var index = 0

        override fun beginByteArrayEntry() = Unit

        override fun endByteArray() {
            writer = parent
            parent.consumeTag(NbtByteArray(array))
        }

        override fun writeByte(value: Byte) {
            array[index++] = value
        }
    }

    private inner class IntArrayWriter(private val parent: TagWriter, size: Int) : TagWriter() {
        private val array = IntArray(size)
        private var index = 0

        override fun beginIntArrayEntry() = Unit

        override fun endIntArray() {
            writer = parent
            parent.consumeTag(NbtIntArray(array))
        }

        override fun writeInt(value: Int) {
            array[index++] = value
        }
    }

    private inner class LongArrayWriter(private val parent: TagWriter, size: Int) : TagWriter() {
        private val array = LongArray(size)
        private var index = 0

        override fun beginLongArrayEntry() = Unit

        override fun endLongArray() {
            writer = parent
            parent.consumeTag(NbtLongArray(array))
        }

        override fun writeLong(value: Long) {
            array[index++] = value
        }
    }

}
