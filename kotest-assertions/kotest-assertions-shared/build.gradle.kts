@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   id("kotest-multiplatform-library-conventions")
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

kotlin {

   sourceSets {

      commonMain {
         dependencies {
            // this is api because we want to expose `shouldBe` etc
            api(projects.kotestAssertions.kotestAssertionsApi)

            implementation(kotlin("reflect"))
            implementation(projects.kotestCommon)
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      jvmMain {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
            implementation(libs.diffutils)
            implementation(libs.opentest4j)
         }
      }
   }
}
