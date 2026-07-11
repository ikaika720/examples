plugins {
    java
}

group = "hoge.exp"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.activemq:artemis-jakarta-client:2.42.0")
}