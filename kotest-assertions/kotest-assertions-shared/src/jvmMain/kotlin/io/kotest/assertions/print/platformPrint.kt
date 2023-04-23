@file:JvmName("platformjvm")
package io.kotest.assertions.print

import kotlin.reflect.KClass

/**
 * Return a [Print] based on available runtime classes.
 * Ex. A standard Java 8 JVM will have the 'java.nio.file.Path'
 * type while certain versions of the Android JVM will not.
 *
 * Uses reflection to check for the available classes
 * to avoid a runtime [ClassNotFoundException] when
 * called on a JVM platform that may not have the
 * required compiled types.
 *
 * @return [PathPrint] if [A] is a 'java.nio.file.Path',
 * or [FilePrint] if [A] is `java.io.File`,
 * or `null` otherwise.
 */
@Suppress("UNCHECKED_CAST")
actual fun <A : Any> platformPrint(a: A): Print<A>? = when {
  javaNioPathKlass()?.isInstance(a) ?: false -> PathPrint as Print<A>
  javaIoFileKlass()?.isInstance(a) ?: false -> FilePrint as Print<A>
  else -> null
}

private fun javaNioPathKlass(): KClass<*>? = try {
  /*
   * There is no KClass reflection API to find a
   * class by string so the Java Class Reflection
   * API must be used.
   * See https://youtrack.jetbrains.com/issue/KT-10440
   */
  Class.forName("java.nio.file.Path").kotlin
} catch (_: ClassNotFoundException) {
  // ignore Path as it may not exist on Android.
  null
}

private fun javaIoFileKlass(): KClass<*>? = try {
  /*
   * There is no KClass reflection API to find a
   * class by string so the Java Class Reflection
   * API must be used.
   * See https://youtrack.jetbrains.com/issue/KT-10440
   */
  Class.forName("java.io.File").kotlin
} catch (_: ClassNotFoundException) {
  null
}

internal actual fun Any?.printType() = when {
   this == null -> "Nothing"
   else -> this::class.qualifiedName ?: "UnknownType"
}
