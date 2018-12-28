package io.kotlintest.assertions.arrow

import arrow.data.Validated
import arrow.instances.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.negative.negative
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.assertions.arrow.eq.shouldBeRefinedBy
import org.junit.runner.RunWith


@RunWith(KTestJUnitRunner::class)
class GenInstancesTests : UnitSpec() {
  init {
    /**
     * can't make this work until Arrow is updated to the latest kotlintest as there are bin compat issues with the
     * transitive deps in arrow-test
     */
    testLaws(
//      MonoidLaws.laws(Gen.monoid(Int.monoid()), Gen.constant(1), Eq.any()),
//      MonadLaws.laws(Gen.monad(), Eq.any())
    )

    "shouldRefine" {
      1 shouldBeRefinedBy Validated.negative(Int.order())
    }

  }
}