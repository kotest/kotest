package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.engine.instantiateOrObject

actual fun loadPackageConfig(packageName: String): AbstractPackageConfig? {
   // ok to skip if the class doesn't exist
   val kclass = runCatching { Class.forName(packageConfigName(packageName)).kotlin }.getOrNull() ?: return null
   // but should fail if the class exists but cannot be instantiated
   return instantiateOrObject(kclass).getOrThrow() as AbstractPackageConfig
}

private fun packageConfigName(packageName: String) = "$packageName.PackageConfig"
