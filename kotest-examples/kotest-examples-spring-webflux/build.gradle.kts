import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   id("org.springframework.boot") version "2.3.0.RELEASE"
   id("io.spring.dependency-management") version "1.0.9.RELEASE"
   kotlin("jvm")
   kotlin("plugin.spring") version "1.3.72"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
   mavenCentral()
}

dependencies {
   implementation("org.springframework.boot:spring-boot-starter-webflux")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
   implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
   implementation("org.jetbrains.kotlin:kotlin-reflect")
   implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
   testImplementation("org.springframework.boot:spring-boot-starter-test") {
      exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
      exclude(module = "mockito-core")
   }
   testImplementation("com.ninja-squad:springmockk:2.0.1")
   testImplementation("io.projectreactor:reactor-test")
   testImplementation(project(Projects.JunitRunner))
   testImplementation(project(":kotest-extensions:kotest-extensions-spring"))
}

tasks.withType<Test> {
   useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
   kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = "1.8"
   }
}
//
//
//plugins {
//
//   id("java")
//   id("kotlin-multiplatform")
//   id("io.spring.dependency-management") version "1.0.9.RELEASE"
//   kotlin("plugin.spring") version "1.3.72"
//   id("java-library")
//}
//
//repositories {
//   mavenCentral()
//}
//
//kotlin {
//
//   targets {
//      jvm {
//         compilations.all {
//            kotlinOptions {
//               jvmTarget = "1.8"
//            }
//         }
//      }
//   }
//
//   targets.all {
//      compilations.all {
//         kotlinOptions {
//            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
//         }
//      }
//   }
//
//   sourceSets {
//      val jvmMain by getting {
//         dependencies {
//            api(kotlin("stdlib-jdk8"))
//            api(kotlin("reflect"))
//            implementation("org.springframework.boot:spring-boot-starter-webflux")
//            implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//            implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
//            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
//         }
//      }
//      val jvmTest by getting {
//         dependencies {
//            api(kotlin("stdlib-jdk8"))
//            implementation(project(Projects.JunitRunner))
//            implementation(project(":kotest-extensions:kotest-extensions-spring"))
//            implementation("org.springframework.boot:spring-boot-starter-test") {
//               exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
//            }
//            implementation("io.projectreactor:reactor-test")
//            implementation("com.ninja-squad:springmockk:2.0.1")
//         }
//      }
//   }
//}
//
//tasks.named<Test>("jvmTest") {
//   useJUnitPlatform()
//   filter {
//      setFailOnNoMatchingTests(false)
//   }
//   testLogging {
//      showExceptions = true
//      showStandardStreams = true
//      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
//      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//   }
//}
