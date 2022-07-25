plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {
      val jvmMain by getting {
         dependencies {
            compileOnly(libs.kotlin.compiler.embeddable)
         }
      }
   }
}

// HACK
// This fixes errors like https://github.com/kotest/kotest/runs/7493505880?check_suite_focus=true#step:5:60
// when using this project as an included build in some environments.
// This is definitely not the right way to fix this, but I'm not sure what the 'right' way is...
configurations {
   runtimeElements {
      isCanBeConsumed = false
   }
}
