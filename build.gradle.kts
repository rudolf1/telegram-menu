plugins {
    kotlin("jvm") version "1.2.61"
    maven
    `maven-publish`
}
group = "com.github.rudolf1"
repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile("org.telegram:telegrambots:3.6.1")
    compile("org.apache.logging.log4j:log4j-api:2.5")
    compile("org.apache.logging.log4j:log4j-core:2.5")
//    compile "com.google.code.gson:gson:2.8.4"

    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    testCompile(kotlin("test"))
    testCompile(kotlin("test-junit"))

}

publishing {
    repositories {
        maven(url = "build/repository")
    }
}
