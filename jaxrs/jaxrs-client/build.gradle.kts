plugins {
    application
}

dependencies {
    implementation(project(":jaxrs-common"))
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    runtimeOnly("org.glassfish.jersey.core:jersey-client:4.0.0")
    runtimeOnly("org.glassfish.jersey.media:jersey-media-json-jackson:4.0.0")
    runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:4.0.0")
}

application {
    mainClass = "hoge.exp.jaxrs.client.TemperatureConversionClient"
}
