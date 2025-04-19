@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class, SealedSerializationApi::class)

package cn.altawk.nbt.tag

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

/**
 * NbtSerializers
 *
 * @author TheFloodDragon
 * @since 2025/1/25 11:07
 */

internal object NbtTagSerializer : KSerializer<NbtTag> {

    override val descriptor: SerialDescriptor = buildSerialDescriptor("nbt.NbtTag", PolymorphicKind.SEALED) {
        // Resolve cyclic dependency in descriptors by late binding
        element("NbtCompoundSerializer", defer { NbtCompoundSerializer.descriptor })
        element("NbtListSerializer", defer { NbtListSerializer.descriptor })
        element("NbtByteSerializer", defer { NbtByteSerializer.descriptor })
        element("NbtShortSerializer", defer { NbtShortSerializer.descriptor })
        element("NbtIntSerializer", defer { NbtIntSerializer.descriptor })
        element("NbtLongSerializer", defer { NbtLongSerializer.descriptor })
        element("NbtFloatSerializer", defer { NbtFloatSerializer.descriptor })
        element("NbtDoubleSerializer", defer { NbtDoubleSerializer.descriptor })
        element("NbtStringSerializer", defer { NbtStringSerializer.descriptor })
        element("NbtByteArraySerializer", defer { NbtByteArraySerializer.descriptor })
        element("NbtIntArraySerializer", defer { NbtIntArraySerializer.descriptor })
        element("NbtLongArraySerializer", defer { NbtLongArraySerializer.descriptor })
    }

    override fun serialize(encoder: Encoder, value: NbtTag) = when (value) {
        is NbtByte -> NbtByteSerializer.serialize(encoder, value)
        is NbtShort -> NbtShortSerializer.serialize(encoder, value)
        is NbtInt -> NbtIntSerializer.serialize(encoder, value)
        is NbtLong -> NbtLongSerializer.serialize(encoder, value)
        is NbtFloat -> NbtFloatSerializer.serialize(encoder, value)
        is NbtDouble -> NbtDoubleSerializer.serialize(encoder, value)
        is NbtByteArray -> NbtByteArraySerializer.serialize(encoder, value)
        is NbtString -> NbtStringSerializer.serialize(encoder, value)
        is NbtList -> NbtListSerializer.serialize(encoder, value)
        is NbtCompound -> NbtCompoundSerializer.serialize(encoder, value)
        is NbtIntArray -> NbtIntArraySerializer.serialize(encoder, value)
        is NbtLongArray -> NbtLongArraySerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): NbtTag {
        return decoder.asNbtDecoder().decodeNbtTag()
    }

}

internal object NbtListSerializer : KSerializer<NbtList> {

    override val descriptor: SerialDescriptor =
        object : SerialDescriptor by listSerialDescriptor(serializer<NbtTag>().descriptor) {
            override val serialName: String = "nbt.NbtList"
        }

    private val listSerializer = ListSerializer(serializer<NbtTag>())

    override fun serialize(encoder: Encoder, value: NbtList) = listSerializer.serialize(encoder, value.content)

    override fun deserialize(decoder: Decoder): NbtList = NbtList.of(listSerializer.deserialize(decoder))

}

internal object NbtCompoundSerializer : KSerializer<NbtCompound> {

    override val descriptor: SerialDescriptor = object :
        SerialDescriptor by mapSerialDescriptor(String.serializer().descriptor, serializer<NbtTag>().descriptor) {
        override val serialName: String = "nbt.NbtCompound"
    }

    private val mapSerializer = MapSerializer(String.serializer(), serializer<NbtTag>())

    override fun serialize(encoder: Encoder, value: NbtCompound) = mapSerializer.serialize(encoder, value.content)

    override fun deserialize(decoder: Decoder): NbtCompound = NbtCompound.of(mapSerializer.deserialize(decoder))

}

internal object NbtByteArraySerializer : KSerializer<NbtByteArray> {

    private object NbtByteArrayDescriptor : SerialDescriptor by ListSerializer(Byte.serializer()).descriptor {
        override val serialName: String = "nbt.NbtByteArray"
        override val annotations: List<Annotation> = listOf()
    }

    override val descriptor: SerialDescriptor = NbtByteArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtByteArray): Unit =
        encoder.encodeCollection(descriptor, value.content.size) {
            value.content.forEachIndexed { index, element -> encodeByteElement(descriptor, index, element) }
        }

    override fun deserialize(decoder: Decoder): NbtByteArray =
        NbtByteArray(decoder.decodeList(descriptor, CompositeDecoder::decodeByteElement).toByteArray())

}

internal object NbtIntArraySerializer : KSerializer<NbtIntArray> {
    private object NbtIntArrayDescriptor : SerialDescriptor by ListSerializer(Int.serializer()).descriptor {
        override val serialName: String = "nbt.NbtIntArray"
        override val annotations: List<Annotation> = listOf()
    }

    override val descriptor: SerialDescriptor = NbtIntArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtIntArray): Unit =
        encoder.encodeCollection(descriptor, value.content.size) {
            value.content.forEachIndexed { index, element -> encodeIntElement(descriptor, index, element) }
        }

    override fun deserialize(decoder: Decoder): NbtIntArray =
        NbtIntArray(decoder.decodeList(descriptor, CompositeDecoder::decodeIntElement).toIntArray())
}

internal object NbtLongArraySerializer : KSerializer<NbtLongArray> {

    private object NbtLongArrayDescriptor : SerialDescriptor by ListSerializer(Long.serializer()).descriptor {
        override val serialName: String = "nbt.NbtLongArray"
        override val annotations: List<Annotation> = listOf()
    }

    override val descriptor: SerialDescriptor = NbtLongArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtLongArray): Unit =
        encoder.encodeCollection(descriptor, value.content.size) {
            value.content.forEachIndexed { index, element -> encodeLongElement(descriptor, index, element) }
        }

    override fun deserialize(decoder: Decoder): NbtLongArray =
        NbtLongArray(decoder.decodeList(descriptor, CompositeDecoder::decodeLongElement).toLongArray())
}

internal object NbtByteSerializer : KSerializer<NbtByte> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("nbt.NbtByte", PrimitiveKind.BYTE)

    override fun serialize(encoder: Encoder, value: NbtByte): Unit = encoder.encodeByte(value.content)

    override fun deserialize(decoder: Decoder): NbtByte = NbtByte(decoder.decodeByte())

}

internal object NbtShortSerializer : KSerializer<NbtShort> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("nbt.NbtShort", PrimitiveKind.SHORT)

    override fun serialize(encoder: Encoder, value: NbtShort): Unit = encoder.encodeShort(value.content)

    override fun deserialize(decoder: Decoder): NbtShort = NbtShort(decoder.decodeShort())
}

internal object NbtIntSerializer : KSerializer<NbtInt> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("nbt.NbtInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: NbtInt): Unit = encoder.encodeInt(value.content)

    override fun deserialize(decoder: Decoder): NbtInt = NbtInt(decoder.decodeInt())

}

internal object NbtLongSerializer : KSerializer<NbtLong> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("nbt.NbtLong", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: NbtLong): Unit = encoder.encodeLong(value.content)

    override fun deserialize(decoder: Decoder): NbtLong = NbtLong(decoder.decodeLong())

}

internal object NbtFloatSerializer : KSerializer<NbtFloat> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("nbt.NbtFloat", PrimitiveKind.FLOAT)

    override fun serialize(encoder: Encoder, value: NbtFloat): Unit = encoder.encodeFloat(value.content)

    override fun deserialize(decoder: Decoder): NbtFloat = NbtFloat(decoder.decodeFloat())

}

internal object NbtDoubleSerializer : KSerializer<NbtDouble> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("nbt.NbtDouble", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: NbtDouble): Unit = encoder.encodeDouble(value.content)

    override fun deserialize(decoder: Decoder): NbtDouble = NbtDouble(decoder.decodeDouble())

}

internal object NbtStringSerializer : KSerializer<NbtString> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("nbt.NbtString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NbtString): Unit = encoder.encodeString(value.content)

    override fun deserialize(decoder: Decoder): NbtString = NbtString(decoder.decodeString())

}

internal fun Encoder.asNbtEncoder(): NbtEncoder = this as? NbtEncoder ?: throw IllegalArgumentException(
    "This serializer can be used only with NBT format. Expected Encoder to be NbtEncoder, got ${this::class}"
)

internal fun Decoder.asNbtDecoder(): NbtDecoder = this as? NbtDecoder ?: throw IllegalArgumentException(
    "This serializer can be used only with NBT format. Expected Decoder to be NbtDecoder, got ${this::class}"
)

@OptIn(ExperimentalSerializationApi::class)
private inline fun <T> Decoder.decodeList(
    descriptor: SerialDescriptor,
    crossinline decodeElement: CompositeDecoder.(descriptor: SerialDescriptor, index: Int) -> T
): List<T> = decodeStructure(descriptor) {
    val size = decodeCollectionSize(NbtLongArraySerializer.descriptor)

    when {
        decodeSequentially() -> List(size) { index ->
            decodeElement(descriptor, index)
        }

        size >= 0 -> buildList(size) {
            while (true) {
                val index = decodeElementIndex(descriptor)
                if (index == CompositeDecoder.DECODE_DONE) break

                add(index, decodeElement(descriptor, index))
            }
        }

        else -> buildList {
            while (true) {
                val index = decodeElementIndex(descriptor)
                if (index == CompositeDecoder.DECODE_DONE) break

                add(index, decodeElement(descriptor, index))
            }
        }
    }
}

/**
 * Returns serial descriptor that delegates all the calls to descriptor returned by [deferred] block.
 * Used to resolve cyclic dependencies between recursive serializable structures.
 */
private fun defer(deferred: () -> SerialDescriptor): SerialDescriptor = object : SerialDescriptor {
    private val original: SerialDescriptor by lazy(deferred)

    override val serialName: String
        get() = original.serialName
    override val kind: SerialKind
        get() = original.kind
    override val elementsCount: Int
        get() = original.elementsCount

    override fun getElementName(index: Int): String = original.getElementName(index)
    override fun getElementIndex(name: String): Int = original.getElementIndex(name)
    override fun getElementAnnotations(index: Int): List<Annotation> = original.getElementAnnotations(index)
    override fun getElementDescriptor(index: Int): SerialDescriptor = original.getElementDescriptor(index)
    override fun isElementOptional(index: Int): Boolean = original.isElementOptional(index)
}
