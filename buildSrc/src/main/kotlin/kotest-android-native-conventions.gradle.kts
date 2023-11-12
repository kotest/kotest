@file:Suppress("UNUSED_VARIABLE")

plugins {
   id("kotlin-conventions")
}

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY)) {
      androidNativeX86()
      androidNativeX64()
      androidNativeArm32()
      androidNativeArm64()
      sourceSets {

         // Main source sets
         val commonMain by getting {}

         val desktopMain by getting {
            dependsOn(commonMain)
         }
         val androidNativeX86Main by getting { dependsOn(desktopMain) }
         val androidNativeX64Main by getting { dependsOn(desktopMain) }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
