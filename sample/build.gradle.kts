plugins {
    kotlin("jvm")
}

group = "com.github.rudolf1"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Replace with implementation("com.github.rudolf1:telegram-menu:<version>")
    implementation(rootProject)

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

