package cn.altawk.nbt.internal

/**
 * Tokens
 *
 * @author TheFloodDragon
 * @since 2025/2/22 15:27
 */
internal object Tokens {

    // Pretty Print Defaults
    const val PRETTY_PRINT_SPACE: String = " "
    const val PRETTY_PRINT_INDENT: String = "    "

    // Compounds
    const val COMPOUND_BEGIN: Char = '{'
    const val COMPOUND_END: Char = '}'
    const val COMPOUND_KEY_TERMINATOR: Char = ':'

    // Arrays
    const val ARRAY_BEGIN: Char = '['
    const val ARRAY_END: Char = ']'
    const val ARRAY_SIGNATURE_SEPARATOR: Char = ';'

    const val VALUE_SEPARATOR: Char = ','

    const val SINGLE_QUOTE: Char = '\''
    const val DOUBLE_QUOTE: Char = '"'
    const val ESCAPE_MARKER: Char = '\\'

    const val TYPE_INT_ARRAY: Char = 'I'
    const val TYPE_BYTE_ARRAY: Char = 'B'
    const val TYPE_LONG_ARRAY: Char = 'L'
    const val TYPE_BYTE: Char = 'b'
    const val TYPE_SHORT: Char = 's'
    const val TYPE_LONG: Char = 'L'
    const val TYPE_FLOAT: Char = 'f'
    const val TYPE_DOUBLE: Char = 'd'

    const val LITERAL_TRUE: String = "true"
    const val LITERAL_FALSE: String = "false"

    val NEWLINE: String = System.getProperty("line.separator", "\n")

    /**
     * Return if a character is a valid component in an identifier.
     *
     *
     * An identifier character must match the expression `[a-zA-Z0-9_+.-]`
     *
     * @param c the character
     * @return identifier
     */
    fun id(c: Char): Boolean {
        return (c in 'a'..'z')
                || (c in 'A'..'Z')
                || (c in '0'..'9')
                || c == '-' || c == '_' || c == '.' || c == '+'
    }

    /**
     * Return whether a character could be at some position in a number.
     *
     *
     * A string passing this check does not necessarily mean it is syntactically valid.
     *
     * @param c character to check
     * @return if possibly part of a number
     */
    fun numeric(c: Char): Boolean {
        return (c in '0'..'9') // digit
                || c == '+' || c == '-' // positive or negative
                || c == 'e' || c == 'E' // exponent
                || c == '.' // decimal
    }

}