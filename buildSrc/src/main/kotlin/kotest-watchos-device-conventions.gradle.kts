@file:Suppress("UNUSED_VARIABLE")

plugins {
   id("kotlin-conventions")
}

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY)) {
      watchosDeviceArm64()
      sourceSets {

         // Main source sets
         val commonMain by getting {}
         val desktopMain by getting { dependsOn(commonMain) }
         val watchosDeviceArm64Main by getting { dependsOn(desktopMain) }

         // Test sourcesets
         val commonTest by getting
         val nativeTest by getting { dependsOn(commonTest) }
         val watchosDeviceArm64Test by getting { dependsOn(nativeTest) }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
