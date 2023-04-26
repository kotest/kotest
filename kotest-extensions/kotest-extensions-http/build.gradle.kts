@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {
   sourceSets {

      commonMain {
         dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.ktor.client.apache)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.mockserver.netty)
            implementation(libs.kotest.extensions.mockserver)
         }
      }

      if (!project.hasProperty(Ci.JVM_ONLY)) {
         jsMain {
            dependencies {
               implementation(libs.ktor.client.js)
            }
         }
      }
   }
}
