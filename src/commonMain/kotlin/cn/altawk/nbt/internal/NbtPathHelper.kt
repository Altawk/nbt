package cn.altawk.nbt.internal

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.NbtPath.IndexNode
import cn.altawk.nbt.NbtPath.NameNode

/**
 * NbtPathHelper
 *
 * @since 2025/3/29 17:34
 */
internal object NbtPathHelper {

    @JvmStatic
    fun fromString(path: String) = buildList {
        val buf = CharBuffer(path)
        var name = ""
        fun release() {
            if (name.isNotEmpty()) {
                add(NameNode(name))
                name = ""
            }
        }
        while (buf.hasMore()) {
            when (buf.skipWhitespace().peek()) {
                '`' -> {
                    release()
                    buf.advance()
                    add(NameNode(buf.takeUntil('`')))
                }

                '[' -> {
                    release()
                    buf.advance()
                    add(IndexNode(buf.takeUntil(']').toInt()))
                }

                '.' -> {
                    release()
                    buf.advance()
                }

                else -> name += buf.take() // Add to cache
            }
        }
        release() // release the last name
    }

    @JvmStatic
    fun toString(path: Iterable<NbtPath.Node>) = buildString {
        var first = true
        for (node in path) {
            when (node) {
                is IndexNode -> append("[${node.index}]")
                is NameNode -> {
                    if (!first) append('.') else first = false
                    if (node.name.all { Tokens.id(it) }) {
                        append(node.name)
                    } else append("`${node.name}`")
                }
            }
        }
    }

}