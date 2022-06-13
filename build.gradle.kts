plugins {
    id("java")
}

group = "chaos.unity.nenggao"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.compileJava {
    // FUCKING WINDOWS ENCODING
    options.compilerArgs.addAll(arrayOf("-encoding", "UTF-8"))
}

dependencies {
    implementation("net.java.dev.jna:jna:5.11.0")
    implementation("net.java.dev.jna:jna-platform:5.11.0")
    implementation("com.diogonunes:JColor:5.5.1")

    compileOnly("org.jetbrains:annotations:23.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}