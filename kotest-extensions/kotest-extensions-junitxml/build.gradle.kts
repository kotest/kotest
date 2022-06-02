plugins {
   id("kotest-jvm-conventions")
   id("kotest-publishing-conventions")
}

kotlin {
   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Framework.api))
            implementation(libs.jdom2)
         }
      }
   }
}
