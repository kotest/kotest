plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(projects.kotestFramework.kotestFrameworkApi)
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestCommon)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mockk)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   // --add-opens was added in Java 9, so only add the args if the Java launcher version is >= 9
   jvmArgumentProviders.add(CommandLineArgumentProvider {
      javaLauncher.orNull?.let {
         if (it.metadata.languageVersion >= JavaLanguageVersion.of(9)) {
            listOf(
               "--add-opens=java.base/java.util=ALL-UNNAMED",
               "--add-opens=java.base/java.lang=ALL-UNNAMED",
            )
         } else {
            emptyList()
         }
      }
   })

   jvmArgs("-Djava.security.manager=allow")
}
