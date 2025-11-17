plugins {
   id("kotest-jvm-conventions")
   id("jvm-only-tests-conventions")
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
