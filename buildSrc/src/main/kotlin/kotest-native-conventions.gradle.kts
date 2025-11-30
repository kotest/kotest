import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotlin-conventions")
}

val kotestSettings = extensions.getByType<KotestBuildLogicSettings>()

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY) && kotestSettings.enableKotlinNative.get()) {

      linuxX64()
      linuxArm64()

      macosX64()
      macosArm64()

      if (System.getProperty("os.name").startsWith("Windows")) {
         mingwX64()
      }

      /**
       * Macos runners have limited CPU resources as they are expensive, so github (very reasonably) limits
       * concurrency on them for open source projects. We therefore only run these targets on master builds,
       * or on non-CI builds. Since the chances of a PR breaking watch/tv/ios but working on macos is pretty low,
       * we get to speed up the builds considerably, and worst case is we'll have a green PR build but a red
       * master build after merge. I think this trade off is acceptable.
       */
      if (Ci.shouldRunWatchTvIosModules) {
         iosX64()
         iosArm64()
         iosSimulatorArm64()

         tvosX64()
         tvosArm64()
         tvosSimulatorArm64()

         watchosArm32()
         watchosArm64()
         watchosX64()
         watchosSimulatorArm64()
      }

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
