buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.google.javascript:closure-compiler:v20260819")
    }
}

import java.security.MessageDigest
import java.util.Base64
import java.util.logging.Level
import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.CompilationLevel
import com.google.javascript.jscomp.CompilerOptions.LanguageMode
import com.google.javascript.jscomp.SourceFile

plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

springBoot {
    mainClass.set("com.example.cspsri.CspSriSpringApplication")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-json")
    runtimeOnly("org.postgresql:postgresql")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("com.microsoft.playwright:playwright:1.62.0")
}

fun calculateSha384(bytes: ByteArray): String {
    val digest = MessageDigest.getInstance("SHA-384").digest(bytes)
    return "sha384-" + Base64.getEncoder().encodeToString(digest)
}

fun minifyJs(code: String, filename: String = "app.js"): String {
    val compiler = Compiler()
    Compiler.setLoggingLevel(Level.OFF)
    val options = CompilerOptions().apply {
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(this)
        setLanguageIn(LanguageMode.ECMASCRIPT_NEXT)
        setLanguageOut(LanguageMode.ECMASCRIPT_NEXT)
        lineLengthThreshold = Int.MAX_VALUE
        setWarningLevel(com.google.javascript.jscomp.DiagnosticGroups.NON_STANDARD_JSDOC, com.google.javascript.jscomp.CheckLevel.OFF)
    }
    val extern = SourceFile.fromCode("externs.js", "")
    val input = SourceFile.fromCode(filename, code)
    val result = compiler.compile(listOf(extern), listOf(input), options)
    if (!result.success) {
        val errorMsg = compiler.errors.joinToString("\n") { "${it.sourceName}:${it.lineNumber} ${it.description}" }
        throw GradleException("JavaScript minification failed:\n$errorMsg")
    }
    return compiler.toSource().trim().replace(Regex("\n+"), "")
}

fun minifyCss(css: String): String {
    var minified = css
    // 1. Remove comments
    minified = minified.replace(Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL), "")
    // 2. Collapse whitespace
    minified = minified.replace(Regex("\\s+"), " ")
    // 3. Remove whitespace around delimiters
    minified = minified.replace(Regex("\\s*([{};,])\\s*"), "$1")
    // 4. Remove whitespace around colons
    minified = minified.replace(Regex("([a-zA-Z0-9_-]+)\\s*:\\s*"), "$1:")
    // 5. Remove trailing semicolons in blocks
    minified = minified.replace(";}", "}")
    return minified.trim()
}

tasks.named<ProcessResources>("processResources") {
    val srcDir = file("src/main/resources")
    val cssFile = file("src/main/resources/static/css/style.css")
    val jsFile = file("src/main/resources/static/js/app.js")

    // Declare inputs for incremental build checks
    inputs.dir(srcDir)

    doLast {
        // 1. Minify CSS
        if (cssFile.exists()) {
            val originalCss = cssFile.readText(Charsets.UTF_8)
            val minifiedCss = minifyCss(originalCss)
            val outCss = destinationDir.resolve("static/css/style.css")
            outCss.parentFile.mkdirs()
            outCss.writeText(minifiedCss, Charsets.UTF_8)
        }

        // 2. Minify JS
        if (jsFile.exists()) {
            val originalJs = jsFile.readText(Charsets.UTF_8)
            val minifiedJs = minifyJs(originalJs, "app.js")
            val outJs = destinationDir.resolve("static/js/app.js")
            outJs.parentFile.mkdirs()
            outJs.writeText(minifiedJs, Charsets.UTF_8)
        }

        // 3. Compute SRI hashes from the minified output files
        val minifiedCssFile = destinationDir.resolve("static/css/style.css")
        val minifiedJsFile = destinationDir.resolve("static/js/app.js")

        val styleHash = if (minifiedCssFile.exists()) calculateSha384(minifiedCssFile.readBytes()) else ""
        val scriptHash = if (minifiedJsFile.exists()) calculateSha384(minifiedJsFile.readBytes()) else ""

        // 4. Replace SRI placeholders in HTML templates
        val templatesDir = destinationDir.resolve("templates")
        if (templatesDir.exists()) {
            templatesDir.walkTopDown().filter { it.isFile && it.extension == "html" }.forEach { htmlFile ->
                val content = htmlFile.readText(Charsets.UTF_8)
                val updated = content
                    .replace("@STYLE_SRI_HASH@", styleHash)
                    .replace("@SCRIPT_SRI_HASH@", scriptHash)
                htmlFile.writeText(updated, Charsets.UTF_8)
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    val colimaSock = file("${System.getProperty("user.home")}/.colima/default/docker.sock")
    if (colimaSock.exists() && System.getenv("DOCKER_HOST") == null) {
        environment("DOCKER_HOST", "unix://$colimaSock")
        environment("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock")
        environment("TESTCONTAINERS_RYUK_DISABLED", "true")
    }
}
