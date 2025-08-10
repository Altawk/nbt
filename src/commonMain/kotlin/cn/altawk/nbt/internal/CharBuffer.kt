package cn.altawk.nbt.internal

import cn.altawk.nbt.exception.StringifiedNbtParseException

/**
 * CharBuffer
 *
 * refer to [adventure-nbt](https://github.com/KyoriPowered/adventure/blob/main/4/nbt/src/main/java/net/kyori/adventure/nbt/CharBuffer.java)
 *
 * @author TheFloodDragon
 * @since 2025/3/15 11:17
 */
internal class CharBuffer(private val sequence: CharSequence) {
    private var index = 0

    /**
     * Get the character at the current position.
     *
     * @return The current character
     */
    fun peek(): Char = sequence[index]

    fun peek(offset: Int): Char = sequence[index + offset]

    /**
     * Get the current character and advance.
     *
     * @return current character
     */
    fun take(): Char = sequence[index++]

    fun advance(): Boolean {
        index++
        return this.hasMore()
    }

    fun hasMore(): Boolean = this.index < sequence.length

    fun hasMore(offset: Int): Boolean = this.index + offset < sequence.length

    /**
     * Search for the provided token, and advance the reader index past the `until` character.
     *
     * @param until case-insensitive token
     * @return the string starting at the current position (inclusive) and going until the location of `until`, exclusive
     * @throws StringifiedNbtParseException if `until` is not present in the remaining string
     */
    @Throws(StringifiedNbtParseException::class)
    fun takeUntil(until: Char): String {
        val u = until.lowercaseChar()
        var endIdx = -1
        var idx = this.index
        while (idx < sequence.length) {
            val char = sequence[idx]
            if (char == Tokens.ESCAPE_MARKER) {
                idx++
            } else if (char.lowercaseChar() == u) {
                endIdx = idx
                break
            }
            ++idx
        }
        if (endIdx == -1) {
            this.makeError("No occurrence of $u was found")
        }

        val result = sequence.substring(this.index, endIdx)
        this.index = endIdx + 1
        return result
    }

    /**
     * Assert that the next non-whitespace character is the provided parameter.
     *
     *
     * If the assertion is successful, the token will be consumed.
     *
     * @param expectedChar expected character
     * @return this
     * @throws StringifiedNbtParseException if EOF or non-matching value is found
     */
    @Throws(StringifiedNbtParseException::class)
    fun expect(expectedChar: Char, ignoreCase: Boolean = false): CharBuffer = this.apply {
        this.skipWhitespace()
        if (!this.hasMore()) {
            this.makeError("Expected character '$expectedChar' but got EOF")
        }
        if (
            (ignoreCase && this.peek().lowercaseChar() != expectedChar.lowercaseChar())
            || this.peek() != expectedChar
        ) {
            this.makeError("Expected character '" + expectedChar + "' but got '" + this.peek() + "'")
        }
        this.take()
    }

    fun <R> tempt(action: CharBuffer.() -> R): R {
        val newBuffer = CharBuffer(sequence)
        newBuffer.index = this.index
        return action(newBuffer)
    }

    fun skipWhitespace(): CharBuffer = this.apply {
        while (this.hasMore() && Character.isWhitespace(this.peek())) this.advance()
    }

    @Throws(StringifiedNbtParseException::class)
    internal fun makeError(message: String): Nothing = throw StringifiedNbtParseException(message, this.sequence, this.index)

}
