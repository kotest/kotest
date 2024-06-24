plugins {
   id("kotlin-conventions")
}

kotlin {
   jvm()

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsShared)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   systemProperty("kotest.framework.config.fqn", "com.sksamuel.kotest.tag.ProjectConfig")
}
