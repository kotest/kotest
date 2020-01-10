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
            api(project(":kotest-fp"))
            api(project(":kotest-core"))
//            api(project(":kotest-runner:kotest-runner-console"))
            api(project(":kotest-runner:kotest-runner-jvm"))
            api(Libs.JUnitPlatform.engine)
            api(Libs.JUnitPlatform.api)
            api(Libs.JUnitPlatform.launcher)
            api(Libs.JUnitJupiter.api)
            api(Libs.Slf4j.api)
         }
      }
   }
}


apply(from = "../../publish.gradle")
