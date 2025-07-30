plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(libs.blockhound)
            implementation(libs.kotlinx.coroutines.debug)
         }
      }
   }
}

tasks.withType<Test>().configureEach {
   jvmArgumentProviders.add(CommandLineArgumentProvider {
      val javaLauncher = javaLauncher.orNull
      buildList {
         if (javaLauncher != null && javaLauncher.metadata.languageVersion >= JavaLanguageVersion.of(16)) {
            // https://github.com/reactor/BlockHound/issues/33
            add("-XX:+AllowRedefinitionToAddDeleteMethods")
         }
      }
   })
}
