package io.kotest.core.names

object UniqueNames {

   /**
    * Returns a unique ident for the given ident and current idents.
    */
   fun unique(ident: String, indents: Set<String>): String? {
      if (!indents.contains(ident)) return null
      var n = 1
      fun next() = "($n) $ident"
      while (indents.contains(next()))
         n++
      return next()
   }
}
