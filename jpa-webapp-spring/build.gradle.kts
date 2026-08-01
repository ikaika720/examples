plugins {
    java
    war
}

group = "hoge.exp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
}

val springVersion = "6.2.18"

dependencies {
    providedCompile("jakarta.persistence:jakarta.persistence-api:3.1.0")

    compileOnly("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.projectlombok:lombok:1.18.40")

    implementation("org.springframework:spring-orm:$springVersion")
    implementation("org.springframework:spring-tx:$springVersion")
    implementation("org.springframework:spring-webmvc:$springVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testRuntimeOnly("org.postgresql:postgresql:42.7.12")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}
