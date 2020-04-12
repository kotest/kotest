package io.kotest.core.factory

import io.kotest.core.spec.Spec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase

/**
 * Generates a [TestCase] for each [DynamicTest] in this factory.
 * Tags and assertion mode are applied to the tests.
 * Any included factories are recursively called and their generated
 * tests included in the returned list.
 *
 * @param description the parent description for the generated tests.
 * @param spec the [Spec] that will contain the generated tests.
 */
fun TestFactory.generate(description: Description, spec: Spec): List<TestCase> {
   return tests.map { dyn ->
      TestCase(
         description = description.append(dyn.name),
         spec = spec,
         test = dyn.test,
         type = dyn.type,
         source = dyn.source,
         config = dyn.config.copy(tags = dyn.config.tags + this.tags),
         factoryId = this.factoryId,
         assertionMode = this.assertionMode
      )
   } + factories.flatMap { it.generate(description, spec) }
}
