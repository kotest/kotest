plugins {
   id("java")
   kotlin("multiplatform")
   kotlin("plugin.allopen") version Libs.kotlinVersion
   id("java-library")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

val quarkusVersion = "1.4.1.Final"

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
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
         }
      }
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(project(":kotest-core"))
            implementation(project(Projects.AssertionsShared))
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation("io.quarkus:quarkus-kotlin:$quarkusVersion")
            implementation("io.quarkus:quarkus-junit5:$quarkusVersion")
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }
   }
}

allOpen {
   annotation("javax.enterprise.context.ApplicationScoped")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
   kotlinOptions.jvmTarget = "1.8"
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
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

apply(from = "../../publish-mpp.gradle.kts")
