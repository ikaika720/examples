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

    providedCompile("jakarta.servlet:jakarta.servlet-api:6.1.0")

    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")

    implementation("org.hibernate.orm:hibernate-core:7.3.2.Final")
    implementation("org.postgresql:postgresql:42.7.13")
    implementation("org.apache.commons:commons-dbcp2:2.13.0") {
        exclude(group = "commons-logging", module = "commons-logging")
    }

    implementation("org.springframework:spring-orm")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework:spring-webmvc")

    runtimeOnly("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    runtimeOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2")
    runtimeOnly("org.apache.logging.log4j:log4j-core:2.25.4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.18.0")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}

tasks.war {
    archiveFileName.set("jpa-webapp-spring-tomcat.war")
}
