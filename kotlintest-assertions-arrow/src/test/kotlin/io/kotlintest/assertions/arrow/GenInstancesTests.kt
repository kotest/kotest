package io.kotlintest.assertions.arrow

import arrow.Kind
import arrow.core.toT
import arrow.instances.monoid
import arrow.test.UnitSpec
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.binding
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.assertions.arrow.gen.gen.monad.monad
import io.kotlintest.assertions.arrow.gen.gen.monoid.monoid
import io.kotlintest.properties.ForGen
import io.kotlintest.properties.Gen
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

  }
}