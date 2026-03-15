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
            is NbtByte -> {
                assertEquals(expected.content, NbtFormat.decodeFromString(Byte.serializer(), snbt))
            }

            is NbtShort -> {
                assertEquals(expected.content, NbtFormat.decodeFromString(Short.serializer(), snbt))
            }

            is NbtInt -> {
                assertEquals(expected.content, NbtFormat.decodeFromString(Int.serializer(), snbt))
            }

            is NbtLong -> {
                assertEquals(expected.content, NbtFormat.decodeFromString(Long.serializer(), snbt))
            }

            is NbtFloat -> {
                assertEquals(expected.content, NbtFormat.decodeFromString(Float.serializer(), snbt))
            }

            is NbtDouble -> {
                assertEquals(expected.content, NbtFormat.decodeFromString(Double.serializer(), snbt))
            }

            is NbtByteArray -> {
                assertContentEquals(
                    expected.content,
                    NbtFormat.decodeFromString(ByteArraySerializer(), snbt),
                )
            }

            is NbtIntArray -> {
                assertContentEquals(
                    expected.content,
                    NbtFormat.decodeFromString(IntArraySerializer(), snbt),
                )
            }

            is NbtLongArray -> {
                assertContentEquals(
                    expected.content,
                    NbtFormat.decodeFromString(LongArraySerializer(), snbt),
                )
            }

            is NbtString -> {
                assertEquals(expected.content, NbtFormat.decodeFromString(String.serializer(), snbt))
            }

            is NbtCompound -> {
                assertEquals(
                    expected.toMap(),
                    NbtFormat.decodeFromString(
                        MapSerializer(String.serializer(), NbtTag.serializer()),
                        snbt,
                    ),
                )
            }

            is NbtList -> {
                assertEquals(
                    expected.toList(),
                    NbtFormat.decodeFromString(ListSerializer(NbtTag.serializer()), snbt),
                )
            }
        }
    }

    @Test
    fun should_read_byte_correctly() {
        check(NbtByte(0), "0b")
        check(NbtByte(Byte.MIN_VALUE), "${Byte.MIN_VALUE}b")
        check(NbtByte(Byte.MAX_VALUE), "${Byte.MAX_VALUE}b")
        check(NbtByte(0), "false")
        check(NbtByte(1), "true")
        check(NbtByte(0), "FALSE")
        check(NbtByte(1), "TrUe")

        check(NbtByte(0), " 0b ")
    }

    @Test
    fun should_read_short_correctly() {
        check(NbtShort(0), "0s")
        check(NbtShort(Short.MIN_VALUE), "${Short.MIN_VALUE}s")
        check(NbtShort(Short.MAX_VALUE), "${Short.MAX_VALUE}s")

        check(NbtShort(0), " 0s ")
    }

    @Test
    fun should_read_int_correctly() {
        check(NbtInt(0), "0")
        check(NbtInt(Int.MIN_VALUE), "${Int.MIN_VALUE}")
        check(NbtInt(Int.MAX_VALUE), "${Int.MAX_VALUE}")

        check(NbtInt(0), " 0 ")
    }

    @Test
    fun should_read_long_correctly() {
        check(NbtLong(0), "0L")
        check(NbtLong(Long.MIN_VALUE), "${Long.MIN_VALUE}L")
        check(NbtLong(Long.MAX_VALUE), "${Long.MAX_VALUE}L")

        check(NbtLong(0), " 0L ")
    }

    @Test
    fun should_read_float_correctly() {
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
    fun should_read_double_correctly() {
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
    fun should_parse_byte_array_correctly() {
        check(NbtByteArray(byteArrayOf()), "[B;]")
        check(NbtByteArray(byteArrayOf(1, 2, 3)), "[B; 1b, 2b, 3b]")

        check(NbtByteArray(byteArrayOf(1, 2, 3)), " [ B ; 1b , 2b , 3b ] ")

        // Implicit type suffix: values without suffix assume the array's type
        check(NbtByteArray(byteArrayOf(1, 2)), "[B;1,2]")
        check(NbtByteArray(byteArrayOf(1, 2)), "[B; 1, 2]")
    }

    @Test
    fun should_parse_int_array_correctly() {
        check(NbtIntArray(intArrayOf()), "[I;]")
        check(NbtIntArray(intArrayOf(1, 2, 3)), "[I; 1, 2, 3]")

        check(NbtIntArray(intArrayOf(1, 2, 3)), " [ I ; 1 , 2 , 3 ] ")

        // Smaller types are allowed and widened to int
        check(NbtIntArray(intArrayOf(1, 2, 3)), "[I;1b,2s,3]")
    }

    @Test
    fun should_parse_long_array_correctly() {
        check(NbtLongArray(longArrayOf()), "[L;]")
        check(NbtLongArray(longArrayOf(1, 2, 3)), "[L; 1L, 2L, 3L]")

        check(NbtLongArray(longArrayOf(1, 2, 3)), " [ L ; 1L , 2L , 3L ] ")

        // Smaller types are allowed and widened to long
        check(NbtLongArray(longArrayOf(1, 2, 3)), "[L;1b,2s,3]")
    }

    @Test
    fun should_parse_array_with_trailing_comma() {
        check(NbtByteArray(byteArrayOf(1, 2)), "[B;1b,2b,]")
        check(NbtIntArray(intArrayOf(1, 2)), "[I;1,2,]")
        check(NbtLongArray(longArrayOf(1, 2)), "[L;1L,2L,]")
    }

    @Test
    fun should_parse_string_correctly() {
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
        check(NbtString("escaped \"quote\" and slash"), "\"escaped \\\"quote\\\" and slash\"")
    }

    @Test
    fun should_parse_list_correctly() {
        check(NbtList {}, "[]")
        check(NbtList { add(0.toByte()) }, "[0b]")
        check(NbtList { add(0.toShort()) }, "[0s]")
        check(NbtList { add(0) }, "[0]")
        check(NbtList { add(0.toLong()) }, "[0L]")
        check(NbtList { add(0f) }, "[0f]")
        check(NbtList { add(0.0) }, "[0d]")
        check(NbtList { add(byteArrayOf()) }, "[[B;]]")
        check(NbtList { add(intArrayOf()) }, "[[I;]]")
        check(NbtList { add(longArrayOf()) }, "[[L;]]")
        check(NbtList { addList {} }, "[[]]")
        check(NbtList { addCompound {} }, "[{}]")

        check(
            NbtList {
                addList { add(1) }
                addList {
                    add(2.toByte())
                    add(3.toByte())
                }
            },
            " [ [ 1 ] , [ 2b , 3b ] ] ",
        )

        // Mixed types in list
        check(
            NbtList {
                add("")
                addCompound { put("text", "hello") }
                add(123)
            },
            "['', {text:\"hello\"}, 123]",
        )
    }

    @Test
    fun should_parse_list_with_trailing_comma() {
        // Trailing comma is allowed after a valid element
        check(NbtList { add(1); add(2) }, "[1,2,]")
        check(NbtList { add(1); add(2) }, "[1, 2, ]")
        check(NbtList { add(1.toByte()) }, "[1b,]")
        // Lenient: multiple trailing commas and leading commas are tolerated
        check(NbtList {}, "[,]")
        check(NbtList { add(1) }, "[1,,]")
        check(NbtList { add(1); add(2) }, "[1,,2]")
    }

    @Test
    fun should_parse_compound_correctly() {
        check(NbtCompound {}, "{}")
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
    fun should_parse_compound_with_trailing_comma() {
        // Trailing comma is allowed after a valid entry
        check(NbtCompound { put("a", "b") }, "{a:b,}")
        check(NbtCompound { put("a", 1); put("b", 2) }, "{a:1,b:2,}")
        check(NbtCompound { put("a", 1); put("b", 2) }, "{a: 1, b: 2, }")
        // Lenient: multiple trailing commas and leading commas are tolerated
        check(NbtCompound {}, "{,}")
        check(NbtCompound { put("a", "b") }, "{a:b,,}")
    }

    @Test
    fun should_round_trip_nested_compound_structure() {
        val expected = NbtCompound {
            put("name", "root")
            putList("items") {
                addCompound {
                    put("id", 1)
                    putList("flags") {
                        add(1.toByte())
                        add(0.toByte())
                    }
                }
                addCompound {
                    put("id", 2)
                    putCompound("meta") {
                        put("enabled", 1.toByte())
                        put("title", "demo")
                    }
                }
            }
        }
        val snbt = expected.toString()

        check(expected, snbt)
    }

    @Test
    fun should_fail_on_missing_key() {
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "{ : value}")
        }
    }

    @Test
    fun should_fail_on_missing_value() {
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "{ key: }")
        }
    }

    @Test
    fun should_fail_if_only_whitespace() {
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "")
        }
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "    ")
        }

        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString<String>("")
        }
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString<String>("    ")
        }
    }

    @Test
    fun should_fail_on_unclosed_string() {
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "\"unterminated")
        }
    }

    @Test
    fun should_fail_on_unclosed_list() {
        assertFailsWith<Throwable> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "[1b, 2b")
        }
    }

    @Test
    fun should_fail_on_unclosed_compound() {
        assertFailsWith<Throwable> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "{key: 1b")
        }
    }

    @Test
    fun should_fail_on_invalid_array_type() {
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "[Q; 1]")
        }
    }

    // #1 Nested structures should preserve firstEntry state
    @Test
    fun should_parse_nested_structures_correctly() {
        // After inner compound ends, outer compound must still require comma
        check(
            NbtCompound {
                putCompound("a") { put("b", 1) }
                put("c", 2)
            },
            "{a:{b:1},c:2}",
        )
        // After inner list ends, outer list must still require comma
        check(
            NbtList {
                addList { add(1) }
                addList { add(2) }
            },
            "[[1],[2]]",
        )
        // Compound inside list inside compound
        check(
            NbtCompound {
                putList("items") {
                    addCompound { put("id", 1) }
                    addCompound { put("id", 2) }
                }
                put("count", 2)
            },
            "{items:[{id:1},{id:2}],count:2}",
        )
    }

    // #1 Missing comma after nested structure must be rejected
    @Test
    fun should_fail_on_missing_comma_after_nested_structure() {
        // {a:{b:1}c:2} — missing comma between entries
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "{a:{b:1}c:2}")
        }
        // {a:[1]c:2} — missing comma after nested list
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "{a:[1]c:2}")
        }
        // [[1][2]] — missing comma between nested lists
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "[[1][2]]")
        }
        // Empty nested: {a:{}c:2} — firstEntry left as true without stack protection
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "{a:{}c:2}")
        }
        // Empty nested list: {a:[]c:2}
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "{a:[]c:2}")
        }
        // Empty nested lists: [[][]]
        assertFailsWith<StringifiedNbtParseException> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "[[][]]")
        }
    }

    // #2 Backslash escaping round-trip
    @Test
    fun should_round_trip_strings_with_backslash() {
        val tag = NbtString("hello\\world")
        val snbt = tag.toString()
        check(tag, snbt)

        val tag2 = NbtString("a\\\"b")
        val snbt2 = tag2.toString()
        check(tag2, snbt2)
    }

    // #4 Trailing backslash in unquoted scalar should not crash (graceful handling)
    @Test
    fun should_handle_trailing_backslash_in_scalar() {
        // Trailing backslash: should not throw IndexOutOfBoundsException
        // Lenient: the backslash is ignored, parsed as "abc"
        check(NbtString("abc"), "abc\\")
    }

    // #5 Truncated input should throw, not crash with IndexOutOfBounds
    @Test
    fun should_fail_gracefully_on_truncated_input() {
        assertFailsWith<Throwable> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "[")
        }
        assertFailsWith<Throwable> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "{")
        }
        assertFailsWith<Throwable> {
            NbtFormat.decodeFromString(NbtTag.serializer(), "[B;")
        }
    }

    // #8 Large integers without suffix should parse as Long, scientific notation as Double
    @Test
    fun should_parse_large_int_as_long() {
        check(NbtLong(2147483648L), "2147483648")
        check(NbtLong(-2147483649L), "-2147483649")
    }

    @Test
    fun should_parse_scientific_notation_without_dot_as_double() {
        check(NbtDouble(1e4), "1e4")
        check(NbtDouble(-5E3), "-5E3")
    }
}
