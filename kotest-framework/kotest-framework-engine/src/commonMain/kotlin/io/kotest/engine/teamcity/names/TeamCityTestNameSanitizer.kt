package io.kotest.engine.teamcity.names

import io.kotest.core.names.TestName

/**
 * Sanitizes test names to work around platform/framework limitations.
 * Delegates to various specific workaround implementations.
 */
internal object TeamCityTestNameSanitizer {

   fun sanitize(name: TestName, parent: TestName?): TestName {
      val parentStripped = ParentNameStripper.stripe(name, parent)
      return name.copy(name = TeamCityTestNameEscaper.escape(parentStripped))
   }
}
