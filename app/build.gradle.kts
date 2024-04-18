plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    application
}

repositories {
    mavenCentral()
}

dependencies {

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("io.github.seggan.swgv.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
