plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params") // for @ParameterizedTest

    // Mockito
    testImplementation("org.mockito:mockito-core:5.13.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.13.0") // enables @ExtendWith(MockitoExtension.class)
}

javafx {
    version = "21.0.4"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    // fully-qualified name of your launcher class
    mainClass.set("Project2.uiMain")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
