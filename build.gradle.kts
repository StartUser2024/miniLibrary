plugins {
    java

}

group = "org.University"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}


dependencies {
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation ("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation ("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation ("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
