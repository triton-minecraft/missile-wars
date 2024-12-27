import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "dev.kyriji"
version = ""

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:9803f2bfe3")
    implementation("dev.hollowcube:schem:1.3.1")

    implementation("dev.kyriji:triton-stom:0.0.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "dev.kyriji.missilewars.MissileWars"
        }
    }
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
}