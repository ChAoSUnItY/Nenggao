plugins {
    id("maven-publish")
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val commonVersion: String by properties

group = "chaos.unity.nenggao"
version = commonVersion

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

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

tasks.shadowJar {
    archiveName = "$baseName-$version.$extension"
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = group.toString()
            artifactId = "nenggao"
            version = version

            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

artifacts {
    archives(tasks.shadowJar)
}