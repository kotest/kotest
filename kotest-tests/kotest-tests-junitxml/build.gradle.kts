plugins {
   kotlin("multiplatform")
}

kotlin {

   targets {
      jvm()
   }

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.JunitXmlExtension))
            implementation(libs.jdom2)
         }
      }
   }
}

apply(from = "../../nopublish.gradle")
