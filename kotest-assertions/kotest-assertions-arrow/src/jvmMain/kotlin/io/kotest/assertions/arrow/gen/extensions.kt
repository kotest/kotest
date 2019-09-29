//package io.kotest.assertions.arrow.gen
//
//import arrow.Kind
//import arrow.core.Either
//import arrow.extension
//import arrow.typeclasses.*
//import arrow.typeclasses.suspended.monad.Fx
//import io.kotest.assertions.arrow.gen.gen.monad.monad
//import io.kotest.properties.ForGen
//import io.kotest.properties.Gen
//import io.kotest.properties.GenOf
//import io.kotest.properties.fix
//
///**
// * [Semigroup] extension for Gen<A>
// *
// * Combine two generators of type Gen<A> into a single Gen<A> provided
// * there is a evidence of a Semigroup<A>
// *
// * (Gen<A>, Gen<A>, with Semigroup<A>) -> Gen<A>
// */
//@extension
//interface GenSemigroup<A> : Semigroup<Gen<A>> {
//  fun SA(): Semigroup<A>
//
//  override fun Gen<A>.combine(b: Gen<A>): Gen<A> =
//      SA().run {
//        flatMap { x ->
//          b.map { y ->
//            x.combine(y)
//          }
//        }
//      }
//}
//
///**
// * [Monoid] extension for Gen<A>
// *
// * Provide an [empty] [Gen] for [A] provided there is an existing
// * [Monoid] for [A]
// *
// * (Gen<A>, Gen<A>, with Semigroup<A>) -> Gen<A>
// * (with Monoid<A>) -> Gen<A>
// */
//@extension
//interface GenMonoid<A> : Monoid<Gen<A>>, GenSemigroup<A> {
//  fun MA(): Monoid<A>
//
//  override fun SA(): Semigroup<A> = MA()
//
//  override fun empty(): Gen<A> = Gen.constant(MA().empty())
//}
//
///**
// * [Functor] extension for Gen<_>
// *
// * Transform a `Gen<A>` into a `Gen<B>` given a `(A) -> B` function
// *
// * (Gen<A>, (A) -> B) -> Gen<B>
// */
//@extension
//interface GenFunctor : Functor<ForGen> {
//  override fun <A, B> GenOf<A>.map(f: (A) -> B): Gen<B> =
//      fix().map(f)
//}
//
///**
// * [Applicative] extension for Gen<_>
// *
// * Lift a value [A] in the environment into a Gen<A> via [just]
// *
// * (A) -> Gen<A>
// *
// * Map multiple [Gen] with arbitrary arity to compose new Generators with [map]
// *
// * (Gen<A>, Gen<B>, Gen<C>, ...) { (a, b, c, d) -> ... Gen<Z> }
// */
//@extension
//interface GenApplicative : Applicative<ForGen>, GenFunctor {
//  override fun <A, B> GenOf<A>.ap(ff: Kind<ForGen, (A) -> B>): Gen<B> =
//      ff.fix().flatMap { this.fix().map(it) }
//
//  override fun <A, B> GenOf<A>.map(f: (A) -> B): Gen<B> =
//      fix().map(f)
//
//  override fun <A> just(a: A): Gen<A> =
//      Gen.constant(a)
//}
//
///**
// * [Monad] extension for Gen<_>
// *
// * Sequentially bind generators to produce new generators
// *
// * @see [GenFx]
// **/
//@extension
//interface GenMonad : Monad<ForGen>, GenApplicative {
//  override fun <A, B> GenOf<A>.ap(ff: Kind<ForGen, (A) -> B>): Gen<B> =
//      fix().ap(ff)
//
//  override fun <A, B> GenOf<A>.flatMap(f: (A) -> GenOf<B>): Gen<B> =
//      fix().flatMap(f)
//
//
//  override fun <A, B> tailRecM(a: A, f: (A) -> GenOf<Either<A, B>>): Gen<B> =
//      f(a).fix().flatMap { x ->
//        when (x) {
//          is Either.Left -> tailRecM(x.a, f)
//          is Either.Right -> Gen.constant(x.b)
//        }
//      }
//
//  override fun <A, B> GenOf<A>.map(f: (A) -> B): Gen<B> =
//      fix().map(f)
//
//  override fun <A> just(a: A): Gen<A> =
//      Gen.constant(a)
//}
//
///**
// * [Fx] extension for Gen<_>
// *
// * Sequentially bind generators to produce new generators
// *
// * ```kotlin
// * val prefix = "_"
// * val personGen: Gen<Person> =
// *   fx {
// *     val id = !Gen.long()
// *     val name = !Gen.string()
// *     Person(id, prefix + name)
// *   }
// * forAll(personGen) { it.name.startsWith(prefix) }
// * ```
// */
//@extension
//interface GenFx : Fx<ForGen> {
//  override fun monad(): Monad<ForGen> = Gen.monad()
//}
