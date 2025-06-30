plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      jvmMain {
         dependencies {
            implementation("com.google.devtools.ksp:symbol-processing-api:2.2.0-2.0.2")
         }
      }
   }
}
