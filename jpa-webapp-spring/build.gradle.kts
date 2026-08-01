plugins {
    java
    war
}

group = "hoge.exp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework:spring-framework-bom:7.0.8"))

    providedCompile("jakarta.persistence:jakarta.persistence-api:3.2.0")
    providedCompile("jakarta.servlet:jakarta.servlet-api:6.1.0")

    compileOnly("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.projectlombok:lombok:1.18.40")

    implementation("org.springframework:spring-orm")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework:spring-webmvc")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testRuntimeOnly("org.postgresql:postgresql:42.7.13")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}

tasks.register<Copy>("copyPostgresqlJar") {
    from({ configurations.testRuntimeClasspath.get().filter { it.name.matches(Regex("postgresql-\\d.*\\.jar")) } })
    into(layout.buildDirectory.dir("postgresql"))
    rename { "postgresql.jar" }
}

tasks.war {
    archiveFileName.set("jpa-webapp-spring.war")
    from("src/main/resources/META-INF") {
        into("META-INF")
    }
}

