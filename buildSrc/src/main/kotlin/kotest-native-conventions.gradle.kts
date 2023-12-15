@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate


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
      watchosX64()
      watchosSimulatorArm64()

      iosX64()
      iosArm64()
      iosSimulatorArm64()

      // FIXME: The "desktop" intermediate source set can be integrated into "native". In this case
      //     the following block can be replaced with `applyDefaultHierarchyTemplate()`.
      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            group("desktop") {
               withNative()
            }
         }
      }

      // WORKAROUND https://github.com/Kotlin/kotlinx.coroutines/issues/3968
      //     The following block can be removed when the issue is resolved.
      sourceSets {
         val nativeMain by getting {
            dependencies {
               implementation("org.jetbrains.kotlinx:atomicfu:0.23.1")
            }
         }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
