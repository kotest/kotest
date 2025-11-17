plugins {
   id("kotest-jvm-conventions")
   id("linux-only-tests-conventions")
}

kotlin {
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}

tasks.jvmTest {
   useJUnitPlatform()
   systemProperty("kotest.framework.config.fqn", "com.sksamuel.kotest.config.classname.WibbleConfig")
}

tasks.jvmMaxJdkTest {
   useJUnitPlatform()
   systemProperty("kotest.framework.config.fqn", "com.sksamuel.kotest.config.classname.WibbleConfig")
}
