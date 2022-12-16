plugins {
    kotlin("jvm") version "1.7.20"
}

val commonVersion: String by project(":").properties

group = "chaos.unity.nenggao"
version = commonVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
}
