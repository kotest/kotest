plugins {
   id("kotest-jvm-conventions")
}

kotlin {

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
