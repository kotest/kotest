plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {

      val jvmMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Framework.api))
            implementation(libs.jdom2)
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }
   }
}
