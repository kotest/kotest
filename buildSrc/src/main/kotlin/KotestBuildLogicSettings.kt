import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

/**
 * Common settings for configuring Kotest's build logic.
 *
 * The settings need to be accessible during configuration, so they should come from Gradle
 * properties or environment variables.
 */
abstract class KotestBuildLogicSettings @Inject constructor(
   private val providers: ProviderFactory,
) {

   /** Controls whether Kotlin Multiplatform JS is enabled */
   val enableKotlinJs: Provider<Boolean> = kotestFlag("enableKotlinJs", true)

   /** Controls whether Kotlin Multiplatform Native is enabled */
   val enableKotlinNative: Provider<Boolean> = kotestFlag("enableKotlinNative", false)

   /**
    * Comma separated list of MavenPublication names that will have the publishing task enabled.
    * The provided names will be matched ignoring case, and by prefix, so `iOS` will match
    * `iosArm64`, `iosX64`, and `iosSimulatorArm64`.
    *
    * This is used to avoid duplicate publications, which can occur when a Kotlin Multiplatform
    * project is published in CI/CD on different host machines (Linux, Windows, and macOS).
    *
    * For example, by including `jvm` in the values when publishing on Linux, but omitting `jvm` on
    * Windows and macOS, this results in any Kotlin/JVM publications only being published once.
    */
   val enabledPublicationNamePrefixes: Provider<Set<String>> =
      kotestSetting("enabledPublicationNamePrefixes", "KotlinMultiplatform,Jvm,Js,iOS,macOS,watchOS,tvOS,mingw,wasm,android,linux,pluginMaven,KotestBom")
         .map { enabledPlatforms ->
            enabledPlatforms
               .split(",")
               .map { it.trim() }
               .filter { it.isNotBlank() }
               .toSet()
         }

   private fun kotestSetting(name: String, default: String? = null) =
      providers.gradleProperty("kotest_$name")
         .orElse(providers.provider { default }) // workaround for https://github.com/gradle/gradle/issues/12388

   private fun kotestFlag(name: String, default: Boolean) =
      providers.gradleProperty("kotest_$name").map { it.toBoolean() }.orElse(default)

   companion object {
      const val EXTENSION_NAME = "kotestSettings"

      /**
       * Regex for matching the release version.
       *
       * If a version does not match this code it should be treated as a SNAPSHOT version.
       */
      val releaseVersionRegex = Regex("\\d+.\\d+.\\d+")
   }
}
