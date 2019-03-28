package io.kotlintest.matchers.string

import io.kotlintest.*
import java.math.BigInteger
import java.security.MessageDigest

fun String?.shouldHaveDigest(algo: String, digest: String) = this should haveDigest(algo, digest)
fun String?.shouldNotHaveDigest(algo: String, digest: String) = this shouldNot haveDigest(algo, digest)
fun haveDigest(algo: String, digest: String) = neverNullMatcher<String> { value ->
  val alg = MessageDigest.getInstance(algo)
  val result = alg.digest(value.toByteArray())
  val bigInt = BigInteger(1, result)
  val output = bigInt.toString(16)
  Result(
      output == digest,
      "${convertValueToString(value)} should have $algo digest $digest but was $output",
      "${convertValueToString(value)} should not have $algo digest $digest")
}