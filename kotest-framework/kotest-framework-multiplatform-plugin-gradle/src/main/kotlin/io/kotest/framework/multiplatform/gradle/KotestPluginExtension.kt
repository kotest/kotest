package io.kotest.framework.multiplatform.gradle

import org.gradle.api.provider.Property


abstract class KotestPluginExtension {
   /**
    * The version to use for the Kotest compiler plugins.
    *
    * Defaults to [KOTEST_COMPILER_PLUGIN_VERSION].
    */
   abstract val kotestCompilerPluginVersion: Property<String>
}
