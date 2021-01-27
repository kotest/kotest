package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Generates random domains
 *
 * https://en.wikipedia.org/wiki/Domain_name#Domain_name_syntax
 * https://whogohost.com/host/knowledgebase/308/Valid-Domain-Name-Characters.html
 */
fun Arb.Companion.domain(
   tlds: List<String> = top120TLDs,
   labelArb: Arb<String> = _labelArb
): Arb<String> = arbitrary { rs ->
   val amountOfLabels = rs.random.nextInt(1, 4)

   val labels = labelArb.take(amountOfLabels).joinToString(separator = ".", postfix = ".")

   labels + tlds.random(rs.random)
}

private val _labelArb = arbitrary {
   buildString(63) {
      append(validCharsArb.filter { it != '-' }.single(it))

      append(validCharsArb.take(it.random.nextInt(0, 61), it).joinToString(""))

      append(validCharsArb.filter { it != '-' }.single(it))

      if(length >= 4) {
         if(get(2) == '-' && get(3) == '-') {
            set(3, validCharsArb.filter { it != '-' }.single(it))
         }
      }
   }
}

private val validCharsArb = arbitrary { (('a'..'z') + ('A'..'Z') + ('0'..'9') + '-').random(it.random) }

/**
 * https://www.scoutdns.com/100-most-popular-tlds-by-google-index/
 */
private val top120TLDs = listOf(
   "com",
   "net",
   "org",
   "jp",
   "de",
   "uk",
   "fr",
   "br",
   "it",
   "ru",
   "es",
   "me",
   "gov",
   "pl",
   "ca",
   "au",
   "cn",
   "co",
   "in",
   "nl",
   "edu",
   "info",
   "eu",
   "ch",
   "id",
   "at",
   "kr",
   "cz",
   "mx",
   "be",
   "tv",
   "se",
   "tr",
   "tw",
   "al",
   "ua",
   "ir",
   "vn",
   "cl",
   "sk",
   "ly",
   "cc",
   "to",
   "no",
   "fi",
   "us",
   "pt",
   "dk",
   "ar",
   "hu",
   "tk",
   "gr",
   "il",
   "news",
   "ro",
   "my",
   "biz",
   "ie",
   "za",
   "nz",
   "sg",
   "ee",
   "th",
   "io",
   "xyz",
   "pe",
   "bg",
   "hk",
   "rs",
   "lt",
   "link",
   "ph",
   "club",
   "si",
   "site",
   "mobi",
   "by",
   "cat",
   "wiki",
   "la",
   "ga",
   "xxx",
   "cf",
   "hr",
   "ng",
   "jobs",
   "online",
   "kz",
   "ug",
   "gq",
   "ae",
   "is",
   "lv",
   "pro",
   "fm",
   "tips",
   "ms",
   "sa",
   "app",
   "lat",
   "pk",
   "ws",
   "top",
   "xn--p1ai",
   "pw",
   "ai",
   "kw",
   "ml",
   "su",
   "lu",
   "nu",
   "ec",
   "uy",
   "az",
   "ma",
   "st",
   "asia",
   "im",
   "am",
   "email",
)
