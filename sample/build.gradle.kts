plugins {
    kotlin("jvm")
}

group = "com.github.rudolf1"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    // Replace with implementation("com.github.rudolf1:telegram-menu:<version>")
    compile(rootProject)

    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
}

