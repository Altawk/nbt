import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.tag.NbtTag
import cn.altawk.nbt.tag.NbtTagSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
@SerialName("Example")
data class Example<T>(
    val name: String,
    val list: List<String> = emptyList(),
    val tn: Byte?,
    val map: Map<String, Int> = emptyMap(),
    val byteArray: ByteArray = ByteArray(0),
    val byteList: List<Byte> = emptyList(),
    val description: String? = null,
    val website: String?,
)

val format = NbtFormat {
}

class NbtWriterTest {


    @Test
    fun Encode() {

        val example = Example<NbtTag>(
            "Good",
            listOf("1", "2"),
            null,
            mapOf("Ket1" to 1, "Key2" to 2),
            byteArrayOf(1, 2, 3),
            listOf(2, 3, 4),
            null,
            null,
        )

        val serializer = Example.serializer(NbtTagSerializer)

        val tag = format.encodeToNbtTag(serializer, example)

        val str = format.encodeToString(serializer, example)

        println(tag)
        println(str)

        assertEquals(str, tag.toString())
    }

}
