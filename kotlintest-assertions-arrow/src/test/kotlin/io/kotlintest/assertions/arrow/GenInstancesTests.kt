package io.kotlintest.assertions.arrow

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.data.Nel
import arrow.data.NonEmptyList
import arrow.data.Validated
import arrow.data.ValidatedPartialOf
import arrow.effects.DeferredK
import arrow.effects.IO
import arrow.effects.deferredk.applicative.applicative
import arrow.effects.instances.io.applicative.applicative
import arrow.extension
import arrow.instances.nonemptylist.semigroup.semigroup
import arrow.instances.order
import arrow.instances.validated.applicativeError.applicativeError
import arrow.product
import arrow.typeclasses.Applicative
import arrow.validation.RefinedPredicateException
import arrow.validation.Refinement
import arrow.validation.refinedTypes.numeric.validated.negative.negative
import io.kotlintest.assertions.arrow.either.either
import io.kotlintest.assertions.arrow.eq.deferredk.effectMatchers.shouldBeInterpretedTo
import io.kotlintest.assertions.arrow.eq.forAll
import io.kotlintest.assertions.arrow.eq.io.effectMatchers.shouldBeInterpretedTo
import io.kotlintest.assertions.arrow.eq.shouldBeRefinedBy
import io.kotlintest.assertions.arrow.gen.gen.applicative.map
import io.kotlintest.assertions.arrow.nel.nel
import io.kotlintest.assertions.arrow.validated.nonEmptyPerson.nonEmptyPerson
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec


@product
data class Person(val id: Long, val name: String) {
  companion object
}

interface NonEmptyPerson<F> : Refinement<F, Person> {
  override fun invalidValueMsg(a: Person): String =
    "$a should have a name"

  override fun Person.refinement(): Boolean =
    name.isNotEmpty()
}

@extension
interface ValidatedNonEmptyPerson :
  NonEmptyPerson<ValidatedPartialOf<Nel<RefinedPredicateException>>> {
  override fun applicativeError() =
    Validated.applicativeError(NonEmptyList.semigroup<RefinedPredicateException>())
}

fun Person.Companion.gen(): Gen<Person> =
  map(
    Gen.long(),
    Gen.string(),
    Tuple2<Long, String>::toPerson
  )

fun <F> Applicative<F>.helloWorldPoly(): Kind<F, String> = just("Hello World")

class GenInstancesTests : StringSpec({

  "Provide a `shouldBeRefinedBy` matcher application for reified types" {
    -1 shouldBeRefinedBy Validated.negative(Int.order())
  }

  "Allow semi automatic derivation of Gen encoders for arbitrary product types" {
    shouldThrow<AssertionError> {
      forAll(Person.gen()) { it.name.isNotEmpty() }
    }
  }

  "Allow semi automatic derivation and refined predicates in `forAll` universal quantifiers" {
    forAll(Person.gen(), Validated.nonEmptyPerson()) { it.name.isNotEmpty() }
  }

  "Allow matchers for ad-hoc polymorphic programs and higher kinded values [IO]" {
    IO.applicative().run {
      helloWorldPoly() shouldBeInterpretedTo "Hello World"
    }
  }

  "Allow matchers for ad-hoc polymorphic programs and higher kinded values [Deferred]" {
    DeferredK.applicative().run {
      helloWorldPoly() shouldBeInterpretedTo "Hello World"
    }
  }

  "Gen<NonEmptyList<A>>" {
    forAll(Gen.nel(Gen.int(), 0)) { it.contains(0) }
  }

  "Gen<Either<A, B>>" {
    forAll(Gen.either(Gen.constant(1), Gen.constant(0))) {
      when (it) {
        is Either.Left -> it.a == 1
        is Either.Right -> it.b == 0
      }
    }
  }


})