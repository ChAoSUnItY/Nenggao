import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("maven-publish")
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "chaos.unity.nenggao"
version = "1.0.6"

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

tasks.shadowJar {
    dependencies {
        include(dependency("net.java.dev.jna:jna:5.11.0"))
        include(dependency("net.java.dev.jna:jna-platform:5.11.0"))
        include(dependency("com.diogonunes:JColor:5.5.1"))
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            // change to point to your repo, e.g. http://my.org/repo
            url = uri("$buildDir/repo")
        }
    }
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