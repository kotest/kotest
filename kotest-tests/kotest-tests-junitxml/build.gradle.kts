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

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Engine))
            implementation(project(Projects.AssertionsCore))
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.JunitXmlExtension))
            implementation(Libs.Jdom.jdom2)
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   systemProperty("gradle.build.dir", project.buildDir)
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

apply(from = "../../nopublish.gradle")
