package io.kotest.collections

/**
 * Returns an iterator that will repeat the values from this sequence.
 * If the sequence is empty, then it will return an empty iterator.
 */
fun <A> Sequence<A>.loopedIterator(): Iterator<A> {

   var iter = this@loopedIterator.iterator()
   return object : Iterator<A> {

      override fun hasNext(): Boolean {
         if (iter.hasNext()) return true
         iter = this@loopedIterator.iterator()
         return iter.hasNext()
      }

      override fun next(): A = iter.next()
   }
}
