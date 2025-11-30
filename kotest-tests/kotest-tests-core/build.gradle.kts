plugins {
   id("kotest-jvm-conventions")
}

kotlin {

   sourceSets {
      val jvmTest by getting {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            // We want to test that JAXBElement is compared properly
            implementation("javax.xml.bind:jaxb-api:2.3.1")
         }
      }
   }
}
