plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")

}

kotlin {

   targets {
      jvm()
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
//            implementation(Libs.Kotlin.kotlinScriptRuntime)

            api(project(Projects.Common))
            // needed to compile against Spec
            // but runtime classes must be provided by modules using discovery
            compileOnly(project(Projects.Framework.api))

            // needed to scan the classpath for classes
            implementation(Libs.Classgraph.classgraph)
         }
      }

      all {
         languageSettings.optIn("kotlin.time.ExperimentalTime")
         languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

apply(from = "../../publish-mpp.gradle.kts")
