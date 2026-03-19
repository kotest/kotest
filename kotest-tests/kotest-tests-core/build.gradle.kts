plugins {
   id("kotest-jvm-conventions")
}

kotlin {
   sourceSets {
      jvmTest {
         dependencies {
            implementation(projects.kotestFramework.kotestFrameworkEngine)
            implementation(projects.kotestAssertions.kotestAssertionsCore)
            // We want to test that JAXBElement is compared properly
            implementation(libs.jaxb.api)
         }
      }
   }
}
