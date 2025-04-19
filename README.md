# Altawk NBT

[![](https://www.codefactor.io/repository/github/altawk/nbt/badge)](https://www.codefactor.io/repository/github/altawk/nbt)
![](https://img.shields.io/github/languages/code-size/altawk/nbt)

An implementation of [Minecraft's NBT format](https://zh.minecraft.wiki/w/NBT%E6%A0%BC%E5%BC%8F)
for [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).

Technical information about NBT can be found [here](https://wiki.vg/NBT).

Reffered to [knbt](https://github.com/BenWoodworth/knbt) and [kotlin.serialization/Json](https://github.com/Kotlin/kotlinx.serialization/tree/master/formats/json).

### Gradle

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Altawk.nbt:nbt-jvm:1.0.0") { isTransitive = false }
}
```
