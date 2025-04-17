package utils

import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

/** Convert a Gradle [JavaLanguageVersion] to a Kotlin [JvmTarget]. */
internal fun JavaLanguageVersion.jvmTarget(): JvmTarget {
   return JvmTarget.fromTarget(asInt().toString())
}

internal fun Provider<JavaLanguageVersion>.jvmTarget(): Provider<JvmTarget> =
   map { JvmTarget.fromTarget(it.asInt().toString()) }

internal fun Provider<JavaLanguageVersion>.asInt(): Provider<Int> =
   map { it.asInt() }

internal fun KotlinJvmCompilerOptions.jdkRelease(version: Provider<JavaLanguageVersion>) {
   freeCompilerArgs.add(version.map {
      "-Xjdk-release=${it.jvmTarget().target}"
   })
}
