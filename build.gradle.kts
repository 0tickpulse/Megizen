plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://repo.md-5.net/content/groups/public/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://maven.citizensnpcs.co/repo")
    }
}

dependencies {
    implementation("io.papermc.paper:paper-api:${project.properties["craftbukkit.version"]}")
    implementation("com.denizenscript:denizen:${project.properties["denizen.version"]}")
    implementation("io.netty:netty-all:4.1.86.Final")
    implementation("me.clip:placeholderapi:2.10.0")
    implementation("com.ticxo.modelengine:ModelEngine:${project.properties["modelengine.version"]}")
}

group = "net.tickmc"
version = "0.25.0-b" + buildNumber() + "-DEV"
description = "Megizen"
java.sourceCompatibility = JavaVersion.VERSION_17

fun buildNumber(): String = System.getenv("BUILD_NUMBER") ?: "UNKNOWN"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("BUILD_NUMBER" to System.getenv("BUILD_NUMBER")))
    }
}
