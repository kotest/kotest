plugins {
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots/")
   google()
   gradlePluginPortal() // tvOS builds need to be able to fetch a kotlin gradle plugin
}
