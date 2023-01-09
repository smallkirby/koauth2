import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("multiplatform") version "1.8.0"
    id("org.jetbrains.dokka") version "1.7.20"
    id("maven-publish")
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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
    }
}
