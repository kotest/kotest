package io.kotest.core.factory

/**
 * Builds an immutable [TestFactory] from this [TestFactoryConfiguration].
 */
internal fun TestFactoryConfiguration.build(): TestFactory {
   TODO()
//   return TestFactory(
//      factoryId = factoryId,
//      tests = tests,
//      tags = _tags,
//      extensions = _extensions.map {
//         when (it) {
//            is TestListener -> FactoryConstrainedTestListener(factoryId, it)
//            else -> it
//         }
//      },
//      assertionMode = assertions,
//   )
}
