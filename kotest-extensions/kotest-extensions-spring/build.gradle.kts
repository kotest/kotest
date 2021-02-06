plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("org.jetbrains.kotlin.plugin.spring") version "1.4.30"
   id("com.adarshr.test-logger")
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

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(kotlin("stdlib"))
            implementation(kotlin("reflect"))
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(project(Projects.Common))
            implementation(project(Projects.Api))
            implementation(project(Projects.Engine))
            implementation(project(Projects.AssertionsShared))
            implementation(Libs.Spring.context)
            implementation(Libs.Spring.test)
            implementation(Libs.Coroutines.coreJvm)
            implementation(Libs.Bytebuddy.bytebuddy)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation("org.springframework.boot:spring-boot-starter-test:2.2.5.RELEASE")
         }
      }

      all {
         languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
         languageSettings.useExperimentalAnnotation("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
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
      events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")
