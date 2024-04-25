plugins {
    kotlin("jvm") version "1.9.23" apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.aikar.co/content/groups/aikar/")
    }
}