import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotlin-conventions")
}

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY)) {
      androidNativeX86()
      androidNativeX64()
      androidNativeArm32()
      androidNativeArm64()

      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            // many KMP functions boil down to "jvm" implementations and "other" implementations, for example
            // anything that needs reflection will be jvm only, so we create a common group for all non-jvm targets
            group("nonjvm") {
               withAndroidNative()
            }
         }
      }

   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
