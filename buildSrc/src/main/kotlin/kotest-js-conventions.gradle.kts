plugins {
   id("kotlin-conventions")
}

kotlin {
   targets {
      js(BOTH) {
         browser()
         nodejs()
      }
   }
}
