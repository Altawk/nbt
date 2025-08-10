package cn.altawk.nbt

import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * SerialNameDeterminer
 *
 * @author TheFloodDragon
 * @since 2025/2/22 14:18
 */
public interface SerialNameDeterminer {

    /**
     * Retrieves the element name for a given descriptor and index.
     *
     * @param elementName original element name
     * @param descriptor The serial descriptor.
     * @return The element name.
     */
    public fun determineName(elementName: String, descriptor: SerialDescriptor): String

    /**
     * Map the element name.
     *
     * @param elementName original element name
     * @param descriptor The serial descriptor.
     * @return The element name.
     */
    public fun mapName(elementName: String, descriptor: SerialDescriptor): String

    /**
     * Default implementation of [SerialNameDeterminer].
     */
    public object Default : SerialNameDeterminer {
        override fun determineName(elementName: String, descriptor: SerialDescriptor): String = elementName
        override fun mapName(elementName: String, descriptor: SerialDescriptor): String = elementName
    }

}