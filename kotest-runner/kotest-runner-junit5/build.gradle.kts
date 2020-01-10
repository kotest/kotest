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
         targets {
            jvm {
               compilations.all {
                  kotlinOptions {
                     jvmTarget = "1.8"
                  }
               }
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
            implementation(kotlin("stdlib-jdk8"))
            api(project(":kotest-core"))
//            api(project(":kotest-runner:kotest-runner-console"))
            api(project(":kotest-runner:kotest-runner-jvm"))
            implementation(Libs.JUnitPlatform.engine)
            implementation(Libs.JUnitPlatform.api)
            implementation(Libs.JUnitPlatform.launcher)
            implementation(Libs.JUnitJupiter.api)
            implementation(Libs.Slf4j.api)
         }
      }
   }
}


apply(from = "../../publish.gradle")
