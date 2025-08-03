package io.kotest.extensions.pitest

import org.pitest.help.PitHelpError
import org.pitest.testapi.Configuration
import org.pitest.testapi.TestSuiteFinder
import org.pitest.testapi.TestUnitFinder
import java.util.Optional

class KotestConfiguration : Configuration {

   override fun verifyEnvironment(): Optional<PitHelpError> = Optional.empty()

   override fun testUnitFinder(): TestUnitFinder = KotestUnitFinder()

   override fun testSuiteFinder(): TestSuiteFinder = KotestSuiteFinder()
}
