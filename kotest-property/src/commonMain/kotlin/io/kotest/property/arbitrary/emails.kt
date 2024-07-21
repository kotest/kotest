package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import kotlin.random.nextInt

fun Arb.Companion.email(
   localPartGen: Gen<String> = emailLocalPart(),
   domainGen: Gen<String> = domain()
) = bind(localPartGen, domainGen) { localPart, domain -> "$localPart@$domain" }

/**
 * https://en.wikipedia.org/wiki/Email_address#Local-part
 *
 * If unquoted, it may use any of these ASCII characters:
 * - uppercase and lowercase Latin letters A to Z and a to z
 * - digits 0 to 9
 * - printable characters !#$%&'*+-/=?^_`{|}~
 * - dot ., provided that it is not the first or last character and provided also that it does not appear consecutively
 * (e.g., John..Doe@example.com is not allowed)
 */
fun Arb.Companion.emailLocalPart(): Arb<String> = arbitrary { rs ->
   val possibleChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + """!#$%&'*+-/=?^_`{|}~.""".toList()
   val firstAndLastChars = possibleChars - '.'
   val size = rs.random.nextInt(1..64)

   val str = if (size <= 2) {
      List(size) { firstAndLastChars.random(rs.random) }.joinToString("")
   } else {
      firstAndLastChars.random(rs.random) +
         List(size - 2) { possibleChars.random(rs.random) }.joinToString("") +
         firstAndLastChars.random(rs.random)
   }
   str.replace("\\.+".toRegex(), ".")
}
