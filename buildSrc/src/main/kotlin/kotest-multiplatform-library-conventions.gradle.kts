plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-native-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      all {
         languageSettings {
            optIn("kotlin.RequiresOptIn")
            optIn("io.kotest.common.KotestInternal")
         }
      }
   }
}
