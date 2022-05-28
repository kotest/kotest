plugins {
   `kotlin-dsl`
}

repositories {
   gradlePluginPortal()
}

dependencies {
   implementation("com.adarshr:gradle-test-logger-plugin:3.2.0")
   implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
}
