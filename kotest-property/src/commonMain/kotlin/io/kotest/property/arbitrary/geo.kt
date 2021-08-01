package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.math.PI
import kotlin.math.acos


@Deprecated("Use geoLocation()", replaceWith = ReplaceWith("geoLocation()"))
fun Arb.Companion.latlong(): Arb<Pair<Double, Double>> =
   Arb.geoLocation().map { loc -> Pair(loc.latitudeDeg, loc.longitudeDeg) }

data class GeoLocation(val latitude: Double, val longitude: Double) {
   @Deprecated("Use latitude", replaceWith = ReplaceWith("latitude"))
   val first = latitude
   @Deprecated("Use longitude", replaceWith = ReplaceWith("longitude"))
   val second = longitude

   val latitudeRad: Double = latitude
   val longitudeRad: Double = longitude
   val latitudeDeg: Double = 180.0 * latitude / PI
   val longitudeDeg: Double = 180.0 * longitude / PI
}

fun Arb.Companion.geoLocation(): Arb<GeoLocation> = arbitrary(
   listOf(
      GeoLocation(-PI / 2, 0.0), // south pole
      GeoLocation(PI / 2, 0.0) // north pole
   )
) {
   val random = it.random
   val lat = acos(random.nextDouble(-1.0, 1.0)) - PI / 2
   val lon = random.nextDouble(-PI, PI)
   GeoLocation(latitude = lat, longitude = lon)
}
