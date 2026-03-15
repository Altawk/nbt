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

    // State stack: pairs of (firstEntry, inArray) packed as two booleans per entry
    private val firstEntryStack = ArrayDeque<Boolean>()
    private val inArrayStack = ArrayDeque<Boolean>()
    private var level = 0

    private fun beginArray(prefix: String) {
        firstEntryStack.addLast(true)
        inArrayStack.addLast(true)
        builder.append(prefix)
        if (prettyPrint) builder.append(PRETTY_PRINT_SPACE)
        level++
    }

    private fun beginCollection(prefix: Char) {
        firstEntryStack.addLast(true)
        inArrayStack.addLast(false)
        builder.append(prefix)
        level++
    }

    private fun beginCollectionEntry() {
        if (!firstEntryStack.last()) {
            builder.append(VALUE_SEPARATOR)
            if (prettyPrint && inArrayStack.last()) builder.append(PRETTY_PRINT_SPACE)
        }

        if (prettyPrint && !inArrayStack.last()) appendPrettyNewLine()

        firstEntryStack[firstEntryStack.lastIndex] = false
    }

    private fun endCollection(suffix: Char) {
        level--
        if (!firstEntryStack.last()) appendPrettyNewLine() // skip newline for empty collections
        builder.append(suffix)
        firstEntryStack.removeLast()
        inArrayStack.removeLast()
    }

    private fun endArray() {
        level--
        builder.append(ARRAY_END)
        firstEntryStack.removeLast()
        inArrayStack.removeLast()
    }

    override fun beginCompound() = beginCollection(COMPOUND_BEGIN)

    override fun beginCompoundEntry(name: String) {
        beginCollectionEntry()
        builder.appendValid(name).append(COMPOUND_KEY_TERMINATOR)
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
        builder.append(value.toString()).append(if (inArrayStack.last()) TYPE_BYTE_ARRAY else TYPE_BYTE)
    }

    override fun writeLong(value: Long) {
        builder.append(value.toString()).append(TYPE_LONG)
    }

    override fun writeShort(value: Short) {
        builder.append(value.toString()).append(TYPE_SHORT)
    }

    override fun writeInt(value: Int) {
        builder.append(value.toString())
    }

    override fun writeFloat(value: Float) {
        builder.append(value.toString()).append(TYPE_FLOAT)
    }

    override fun writeDouble(value: Double) {
        builder.append(value.toString()).append(TYPE_DOUBLE)
    }

    override fun writeString(value: String) {
        builder.appendQuoted(value)
    }

    private fun appendPrettyNewLine() {
        if (prettyPrint) {
            builder.appendLine()
            repeat(level) { _ -> builder.append(PRETTY_PRINT_SPACE) }
        }
    }

}

internal fun Appendable.appendQuoted(value: String): Appendable = apply {
    append(DOUBLE_QUOTE)
    var lastCopy = 0
    val len = value.length
    for (i in 0 until len) {
        val c = value[i]
        if (c == DOUBLE_QUOTE || c == ESCAPE_MARKER) {
            // Batch append everything before this char
            if (i > lastCopy) append(value, lastCopy, i)
            append(ESCAPE_MARKER)
            lastCopy = i // the char itself will be included in the next batch
        }
    }
    // Append remaining
    if (lastCopy == 0) {
        append(value) // no escapes needed, append whole string at once
    } else if (lastCopy < len) {
        append(value, lastCopy, len)
    }
    append(DOUBLE_QUOTE)
}

internal fun Appendable.appendValid(value: String): Appendable {
    if (value.isEmpty()) return append(DOUBLE_QUOTE).append(DOUBLE_QUOTE)

    // Single pass: determine all properties at once
    var allId = true
    var hasDoubleQuote = false
    var hasSingleQuote = false
    for (c in value) {
        if (allId && !Tokens.id(c)) allId = false
        if (c == DOUBLE_QUOTE) hasDoubleQuote = true
        if (c == SINGLE_QUOTE) hasSingleQuote = true
        // Early exit: if we know we need full escaping, stop scanning
        if (!allId && hasDoubleQuote && hasSingleQuote) break
    }

    return when {
        allId -> append(value)
        !hasDoubleQuote -> append(DOUBLE_QUOTE).append(value).append(DOUBLE_QUOTE)
        !hasSingleQuote -> append(SINGLE_QUOTE).append(value).append(SINGLE_QUOTE)
        else -> appendQuoted(value)
    }
}
