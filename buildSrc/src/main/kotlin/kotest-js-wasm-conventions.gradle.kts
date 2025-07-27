import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotlin-conventions")
}

val kotestSettings = extensions.getByType<KotestBuildLogicSettings>()

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY) && kotestSettings.enableKotlinJs.get()) {
      js {
         browser()
         nodejs()
      }

      @OptIn(ExperimentalWasmDsl::class)
      wasmJs {
         browser()
         nodejs()
      }

      @OptIn(ExperimentalWasmDsl::class)
      wasmWasi {
         nodejs()
      }

      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            // many KMP functions boil down to "jvm" implementations and "other" implementations, for example
            // anything that needs reflection will be jvm only, so we create a common group for all non-jvm targets
            group("nonjvm") {
               withJs()
               withWasmJs()
               withWasmWasi()
            }
         }
      }

   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
