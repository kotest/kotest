package io.kotest.engine.spec

import io.kotest.core.spec.Order
import io.kotest.core.spec.SpecRef
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import java.nio.file.Paths

actual val FailureFirstSorter: SpecSorter = object : SpecSorter {

   private val path = Paths.get(".kotest").resolve("spec_failures")

   override fun sort(specs: List<SpecRef>): List<SpecRef> {
      // try to locate a folder called .kotest which should contain a file called spec_failures
      // each line in this file is a failed spec and they should run first
      // if the file doesn't exist then we just execute in Lexicographic order
      return if (path.toFile().exists()) {
         val classnames = path.toFile().readLines()
         specs.sortedWith { a, b ->
            val afailed = classnames.contains(a.kclass.qualifiedName)
            val bfailed = classnames.contains(b.kclass.qualifiedName)
            when {
               afailed && !bfailed -> -1
               bfailed -> 1
               else -> a.kclass.bestName().compareTo(b.kclass.bestName())
            }
         }
      } else {
         LexicographicSpecSorter.sort(specs)
      }
   }
}

actual val AnnotatedSpecSorter: SpecSorter = object : SpecSorter {
   override fun sort(specs: List<SpecRef>): List<SpecRef> {
      return specs.sortedBy { ref -> ref.kclass.annotation<Order>()?.value ?: Int.MAX_VALUE }
   }
}
