plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   // These tests in this subproject actually test autoscanning, so autoscanning needs to be enabled.
   systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "false")
}
