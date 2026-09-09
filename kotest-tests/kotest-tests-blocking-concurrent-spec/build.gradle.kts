plugins {
   id("kotlin-conventions")
   id("com.google.devtools.ksp").version("2.3.9")
   // using a published version // TODO do we need this for this module - will see
   id("io.kotest").version("6.1.11")
}

kotlin {
   jvm()
   macosArm64()
   linuxX64()

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
