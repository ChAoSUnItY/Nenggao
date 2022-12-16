plugins {
    kotlin("jvm") version "1.7.20"
}

group = "chaos.unity.nenggao"
version = "1.4.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
}
