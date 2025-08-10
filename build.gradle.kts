import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.17.0"
    `maven-publish`
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm()

    sourceSets {
        configureEach {
            dependencies {
                compileOnly(libs.serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
            }
        }
    }

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
            extraWarnings.set(true)
        }
    }

    // JVM 工具链版本
    jvmToolchain(8)

}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Altawk/nbt")
            credentials {
                username = project.findProperty("github.user")?.toString() ?: System.getenv("GITHUB_USER")
                password = project.findProperty("github.token")?.toString() ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications.withType<MavenPublication> {
        pom {
            name.set("nbt")
            description.set("Minecraft NBT API written by Kotlin.")
            url.set("https://github.com/Altawk/nbt")

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("TheFloodDragon")
                    name.set("Flood Dragon")
                    email.set("theflooddragon@foxmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com:Altawk/nbt.git")
                developerConnection.set("scm:git:ssh://github.com:Altawk/nbt.git")
                url.set("https://github.com/Altawk/nbt")
            }
        }
    }
}
