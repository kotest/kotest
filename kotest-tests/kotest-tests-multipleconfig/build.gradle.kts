plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsShared)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   systemProperty("kotest.framework.classpath.scanning.config.disable", "false")
}
