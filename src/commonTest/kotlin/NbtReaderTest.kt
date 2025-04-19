import cn.altawk.nbt.tag.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * NbtReaderTest
 *
 * @author TheFloodDragon
 * @since 2025/3/15 13:29
 */
class NbtReaderTest {

    @Test
    fun Decode() {

        val tag = NbtCompound {
            put("name", "Good")
            putList("list") {
                add("1")
                add("2")
            }
            putCompound("map") {
                put("Ket1", 1)
                put("Key2", 2)
            }
            put("byteArray", byteArrayOf(1, 2, 3))
            putList("byteList") {
                add(2.toByte())
                add(3.toByte())
                add(4.toByte())
            }
        }

        val serializer = Example.serializer(NbtTagSerializer)

        val example = format.decodeFromNbtTag(serializer, tag)

        val str = format.encodeToString(serializer, example)

        assertEquals(str, tag.toString())
    }

}