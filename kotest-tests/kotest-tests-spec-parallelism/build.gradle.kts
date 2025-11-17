plugins {
   id("kotlin-conventions")
   id("jvm-only-tests-conventions")
}

kotlin {
   jvm()

   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   systemProperty("kotest.framework.config.fqn", "com.sksamuel.kotest.parallelism.ProjectConfig")
}
