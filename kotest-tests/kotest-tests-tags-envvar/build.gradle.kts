plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   environment("KOTEST_TAGS", "B")
}
