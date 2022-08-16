repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      mavenContent { snapshotsOnly() }
   }
   google()
   gradlePluginPortal() // tvOS builds need to be able to fetch a kotlin gradle plugin
}
