import cn.altawk.nbt.NbtPath
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * NbtPathTest
 *
 * @author TheFloodDragon
 * @since 2025/3/29 17:44
 */
class NbtPathTest {

    private val path = NbtPath(
        NbtPath.NameNode("hello"),
        NbtPath.NameNode("world"),
        NbtPath.NameNode("list"),
        NbtPath.IndexNode(1),
        NbtPath.NameNode("你好"),
    ).plus(NbtPath.NameNode("name"))

    private val pathText = "hello.world.list[1].`你好`.name"

    @Test
    fun should_create_path_from_string() {
        assertEquals(path, NbtPath(pathText))
    }

    @Test
    fun should_parse_only_index_nodes() {
        assertEquals(
            NbtPath(NbtPath.IndexNode(0), NbtPath.IndexNode(12)),
            NbtPath("[0][12]"),
        )
    }

    @Test
    fun should_parse_nodes_with_internal_whitespace_and_special_characters() {
        assertEquals(
            NbtPath(
                NbtPath.NameNode("root node"),
                NbtPath.NameNode("with.dot"),
                NbtPath.IndexNode(2),
                NbtPath.NameNode("with-hyphen"),
            ),
            NbtPath("`root node`.`with.dot`[2].with-hyphen"),
        )
    }

    @Test
    fun should_convert_path_to_string() {
        assertEquals(pathText, path.toString())
    }

    @Test
    fun should_round_trip_quoted_and_plain_names() {
        val quoted = NbtPath("plain.`space name`.with-dash")
        val plain = NbtPath("plain.with-dash")

        assertEquals("plain.`space name`.with-dash", quoted.toString())
        assertEquals("plain.with-dash", plain.toString())
        assertEquals(quoted, NbtPath(quoted.toString()))
        assertEquals(plain, NbtPath(plain.toString()))
    }

    @Test
    fun should_append_multiple_nodes_in_order() {
        val appended = NbtPath(NbtPath.NameNode("root")) + listOf(
            NbtPath.NameNode("items"),
            NbtPath.IndexNode(3),
            NbtPath.NameNode("display name"),
        )

        assertEquals("root.items[3].`display name`", appended.toString())
        assertEquals(NbtPath("root.items[3].`display name`"), appended)
    }

}
