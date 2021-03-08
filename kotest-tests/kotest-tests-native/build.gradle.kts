plugins {
   kotlin("multiplatform")
}

repositories {
   mavenCentral()
}


repositories {
   mavenCentral()
}

kotlin {
   targets {
      linuxX64()
      mingwX64()
      macosX64()
      tvos()
//      watchos()
      iosX64()
      iosArm64()
      iosArm32()
   }

   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(project(Projects.AssertionsCore))
         }
      }

      val nativeTest by creating {
         dependsOn(commonTest)
      }

      val macosX64Test by getting {
         dependsOn(nativeTest)
      }

      val mingwX64Test by getting {
         dependsOn(nativeTest)
      }

      val linuxX64Test by getting {
         dependsOn(nativeTest)
      }

      val iosX64Test by getting {
         dependsOn(nativeTest)
      }

      val iosArm64Test by getting {
         dependsOn(nativeTest)
      }

      val iosArm32Test by getting {
         dependsOn(nativeTest)
      }

//      val watchosTest by getting {
//         dependsOn(nativeTest)
//      }

      val tvosTest by getting {
         dependsOn(nativeTest)
      }
   }
}


apply(from = "../../nopublish.gradle")
