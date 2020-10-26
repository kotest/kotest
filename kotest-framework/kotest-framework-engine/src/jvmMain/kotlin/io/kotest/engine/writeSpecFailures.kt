package io.kotest.engine

import io.kotest.core.spec.Spec
import io.kotest.fp.Try
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KClass

fun writeSpecFailures(failures: Set<KClass<out Spec>>, filename: String) = Try {
    val path = Paths.get(filename).toAbsolutePath()
    path.parent.toFile().mkdirs()
    val content = failures.distinct().joinToString("\n") { it.java.canonicalName }
    Files.write(path, content.toByteArray())
}
