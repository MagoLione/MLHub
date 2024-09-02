plugins {
    kotlin("jvm") version "2.0.20-RC"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.magoliopoli.mc"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://papermc.io/repo/repository/maven-public/") {
        name = "papermc"
    }
    maven("essentialsxReleases") {
        url = uri("https://repo.essentialsx.net/releases")
    }

    maven("https://repo.onarandombox.com/content/groups/public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")

    compileOnly("net.essentialsx:EssentialsX:2.20.1")
    compileOnly("com.onarandombox.multiversecore:multiverse-core:4.3.12")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }

    jar {
        archiveFileName.set("${project.name}-${project.version}_slim.jar")
    }

}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
