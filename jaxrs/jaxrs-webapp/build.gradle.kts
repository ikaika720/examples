plugins {
    war
}

dependencies {
    implementation(project(":jaxrs-common"))
    compileOnly("jakarta.platform:jakarta.jakartaee-api:11.0.0")
    implementation("org.glassfish.jersey.containers:jersey-container-servlet:4.0.0")
    implementation("org.glassfish.jersey.inject:jersey-hk2:4.0.0")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:4.0.0")
}

tasks.war {
    archiveFileName = "jaxrs.war"
}
