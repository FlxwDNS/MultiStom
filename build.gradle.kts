plugins {
    id("java")
    id("maven-publish")

    id("com.gradleup.shadow") version "8.3.0"
}

group = "dev.flxwdns.multistom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()

    maven("http://127.0.0.1:8080/releases")
}

dependencies {
    // appoloserver
    implementation("dev.appolo:appolo-server:1.0.8")

    implementation("net.kyori:adventure-text-minimessage:4.17.0")

    // log4j
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-beta2")

    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
    implementation("ch.qos.logback:logback-classic:1.5.7")

    implementation("net.minestom:minestom-snapshots:a521c4e7cd")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.shadowJar {
    archiveFileName.set("multistom-stable.jar")
    manifest {
        attributes["Main-Class"] = "dev.flxwdns.multistom.MultiStomBootstrap"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.flxwdns.multistom"
            artifactId = "multistom"
            version = "1.0-SNAPSHOT"

            from(components["java"])
        }
    }
}