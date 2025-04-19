package cn.altawk.nbt.tag

/**
 * NbtType
 *
 * @author TheFloodDragon
 * @since 2024/11/24 10:35
 */
public enum class NbtType(public val id: Byte) {

    END(0),
    BYTE(1),
    SHORT(2),
    INT(3),
    LONG(4),
    FLOAT(5),
    DOUBLE(6),
    BYTE_ARRAY(7),
    STRING(8),
    LIST(9),
    COMPOUND(10),
    INT_ARRAY(11),
    LONG_ARRAY(12);

    public companion object {

        /**
         * Get the [NbtType] by its [id].
         */
        public fun of(id: Byte): NbtType? = entries.find { it.id == id }

    }

}