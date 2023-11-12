@file:Suppress("UNUSED_VARIABLE")

plugins {
   id("kotlin-conventions")
}

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY)) {
      linuxX64()
      linuxArm64()

      mingwX64()

      macosX64()
      macosArm64()

      tvosX64()
      tvosArm64()
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

      sourceSets {

         // Main source sets
         val commonMain by getting {}

         val desktopMain by creating {
            dependsOn(commonMain)
         }

         val macosX64Main by getting { dependsOn(desktopMain) }
         val macosArm64Main by getting { dependsOn(desktopMain) }

         val mingwX64Main by getting { dependsOn(desktopMain) }

         val linuxX64Main by getting { dependsOn(desktopMain) }
         val linuxArm64Main by getting { dependsOn(desktopMain) }

         val iosX64Main by getting { dependsOn(desktopMain) }
         val iosArm64Main by getting { dependsOn(desktopMain) }
         val iosArm32Main by getting { dependsOn(desktopMain) }
         val iosSimulatorArm64Main by getting { dependsOn(desktopMain) }

         val watchosArm32Main by getting { dependsOn(desktopMain) }
         val watchosArm64Main by getting { dependsOn(desktopMain) }
         val watchosX86Main by getting { dependsOn(desktopMain) }
         val watchosX64Main by getting { dependsOn(desktopMain) }
         val watchosSimulatorArm64Main by getting { dependsOn(desktopMain) }

         val tvosX64Main by getting { dependsOn(desktopMain) }
         val tvosArm64Main by getting { dependsOn(desktopMain) }
         val tvosSimulatorArm64Main by getting { dependsOn(desktopMain) }

         // Test sourcesets
         val commonTest by getting

         val nativeTest by creating { dependsOn(commonTest) }

         val macosX64Test by getting { dependsOn(nativeTest) }
         val macosArm64Test by getting { dependsOn(nativeTest) }

         val mingwX64Test by getting { dependsOn(nativeTest) }

         val linuxX64Test by getting { dependsOn(nativeTest) }
         val linuxArm64Test by getting { dependsOn(nativeTest) }

         val iosX64Test by getting { dependsOn(nativeTest) }
         val iosArm64Test by getting { dependsOn(nativeTest) }
         val iosArm32Test by getting { dependsOn(nativeTest) }
         val iosSimulatorArm64Test by getting { dependsOn(nativeTest) }

         val watchosArm32Test by getting { dependsOn(nativeTest) }
         val watchosArm64Test by getting { dependsOn(nativeTest) }
         val watchosX86Test by getting { dependsOn(nativeTest) }
         val watchosX64Test by getting { dependsOn(nativeTest) }
         val watchosSimulatorArm64Test by getting { dependsOn(nativeTest) }

         val tvosX64Test by getting { dependsOn(nativeTest) }
         val tvosArm64Test by getting { dependsOn(nativeTest) }
         val tvosSimulatorArm64Test by getting { dependsOn(nativeTest) }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
