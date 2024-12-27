import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "dev.kyriji"
version = ""

repositories {
    mavenCentral()
}

dependencies {
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "dev.kyriji.missilewars.MissileWars"
        )
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
}