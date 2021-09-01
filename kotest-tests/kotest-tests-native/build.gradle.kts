plugins {
   kotlin("multiplatform")
}

repositories {
   mavenCentral()
}

kotlin {
   targets {
      linuxX64()
      linuxArm64()

      mingwX64()
      mingwX86()

      macosX64()
      macosArm64()

      tvos()

      watchosArm32()
      watchosArm64()
      watchosX86()
      watchosX64()

      iosX64()
      iosArm64()
      iosArm32()
   }

   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation(kotlin("stdlib"))
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

      val macosArm64Test by getting {
         dependsOn(nativeTest)
      }

      val mingwX64Test by getting {
         dependsOn(nativeTest)
      }

      val mingwX86Test by getting {
         dependsOn(nativeTest)
      }

      val linuxX64Test by getting {
         dependsOn(nativeTest)
      }

      val linuxArm64Test by getting {
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

      val tvosTest by getting {
         dependsOn(nativeTest)
      }
   }
}


apply(from = "../../nopublish.gradle")
