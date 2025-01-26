package io.kotest.core.descriptors

data class DescriptorId(
   val value: String,
) {

   /**
    * Treats the lhs and rhs both as wildcard regex one by one and check if it matches the other
    */
   fun wildCardMatch(id: DescriptorId): Boolean {
      val thisRegex = with(this.value) {
         ("\\Q$this\\E").replace("*", "\\E.*\\Q").toRegex()
      }
      val thatRegex = with(id.value) {
         ("\\Q$this\\E").replace("*", "\\E.*\\Q").toRegex()
      }
      return (thisRegex.matches(id.value) || thatRegex.matches(this.value))
   }
}
