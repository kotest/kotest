import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
   id("kotlin-conventions")
}

val kotestSettings = extensions.getByType<KotestBuildLogicSettings>()

kotlin {
   if (!project.hasProperty(Ci.JVM_ONLY) && kotestSettings.enableKotlinJs.get()) {

      // Shared Karma config (repo-root/karma.config.d) used by every browser test task. It raises the
      // Karma disconnect / no-activity timeouts so slow or loaded CI runners don't fail with a spurious
      // "Disconnected ... because no message in 30000 ms" while the large JS/WasmJS test bundle starts
      // up in the headless browser.
      val karmaConfigDir = rootDir.resolve("karma.config.d")

      js {
         browser {
            testTask {
               useKarma {
                  // providing a custom useKarma block clears the default browser, so re-add the
                  // Chrome Headless browser the plugin would otherwise configure by default
                  useChromeHeadless()
                  useConfigDirectory(karmaConfigDir)
               }
            }
         }
         nodejs()
      }

      @OptIn(ExperimentalWasmDsl::class)
      wasmJs {
         browser {
            testTask {
               useKarma {
                  // providing a custom useKarma block clears the default browser, so re-add the
                  // Chrome Headless browser the plugin would otherwise configure by default
                  useChromeHeadless()
                  useConfigDirectory(karmaConfigDir)
               }
            }
         }
         nodejs()
      }

      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
         group("common") {
            // many KMP functions boil down to "jvm" implementations and "other" implementations, for example
            // anything that needs reflection will be jvm only, so we create a common group for all non-jvm targets
            group("nonjvm") {
               group("jsHosted") {
                  withJs()
                  withWasmJs()
               }
            }
         }
      }
   } else {
      // Make sure every project has at least one valid target, otherwise Kotlin compiler will complain
      jvm()
   }
}
