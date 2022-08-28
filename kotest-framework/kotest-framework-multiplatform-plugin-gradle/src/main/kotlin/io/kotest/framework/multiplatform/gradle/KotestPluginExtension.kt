package io.kotest.framework.multiplatform.gradle

import org.gradle.api.provider.Property

abstract class KotestPluginExtension {
   /**
    * The version of the Kotest compiler plugin required by the embeddable Kotlin 1.7 compiler.
    *
    * Defaults to [KOTEST_COMPILER_PLUGIN_VERSION].
    *
    * See: ["Unified compiler plugin ABI with JVM and JS IR backends"](https://kotlinlang.org/docs/whatsnew17.html#unified-compiler-plugin-abi-with-jvm-and-js-ir-backends)
    */
   abstract val kotestCompilerPluginVersion: Property<String>
}
