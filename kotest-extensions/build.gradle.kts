plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      jvmMain {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestCommon)
         }
      }

      jvmTest {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mockk)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   jvmArgumentProviders.add(CommandLineArgumentProvider {
      val javaLauncher = javaLauncher.orNull
      buildList {
         if (javaLauncher != null) {
            // --add-opens is only available in Java 9+
            add("--add-opens=java.base/java.util=ALL-UNNAMED")
            add("--add-opens=java.base/java.lang=ALL-UNNAMED")
            // have to manually allow security.manager in Java 9+ https://github.com/kotest/kotest/issues/3841
            add("-Djava.security.manager=allow")
         }
      }
   })
}
