@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)

package cn.altawk.nbt.internal

import cn.altawk.nbt.NbtEncoder
import cn.altawk.nbt.tag.NbtTag
import cn.altawk.nbt.tag.NbtTagSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

/**
 * AbstractNbtEncoder
 *
 * @author TheFloodDragon
 * @since 2025/3/14 20:31
 */
internal abstract class AbstractNbtEncoder : NbtEncoder, AbstractEncoder() {

    override val serializersModule: SerializersModule get() = nbt.serializersModule

    open fun encodeSerializableElement(descriptor: SerialDescriptor, index: Int): Boolean = true

    abstract fun beginCompound(descriptor: SerialDescriptor): CompositeEncoder

    abstract fun beginList(descriptor: SerialDescriptor, size: Int): CompositeEncoder

    override fun beginStructure(descriptor: SerialDescriptor) = beginCompound(descriptor)

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int) =
        if (descriptor.kind == StructureKind.LIST) {
            beginList(descriptor, collectionSize)
        } else beginCompound(descriptor)

    override fun <T : Any?> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        if (encodeSerializableElement(descriptor, index)) {
            super.encodeSerializableElement(descriptor, index, serializer, value)
        }
    }

    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        if (encodeSerializableElement(descriptor, index) && (value != null || nbt.configuration.explicitNulls)) {
            super.encodeNullableSerializableElement(descriptor, index, serializer, value)
        }
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T): Unit =
        when (serializer) {
            ByteArraySerializer() -> encodeByteArray(value as ByteArray)
            IntArraySerializer() -> encodeIntArray(value as IntArray)
            LongArraySerializer() -> encodeLongArray(value as LongArray)

            NbtTagSerializer -> encodeNbtTag(value as NbtTag)

            else -> super<AbstractEncoder>.encodeSerializableValue(serializer, value)
        }

}