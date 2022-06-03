plugins {
   id("kotest-multiplatform-library-conventions")
}

kotlin {
   sourceSets {

      val commonMain by getting {
         dependencies {
            // this is api because we want to expose `shouldBe` etc
            api(project(Projects.Assertions.Shared))

            implementation(kotlin("reflect"))
            implementation(project(Projects.Common))
            implementation(project(Projects.Assertions.Api))
            implementation(libs.kotlinx.coroutines.core)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(libs.kotlinx.coroutines.jdk8)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Property))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.opentest4j)
            implementation(libs.apache.commons.lang)
            implementation(libs.mockk)
         }
      }
   }
}
