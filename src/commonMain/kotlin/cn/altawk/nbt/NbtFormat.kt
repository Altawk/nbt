package cn.altawk.nbt

import cn.altawk.nbt.internal.*
import cn.altawk.nbt.tag.NbtTag
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * NbtFormat
 *
 * @author TheFloodDragon
 * @since 2025/1/25 11:31
 */
public open class NbtFormat(
    public val configuration: NbtConfiguration,
    override val serializersModule: SerializersModule
) : StringFormat {

    /**
     * The default instance of [NbtFormat] with default configuration.
     */
    public companion object Default : NbtFormat(NbtConfiguration(), EmptySerializersModule())

    /**
     * Serializes and encodes the given [value] to an [NbtTag] using the given [serializer].
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag {
        lateinit var result: NbtTag
        NbtWriterEncoder(this, TreeNbtWriter { result = it }).encodeSerializableValue(serializer, value)
        return result
    }

    /**
     * Deserializes and decodes the given [tag] to an object of type [T] using the given [deserializer].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T {
        return NbtReaderDecoder(this, TreeNbtReader(tag)).decodeSerializableValue(deserializer)
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        val builder = StringBuilder()
        val writer = StringifiedNbtWriter(builder, configuration.prettyPrint)
        NbtWriterEncoder(this, writer).encodeSerializableValue(serializer, value)
        return builder.toString()
    }

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, snbt: String): T {
        return NbtReaderDecoder(this, StringifiedNbtReader(snbt)).decodeSerializableValue(deserializer)
    }

}

/**
 * Creates an instance of [NbtFormat] configured from the optionally given [NbtFormat instance][from] and adjusted with [builderAction].
 */
public fun NbtFormat(from: NbtFormat = NbtFormat.Default, builderAction: NbtFormatBuilder.() -> Unit): NbtFormat {
    val builder = NbtFormatBuilder(from)
    builder.builderAction()
    val configuration = builder.build()
    return NbtFormat(configuration, builder.serializersModule)
}

/**
 * Builder of the [NbtFormat] instance provided by `NbtFormat { ... }` factory function:
 *
 * ```
 * val nbt = NbtFormat { // this: NbtFormatBuilder
 *     encodeDefaults = true
 *     ignoreUnknownKeys = true
 * }
 * ```
 */
@Suppress("unused")
public class NbtFormatBuilder internal constructor(nbt: NbtFormat) {

    /**
     * Specifies whether resulting Stringified NBT should be pretty-printed.
     */
    public var prettyPrint: Boolean = nbt.configuration.prettyPrint

    /**
     * Specifies whether `null` values should be encoded for nullable properties and must be present in JSON object
     * during decoding.
     *
     * When this flag is disabled properties with `null` values are not encoded;
     * during decoding, the absence of a field value is treated as `null` for nullable properties without a default value.
     */
    public var explicitNulls: Boolean = nbt.configuration.explicitNulls

    /**
     * Specifies the [SerialNameDeterminer] the coder used
     */
    public var nameDeterminer: SerialNameDeterminer = nbt.configuration.nameDeterminer

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [NbtFormat] instance.
     *
     * @see SerializersModule
     * @see Contextual
     * @see Polymorphic
     */
    public var serializersModule: SerializersModule = nbt.serializersModule

    internal fun build(): NbtConfiguration {
        return NbtConfiguration(
            prettyPrint, explicitNulls, nameDeterminer
        )
    }

}