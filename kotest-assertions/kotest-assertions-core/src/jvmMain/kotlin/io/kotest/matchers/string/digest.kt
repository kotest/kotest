package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.*
import java.math.BigInteger
import java.security.MessageDigest

fun String?.shouldHaveDigest(algo: String, digest: String) = this should haveDigest(algo, digest)
fun String?.shouldNotHaveDigest(algo: String, digest: String) = this shouldNot haveDigest(algo, digest)
fun haveDigest(algo: String, digest: String) : Matcher<String?> = neverNullMatcher { value ->
  val alg = MessageDigest.getInstance(algo)
  val result = alg.digest(value.toByteArray())
  val bigInt = BigInteger(1, result)
  val output = bigInt.toString(16)
  MatcherResult(
    output == digest,
    "${value.show().value} should have $algo digest ${digest.show().value} but was ${output.show().value}",
    "${value.show().value} should not have $algo digest ${digest.show().value}")
}
