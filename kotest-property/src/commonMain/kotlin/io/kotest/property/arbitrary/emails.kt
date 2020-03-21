package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.azstring
import kotlin.random.nextInt

fun Arb.Companion.email(usernameSize: IntRange = 3..10, domainSize: IntRange = 3..10): Arb<String> {
   val tlds = listOf("com", "net", "gov", "co.uk", "jp", "nl", "ru", "de", "com.br", "it", "pl", "io")
   return Arb.create {
      val tld = tlds.random(it.random)
      val username = it.random.azstring(size = it.random.nextInt(usernameSize))
      val domain = it.random.azstring(size = it.random.nextInt(domainSize))
      val usernamep = if (username.length > 5 && it.random.nextBoolean()) {
         username.take(username.length / 2) + "." + username.drop(username.length / 2)
      } else username
      "$usernamep@$domain.$tld"
   }
}
