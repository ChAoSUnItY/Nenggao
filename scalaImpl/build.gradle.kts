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
    implementation("org.scala-lang:scala-library:2.13.10")
    implementation(project(":"))
}
