package cn.altawk.nbt

import cn.altawk.nbt.internal.NbtReaderDecoder
import cn.altawk.nbt.internal.NbtWriterEncoder
import cn.altawk.nbt.internal.TreeNbtReader
import cn.altawk.nbt.internal.TreeNbtWriter
import cn.altawk.nbt.tag.NbtTag
import cn.altawk.nbt.tag.asNbtDecoder
import cn.altawk.nbt.tag.asNbtEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * NbtTransformingSerializer
 *
 * @author TheFloodDragon
 * @since 2025/4/19 12:56
 */
public abstract class NbtTransformingSerializer<T : Any>(
    /**
     * The [KSerializer] to be transformed.
     */
    protected val tSerializer: KSerializer<T>,
    /**
     * If enabled, other encoders and decoders are allowed instead of throwing an exception.
     */
    private val tolerant: Boolean = false
) : KSerializer<T> {

    /**
     * A descriptor for this transformation.
     * By default, it delegates to [tSerializer]'s descriptor.
     *
     * However, this descriptor can be overridden to achieve better representation of the resulting NBT shape
     * for schema generating or introspection purposes.
     */
    override val descriptor: SerialDescriptor get() = tSerializer.descriptor

    final override fun serialize(encoder: Encoder, value: T) {
        if (tolerant && encoder !is NbtEncoder) {
            return tSerializer.serialize(encoder, value)
        }

        val output = encoder.asNbtEncoder()
        var element: NbtTag? = null

        NbtWriterEncoder(output.nbt, TreeNbtWriter { element = it })
            .encodeSerializableValue(tSerializer, value)

        checkNotNull(element) { "Expected element to be initialized by TreeNbtWriter" }
        output.encodeNbtTag(transformSerialize(element))
    }

    final override fun deserialize(decoder: Decoder): T {
        if (tolerant && decoder !is NbtDecoder) {
            return tSerializer.deserialize(decoder)
        }

        val input = decoder.asNbtDecoder()
        val element = transformDeserialize(input.decodeNbtTag())

        return NbtReaderDecoder(input.nbt, TreeNbtReader(element)).decodeSerializableValue(tSerializer)
    }

    /**
     * Transformation that happens during [deserialize] call.
     * Does nothing by default.
     *
     * During deserialization, a value from NBT is firstly decoded to a [NbtTag],
     * user transformation in [transformDeserialize] is applied,
     * and then resulting [NbtTag] is deserialized to [T] with [tSerializer].
     */
    protected open fun transformDeserialize(tag: NbtTag): NbtTag = tag

    /**
     * Transformation that happens during [serialize] call.
     * Does nothing by default.
     *
     * During serialization, a value of type [T] is serialized with [tSerializer] to a [NbtTag],
     * user transformation in [transformSerialize] is applied, and then resulting [NbtTag] is encoded to NBT.
     */
    protected open fun transformSerialize(tag: NbtTag): NbtTag = tag

}
