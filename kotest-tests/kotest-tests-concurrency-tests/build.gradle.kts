plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}
