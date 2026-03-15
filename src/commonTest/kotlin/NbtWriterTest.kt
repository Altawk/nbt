import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtString
import cn.altawk.nbt.tag.NbtTag
import cn.altawk.nbt.tag.NbtTagSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

@Serializable
private data class NullableExample(
    val name: String,
    val optional: String?,
    val fallback: String? = "default",
)

val format = NbtFormat {}

class NbtWriterTest {
    @Test
    fun should_encode_value_to_matching_nbt_tag_and_string() {
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

        assertEquals(str, tag.toString())
    }

    @Test
    fun should_omit_null_fields_when_explicit_nulls_is_false() {
        val serializer = NullableExample.serializer()
        val example = NullableExample(name = "Alex", optional = null, fallback = null)
        val format = NbtFormat { explicitNulls = false }

        val tag = format.encodeToNbtTag(serializer, example)
        val stringified = format.encodeToString(serializer, example)

        assertEquals(NbtCompound().apply { put("name", NbtString("Alex")) }, tag)
        assertEquals("{name:\"Alex\"}", stringified)
        assertEquals(stringified, tag.toString())
    }

    @Test
    fun should_fail_to_encode_null_fields_when_explicit_nulls_is_true() {
        val serializer = NullableExample.serializer()
        val example = NullableExample(name = "Alex", optional = null, fallback = null)
        val format = NbtFormat { explicitNulls = true }

        assertFailsWith<kotlinx.serialization.SerializationException> {
            format.encodeToNbtTag(serializer, example)
        }
        assertFailsWith<kotlinx.serialization.SerializationException> {
            format.encodeToString(serializer, example)
        }
    }

    @Test
    fun should_keep_tag_and_string_consistent_for_nullable_and_default_fields() {
        val serializer = NullableExample.serializer()
        val example = NullableExample(name = "Alex", optional = "site")

        val defaultFormat = NbtFormat { explicitNulls = false }
        val explicitFormat = NbtFormat { explicitNulls = true }

        assertEquals(
            defaultFormat.encodeToString(serializer, example),
            defaultFormat.encodeToNbtTag(serializer, example).toString(),
        )
        assertEquals(
            explicitFormat.encodeToString(serializer, example),
            explicitFormat.encodeToNbtTag(serializer, example).toString(),
        )
    }
}
