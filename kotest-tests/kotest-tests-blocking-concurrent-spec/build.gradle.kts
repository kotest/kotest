plugins {
   id("kotest-jvm-conventions")
   id("kotest-native-conventions")
   id("com.google.devtools.ksp").version("2.3.9")
   // using a published version -- required so ProjectConfig is discovered on non-JVM targets too
   id("io.kotest").version("6.1.11")
}

kotlin {
   sourceSets {
      commonTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(kotlin("test"))
         }
      }
      jvmTest {
         dependencies {
            implementation(projects.kotestRunner.kotestRunnerJunit5)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   useJUnitPlatform()
}
