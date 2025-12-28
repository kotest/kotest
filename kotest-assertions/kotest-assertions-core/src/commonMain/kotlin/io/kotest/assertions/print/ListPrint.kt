package io.kotest.assertions.print

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.AssertionsConfigSystemProperties
import io.kotest.assertions.EnvironmentConfigValue
import io.kotest.common.Platform
import io.kotest.common.platformExecution

class ListPrint<T>(
   private val limitConfigValue: EnvironmentConfigValue<Int> = AssertionsConfig.maxCollectionPrintSize,
) : Print<List<T>> {

   override fun print(a: List<T>): Printed {
      return if (a.isEmpty()) Printed("[]") else {

         val limit = limitConfigValue.value
         val remainingItems = a.size - limit

         val limitHint = when (platformExecution.platform) {
            Platform.JVM, Platform.Native -> " (set '${AssertionsConfigSystemProperties.COLLECTIONS_PRINT_SIZE}' to see more / less items)"
            else -> null
         }

         return Printed(
            a.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]",
            limit = limit,
            truncated = "...and $remainingItems more$limitHint"
         ) {
            when {
               it is Iterable<*> && it.toList() == a && a.size == 1 -> a[0].toString()
               else -> recursiveRepr(a, it).value
            }
         }, null
         )
      }
   }
}
