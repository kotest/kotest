package io.kotest.fp

data class Tuple2<out A, out B>(val a: A, val b: B) {
   override fun toString(): String {
      return "($a, $b)"
   }
}

data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C) {
   override fun toString(): String {
      return "($a, $b, $c)"
   }
}

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) {
   override fun toString(): String {
      return "($a, $b, $c, $d)"
   }
}

data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) {
   override fun toString(): String {
      return "($a, $b, $c, $d, $e)"
   }
}

data class Tuple6<out A, out B, out C, out D, out E, out F>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F
) {
   override fun toString(): String {
      return "($a, $b, $c, $d, $e, $f)"
   }
}
