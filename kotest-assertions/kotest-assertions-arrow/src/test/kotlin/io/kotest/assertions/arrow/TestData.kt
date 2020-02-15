//package io.kotest.assertions.arrow
//
//import arrow.Kind
//import arrow.core.Tuple2
//import arrow.data.Nel
//import arrow.data.NonEmptyList
//import arrow.data.Validated
//import arrow.data.ValidatedPartialOf
//import arrow.data.extensions.nonemptylist.semigroup.semigroup
//import arrow.data.extensions.validated.applicativeError.applicativeError
//import arrow.extension
//import arrow.product
//import arrow.typeclasses.Applicative
//import arrow.validation.RefinedPredicateException
//import arrow.validation.Refinement
//import io.kotest.properties.Gen
//
///**
// * Marker [Throwable] used to generate random [arrow.core.Failure] cases
// */
//object Ex : RuntimeException("BOOM")
//
//@product
//data class Person(val id: Long, val name: String) {
//  companion object
//}
//
//interface NonEmptyPerson<F> : Refinement<F, Person> {
//  override fun invalidValueMsg(a: Person): String =
//    "$a should have a name"
//
//  override fun Person.refinement(): Boolean =
//    name.isNotEmpty()
//}
//
//@extension
//interface ValidatedNonEmptyPerson :
//  NonEmptyPerson<ValidatedPartialOf<Nel<RefinedPredicateException>>> {
//  override fun applicativeError() =
//    Validated.applicativeError(NonEmptyList.semigroup<RefinedPredicateException>())
//}
//
//fun Person.Companion.gen(): Gen<Person> =
//  map(
//    Gen.long(),
//    Gen.string(),
//    Tuple2<Long, String>::toPerson
//  )
//
//fun <F> Applicative<F>.helloWorldPoly(): Kind<F, String> = just("Hello World")
