package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun <A, B, C> Exhaustive<A>.cartesian(other: Exhaustive<B>, f: (A, B) -> C): Exhaustive<C> {
   val cs = values.flatMap { _a ->
      other.values.map { _b ->
         f(_a, _b)
      }
   }
   return cs.exhaustive()
}


fun <A, B> Exhaustive<A>.cartesianPairs(other: Exhaustive<B>): Exhaustive<Pair<A, B>> {
   val pairs = values.flatMap { _a ->
      other.values.map { _b ->
         Pair(_a, _b)
      }
   }
   return pairs.exhaustive()
}

/**
 * Returns the cartesian join of this exhaustive with itself, with the results as pairs.
 */
fun <A> Exhaustive<A>.cartesianPairs(): Exhaustive<Pair<A, A>> {
   val cs = values.flatMap { _a ->
      values.map { _b ->
         Pair(_a, _b)
      }
   }
   return cs.exhaustive()
}

fun <A> Exhaustive<A>.cartesianTriples(): Exhaustive<Triple<A, A, A>> {
   val cs = values.flatMap { _a ->
      values.flatMap { _b ->
         values.map { _c ->
            Triple(_a, _b, _c)
         }
      }
   }
   return cs.exhaustive()
}

fun <A, B, C> Exhaustive.Companion.cartesian(a: Exhaustive<A>, b: Exhaustive<B>, f: (A, B) -> C): Exhaustive<C> {
   val cs = a.values.flatMap { _a ->
      b.values.map { _b ->
         f(_a, _b)
      }
   }
   return cs.exhaustive()
}

fun <A, B> Exhaustive.Companion.cartesianPairs(a: Exhaustive<A>, b: Exhaustive<B>): Exhaustive<Pair<A, B>> {
   val pairs = a.values.flatMap { _a ->
      b.values.map { _b ->
         Pair(_a, _b)
      }
   }
   return pairs.exhaustive()
}

fun <A, B, C> Exhaustive.Companion.cartesianTriples(a: Exhaustive<A>, b: Exhaustive<B>, c: Exhaustive<C>): Exhaustive<Triple<A, B, C>> {
   val triples = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.map { _c ->
            Triple(_a, _b, _c)
         }
      }
   }
   return triples.exhaustive()
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


fun <A, B, C, D, E, F, G> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   e: Exhaustive<E>,
   f: Exhaustive<F>,
   g: (A, B, C, D, E, F) -> G
): Exhaustive<G> {
   val gs = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.flatMap { _d ->
               e.values.flatMap { _e ->
                  f.values.map { _f ->
                     g(_a, _b, _c, _d, _e, _f)
                  }
               }
            }
         }
      }
   }
   return gs.exhaustive()
}

fun <A, B, C, D, E, F, G, H> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   e: Exhaustive<E>,
   f: Exhaustive<F>,
   g: Exhaustive<G>,
   h: (A, B, C, D, E, F, G) -> H
): Exhaustive<H> {
   val hs = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.flatMap { _d ->
               e.values.flatMap { _e ->
                  f.values.flatMap { _f ->
                     g.values.map { _g ->
                        h(_a, _b, _c, _d, _e, _f, _g)
                     }
                  }
               }
            }
         }
      }
   }
   return hs.exhaustive()
}

fun <A, B, C, D, E, F, G, H, I> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   e: Exhaustive<E>,
   f: Exhaustive<F>,
   g: Exhaustive<G>,
   h: Exhaustive<H>,
   i: (A, B, C, D, E, F, G, H) -> I
): Exhaustive<I> {
   val `is` = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.flatMap { _d ->
               e.values.flatMap { _e ->
                  f.values.flatMap { _f ->
                     g.values.flatMap { _g ->
                        h.values.map { _h ->
                           i(_a, _b, _c, _d, _e, _f, _g, _h)
                        }
                     }
                  }
               }
            }
         }
      }
   }
   return `is`.exhaustive()
}

fun <A, B, C, D, E, F, G, H, I, J> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   e: Exhaustive<E>,
   f: Exhaustive<F>,
   g: Exhaustive<G>,
   h: Exhaustive<H>,
   i: Exhaustive<I>,
   j: (A, B, C, D, E, F, G, H, I) -> J
): Exhaustive<J> {
   val js = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.flatMap { _d ->
               e.values.flatMap { _e ->
                  f.values.flatMap { _f ->
                     g.values.flatMap { _g ->
                        h.values.flatMap { _h ->
                           i.values.map { _i ->
                              j(_a, _b, _c, _d, _e, _f, _g, _h, _i)
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
   return js.exhaustive()
}

fun <A, B, C, D, E, F, G, H, I, J, K> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   e: Exhaustive<E>,
   f: Exhaustive<F>,
   g: Exhaustive<G>,
   h: Exhaustive<H>,
   i: Exhaustive<I>,
   j: Exhaustive<J>,
   k: (A, B, C, D, E, F, G, H, I, J) -> K
): Exhaustive<K> {
   val ks = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.flatMap { _d ->
               e.values.flatMap { _e ->
                  f.values.flatMap { _f ->
                     g.values.flatMap { _g ->
                        h.values.flatMap { _h ->
                           i.values.flatMap { _i ->
                              j.values.map { _j ->
                                 k(_a, _b, _c, _d, _e, _f, _g, _h, _i, _j)
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
   return ks.exhaustive()
}

fun <A, B, C, D, E, F, G, H, I, J, K, L> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   e: Exhaustive<E>,
   f: Exhaustive<F>,
   g: Exhaustive<G>,
   h: Exhaustive<H>,
   i: Exhaustive<I>,
   j: Exhaustive<J>,
   k: Exhaustive<K>,
   l: (A, B, C, D, E, F, G, H, I, J, K) -> L
): Exhaustive<L> {
   val ls = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.flatMap { _d ->
               e.values.flatMap { _e ->
                  f.values.flatMap { _f ->
                     g.values.flatMap { _g ->
                        h.values.flatMap { _h ->
                           i.values.flatMap { _i ->
                              j.values.flatMap { _j ->
                                 k.values.map { _k ->
                                    l(_a, _b, _c, _d, _e, _f, _g, _h, _i, _j, _k)
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
   return ls.exhaustive()
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Exhaustive.Companion.cartesian(
   a: Exhaustive<A>,
   b: Exhaustive<B>,
   c: Exhaustive<C>,
   d: Exhaustive<D>,
   e: Exhaustive<E>,
   f: Exhaustive<F>,
   g: Exhaustive<G>,
   h: Exhaustive<H>,
   i: Exhaustive<I>,
   j: Exhaustive<J>,
   k: Exhaustive<K>,
   l: Exhaustive<L>,
   m: (A, B, C, D, E, F, G, H, I, J, K, L) -> M
): Exhaustive<M> {
   val ms = a.values.flatMap { _a ->
      b.values.flatMap { _b ->
         c.values.flatMap { _c ->
            d.values.flatMap { _d ->
               e.values.flatMap { _e ->
                  f.values.flatMap { _f ->
                     g.values.flatMap { _g ->
                        h.values.flatMap { _h ->
                           i.values.flatMap { _i ->
                              j.values.flatMap { _j ->
                                 k.values.flatMap { _k ->
                                    l.values.map { _l ->
                                       m(_a, _b, _c, _d, _e, _f, _g, _h, _i, _j, _k, _l)
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
   return ms.exhaustive()
}
