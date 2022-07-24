plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.Assertions.Api))

            implementation(kotlin("reflect"))
            implementation(project(Projects.Common))
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
            implementation(libs.diffutils)
            implementation(libs.opentest4j)
         }
      }
   }
}
