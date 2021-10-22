object Projects {

   const val Common = ":kotest-common"
   const val Discovery = ":kotest-framework:kotest-framework-discovery"

   object Framework {
      const val concurrency = ":kotest-framework:kotest-framework-concurrency"
      const val mppshared = ":kotest-framework:kotest-framework-multiplatform-plugin-shared"
      const val engine = ":kotest-framework:kotest-framework-engine"
      const val api = ":kotest-framework:kotest-framework-api"
   }

   object Assertions {
      const val Api = ":kotest-assertions:kotest-assertions-api"
      const val Core = ":kotest-assertions:kotest-assertions-core"
      const val Ktor = ":kotest-assertions:kotest-assertions-ktor"
      const val Shared = ":kotest-assertions:kotest-assertions-shared"
   }

   const val Extensions = ":kotest-extensions"
   const val JunitXmlExtension = ":kotest-extensions:kotest-extensions-junitxml"
   const val JunitRunner = ":kotest-runner:kotest-runner-junit5"
   const val Property = ":kotest-property"
   const val HtmlReporter = ":kotest-extensions:kotest-extensions-htmlreporter"

   fun extension(name: String) = ":kotest-extensions:kotest-extensions-$name"
}
