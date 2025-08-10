import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.tag.*
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Performance benchmark tests for NBT encoding/decoding
 * These tests measure and compare performance characteristics
 */
class NbtPerformanceBenchmark {

    val iterations: Int = 100

    private val format = NbtFormat {
    }

    /**
     * Data class to store benchmark timing results
     */
    data class BenchmarkResult(
        val totalTime: Duration,
        val averageTime: Duration,
        val minTime: Duration,
        val maxTime: Duration
    )

    @Serializable
    data class BenchmarkData(
        val id: String,
        val timestamp: Long,
        val values: List<Double>,
        val metadata: Map<String, String>,
        val flags: List<Boolean>,
        val coordinates: List<Triple<Double, Double, Double>>
    )

    @Serializable
    data class MinecraftLikeData(
        val playerName: String,
        val level: Int,
        val health: Float,
        val inventory: List<ItemStack>,
        val position: Position,
        val gameMode: String,
        val achievements: Map<String, Boolean>
    )

    @Serializable
    data class ItemStack(
        val id: String,
        val count: Int,
        val damage: Short,
        val enchantments: Map<String, Int>
    )

    @Serializable
    data class Position(
        val x: Double,
        val y: Double,
        val z: Double,
        val yaw: Float,
        val pitch: Float
    )

    private fun createBenchmarkData(size: Int): BenchmarkData {
        return BenchmarkData(
            id = "benchmark_$size",
            timestamp = System.currentTimeMillis(),
            values = (1..size).map { it * 0.1 },
            metadata = (1..size / 10).associate { "key_$it" to "value_$it" },
            flags = (1..size / 5).map { it % 2 == 0 },
            coordinates = (1..size / 20).map { Triple(it.toDouble(), it * 2.0, it * 3.0) }
        )
    }

    private fun createMinecraftLikeData(): MinecraftLikeData {
        return MinecraftLikeData(
            playerName = "TestPlayer",
            level = 42,
            health = 20.0f,
            inventory = (1..36).map { slot ->
                ItemStack(
                    id = "minecraft:item_$slot",
                    count = slot % 64 + 1,
                    damage = (slot % 100).toShort(),
                    enchantments = mapOf(
                        "sharpness" to slot % 5 + 1,
                        "unbreaking" to slot % 3 + 1
                    )
                )
            },
            position = Position(
                x = 123.456,
                y = 64.0,
                z = -789.012,
                yaw = 45.0f,
                pitch = -30.0f
            ),
            gameMode = "survival",
            achievements = (1..50).associate { "achievement_$it" to (it % 2 == 0) }
        )
    }

    private fun createComplexNbtStructure(): NbtCompound {
        return NbtCompound {
            put("version", "1.0.0")
            put("timestamp", System.currentTimeMillis())

            putList("players") {
                repeat(10) { i ->
                    addCompound {
                        put("name", "Player$i")
                        put("level", i * 10)
                        put("health", 20.0f)
                        put("experience", i * 1000.0)

                        putList("inventory") {
                            repeat(27) { slot ->
                                addCompound {
                                    put("slot", slot)
                                    put("id", "item_${slot % 10}")
                                    put("count", slot % 64 + 1)
                                    put("damage", (slot % 100).toShort())
                                }
                            }
                        }

                        putCompound("position") {
                            put("x", i * 100.0)
                            put("y", 64.0 + i)
                            put("z", i * -50.0)
                            put("dimension", "overworld")
                        }

                        put("stats", intArrayOf(i, i * 2, i * 3, i * 4, i * 5))
                        put(
                            "uuid", byteArrayOf(
                                i.toByte(), (i + 1).toByte(), (i + 2).toByte(), (i + 3).toByte(),
                                (i + 4).toByte(), (i + 5).toByte(), (i + 6).toByte(), (i + 7).toByte(),
                                (i + 8).toByte(), (i + 9).toByte(), (i + 10).toByte(), (i + 11).toByte(),
                                (i + 12).toByte(), (i + 13).toByte(), (i + 14).toByte(), (i + 15).toByte()
                            )
                        )
                    }
                }
            }

            putCompound("world_data") {
                put("seed", 123456789L)
                put("spawn_x", 0)
                put("spawn_y", 64)
                put("spawn_z", 0)
                put("time", 24000L)
                put("weather", "clear")

                putList("loaded_chunks") {
                    repeat(100) { i ->
                        addCompound {
                            put("x", i % 10)
                            put("z", i / 10)
                            put("last_update", System.currentTimeMillis() - i * 1000)
                        }
                    }
                }
            }
        }
    }

    /**
     * Execute a benchmark operation multiple times and collect timing statistics
     */
    private fun benchmark(operation: () -> Unit): BenchmarkResult {
        val times = mutableListOf<Duration>()

        repeat(iterations) {
            val time = measureTime {
                operation()
            }
            times.add(time)
        }

        val totalTime = times.reduce { acc, duration -> acc + duration }
        val averageTime = totalTime / iterations
        val minTime = times.minOrNull() ?: Duration.ZERO
        val maxTime = times.maxOrNull() ?: Duration.ZERO

        return BenchmarkResult(totalTime, averageTime, minTime, maxTime)
    }

    @Test
    fun benchmarkSmallDataSerialization() {
        val data = createBenchmarkData(100)

        println("=== Small Data Benchmark (100 elements, $iterations iterations) ===")

        // TreeNbt encoding
        val treeEncodeResult = benchmark {
            format.encodeToNbtTag(BenchmarkData.serializer(), data)
        }

        // TreeNbt decoding
        val nbtTag = format.encodeToNbtTag(BenchmarkData.serializer(), data)
        val treeDecodeResult = benchmark {
            format.decodeFromNbtTag(BenchmarkData.serializer(), nbtTag)
        }

        // SNBT encoding
        val snbtEncodeResult = benchmark {
            format.encodeToString(BenchmarkData.serializer(), data)
        }

        // SNBT decoding
        val snbtString = format.encodeToString(BenchmarkData.serializer(), data)
        val snbtDecodeResult = benchmark {
            format.decodeFromString(BenchmarkData.serializer(), snbtString)
        }

        printBenchmarkResults("Small Data", treeEncodeResult, treeDecodeResult, snbtEncodeResult, snbtDecodeResult)
    }

    @Test
    fun benchmarkMediumDataSerialization() {
        val data = createBenchmarkData(1000)

        println("=== Medium Data Benchmark (1000 elements, $iterations iterations) ===")

        val treeEncodeResult = benchmark {
            format.encodeToNbtTag(BenchmarkData.serializer(), data)
        }

        val nbtTag = format.encodeToNbtTag(BenchmarkData.serializer(), data)
        val treeDecodeResult = benchmark {
            format.decodeFromNbtTag(BenchmarkData.serializer(), nbtTag)
        }

        val snbtEncodeResult = benchmark {
            format.encodeToString(BenchmarkData.serializer(), data)
        }

        val snbtString = format.encodeToString(BenchmarkData.serializer(), data)
        val snbtDecodeResult = benchmark {
            format.decodeFromString(BenchmarkData.serializer(), snbtString)
        }

        printBenchmarkResults("Medium Data", treeEncodeResult, treeDecodeResult, snbtEncodeResult, snbtDecodeResult)
    }

    @Test
    fun benchmarkLargeDataSerialization() {
        val data = createBenchmarkData(10000)

        println("=== Large Data Benchmark (10000 elements, $iterations iterations) ===")

        val treeEncodeResult = benchmark {
            format.encodeToNbtTag(BenchmarkData.serializer(), data)
        }

        val nbtTag = format.encodeToNbtTag(BenchmarkData.serializer(), data)
        val treeDecodeResult = benchmark {
            format.decodeFromNbtTag(BenchmarkData.serializer(), nbtTag)
        }

        val snbtEncodeResult = benchmark {
            format.encodeToString(BenchmarkData.serializer(), data)
        }

        val snbtString = format.encodeToString(BenchmarkData.serializer(), data)
        val snbtDecodeResult = benchmark {
            format.decodeFromString(BenchmarkData.serializer(), snbtString)
        }

        printBenchmarkResults("Large Data", treeEncodeResult, treeDecodeResult, snbtEncodeResult, snbtDecodeResult)
    }

    @Test
    fun benchmarkMinecraftLikeData() {
        val data = createMinecraftLikeData()

        println("=== Minecraft-like Data Benchmark ($iterations iterations) ===")

        val treeEncodeResult = benchmark {
            format.encodeToNbtTag(MinecraftLikeData.serializer(), data)
        }

        val nbtTag = format.encodeToNbtTag(MinecraftLikeData.serializer(), data)
        val treeDecodeResult = benchmark {
            format.decodeFromNbtTag(MinecraftLikeData.serializer(), nbtTag)
        }

        val snbtEncodeResult = benchmark {
            format.encodeToString(MinecraftLikeData.serializer(), data)
        }

        val snbtString = format.encodeToString(MinecraftLikeData.serializer(), data)
        val snbtDecodeResult = benchmark {
            format.decodeFromString(MinecraftLikeData.serializer(), snbtString)
        }

        printBenchmarkResults("Minecraft-like", treeEncodeResult, treeDecodeResult, snbtEncodeResult, snbtDecodeResult)
    }

    @Test
    fun benchmarkComplexNbtStructure() {
        val nbtData = createComplexNbtStructure()

        println("=== Complex NBT Structure Benchmark ($iterations iterations) ===")

        val treeEncodeResult = benchmark {
            format.encodeToNbtTag(NbtTag.serializer(), nbtData)
        }

        val treeDecodeResult = benchmark {
            format.decodeFromNbtTag(NbtTag.serializer(), nbtData)
        }

        val snbtEncodeResult = benchmark {
            nbtData.toString()
        }

        val snbtString = nbtData.toString()
        val snbtDecodeResult = benchmark {
            format.decodeFromString(NbtTag.serializer(), snbtString)
        }

        printBenchmarkResults("Complex NBT", treeEncodeResult, treeDecodeResult, snbtEncodeResult, snbtDecodeResult)
    }

    private fun printBenchmarkResults(
        testName: String,
        treeEncodeResult: BenchmarkResult,
        treeDecodeResult: BenchmarkResult,
        snbtEncodeResult: BenchmarkResult,
        snbtDecodeResult: BenchmarkResult
    ) {
        println("$testName Results:")
        println("  TreeNbt Encode: ${treeEncodeResult.averageTime} avg (min: ${treeEncodeResult.minTime}, max: ${treeEncodeResult.maxTime})")
        println("  TreeNbt Decode: ${treeDecodeResult.averageTime} avg (min: ${treeDecodeResult.minTime}, max: ${treeDecodeResult.maxTime})")
        println("  SNBT Encode:    ${snbtEncodeResult.averageTime} avg (min: ${snbtEncodeResult.minTime}, max: ${snbtEncodeResult.maxTime})")
        println("  SNBT Decode:    ${snbtDecodeResult.averageTime} avg (min: ${snbtDecodeResult.minTime}, max: ${snbtDecodeResult.maxTime})")
        println("  TreeNbt vs SNBT Encode Ratio: ${snbtEncodeResult.averageTime.inWholeNanoseconds.toDouble() / treeEncodeResult.averageTime.inWholeNanoseconds}")
        println("  TreeNbt vs SNBT Decode Ratio: ${snbtDecodeResult.averageTime.inWholeNanoseconds.toDouble() / treeDecodeResult.averageTime.inWholeNanoseconds}")
    }
}
