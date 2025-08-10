plugins {
    java
}

group = "hoge.exp"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.activemq:artemis-jakarta-client:2.42.0")
    implementation("org.springframework:spring-context:6.2.8")
    implementation("org.springframework:spring-jms:6.2.8")
}