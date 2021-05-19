package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.azstring
import kotlin.random.nextInt

@Deprecated("This function is deprecated and will be replaced. Will be removed in 4.7")
fun Arb.Companion.email(usernameSize: IntRange = 3..10, domainSize: IntRange): Arb<String> {
   val tlds = listOf("com", "net", "gov", "co.uk", "jp", "nl", "ru", "de", "com.br", "it", "pl", "io")
   return arbitrary {
      val tld = tlds.random(it.random)
      val username = it.random.azstring(size = it.random.nextInt(usernameSize))
      val domain = it.random.azstring(size = it.random.nextInt(domainSize))
      val usernamep = if (username.length > 5 && it.random.nextBoolean()) {
         username.take(username.length / 2) + "." + username.drop(username.length / 2)
      } else username
      "$usernamep@$domain.$tld"
   }
}

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
