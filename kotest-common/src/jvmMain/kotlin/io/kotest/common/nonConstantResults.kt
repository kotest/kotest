package io.kotest.common

/** returns `true` while preventing the compiler from optimizing it away. */
fun nonConstantTrue() = System.currentTimeMillis() > 0L

/** returns `false` while preventing the compiler from optimizing it away. */
fun nonConstantFalse() = System.currentTimeMillis() == 0L
