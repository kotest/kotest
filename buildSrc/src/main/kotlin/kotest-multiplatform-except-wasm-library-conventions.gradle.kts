plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-not-wasm-conventions")
   id("kotest-native-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      all {
         languageSettings {
            optIn("io.kotest.common.KotestInternal")
            optIn("io.kotest.common.ExperimentalKotest")
         }
      }
   }
}
