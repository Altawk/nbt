package cn.altawk.nbt.exception


/**
 * StringifiedNbtParseException
 *
 * An exception thrown when parsing a tag.
 *
 * @author TheFloodDragon
 * @since 2025/3/15 11:21
 */
public class StringifiedNbtParseException(
    message: String,
    private val buffer: CharSequence,
    private val position: Int
) : NbtDecodingException(message) {

    override val message: String get() = super.message + " (at position " + this.position + ")"

}