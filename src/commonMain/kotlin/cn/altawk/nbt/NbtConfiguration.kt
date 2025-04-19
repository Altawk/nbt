package cn.altawk.nbt

/**
 * NbtConfiguration
 *
 * @author TheFloodDragon
 * @since 2025/1/25 11:32
 */
public class NbtConfiguration internal constructor(
    public val prettyPrint: Boolean = false,
    public val explicitNulls: Boolean = false,
    public val nameDeterminer: SerialNameDeterminer = SerialNameDeterminer.Default,
)