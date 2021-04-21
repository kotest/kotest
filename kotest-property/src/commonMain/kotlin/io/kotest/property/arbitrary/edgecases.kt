package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource

/**
 * Given a list of arbs, will return edgecases by randomizing the edgecase inputs and then reducing them.
 */
fun <A> List<Arb<A>>.edgecases(rs: RandomSource) =
   this.shuffled(rs.random).map { it.edgecases(rs) }.reduce { a, b -> a + b }
