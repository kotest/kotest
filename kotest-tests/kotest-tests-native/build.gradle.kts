plugins {
   id("kotest-native-conventions")
}

kotlin {
   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}
