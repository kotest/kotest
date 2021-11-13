plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("com.adarshr.test-logger")
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
               freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
            }
         }
      }

      val jvmMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(project(Projects.Assertions.Shared))
            implementation(project(Projects.Assertions.Core))
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
            implementation(Libs.Mocking.mockk)
         }
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
