plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   jcenter()
}

kotlin {
   sourceSets {

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
               freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
            }
         }
      }

      val commonMain by getting {
         dependencies {
            implementation(kotlin("stdlib-common"))
            implementation(Libs.Klock.klock)
            implementation(project(":kotest-assertions"))
         }
      }

      val jvmTest by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(project(":kotest-runner:kotest-runner-junit5"))
         }
      }
   }
}

apply(from = "../../publish.gradle")
