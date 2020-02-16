//package io.kotest.assertions.arrow
//
//import arrow.data.Validated
//import arrow.effects.IO
//import arrow.effects.extensions.io.applicative.applicative
//import arrow.core.extensions.eq
//import arrow.core.extensions.order
//import arrow.core.extensions.semigroup
//import arrow.validation.refinedTypes.numeric.validated.negative.negative
//import io.kotest.assertions.arrow.`try`.`try`
//import io.kotest.assertions.arrow.either.either
//import io.kotest.assertions.arrow.eq.EqAssertions
//import io.kotest.assertions.arrow.nel.nel
//import io.kotest.assertions.arrow.option.option
//import io.kotest.assertions.arrow.order.OrderAssertions
//import io.kotest.assertions.arrow.refinements.forAll
//import io.kotest.assertions.arrow.refinements.shouldBeRefinedBy
//import io.kotest.assertions.arrow.tagless.io.taglessAssertions.shouldBeInterpretedTo
//import io.kotest.assertions.arrow.validated.nonEmptyPerson.nonEmptyPerson
//import io.kotest.assertions.arrow.validation.validated
//import io.kotest.properties.Gen
//import io.kotest.properties.forAll
//import io.kotest.shouldThrow
//import io.kotest.core.spec.style.StringSpec
//import io.kotest.assertions.arrow.gen.gen.fx.fx
//
//class ArrowAssertionsTests : StringSpec({
//
//  "Provide assertions and matchers for refined types" {
//    -1 shouldBeRefinedBy Validated.negative(Int.order())
//  }
//
//  "Allow semi automatic derivation of Gen encoders for arbitrary product types" {
//    shouldThrow<AssertionError> {
//      forAll(Person.gen()) { it.name.isNotEmpty() }
//    }
//  }
//
//  "Provide semi automatic derivation and refined predicates in `forAll` universal quantifiers" {
//    forAll(Person.gen(), Validated.nonEmptyPerson()) { it.name.isNotEmpty() }
//  }
//
//  "Provide assertions for ad-hoc polymorphic programs and higher kinded values [IO]" {
//    IO.applicative().run {
//      helloWorldPoly() shouldBeInterpretedTo "Hello World"
//    }
//  }
//
//  "Provide assertions for values bound by the `Eq` type class" {
//    EqAssertions(Int.eq()) {
//      0 shouldBeEqvTo 0
//      0 shouldNotBeEqvTo -1
//    }
//  }
//
//  "Provide assertions for values bound by the `Order` type class" {
//    OrderAssertions(Int.order()) {
//      0 shouldBeEqvTo 0
//      0 shouldNotBeEqvTo -1
//      0 shouldBeGreaterThan -1
//      0 shouldBeGreaterThanOrEqual 0
//      0 shouldBeSmallerThan 1
//      0 shouldBeSmallerThanOrEqual 0
//    }
//  }
//
//  "Gen<NonEmptyList<A>>" {
//    forAll(Gen.nel(Gen.int(), 0)) { it.contains(0) }
//  }
//
//  "Gen<Either<A, B>>" {
//    forAll(Gen.either(Gen.constant(1), Gen.constant(0))) {
//      it.fold({ l -> l == 1 }, { r -> r == 0 })
//    }
//  }
//
//  "Gen<Option<A>>" {
//    forAll(Gen.option(Gen.constant(1))) {
//      it.fold({ true }, { n -> n == 1 })
//    }
//  }
//
//  "Gen<Try<A>>" {
//    forAll(Gen.`try`(Gen.constant(Ex), Gen.constant(1))) {
//      it.fold({ ex -> ex == Ex }, { n -> n == 1 })
//    }
//  }
//
//  "Gen<Validated<A, B>>" {
//    forAll(Gen.validated(Gen.constant(1), Gen.constant(0), Int.semigroup())) {
//      it.fold({ l -> l == 1 }, { r -> r == 0 })
//    }
//  }
//
//  "Gen binding" {
//    val prefix = "_"
//    val personGen: Gen<Person> = fx {
//      val id = !Gen.long()
//      val name = !Gen.string()
//      Person(id, prefix + name)
//    }
//    forAll(personGen) { it.name.startsWith(prefix) }
//  }
//
//})
