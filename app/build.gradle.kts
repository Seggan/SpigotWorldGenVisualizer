plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.23"
}

val gdxVersion: String by project
val ktxVersion: String by project

dependencies {
    api(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("io.github.libktx:ktx-app:$ktxVersion")
    implementation("io.github.libktx:ktx-assets:$ktxVersion")
    implementation("io.github.libktx:ktx-async:$ktxVersion")
    implementation("io.github.libktx:ktx-collections:$ktxVersion")
    implementation("io.github.libktx:ktx-freetype-async:$ktxVersion")
    implementation("io.github.libktx:ktx-freetype:$ktxVersion")
    implementation("io.github.libktx:ktx-graphics:$ktxVersion")

    api("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    api("com.github.seeseemelk:MockBukkit-v1.20:3.80.0")
}

sourceSets.main.get().resources.srcDir(File(rootDir, "assets"))

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
