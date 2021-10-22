plugins {
   kotlin("multiplatform")
}

repositories {
   mavenCentral()
}

kotlin {
   targets {
      linuxX64()

      mingwX64()

      macosX64()
      macosArm64()

      tvos()
      tvosSimulatorArm64()

      watchosArm32()
      watchosArm64()
      watchosX86()
      watchosX64()
      watchosSimulatorArm64()

      iosX64()
      iosArm64()
      iosArm32()
      iosSimulatorArm64()
   }

   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation(kotlin("stdlib"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(project(Projects.Assertions.Core))
         }
      }

      val nativeTest by creating {
         dependsOn(commonTest)
      }

      val macosX64Test by getting {
         dependsOn(nativeTest)
      }

      val macosArm64Test by getting {
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

      val iosSimulatorArm64Test by getting {
         dependsOn(nativeTest)
      }

      val watchosArm32Test by getting {
         dependsOn(nativeTest)
      }

      val watchosArm64Test by getting {
         dependsOn(nativeTest)
      }

      val watchosX86Test by getting {
         dependsOn(nativeTest)
      }

      val watchosX64Test by getting {
         dependsOn(nativeTest)
      }

      val watchosSimulatorArm64Test by getting {
         dependsOn(nativeTest)
      }

      val tvosTest by getting {
         dependsOn(nativeTest)
      }

      val tvosSimulatorArm64Test by getting {
         dependsOn(nativeTest)
      }
   }
}


apply(from = "../../nopublish.gradle")
