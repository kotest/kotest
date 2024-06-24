package io.kotest.common

/** returns `true` while preventing the compiler from optimizing it away. */
@KotestInternal
fun nonConstantTrue() = System.currentTimeMillis() > 0L

/** returns `false` while preventing the compiler from optimizing it away. */
@KotestInternal
fun nonConstantFalse() = System.currentTimeMillis() == 0L
