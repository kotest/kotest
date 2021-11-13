package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Returns an [Arb] where each generated value is a timezone in a three digit format, eg BST.
 *
 * Note, that these timezone codes are not part of the ISO 8601 standard but are nevertheless
 * commonly used.
 *
 * The list of codes used by this Arb is limited to a small selection of relatively well known
 * codes (to this author) and is not meant to be a comprehensive or inclusive list.
 *
 */
fun Arb.Companion.timezoneCodeThree() = Arb.of(
   listOf(
      "UTC",
      "AET", // australian eastern time
      "EST", // eastern standard time
      "EDT", // eastern daylight time
      "PST", // pacific standard time
      "PDT", // pacific daylight time
      "MST", // mountain standard time
      "CDT", // central daylight time
      "CST", // central standard time
      "GMT",
      "BST",
      "CET",
      "CAT", // central african time
      "IDT", //	Israel Daylight Time
      "SGT", // signapore
   )
)
