plugins {
    scala
}

val commonVersion: String by properties

group = "chaos.unity.nenggao"
version = commonVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
}
