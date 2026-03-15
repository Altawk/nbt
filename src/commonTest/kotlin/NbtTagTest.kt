import cn.altawk.nbt.tag.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/**
 * NbtTagTest
 *
 * @author TheFloodDragon
 * @since 2025/1/25 16:06
 */
class NbtTagToStringTest {
    @Test
    fun should_convert_nbt_byte_to_string() {
        assertEquals("7b", NbtByte(7).toString())
    }

    @Test
    fun should_convert_nbt_short_to_string() {
        assertEquals("8s", NbtShort(8).toString())
    }

    @Test
    fun should_convert_nbt_int_to_string() {
        assertEquals("9", NbtInt(9).toString())
    }

    @Test
    fun should_convert_nbt_long_to_string() {
        assertEquals("10L", NbtLong(10).toString())
    }

    @Test
    fun should_convert_nbt_float_to_string() {
        assertEquals("3.1415f", NbtFloat(3.1415f).toString())
    }

    @Test
    fun should_convert_nbt_double_to_string() {
        assertEquals("2.71828d", NbtDouble(2.71828).toString())
    }

    @Test
    fun should_convert_nbt_byte_array_to_string() {
        assertEquals("[B;4B,3B,2B,1B]", NbtByteArray(byteArrayOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun should_convert_nbt_string_to_string() {
        assertEquals("\"\"", NbtString("").toString())
        assertEquals("\"hello\"", NbtString("hello").toString())
        assertEquals("\"\\\"double-quoted\\\"\"", NbtString("\"double-quoted\"").toString())
        assertEquals("\"'single-quoted'\"", NbtString("'single-quoted'").toString())
        assertEquals("\"\\\"'multi-quoted'\\\"\"", NbtString("\"'multi-quoted'\"").toString())
    }

    @Test
    fun should_convert_nbt_list_to_string() {
        assertEquals("[]", NbtList().toString())
        assertEquals("[1b]", NbtList(mutableListOf(NbtByte(1))).toString())
        assertEquals("[[]]", NbtList(mutableListOf(NbtList())).toString())
    }

    @Test
    fun should_convert_nbt_compound_to_string() {
        assertEquals("{}", NbtCompound().toString())
        assertEquals("{a:1b}", NbtCompound().apply { put("a", 1.toByte()) }.toString())
        assertEquals(
            "{a:1b,b:7}",
            NbtCompound().apply {
                put("a", 1.toByte())
                put("b", 7)
            }.toString(),
        )
    }

    @Test
    fun should_convert_nbt_int_array_to_string() {
        assertEquals("[I;4,3,2,1]", NbtIntArray(intArrayOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun should_convert_nbt_long_array_to_string() {
        assertEquals("[L;4L,3L,2L,1L]", NbtLongArray(longArrayOf(4, 3, 2, 1)).toString())
    }
}

class NbtByteArrayTest {
    @Test
    fun should_not_equal_nbt_tag_of_different_type_with_same_contents() {
        assertNotEquals<NbtTag>(NbtList(), NbtByteArray(byteArrayOf()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtByteArray(byteArrayOf()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtByteArray(byteArrayOf()))
    }
}

class NbtListTest {
    @Test
    fun should_equal_list_of_same_contents() {
        fun assertWith(nbtList: NbtList) {
            assertEquals(nbtList.toList(), nbtList)
        }

        assertWith(NbtList.of(emptyList<NbtByte>()))
        assertWith(NbtList(mutableListOf(NbtInt(1))))
        assertWith(NbtList(mutableListOf(NbtString("a"), NbtString("b"))))

        assertEquals(
            NbtList(mutableListOf(NbtInt(1))),
            NbtList(NbtList(mutableListOf(NbtInt(1)))),
        )
    }

    @Test
    fun should_report_element_type_from_first_element() {
        assertEquals(NbtType.END, NbtList().elementType)
        assertEquals(NbtType.INT, NbtList(mutableListOf(NbtInt(1), NbtInt(2))).elementType)
        assertEquals(NbtType.STRING, NbtList(mutableListOf(NbtString("a"), NbtInt(2))).elementType)
    }

    @Test
    fun should_not_equal_nbt_tag_of_different_type_with_same_contents() {
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtList())
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtList())
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtList())
    }
}

class NbtCompoundTest {
    @Test
    fun should_equal_map_of_same_contents() {
        fun assertWith(vararg elements: Pair<String, NbtTag>) {
            assertEquals(elements.toMap(), NbtCompound.of(mapOf(*elements)))
        }

        assertWith()
        assertWith("one" to NbtInt(1))
        assertWith("a" to NbtString("a"), "b" to NbtString("b"))

        assertEquals(
            NbtList(mutableListOf(NbtInt(1))),
            NbtList(NbtList(mutableListOf(NbtInt(1)))),
        )
    }

    @Test
    fun should_merge_deeply_without_replacing_when_replace_is_false() {
        val source = NbtCompound().apply {
            putCompound("nested") {
                put("keep", 1)
                put("shared", 2)
            }
            put("top", "original")
        }
        val target = NbtCompound().apply {
            putCompound("nested") {
                put("shared", 99)
                put("added", 3)
            }
            put("top", "updated")
            put("newKey", 4)
        }

        source.merge(target, replace = false)

        assertEquals(
            NbtCompound().apply {
                putCompound("nested") {
                    put("keep", 1)
                    put("shared", 2)
                    put("added", 3)
                }
                put("top", "original")
                put("newKey", 4)
            },
            source,
        )
    }

    @Test
    fun should_merge_deeply_with_replacement_when_replace_is_true() {
        val source = NbtCompound().apply {
            putCompound("nested") {
                put("keep", 1)
                put("shared", 2)
            }
            put("top", "original")
        }
        val target = NbtCompound().apply {
            putCompound("nested") {
                put("shared", 99)
                put("added", 3)
            }
            put("top", "updated")
        }

        source.merge(target, replace = true)

        assertEquals(
            NbtCompound().apply {
                putCompound("nested") {
                    put("keep", 1)
                    put("shared", 99)
                    put("added", 3)
                }
                put("top", "updated")
            },
            source,
        )
    }

    @Test
    fun should_merge_shallowly_without_replacing_when_replace_is_false() {
        val nested = NbtCompound().apply { put("value", 1) }
        val source = NbtCompound().apply {
            put("nested", nested)
            put("name", "source")
        }
        val target = NbtCompound().apply {
            put("nested", NbtCompound().apply { put("value", 2) })
            put("name", "target")
            put("newKey", 3)
        }

        source.mergeShallow(target, replace = false)

        assertSame(nested, source["nested"])
        assertEquals(
            NbtCompound().apply {
                put("nested", nested)
                put("name", "source")
                put("newKey", 3)
            },
            source,
        )
    }

    @Test
    fun should_merge_shallowly_with_replacement_when_replace_is_true() {
        val source = NbtCompound().apply {
            putCompound("nested") { put("value", 1) }
            put("name", "source")
        }
        val replacement = NbtCompound().apply { put("value", 2) }
        val target = NbtCompound().apply {
            put("nested", replacement)
            put("name", "target")
        }

        source.mergeShallow(target, replace = true)

        assertSame(replacement, source["nested"])
        assertEquals(
            NbtCompound().apply {
                put("nested", replacement)
                put("name", "target")
            },
            source,
        )
    }

    @Test
    fun should_clone_deeply_but_clone_shallowly_share_nested_references() {
        val nestedList = NbtList(mutableListOf(NbtInt(1)))
        val nestedCompound = NbtCompound().apply { put("child", nestedList) }
        val original = NbtCompound().apply {
            put("nested", nestedCompound)
            put("title", "demo")
        }

        val deepClone = original.clone()
        val shallowClone = original.cloneShallow()

        assertNotSame(original, deepClone)
        assertNotSame(original["nested"], deepClone["nested"])
        assertSame(original["nested"], shallowClone["nested"])

        (original["nested"] as NbtCompound)["extra"] = NbtString("value")

        assertEquals(null, (deepClone["nested"] as NbtCompound)["extra"])
        assertEquals(NbtString("value"), (shallowClone["nested"] as NbtCompound)["extra"])
    }
}

class NbtIntArrayTest {
    @Test
    fun should_not_equal_nbt_tag_of_different_type_with_same_contents() {
        assertNotEquals<NbtTag>(NbtList.of(emptyList<NbtInt>()), NbtIntArray(intArrayOf()))
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtIntArray(intArrayOf()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtIntArray(intArrayOf()))
    }
}

class NbtLongArrayTest {
    @Test
    fun should_not_equal_nbt_tag_of_different_type_with_same_contents() {
        assertNotEquals<NbtTag>(NbtList.of(emptyList<NbtLong>()), NbtLongArray(longArrayOf()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtLongArray(longArrayOf()))
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtLongArray(longArrayOf()))
    }
}
