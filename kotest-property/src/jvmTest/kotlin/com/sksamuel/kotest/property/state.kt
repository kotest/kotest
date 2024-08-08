package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.state.Action
import io.kotest.property.state.checkState

/**
 * A cache that tracks how often a user's profile has been viewed.
 *
 * Each time [add] is invoked, that user is added to the list of viewers, and when
 * [remove] is invoked, that user is removed.
 * [get] returns the viewers for a user.
 */
class ViewerCache {

   val map = mutableMapOf<Long, MutableList<Long>>()

   override fun toString(): String {
      return map.toString()
   }

   fun add(viewee: Long, viewer: Long) {
      val viewers = map.getOrPut(viewee) { mutableListOf() }
      viewers.add(viewer)
   }

   fun remove(viewee: Long, viewer: Long) {
      val viewers = map.getOrPut(viewee) { mutableListOf() }
      viewers.remove(viewer)
   }

   fun get(viewee: Long): List<Long> {
      return map.getOrElse(viewee) { emptyList() }
   }
}

class ViewerCacheTest : FunSpec() {
   init {

      val add = Action<ViewerCache> { state, rs ->
         val userId = Arb.long(1L..100L).next(rs)
         val viewerId = Arb.long(1L..100L).next(rs)
         state.add(userId, viewerId)
         state.get(userId).contains(viewerId) shouldBe true
      }

      val remove = Action<ViewerCache> { state, rs ->
         val userId = Arb.long(1L..100L).next(rs)
         val viewerId = Arb.long(1L..100L).next(rs)
         if (state.get(userId).contains(viewerId)) {
            state.remove(userId, viewerId)
            state.get(userId).contains(viewerId) shouldBe false
         }
      }

      test("viewer cache state") {
         checkState(ViewerCache(), add, remove)
      }
   }
}
