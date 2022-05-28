plugins {
   id("kotlin-conventions")
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
            implementation(project(Projects.HtmlReporter))
            implementation(libs.jdom2)
         }
      }
   }
}
