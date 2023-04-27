plugins {
   id("kotest-native-conventions")
}

kotlin {
   sourceSets {
      commonTest {
         dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}
