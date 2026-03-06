val kotlinVersion: String by project
val logbackVersion: String by project
val ktorVersion: String by project

plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.21"
    id("io.ktor.plugin") version "3.3.2"
}

group = "at.eventful.messless"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    implementation("org.jetbrains.exposed:exposed-core:1.0.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0")

    implementation("net.postgis:postgis-jdbc:2023.1.0")

    implementation("org.locationtech.jts:jts-core:1.19.0")

    implementation("io.ktor:ktor-server-content-negotiation:3.3.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.3.2")
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.3.2")

    implementation("com.h2database:h2:2.4.240")

    implementation("org.jetbrains.exposed:exposed-migration-core:1.1.1")
    implementation("org.jetbrains.exposed:exposed-migration-jdbc:1.1.1")

    implementation("io.github.nikitok:exposed-postgis:0.4")
    implementation("net.postgis:postgis-jdbc:2023.1.0")

    implementation("org.locationtech.jts:jts-core:1.19.0")

    implementation("org.flywaydb:flyway-core:9.22.3")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
}