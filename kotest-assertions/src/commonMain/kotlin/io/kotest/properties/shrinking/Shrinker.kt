package io.kotest.properties.shrinking

interface Shrinker<T> {

  /**
   * Given a value, T, this function returns possible "smaller"
   * values to be used as candidates for shrinking.
   *
   * A smaller value is defined by the Shrinker. For a string it may
   * be considered a "simpler" string (one with less duplication), and/or
   * one with less characters. For an integer it is typically
   * considered a smaller value with a positive sign.
   *
   * If the value cannot be shrunk further, or the type
   * does not support meaningful shrinking, then this function should
   * return an empty list.
   */
  fun shrink(failure: T): List<T>
}

