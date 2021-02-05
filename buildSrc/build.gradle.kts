import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
   `java-gradle-plugin`
   `kotlin-dsl`
   `kotlin-dsl-precompiled-script-plugins`
}

repositories {
   jcenter()
   gradlePluginPortal()
}

dependencies {
   api(kotlin("gradle-plugin", version = "1.4.21"))
}
