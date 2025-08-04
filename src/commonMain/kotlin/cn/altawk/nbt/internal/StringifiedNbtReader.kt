package cn.altawk.nbt.internal

import cn.altawk.nbt.exception.StringifiedNbtParseException
import cn.altawk.nbt.internal.NbtReader.Companion.EOF
import cn.altawk.nbt.internal.NbtReader.Companion.UNKNOWN_SIZE
import cn.altawk.nbt.internal.Tokens.ARRAY_BEGIN
import cn.altawk.nbt.internal.Tokens.ARRAY_END
import cn.altawk.nbt.internal.Tokens.ARRAY_SIGNATURE_SEPARATOR
import cn.altawk.nbt.internal.Tokens.COMPOUND_BEGIN
import cn.altawk.nbt.internal.Tokens.COMPOUND_END
import cn.altawk.nbt.internal.Tokens.COMPOUND_KEY_TERMINATOR
import cn.altawk.nbt.internal.Tokens.DOUBLE_QUOTE
import cn.altawk.nbt.internal.Tokens.ESCAPE_MARKER
import cn.altawk.nbt.internal.Tokens.LITERAL_FALSE
import cn.altawk.nbt.internal.Tokens.LITERAL_TRUE
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
import cn.altawk.nbt.tag.*

/**
 * StringifiedNbtReader
 *
 * @since 2025/3/15 11:13
 */
internal class StringifiedNbtReader(private val buffer: CharBuffer) : NbtReader {

    constructor(snbt: String) : this(CharBuffer(snbt))

    private var firstEntry = true

    override fun readTag(): NbtTag {
        if (!buffer.skipWhitespace().hasMore()) buffer.makeError("Expected value, but got nothing")
        return when (buffer.peek()) {
            COMPOUND_BEGIN -> readCompoundTag()
            ARRAY_BEGIN -> buffer.tempt {
                advance()
                val possibleType = skipWhitespace().take()
                if (hasMore() && skipWhitespace().peek() == ARRAY_SIGNATURE_SEPARATOR) {
                    when (possibleType) {
                        TYPE_BYTE_ARRAY -> NbtByteArray(readByteArray())
                        TYPE_INT_ARRAY -> NbtIntArray(readIntArray())
                        TYPE_LONG_ARRAY -> NbtLongArray(readLongArray())
                        else -> buffer.makeError("Unknown array type '$possibleType' !")
                    }
                } else readListTag()
            }
            // definitely a string tag
            DOUBLE_QUOTE, SINGLE_QUOTE -> NbtString(unescape(buffer.takeUntil(buffer.take())))
            else -> when (val content = scalar()) {
                is String -> NbtString(content)
                is Int -> NbtInt(content)
                is Byte -> NbtByte(content)
                is Double -> NbtDouble(content)
                is Long -> NbtLong(content)
                is Short -> NbtShort(content)
                is Float -> NbtFloat(content)
                else -> buffer.makeError("Unknown scalar '$content' !")
            }
        }
    }

    fun readCompoundTag(): NbtCompound {
        beginCompound()
        val compound = NbtCompound()
        var key = beginCompoundEntry()
        while (key != EOF) {
            compound[key] = readTag()
            key = beginCompoundEntry()
        }
        endCompound()
        return compound
    }

    fun readListTag(): NbtList {
        val size = beginList()
        val list = if (size == UNKNOWN_SIZE) {
            NbtList().apply { while (beginListEntry()) add(readTag()) }
        } else {
            NbtList(size).apply { repeat(size) { add(readTag()) } }
        }
        endList()
        return list
    }

    override fun beginCompound() {
        buffer.skipWhitespace().expect(COMPOUND_BEGIN)
        firstEntry = true
    }

    override fun beginCompoundEntry(): String {
        buffer.skipWhitespace()
        if (buffer.peek() == COMPOUND_END) return EOF else {
            if (firstEntry) {
                firstEntry = false
            } else {
                val char = buffer.take()
                if (char != VALUE_SEPARATOR) buffer.makeError("Expected ',' or '}', but got '$char'")
            }

            val key = try {
                readString()
            } catch (_: StringifiedNbtParseException) {
                buffer.makeError("Expected key but got nothing") // 抛出空键异常
            }
            // 校验键值对分隔符 ":"
            buffer.skipWhitespace().expect(COMPOUND_KEY_TERMINATOR)
            // 校验空值
            buffer.tempt {
                val peek = skipWhitespace().peek()
                if (peek == VALUE_SEPARATOR || peek == COMPOUND_END) makeError("Expected value, but got nothing")
            }
            return key
        }
    }

    override fun endCompound() {
        buffer.expect(COMPOUND_END)
    }

    private fun beginArray(type: Char): Int {
        buffer.skipWhitespace().expect(ARRAY_BEGIN)
        buffer.skipWhitespace().expect(type, true)
        buffer.skipWhitespace().expect(ARRAY_SIGNATURE_SEPARATOR)
        firstEntry = true
        return if (buffer.skipWhitespace().peek() == ARRAY_END) 0 else UNKNOWN_SIZE
    }

    private fun beginCollectionEntry(): Boolean {
        if (buffer.skipWhitespace().peek() == ARRAY_END) return false

        if (firstEntry) {
            firstEntry = false
        } else {
            val char = buffer.take()
            if (char != VALUE_SEPARATOR) buffer.makeError("Expected ',' or ']', but got '$char'")
        }
        return true
    }

    private fun endCollection() {
        buffer.skipWhitespace().expect(ARRAY_END)
    }

    override fun beginList(): Int {
        buffer.skipWhitespace().expect(ARRAY_BEGIN)
        buffer.skipWhitespace()
        firstEntry = true
        return if (buffer.peek() == ARRAY_END) 0 else UNKNOWN_SIZE
    }

    override fun beginListEntry() = beginCollectionEntry()

    override fun endList() = endCollection()

    override fun beginByteArray(): Int = beginArray(TYPE_BYTE_ARRAY)
    override fun beginByteArrayEntry() = beginCollectionEntry()
    override fun endByteArray() = endCollection()

    override fun beginIntArray() = beginArray(TYPE_INT_ARRAY)
    override fun beginIntArrayEntry() = beginCollectionEntry()
    override fun endIntArray() = endCollection()

    override fun beginLongArray() = beginArray(TYPE_LONG_ARRAY)
    override fun beginLongArrayEntry() = beginCollectionEntry()
    override fun endLongArray() = endCollection()

    override fun readByte() = (scalar() as Number).toByte()

    override fun readShort() = (scalar() as Number).toShort()

    override fun readInt() = (scalar() as Number).toInt()

    override fun readLong() = (scalar() as Number).toLong()

    override fun readFloat() = (scalar() as Number).toFloat()

    override fun readDouble() = (scalar() as Number).toDouble()

    override fun readString(): String {
        if (!buffer.skipWhitespace().hasMore()) buffer.makeError("Expected String, but was EOF")
        return when (buffer.peek()) {
            DOUBLE_QUOTE, SINGLE_QUOTE -> unescape(buffer.takeUntil(buffer.take()))
            else -> scalar().toString().takeUnless { it.isEmpty() || it.isBlank() } ?: buffer.makeError("Expected String, but was EOF")
        }
    }

    /**
     * A tag that is definitely some sort of scalar.
     *
     * Does not detect quoted strings, so those should have been parsed already.
     *
     * @return a parsed value
     */
    private fun scalar(): Any {
        val builder = StringBuilder()
        var noLongerNumericAt = -1
        while (buffer.skipWhitespace().hasMore()) {
            var current = buffer.peek()
            if (current == ESCAPE_MARKER) { // escape -- we are significantly more lenient than original format at the moment
                buffer.advance()
                current = buffer.take()
            } else if (Tokens.id(current)) {
                buffer.advance()
            } else { // end of value
                break
            }
            builder.append(current)
            if (noLongerNumericAt == -1 && !Tokens.numeric(current)) {
                noLongerNumericAt = builder.length
            }
        }

        val length = builder.length
        val built = builder.toString()
        if (noLongerNumericAt == length && length > 1) {
            when (built.last().lowercaseChar()) {
                TYPE_BYTE -> return built.dropLast(1).toByteOrNull() ?: buffer.makeError("Expected Byte, but was '$built'")
                TYPE_SHORT -> return built.dropLast(1).toShortOrNull() ?: buffer.makeError("Expected Short, but was '$built'")
                TYPE_LONG.lowercaseChar() -> return built.dropLast(1).toLongOrNull() ?: buffer.makeError("Expected Long, but was '$built'")
                TYPE_FLOAT -> {
                    val floatValue = built.dropLast(1).toFloatOrNull() ?: buffer.makeError("Expected Float, but was '$built'")
                    // don't accept NaN and Infinity
                    if (floatValue.isFinite()) return floatValue
                }

                TYPE_DOUBLE -> {
                    val doubleValue = built.dropLast(1).toDoubleOrNull() ?: buffer.makeError("Expected Double, but was '$built'")
                    // don't accept NaN and Infinity
                    if (doubleValue.isFinite()) return doubleValue
                }
            }
        } else if (noLongerNumericAt == -1) { // if we run out of content without an explicit value separator, then we're either an integer or string tag -- all others have a character at the end
            val number = built.toIntOrNull()
            // if the string is a valid representation of a number.
            if (number != null) return number else {
                if (built.indexOf('.') != -1) { // see if we have an unsuffixed double; always needs a dot
                    return built.toDoubleOrNull() ?: buffer.makeError("Expected Double, but was '$built'")
                }
            }
        }

        if (built.equals(LITERAL_TRUE, true)) {
            return 1.toByte()
        } else if (built.equals(LITERAL_FALSE, true)) {
            return 0.toByte()
        }
        return built
    }

    /**
     * Remove simple escape sequences from a string.
     *
     * @param withEscapes input string with escapes
     * @return string with escapes processed
     */
    private fun unescape(withEscapes: String): String {
        var escapeIdx = withEscapes.indexOf(ESCAPE_MARKER)
        // nothing to unescape
        if (escapeIdx == -1) return withEscapes

        var lastEscape = 0
        return buildString(withEscapes.length) {
            do {
                append(withEscapes, lastEscape, escapeIdx)
                lastEscape = escapeIdx + 1
            } while ((withEscapes.indexOf(ESCAPE_MARKER, lastEscape + 1)
                    .also { escapeIdx = it }) != -1
            ) // add one extra character to make sure we don't include escaped backslashes
            append(withEscapes.substring(lastEscape))
        }
    }

}