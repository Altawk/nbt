import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.exception.StringifiedNbtParseException
import cn.altawk.nbt.tag.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.decodeFromString
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringifiedNbtReaderTest {

    private fun check(expected: NbtTag, snbt: String) {
        assertEquals(
            expected = expected,
            actual = NbtFormat.decodeFromString(NbtTag.serializer(), snbt),
            message = "Parsed \"$snbt\" incorrectly.",
        )

        when (expected) {
            is NbtByte -> assertEquals(expected.content, NbtFormat.decodeFromString(Byte.serializer(), snbt))
            is NbtShort -> assertEquals(expected.content, NbtFormat.decodeFromString(Short.serializer(), snbt))
            is NbtInt -> assertEquals(expected.content, NbtFormat.decodeFromString(Int.serializer(), snbt))
            is NbtLong -> assertEquals(expected.content, NbtFormat.decodeFromString(Long.serializer(), snbt))
            is NbtFloat -> assertEquals(expected.content, NbtFormat.decodeFromString(Float.serializer(), snbt))
            is NbtDouble -> assertEquals(expected.content, NbtFormat.decodeFromString(Double.serializer(), snbt))
            is NbtByteArray -> assertContentEquals(expected.content, NbtFormat.decodeFromString(ByteArraySerializer(), snbt))
            is NbtIntArray -> assertContentEquals(expected.content, NbtFormat.decodeFromString(IntArraySerializer(), snbt))
            is NbtLongArray -> assertContentEquals(expected.content, NbtFormat.decodeFromString(LongArraySerializer(), snbt))
            is NbtString -> assertEquals(expected.content, NbtFormat.decodeFromString(String.serializer(), snbt))
            is NbtCompound -> assertEquals(expected.toMap(), NbtFormat.decodeFromString(MapSerializer(String.serializer(), NbtTag.serializer()), snbt))
            is NbtList -> assertEquals(expected.toList(), NbtFormat.decodeFromString(ListSerializer(NbtTag.serializer()), snbt))
        }
    }

    @Test
    fun Should_read_Byte_correctly() {
        check(NbtByte(0), "0b")
        check(NbtByte(Byte.MIN_VALUE), "${Byte.MIN_VALUE}b")
        check(NbtByte(Byte.MAX_VALUE), "${Byte.MAX_VALUE}b")
        check(NbtByte(0), "false")
        check(NbtByte(1), "true")

        check(NbtByte(0), " 0b ")
    }

    @Test
    fun Should_read_Short_correctly() {
        check(NbtShort(0), "0s")
        check(NbtShort(Short.MIN_VALUE), "${Short.MIN_VALUE}s")
        check(NbtShort(Short.MAX_VALUE), "${Short.MAX_VALUE}s")

        check(NbtShort(0), " 0s ")
    }

    @Test
    fun Should_read_Int_correctly() {
        check(NbtInt(0), "0")
        check(NbtInt(Int.MIN_VALUE), "${Int.MIN_VALUE}")
        check(NbtInt(Int.MAX_VALUE), "${Int.MAX_VALUE}")

        check(NbtInt(0), " 0 ")
    }

    @Test
    fun Should_read_Long_correctly() {
        check(NbtLong(0), "0L")
        check(NbtLong(Long.MIN_VALUE), "${Long.MIN_VALUE}L")
        check(NbtLong(Long.MAX_VALUE), "${Long.MAX_VALUE}L")

        check(NbtLong(0), " 0L ")
    }

    @Test
    fun Should_read_Float_correctly() {
        check(NbtFloat(0.0f), "0f")
        check(NbtFloat(0.1f), "0.1f")
        check(NbtFloat(0.1f), ".1f")
        check(NbtFloat(1.0f), "1.f")
        check(NbtFloat(Float.MIN_VALUE), "${Float.MIN_VALUE}f")
        check(NbtFloat(Float.MAX_VALUE), "${Float.MAX_VALUE}f")
        check(NbtFloat(-Float.MIN_VALUE), "-${Float.MIN_VALUE}f")
        check(NbtFloat(-Float.MAX_VALUE), "-${Float.MAX_VALUE}f")
        check(NbtFloat(1.23e4f), "1.23e4f")
        check(NbtFloat(-56.78e-9f), "-56.78e-9f")

        check(NbtFloat(0f), " 0f ")
    }

    @Test
    fun Should_read_Double_correctly() {
        check(NbtDouble(0.0), "0d")
        check(NbtDouble(0.1), "0.1d")
        check(NbtDouble(0.1), ".1d")
        check(NbtDouble(1.0), "1.d")
        check(NbtDouble(Double.MIN_VALUE), "${Double.MIN_VALUE}d")
        check(NbtDouble(Double.MAX_VALUE), "${Double.MAX_VALUE}d")
        check(NbtDouble(-Double.MIN_VALUE), "-${Double.MIN_VALUE}d")
        check(NbtDouble(-Double.MAX_VALUE), "-${Double.MAX_VALUE}d")
        check(NbtDouble(1.23e4), "1.23e4d")
        check(NbtDouble(-56.78e-9), "-56.78e-9d")

        check(NbtDouble(0.1), "0.1")
        check(NbtDouble(0.1), ".1")
        check(NbtDouble(1.0), "1.")
        check(NbtDouble(Double.MIN_VALUE), "${Double.MIN_VALUE}")
        check(NbtDouble(Double.MAX_VALUE), "${Double.MAX_VALUE}")
        check(NbtDouble(-Double.MIN_VALUE), "-${Double.MIN_VALUE}")
        check(NbtDouble(-Double.MAX_VALUE), "-${Double.MAX_VALUE}")
        check(NbtDouble(1.23e4), "1.23e4")
        check(NbtDouble(-56.78e-9), "-56.78e-9")

        check(NbtDouble(0.0), " .0 ")
    }

    @Test
    fun Should_parse_ByteArray_correctly() {
        check(NbtByteArray(byteArrayOf()), "[B;]")
        check(NbtByteArray(byteArrayOf(1, 2, 3)), "[B; 1b, 2b, 3b]")

        check(NbtByteArray(byteArrayOf(1, 2, 3)), " [ B ; 1b , 2b , 3b ] ")
    }

    @Test
    fun Should_parse_IntArray_correctly() {
        check(NbtIntArray(intArrayOf()), "[I;]")
        check(NbtIntArray(intArrayOf(1, 2, 3)), "[I; 1, 2, 3]")

        check(NbtIntArray(intArrayOf(1, 2, 3)), " [ I ; 1 , 2 , 3 ] ")
    }

    @Test
    fun Should_parse_LongArray_correctly() {
        check(NbtLongArray(longArrayOf()), "[L;]")
        check(NbtLongArray(longArrayOf(1, 2, 3)), "[L; 1L, 2L, 3L]")

        check(NbtLongArray(longArrayOf(1, 2, 3)), " [ L ; 1L , 2L , 3L ] ")
    }

    @Test
    fun Should_parse_String_correctly() {
        check(NbtString(""), "''")
        check(NbtString(""), "\"\"")
        check(NbtString("one"), "one")
        check(NbtString("a1"), "a1")
        check(NbtString("2x"), "2x")
        check(NbtString("2_2"), "2_2")
        check(NbtString("'"), "\"'\"")
        check(NbtString("\""), "'\"'")
        check(NbtString("'"), "'\\''")
        check(NbtString("\""), "\"\\\"\"")
    }

    @Test
    fun Should_parse_List_correctly() {
        check(NbtList { }, "[]")
        check(NbtList { add(0.toByte()) }, "[0b]")
        check(NbtList { add(0.toShort()) }, "[0s]")
        check(NbtList { add(0) }, "[0]")
        check(NbtList { add(0.toLong()) }, "[0L]")
        check(NbtList { add(0f) }, "[0f]")
        check(NbtList { add(0.0) }, "[0d]")
        check(NbtList { add(byteArrayOf()) }, "[[B;]]")
        check(NbtList { add(intArrayOf()) }, "[[I;]]")
        check(NbtList { add(longArrayOf()) }, "[[L;]]")
        check(NbtList { addList { } }, "[[]]")
        check(NbtList { addCompound { } }, "[{}]")

        check(NbtList {
            addList { add(1) }
            addList { add(2.toByte()); add(3.toByte()) }
        }, " [ [ 1 ] , [ 2b , 3b ] ] ")
    }

    @Test
    fun Should_parse_Compound_correctly() {
        check(NbtCompound { }, "{}")
        check(NbtCompound { put("one", 1) }, "{one: 1}")
        check(NbtCompound { put("", 0) }, "{'': 0}")
        check(NbtCompound { put("", 0.toByte()) }, "{\"\": 0b}")

        check(
            NbtCompound {
                putCompound("") {
                    put("1234", 1234)
                }
            },
            " { '' : { 1234 : 1234 } } ",
        )
    }

    @Test
    fun Should_fail_on_missing_key() {
        assertFailsWith<StringifiedNbtParseException> { NbtFormat.decodeFromString(NbtTag.serializer(), "{ : value}") }
    }

    @Test
    fun Should_fail_on_missing_value() {
        assertFailsWith<StringifiedNbtParseException> { NbtFormat.decodeFromString(NbtTag.serializer(), "{ key: }") }
    }

    @Test
    fun Should_fail_if_only_whitespace() {
        assertFailsWith<StringifiedNbtParseException> { NbtFormat.decodeFromString(NbtTag.serializer(), "") }
        assertFailsWith<StringifiedNbtParseException> { NbtFormat.decodeFromString(NbtTag.serializer(), "    ") }

        assertFailsWith<StringifiedNbtParseException> { NbtFormat.decodeFromString<String>("") }
        assertFailsWith<StringifiedNbtParseException> { NbtFormat.decodeFromString<String>("    ") }
    }

}
