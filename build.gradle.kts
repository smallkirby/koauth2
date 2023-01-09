import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("multiplatform") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("org.jetbrains.dokka") version "1.7.20"
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    signing
}

group = "com.github.smallkirby"
version = "0.0.1"

repositories {
    mavenCentral()
}

val dokkaHtml by tasks.existing(DokkaTask::class)
val javadocJar by tasks.registering(Jar::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    archiveClassifier.set("javadoc")
    from(dokkaHtml)
}

kotlin {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("0.44.0")
        verbose.set(true)
        additionalEditorconfigFile.set(file(".editorconfig"))
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val ktorVersion = "2.2.2"

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit5"))
                implementation("io.ktor:ktor-server-test-host:$ktorVersion")
                implementation("io.ktor:ktor-server-test-host:$ktorVersion")
            }
        }
        val jvmMain by getting
        val jvmTest by getting
    }
}
