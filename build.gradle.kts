import java.util.Properties

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "2.2.2"
    id("jacoco")
}

group = "kfu.itis.maslennikov"
version = "1.0-SNAPSHOT"

val springSecurityVersion: String by project
val postgresVersion: String by project
val lombokVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("org.springframework.security:spring-security-taglibs:${springSecurityVersion}")

    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    // стартер для ликвидбейза ->
    implementation("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    // библиотека для работы с аргументами командной строки пикокли ->
    liquibaseRuntime("info.picocli:picocli:4.6.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("org.springframework.boot:spring-boot-starter-aop")
}

val props = Properties()
props.load(file("src/main/resources/db/liquibase.properties").inputStream())

val jacocoExcludes =
    listOf("**/kfu/itis/maslennikov/dto/**",
        "**/kfu/itis/maslennikov/model/**",
        "**/kfu/itis/maslennikov/config/**",
        "**/kfu/itis/maslennikov/repository/**",
        "**kfu/itis/maslennikov/service/security**")

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changeLogFile" to props.get("change-log-file"),
            "url" to props.get("url"),
            "username" to props.get("username"),
            "password" to props.get("password"),
            "driver" to props.get("driver-class-name")
        )
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport{
    dependsOn(tasks.test)
    reports{
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).matching {
            exclude(jacocoExcludes)
        }
    }))
}

jacoco{
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}

tasks.jacocoTestCoverageVerification{
    violationRules {
        rule {
            limit {
                minimum = BigDecimal.valueOf(0.1)
            }
        }
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).matching {
            exclude(jacocoExcludes)
        }
    }))
}