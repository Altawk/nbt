import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.tag.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Performance benchmark tests for NBT encoding/decoding.
 * These tests measure and compare performance characteristics.
 */
class NbtPerformanceBenchmark {

    private val warmupIterations: Int = 20
    private val measurementIterations: Int = 300

    private val format = NbtFormat {}

    /**
     * Data class to store benchmark timing results.
     */
    data class BenchmarkResult(
        val totalTime: Duration,
        val averageTime: Duration,
        val minTime: Duration,
        val maxTime: Duration,
        val warmupIterations: Int,
        val measurementIterations: Int,
    )

    data class BenchmarkSuiteResult(
        val suiteName: String,
        val treeEncodeResult: BenchmarkResult,
        val treeDecodeResult: BenchmarkResult,
        val snbtEncodeResult: BenchmarkResult,
        val snbtDecodeResult: BenchmarkResult,
    )

    @Serializable
    data class BenchmarkData(
        val id: String,
        val timestamp: Long,
        val values: List<Double>,
        val metadata: Map<String, String>,
        val flags: List<Boolean>,
        val coordinates: List<Triple<Double, Double, Double>>,
    )

    @Serializable
    data class MinecraftLikeData(
        val playerName: String,
        val level: Int,
        val health: Float,
        val inventory: List<ItemStack>,
        val position: Position,
        val gameMode: String,
        val achievements: Map<String, Boolean>,
    )

    @Serializable
    data class ItemStack(
        val id: String,
        val count: Int,
        val damage: Short,
        val enchantments: Map<String, Int>,
    )

    @Serializable
    data class Position(
        val x: Double,
        val y: Double,
        val z: Double,
        val yaw: Float,
        val pitch: Float,
    )

    private val benchmarkDataSmall = createBenchmarkData(size = 100, timestamp = 1_710_000_000_100L)
    private val benchmarkDataMedium = createBenchmarkData(size = 1_000, timestamp = 1_710_000_001_000L)
    private val benchmarkDataLarge = createBenchmarkData(size = 10_000, timestamp = 1_710_000_010_000L)
    private val minecraftLikeData = createMinecraftLikeData()
    private val complexNbtStructure = createComplexNbtStructure()


    private fun createBenchmarkData(size: Int, timestamp: Long): BenchmarkData {
        return BenchmarkData(
            id = "benchmark_$size",
            timestamp = timestamp,
            values = (1..size).map { it * 0.1 },
            metadata = (1..size / 10).associate { "key_$it" to "value_$it" },
            flags = (1..size / 5).map { it % 2 == 0 },
            coordinates = (1..size / 20).map { Triple(it.toDouble(), it * 2.0, it * 3.0) },
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
                        "unbreaking" to slot % 3 + 1,
                    ),
                )
            },
            position = Position(
                x = 123.456,
                y = 64.0,
                z = -789.012,
                yaw = 45.0f,
                pitch = -30.0f,
            ),
            gameMode = "survival",
            achievements = (1..50).associate { "achievement_$it" to (it % 2 == 0) },
        )
    }

    private fun createComplexNbtStructure(): NbtCompound {
        val baseTimestamp = 1_710_123_456_000L
        return NbtCompound {
            put("version", "1.0.0")
            put("timestamp", baseTimestamp)

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
                            "uuid",
                            byteArrayOf(
                                i.toByte(),
                                (i + 1).toByte(),
                                (i + 2).toByte(),
                                (i + 3).toByte(),
                                (i + 4).toByte(),
                                (i + 5).toByte(),
                                (i + 6).toByte(),
                                (i + 7).toByte(),
                                (i + 8).toByte(),
                                (i + 9).toByte(),
                                (i + 10).toByte(),
                                (i + 11).toByte(),
                                (i + 12).toByte(),
                                (i + 13).toByte(),
                                (i + 14).toByte(),
                                (i + 15).toByte(),
                            ),
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
                            put("last_update", baseTimestamp - i * 1000L)
                        }
                    }
                }
            }
        }
    }

    private fun <T> benchmark(operation: () -> T): BenchmarkResult {
        repeat(warmupIterations) {
            consumeResult(operation())
        }

        val times = ArrayList<Duration>(measurementIterations)
        repeat(measurementIterations) {
            lateinit var result: Any
            val time = measureTime {
                result = operation() as Any
            }
            consumeResult(result)
            times.add(time)
        }

        val totalTime = times.reduce(Duration::plus)
        val averageTime = totalTime / measurementIterations
        val minTime = times.minOrNull() ?: Duration.ZERO
        val maxTime = times.maxOrNull() ?: Duration.ZERO

        return BenchmarkResult(
            totalTime = totalTime,
            averageTime = averageTime,
            minTime = minTime,
            maxTime = maxTime,
            warmupIterations = warmupIterations,
            measurementIterations = measurementIterations,
        )
    }

    private fun consumeResult(value: Any?) {
        blackhole = blackhole xor (value?.hashCode() ?: 0)
    }

    private fun <T> runBenchmarkSuite(name: String, serializer: KSerializer<T>, value: T): BenchmarkSuiteResult {
        println("=== $name Benchmark ===")

        val treeEncoded = format.encodeToNbtTag(serializer, value)
        val snbtEncoded = format.encodeToString(serializer, value)

        val suiteResult = BenchmarkSuiteResult(
            suiteName = name,
            treeEncodeResult = benchmark {
                format.encodeToNbtTag(serializer, value)
            },
            treeDecodeResult = benchmark {
                format.decodeFromNbtTag(serializer, treeEncoded)
            },
            snbtEncodeResult = benchmark {
                format.encodeToString(serializer, value)
            },
            snbtDecodeResult = benchmark {
                format.decodeFromString(serializer, snbtEncoded)
            },
        )

        printBenchmarkResults(suiteResult)
        return suiteResult
    }

    @Test
    fun should_benchmark_small_data_serialization() {
        runBenchmarkSuite("Small Data (100 elements)", BenchmarkData.serializer(), benchmarkDataSmall)
    }

    @Test
    fun should_benchmark_medium_data_serialization() {
        runBenchmarkSuite("Medium Data (1000 elements)", BenchmarkData.serializer(), benchmarkDataMedium)
    }

    @Test
    fun should_benchmark_large_data_serialization() {
        runBenchmarkSuite("Large Data (10000 elements)", BenchmarkData.serializer(), benchmarkDataLarge)
    }

    @Test
    fun should_benchmark_minecraft_like_data() {
        runBenchmarkSuite("Minecraft-like Data", MinecraftLikeData.serializer(), minecraftLikeData)
    }

    @Test
    fun should_benchmark_complex_nbt_structure() {
        val suiteResult = runCustomBenchmarkSuite(
            name = "Complex NBT Structure",
            treeEncodedProvider = { format.encodeToNbtTag(NbtTag.serializer(), complexNbtStructure) },
            snbtEncodedProvider = { complexNbtStructure.toString() },
            treeEncodeOperation = { format.encodeToNbtTag(NbtTag.serializer(), complexNbtStructure) },
            treeDecodeOperation = { encoded -> format.decodeFromNbtTag(NbtTag.serializer(), encoded) },
            snbtEncodeOperation = { complexNbtStructure.toString() },
            snbtDecodeOperation = { encoded -> format.decodeFromString(NbtTag.serializer(), encoded) },
        )
        printBenchmarkResults(suiteResult)
    }

    private fun <TTree, TSnbt> runCustomBenchmarkSuite(
        name: String,
        treeEncodedProvider: () -> TTree,
        snbtEncodedProvider: () -> TSnbt,
        treeEncodeOperation: () -> TTree,
        treeDecodeOperation: (TTree) -> Any,
        snbtEncodeOperation: () -> TSnbt,
        snbtDecodeOperation: (TSnbt) -> Any,
    ): BenchmarkSuiteResult {
        println("=== $name Benchmark ===")

        val treeEncoded = treeEncodedProvider()
        val snbtEncoded = snbtEncodedProvider()

        return BenchmarkSuiteResult(
            suiteName = name,
            treeEncodeResult = benchmark(treeEncodeOperation),
            treeDecodeResult = benchmark { treeDecodeOperation(treeEncoded) },
            snbtEncodeResult = benchmark(snbtEncodeOperation),
            snbtDecodeResult = benchmark { snbtDecodeOperation(snbtEncoded) },
        )
    }

    private fun printBenchmarkResults(suiteResult: BenchmarkSuiteResult) {
        println(
            "${suiteResult.suiteName} Results (warmup=${suiteResult.treeEncodeResult.warmupIterations}, measurement=${suiteResult.treeEncodeResult.measurementIterations}):",
        )
        printBenchmarkLine("TreeNbt Encode", suiteResult.treeEncodeResult)
        printBenchmarkLine("TreeNbt Decode", suiteResult.treeDecodeResult)
        printBenchmarkLine("SNBT Encode", suiteResult.snbtEncodeResult)
        printBenchmarkLine("SNBT Decode", suiteResult.snbtDecodeResult)
    }

    private fun printBenchmarkLine(name: String, result: BenchmarkResult) {
        println(
            "  $name: avg=${result.averageTime}, min=${result.minTime}, max=${result.maxTime}, total=${result.totalTime}",
        )
    }

    private companion object {
        private var blackhole: Int = 0
    }
}
