package io.kotest.engine.teamcity

import org.opentest4j.AssertionFailedError

actual fun TeamCityMessageBuilder.handlePlatformComparisonExceptions(error: Throwable) {
   if (error is AssertionFailedError) {
      type("comparisonFailure")
         .expected(error.expected?.stringRepresentation)
         .actual(error.actual?.stringRepresentation)
         .build()
   }
}
