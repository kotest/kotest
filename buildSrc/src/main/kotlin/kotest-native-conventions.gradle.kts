import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotlin-conventions")
}

val kotestSettings = extensions.getByType<KotestBuildLogicSettings>()

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY) && kotestSettings.enableKotlinNative.get()) {

      iosX64()
      iosArm64()
      iosSimulatorArm64()

      linuxX64()
      linuxArm64()

      macosX64()
      macosArm64()

      mingwX64()

      tvosX64()
      tvosArm64()
      tvosSimulatorArm64()

      watchosArm32()
      watchosArm64()
      watchosX64()
      watchosSimulatorArm64()

      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            // many KMP functions boil down to "jvm" implementations and "other" implementations, for example
            // anything that needs reflection will be jvm only, so we create a common group for all non-jvm targets
            group("nonjvm") {
               withNative()
            }
         }
      }

   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
