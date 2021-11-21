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

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            implementation(kotlin("reflect"))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.Framework.engine))
            implementation(project(Projects.Assertions.Core))
            implementation(project(Projects.JunitRunner))
            implementation(project(Projects.JunitXmlExtension))
            implementation(Libs.Jdom.jdom2)
         }
      }
   }
}

apply(from = "../../nopublish.gradle")
