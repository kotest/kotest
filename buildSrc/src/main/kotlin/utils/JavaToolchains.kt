package utils

import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

/** Convert a Gradle [JavaLanguageVersion] to a Kotlin [JvmTarget]. */
internal fun JavaLanguageVersion.jvmTarget(): JvmTarget {
   val version = asInt()
   val target = if (version <= 8) "1.$version" else "$version"
   return JvmTarget.fromTarget(target)
}

internal fun Provider<JavaLanguageVersion>.jvmTarget(): Provider<JvmTarget> =
   map { it.jvmTarget() }

internal fun Provider<JavaLanguageVersion>.asInt(): Provider<Int> =
   map { it.asInt() }

internal fun KotlinJvmCompilerOptions.jdkRelease(version: Provider<JavaLanguageVersion>) {
   freeCompilerArgs.add(version.map {
      "-Xjdk-release=${it.jvmTarget().target}"
   })
}
