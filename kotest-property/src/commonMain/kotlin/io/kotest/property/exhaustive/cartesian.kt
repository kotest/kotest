package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun <A, B, C> Exhaustive.Companion.cartesian(a: Exhaustive<A>, b: Exhaustive<B>, f: (A, B) -> C): Exhaustive<C> {
   val cs = a.values.flatMap { _a ->
      b.values.map { _b ->
         f(_a, _b)
      }
   }
   return cs.exhaustive()
}

fun <A, B, C, D> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   f: (A, B, C) -> D
): Exhaustive<D> {
   val ds = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.map { _c ->
            f(_a, _b, _c)
         }
      }
   }
   return ds.exhaustive()
}

fun <A, B, C, D, E> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   f: (A, B, C, D) -> E
): Exhaustive<E> {
   val es = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.map { _d ->
               f(_a, _b, _c, _d)
            }
         }
      }
   }
   return es.exhaustive()
}

fun <A, B, C, D, E, F> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   e: Exhaustive<E>,
   f: (A, B, C, D, E) -> F
): Exhaustive<F> {
   val fs = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.flatMap { _d ->
               e.values.map { _e ->
                  f(_a, _b, _c, _d, _e)
               }
            }
         }
      }
   }
   return fs.exhaustive()
}
