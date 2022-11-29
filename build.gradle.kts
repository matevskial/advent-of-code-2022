import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "com.matevskial"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    if (properties.contains("day")) {
        val day = (properties["day"] as String).toIntOrNull()
        if (day != null) {
            ext["validDay"] = String.format("%02d", day)
        }
    }

    if (ext.has("validDay")
        && project.file("src/main/kotlin/day${ext["validDay"]}/Solution.kt").exists()
    ) {
        mainClass.set("day${ext["validDay"]}.SolutionKt")
    } else {
        mainClass.set("MainKt")
    }
}
