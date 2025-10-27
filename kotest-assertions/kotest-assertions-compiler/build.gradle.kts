import org.jetbrains.kotlin.gradle.tasks.KotlinTest

plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.kotlin.compiler.embeddable)
            implementation(libs.kotlin.compile.testing)
         }
      }
   }
}

tasks.withType<KotlinTest>().configureEach {
   failOnNoDiscoveredTests = false
}
