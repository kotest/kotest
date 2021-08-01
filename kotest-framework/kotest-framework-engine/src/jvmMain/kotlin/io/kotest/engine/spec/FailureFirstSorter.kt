package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import java.nio.file.Paths
import kotlin.reflect.KClass

actual class FailureFirstSorter : SpecSorter {

   private val path = Paths.get(".kotest").resolve("spec_failures")

   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int {
      // try to locate a folder called .kotest which should contain a file called spec_failures
      // each line in this file is a failed spec and they should run first
      // if the file doesn't exist then we just execute in Lexico order
      return if (path.toFile().exists()) {
         val classnames = path.toFile().readLines()
         val afailed = classnames.contains(a.qualifiedName)
         val bfailed = classnames.contains(b.qualifiedName)
         return when {
            afailed && bfailed -> LexicographicSpecSorter.compare(a, b)
            afailed -> -1
            bfailed -> 1
            else -> LexicographicSpecSorter.compare(a, b)
         }
      } else {
         LexicographicSpecSorter.compare(a, b)
      }
   }
}
