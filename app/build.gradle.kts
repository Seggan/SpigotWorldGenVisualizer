plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    application
}

repositories {
    mavenCentral()
}

val gdxVersion: String by project
val ktxVersion: String by project

dependencies {
    api(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    api("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
    api("com.badlogicgames.gdx:gdx:$gdxVersion")
    api("io.github.libktx:ktx-app:$ktxVersion")
    api("io.github.libktx:ktx-assets:$ktxVersion")
    api("io.github.libktx:ktx-async:$ktxVersion")
    api("io.github.libktx:ktx-collections:$ktxVersion")
    api("io.github.libktx:ktx-freetype-async:$ktxVersion")
    api("io.github.libktx:ktx-freetype:$ktxVersion")
    api("io.github.libktx:ktx-graphics:$ktxVersion")
}

sourceSets.main.get().resources.srcDir(File(rootDir, "assets"))

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "io.github.seggan.swgv.AppKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    workingDir = file("assets")
}
