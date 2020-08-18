package io.kotest.plugin.intellij.run

// if we have the new launcher on the classpath we're on kotest 4.2+
// if we have the old launcher on the classpath we're on kotest 4.1
private const val mainClass42 = "io.kotest.framework.launcher.LauncherKt"
private const val mainClass41 = "io.kotest.launcher.LauncherKt"

fun determineKotestMainClass(): String = try {
   Class.forName(mainClass42)
   mainClass42
} catch (e: Throwable) {
   mainClass41
}
