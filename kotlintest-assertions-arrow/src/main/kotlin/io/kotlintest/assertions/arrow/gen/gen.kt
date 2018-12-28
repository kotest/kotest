package io.kotlintest.assertions.arrow.gen

import arrow.Kind
import arrow.core.Either
import arrow.core.fix
import arrow.extension
import arrow.typeclasses.*
import io.kotlintest.properties.ForGen
import io.kotlintest.properties.Gen
import io.kotlintest.properties.GenOf
import io.kotlintest.properties.fix

@extension
interface GenSemigroup<A> : Semigroup<Gen<A>> {
  fun SA(): Semigroup<A>

  override fun Gen<A>.combine(b: Gen<A>): Gen<A> =
    SA().run {
      flatMap { x ->
        b.map { y ->
          x.combine(y)
        }
      }
    }
}

@extension
interface GenMonoid<A> : Monoid<Gen<A>>, GenSemigroup<A> {
  fun MA(): Monoid<A>

  override fun SA(): Semigroup<A> = MA()

  override fun empty(): Gen<A> = Gen.constant(MA().empty())
}

@extension
interface GenFunctor : Functor<ForGen> {
  override fun <A, B> GenOf<A>.map(f: (A) -> B): Gen<B> =
    fix().map(f)
}

@extension
interface GenApplicative : Applicative<ForGen> {
  override fun <A, B> GenOf<A>.ap(ff: Kind<ForGen, (A) -> B>): Gen<B> =
    ff.fix().flatMap { this.fix().map(it) }

  override fun <A, B> GenOf<A>.map(f: (A) -> B): Gen<B> =
    fix().map(f)

  override fun <A> just(a: A): Gen<A> =
    Gen.constant(a)
}

@extension
interface GenMonad : Monad<ForGen> {
  override fun <A, B> GenOf<A>.ap(ff: Kind<ForGen, (A) -> B>): Gen<B> =
    fix().ap(ff)

  override fun <A, B> GenOf<A>.flatMap(f: (A) -> Kind<ForGen, B>): Gen<B> =
    fix().flatMap(f)


  override fun <A, B> tailRecM(a: A, f: (A) -> GenOf<Either<A, B>>): Gen<B> =
    f(a).fix().flatMap { x ->
      when (x) {
        is Either.Left -> tailRecM(x.a, f)
        is Either.Right -> Gen.constant(x.b)
      }
    }

  override fun <A, B> GenOf<A>.map(f: (A) -> B): Gen<B> =
    fix().map(f)

  override fun <A> just(a: A): Gen<A> =
    Gen.constant(a)
}
