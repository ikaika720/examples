plugins {
    java
    id("org.springframework.boot") version "4.1.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    group = "hoge.exp"
    version = "0.0.1-SNAPSHOT"

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"("com.fasterxml.jackson.core:jackson-databind")
        "implementation"("com.nimbusds:nimbus-jose-jwt:10.9.1")
        "runtimeOnly"("com.google.crypto.tink:tink:1.23.0")
        "implementation"("org.bouncycastle:bcprov-jdk18on:1.85")

        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
