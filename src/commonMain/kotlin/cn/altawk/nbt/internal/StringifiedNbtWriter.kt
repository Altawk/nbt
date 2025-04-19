package cn.altawk.nbt.internal

import cn.altawk.nbt.internal.Tokens.ARRAY_BEGIN
import cn.altawk.nbt.internal.Tokens.ARRAY_END
import cn.altawk.nbt.internal.Tokens.ARRAY_SIGNATURE_SEPARATOR
import cn.altawk.nbt.internal.Tokens.COMPOUND_BEGIN
import cn.altawk.nbt.internal.Tokens.COMPOUND_END
import cn.altawk.nbt.internal.Tokens.COMPOUND_KEY_TERMINATOR
import cn.altawk.nbt.internal.Tokens.DOUBLE_QUOTE
import cn.altawk.nbt.internal.Tokens.ESCAPE_MARKER
import cn.altawk.nbt.internal.Tokens.PRETTY_PRINT_SPACE
import cn.altawk.nbt.internal.Tokens.SINGLE_QUOTE
import cn.altawk.nbt.internal.Tokens.TYPE_BYTE
import cn.altawk.nbt.internal.Tokens.TYPE_BYTE_ARRAY
import cn.altawk.nbt.internal.Tokens.TYPE_DOUBLE
import cn.altawk.nbt.internal.Tokens.TYPE_FLOAT
import cn.altawk.nbt.internal.Tokens.TYPE_INT_ARRAY
import cn.altawk.nbt.internal.Tokens.TYPE_LONG
import cn.altawk.nbt.internal.Tokens.TYPE_LONG_ARRAY
import cn.altawk.nbt.internal.Tokens.TYPE_SHORT
import cn.altawk.nbt.internal.Tokens.VALUE_SEPARATOR

/**
 * StringifiedNbtWriter
 *
 * @author TheFloodDragon
 * @since 2025/2/22 15:23
 */
internal class StringifiedNbtWriter(private val builder: Appendable, private val prettyPrint: Boolean) : NbtWriter {
    private var firstEntry = false
    private var inArray = false
    private var level = 0

    private fun beginArray(prefix: String) {
        builder.append(prefix)
        if (prettyPrint) builder.append(PRETTY_PRINT_SPACE)

        firstEntry = true
        inArray = true
        level++
    }

    private fun beginCollection(prefix: Char) {
        builder.append(prefix)

        firstEntry = true
        inArray = false
        level++
    }

    private fun beginCollectionEntry() {
        if (!firstEntry) {
            builder.append(VALUE_SEPARATOR)
            if (prettyPrint && inArray) builder.append(PRETTY_PRINT_SPACE)
        }

        if (prettyPrint && !inArray) appendPrettyNewLine()

        firstEntry = false
    }

    private fun endCollection(suffix: Char) {
        level--
        appendPrettyNewLine()
        builder.append(suffix)

        firstEntry = false
        inArray = false
    }

    private fun endArray() {
        level--
        builder.append(ARRAY_END)

        firstEntry = false
        inArray = false
    }

    override fun beginCompound() = beginCollection(COMPOUND_BEGIN)

    override fun beginCompoundEntry(name: String) {
        beginCollectionEntry()
        builder.appendNbtString(name).append(COMPOUND_KEY_TERMINATOR)
        if (prettyPrint) builder.append(PRETTY_PRINT_SPACE)
    }

    override fun endCompound() = endCollection(COMPOUND_END)

    override fun beginList(size: Int) = beginCollection(ARRAY_BEGIN)

    override fun beginListEntry() = beginCollectionEntry()

    override fun endList() = endCollection(ARRAY_END)

    override fun beginByteArray(size: Int) = beginArray("$ARRAY_BEGIN$TYPE_BYTE_ARRAY$ARRAY_SIGNATURE_SEPARATOR")

    override fun beginByteArrayEntry() = beginCollectionEntry()
    override fun endByteArray() = endArray()

    override fun beginIntArray(size: Int) = beginArray("$ARRAY_BEGIN$TYPE_INT_ARRAY$ARRAY_SIGNATURE_SEPARATOR")

    override fun beginIntArrayEntry() = beginCollectionEntry()
    override fun endIntArray() = endArray()

    override fun beginLongArray(size: Int) = beginArray("$ARRAY_BEGIN$TYPE_LONG_ARRAY$ARRAY_SIGNATURE_SEPARATOR")

    override fun beginLongArrayEntry() = beginCollectionEntry()
    override fun endLongArray() = endArray()

    override fun writeByte(value: Byte) {
        builder.append(value.toString()).append(if (inArray) TYPE_BYTE_ARRAY else TYPE_BYTE)
    }

    override fun writeShort(value: Short) {
        builder.append(value.toString()).append(TYPE_SHORT)
    }

    override fun writeInt(value: Int) {
        builder.append(value.toString())
    }

    override fun writeLong(value: Long) {
        builder.append(value.toString()).append(TYPE_LONG)
    }

    override fun writeFloat(value: Float) {
        builder.append(value.toString()).append(TYPE_FLOAT)
    }

    override fun writeDouble(value: Double) {
        builder.append(value.toString()).append(TYPE_DOUBLE)
    }

    override fun writeString(value: String) {
        builder.appendNbtString(value, forceQuote = true)
    }

    private fun appendPrettyNewLine() {
        if (prettyPrint) {
            builder.appendLine()
            repeat(level) { _ -> builder.append(PRETTY_PRINT_SPACE) }
        }
    }

}

private fun Appendable.appendQuoted(value: String): Appendable = apply {
    append(DOUBLE_QUOTE)
    value.forEach {
        if (it == DOUBLE_QUOTE) append(ESCAPE_MARKER)
        append(it)
    }
    append(DOUBLE_QUOTE)
}

internal fun Appendable.appendNbtString(value: String, forceQuote: Boolean = false): Appendable {
    return when {
        forceQuote -> appendQuoted(value)
        value.isEmpty() -> append(DOUBLE_QUOTE).append(DOUBLE_QUOTE)
        value.all { Tokens.id(it) } -> append(value)
        !value.contains(DOUBLE_QUOTE) -> append(DOUBLE_QUOTE).append(value).append(DOUBLE_QUOTE)
        !value.contains(SINGLE_QUOTE) -> append(SINGLE_QUOTE).append(value).append(SINGLE_QUOTE)
        else -> appendQuoted(value)
    }
}