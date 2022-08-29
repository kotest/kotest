plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
//            implementation(Libs.Kotlin.kotlinScriptRuntime)

            api(projects.kotestCommon) // needs to be API so the domain objects are open
            // needed to compile against Spec
            // but runtime classes must be provided by modules using discovery
            compileOnly(projects.kotestFramework.kotestFrameworkApi)

            // needed to scan the classpath for classes
            implementation(libs.classgraph)
         }
      }
   }
}
