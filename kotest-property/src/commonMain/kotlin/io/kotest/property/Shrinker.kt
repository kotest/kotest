package io.kotest.property

interface Shrinker<T> {

   /**
    * Given a value, T, this function returns possible "smaller"
    * values to be used as candidates for shrinking. Each new candidate
    * is returned along with another shrinker to be used if that candidate also fails.
    *
    * A smaller value is defined by the Shrinker. For a string it may
    * be considered a "simpler" string (one with less duplication), and/or
    * one with less characters. For an integer it is typically
    * considered a smaller value with a positive sign.
    *
    * Typically only a single value is returned in a shrink step. Shrinkers can
    * return more than one value if there is no single "best path". For example,
    * when shrinking an integer, you probably want to return a single smaller value
    * at a time. For strings, you may wish to return a string that is simpler (YZ -> YY),
    * as well as smaller (YZ -> Y).
    *
    * If the value cannot be shrunk further, or the type
    * does not support meaningful shrinking, then this function should
    * return an empty list.
    */
   fun shrink(value: T): List<PropertyInput<T>>
}
