plugins {
    kotlin("jvm") version "1.6.10"
    `maven-publish`
}
group = "com.github.rudolf1"
repositories {
    mavenCentral()
}

dependencies {
    api("org.telegram:telegrambots:3.6.1")
    api("org.apache.logging.log4j:log4j-api:2.5")
    api("org.apache.logging.log4j:log4j-core:2.5")
//    compile "com.google.code.gson:gson:2.8.4"

    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
    testApi(kotlin("test"))
    testApi(kotlin("test-junit"))

}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rudolf1/telegram-menu")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
