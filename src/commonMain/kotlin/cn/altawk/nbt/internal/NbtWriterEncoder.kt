package cn.altawk.nbt.internal

import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.tag.*
import cn.altawk.nbt.tag.NbtType.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder

/**
 * NbtWriterEncoder
 *
 * @author TheFloodDragon
 * @since 2025/1/25 11:25
 */
internal class NbtWriterEncoder(
    override val nbt: NbtFormat,
    private val writer: NbtWriter,
) : AbstractNbtEncoder() {

    private lateinit var elementName: String
    private var encodingMapKey: Boolean = false

    private val structureTypeStack = ArrayDeque<NbtType>()

    override fun encodeSerializableElement(descriptor: SerialDescriptor, index: Int): Boolean {
        when (descriptor.kind as StructureKind) {
            StructureKind.CLASS, StructureKind.OBJECT -> {
                val determined = nbt.configuration.nameDeterminer.determineName(index, descriptor)
                // Do not encode if the element name is EOF
                if (determined == NbtReader.EOF) return false
                elementName = determined
            }
            StructureKind.MAP -> if (index % 2 == 0) encodingMapKey = true
            else -> Unit
        }
        return true
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        if (!encodeSerializableElement(descriptor, index)) {
            return false
        }
        when (val structureType = structureTypeStack.last()) {
            COMPOUND -> if (!encodingMapKey) writer.beginCompoundEntry(elementName)
            LIST -> writer.beginListEntry()
            BYTE_ARRAY -> writer.beginByteArrayEntry()
            INT_ARRAY -> writer.beginIntArrayEntry()
            LONG_ARRAY -> writer.beginLongArrayEntry()
            else -> error("Unhandled structure type: $structureType")
        }
        return true
    }

    override fun beginCompound(descriptor: SerialDescriptor): CompositeEncoder {
        writer.beginCompound()
        structureTypeStack += COMPOUND
        return this
    }

    override fun beginList(descriptor: SerialDescriptor, size: Int): CompositeEncoder {
        writer.beginList(size)
        structureTypeStack += LIST
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        when (val structureType = structureTypeStack.removeLast()) {
            COMPOUND -> writer.endCompound()
            LIST -> writer.endList()
            BYTE_ARRAY -> writer.endByteArray()
            INT_ARRAY -> writer.endIntArray()
            LONG_ARRAY -> writer.endLongArray()
            else -> error("Unhandled structure type: $structureType")
        }
    }

    override fun encodeString(value: String) {
        if (encodingMapKey) {
            elementName = value
            encodingMapKey = false
        } else writer.writeString(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = encodeString(enumDescriptor.getElementName(index))

    override fun encodeByte(value: Byte) = writer.writeByte(value)
    override fun encodeBoolean(value: Boolean) = writer.writeByte(if (value) 1 else 0)
    override fun encodeChar(value: Char) = writer.writeString(value.toString())
    override fun encodeDouble(value: Double) = writer.writeDouble(value)
    override fun encodeFloat(value: Float) = writer.writeFloat(value)
    override fun encodeInt(value: Int) = writer.writeInt(value)
    override fun encodeLong(value: Long) = writer.writeLong(value)
    override fun encodeShort(value: Short) = writer.writeShort(value)

    override fun encodeByteArray(value: ByteArray) = writer.writeByteArray(value)
    override fun encodeIntArray(value: IntArray) = writer.writeIntArray(value)
    override fun encodeLongArray(value: LongArray) = writer.writeLongArray(value)

    override fun encodeNbtTag(tag: NbtTag) {
        when (tag.type) {
            BYTE -> writer.writeByte((tag as NbtByte).content)
            SHORT -> writer.writeShort((tag as NbtShort).content)
            INT -> writer.writeInt((tag as NbtInt).content)
            LONG -> writer.writeLong((tag as NbtLong).content)
            FLOAT -> writer.writeFloat((tag as NbtFloat).content)
            DOUBLE -> writer.writeDouble((tag as NbtDouble).content)
            STRING -> writer.writeString((tag as NbtString).content)
            BYTE_ARRAY -> writer.writeByteArray((tag as NbtByteArray).content)
            INT_ARRAY -> writer.writeIntArray((tag as NbtIntArray).content)
            LONG_ARRAY -> writer.writeLongArray((tag as NbtLongArray).content)
            LIST -> {
                writer.beginList((tag as NbtList).size)
                for (element in tag) {
                    encodeNbtTag(element)
                }
                writer.endList()
            }

            COMPOUND -> {
                writer.beginCompound()
                for ((name, value) in tag as NbtCompound) {
                    writer.beginCompoundEntry(name)
                    encodeNbtTag(value)
                }
                writer.endCompound()
            }

            END -> Unit
        }
    }

}
