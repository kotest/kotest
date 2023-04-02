package io.kotest.mpp

import java.io.File
import java.nio.file.Path
import java.util.UUID
import kotlin.reflect.KClass

actual fun isPlatformStable(kclass: KClass<*>): Boolean {
   return jvmPlatformStableTypes.contains(kclass)
}

private val jvmPlatformStableTypes = setOf(
   UUID::class,
   File::class,
   Path::class,
)
