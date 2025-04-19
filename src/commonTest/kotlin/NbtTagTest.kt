import cn.altawk.nbt.tag.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * NbtTagTest
 *
 * @author TheFloodDragon
 * @since 2025/1/25 16:06
 */

class NbtTagToStringTest {
    @Test
    fun NbtByte_toString() {
        assertEquals("7b", NbtByte(7).toString())
    }

    @Test
    fun NbtShort_toString() {
        assertEquals("8s", NbtShort(8).toString())
    }

    @Test
    fun NbtInt_toString() {
        assertEquals("9", NbtInt(9).toString())
    }

    @Test
    fun NbtLong_toString() {
        assertEquals("10L", NbtLong(10).toString())
    }

    @Test
    fun NbtFloat_toString() {
        assertEquals("3.1415f", NbtFloat(3.1415f).toString())
    }

    @Test
    fun NbtDouble_toString() {
        assertEquals("2.71828d", NbtDouble(2.71828).toString())
    }

    @Test
    fun NbtByteArray_toString() {
        assertEquals("[B;4B,3B,2B,1B]", NbtByteArray(byteArrayOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun NbtString_toString() {
        assertEquals("\"\"", NbtString("").toString())
        assertEquals("\"hello\"", NbtString("hello").toString())
        assertEquals("\"\\\"double-quoted\\\"\"", NbtString("\"double-quoted\"").toString())
        assertEquals("\"'single-quoted'\"", NbtString("'single-quoted'").toString())
        assertEquals("\"\\\"'multi-quoted'\\\"\"", NbtString("\"'multi-quoted'\"").toString())
    }

    @Test
    fun NbtList_toString() {
        assertEquals("[]", NbtList().toString())
        assertEquals("[1b]", NbtList(mutableListOf(NbtByte(1))).toString())
        assertEquals("[[]]", NbtList(mutableListOf(NbtList())).toString())
    }

    @Test
    fun NbtCompound_toString() {
        assertEquals("{}", NbtCompound().toString())
        assertEquals("{a:1b}", NbtCompound().apply { put("a", 1.toByte()) }.toString())
        assertEquals("{a:1b,b:7}", NbtCompound().apply { put("a", 1.toByte()); put("b", 7) }.toString())
    }

    @Test
    fun NbtIntArray_toString() {
        assertEquals("[I;4,3,2,1]", NbtIntArray(intArrayOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun NbtLongArray_toString() {
        assertEquals("[L;4L,3L,2L,1L]", NbtLongArray(longArrayOf(4, 3, 2, 1)).toString())
    }
}

class NbtByteArrayTest {

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(), NbtByteArray(byteArrayOf()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtByteArray(byteArrayOf()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtByteArray(byteArrayOf()))
    }
}

class NbtListTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(nbtList: NbtList): Unit =
            assertEquals(nbtList.toList(), nbtList)

        assertWith(NbtList.of(emptyList<NbtByte>()))
        assertWith(NbtList(mutableListOf(NbtInt(1))))
        assertWith(NbtList(mutableListOf(NbtString("a"), NbtString("b"))))

        assertEquals(NbtList(mutableListOf(NbtInt(1))), NbtList(NbtList(mutableListOf(NbtInt(1)))))
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtList())
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtList())
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtList())
    }
}

class NbtCompoundTest {
    @Test
    fun Should_equal_Map_of_same_contents() {
        fun assertWith(vararg elements: Pair<String, NbtTag>): Unit =
            assertEquals(elements.toMap(), NbtCompound.of(mapOf(*elements)))

        assertWith()
        assertWith("one" to NbtInt(1))
        assertWith("a" to NbtString("a"), "b" to NbtString("b"))

        assertEquals(NbtList(mutableListOf(NbtInt(1))), NbtList(NbtList(mutableListOf(NbtInt(1)))))
    }
}

class NbtIntArrayTest {

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList.of(emptyList<NbtInt>()), NbtIntArray(intArrayOf()))
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtIntArray(intArrayOf()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtIntArray(intArrayOf()))
    }
}

class NbtLongArrayTest {

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList.of(emptyList<NbtLong>()), NbtLongArray(longArrayOf()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtLongArray(longArrayOf()))
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtLongArray(longArrayOf()))
    }
}