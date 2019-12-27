plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
}
kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
         }
      }
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(project(":kotest-core"))
            implementation(kotlin("stdlib-jdk8"))
            implementation("io.qameta.allure:allure-java-commons:2.10.0")
            implementation("javax.xml.bind:jaxb-api:2.3.1")
            implementation("com.sun.xml.bind:jaxb-core:2.3.0.1")
            implementation("com.sun.xml.bind:jaxb-impl:2.3.2")

         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
            implementation("org.mockito:mockito-core:2.24.0")
            implementation("com.nhaarman:mockito-kotlin:1.6.0")
            implementation("io.mockk:mockk:1.9.3")
            implementation("log4j:log4j:1.2.17")
            implementation("org.slf4j:slf4j-log4j12:1.7.25")
         }
      }
   }
}

tasks {
   test {
      useJUnitPlatform()
      testLogging {
         showExceptions = true
         showStandardStreams = true
         events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
         )
         exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      }
   }
}

apply(from = "../../publish.gradle")
