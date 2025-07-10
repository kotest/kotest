import utils.configureGradleDaemonJvm

plugins {
   id("kotest-base")
   id("kotest-publishing-aggregator")
   java
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

apiValidation {
   ignoredPackages.addAll(
      listOf(
         "io.kotest.framework.multiplatform.embeddablecompiler",
         "io.kotest.framework.multiplatform.gradle",
         "io.kotest.framework.multiplatform.native"
      )
   )
   nonPublicMarkers.addAll(
      listOf(
         "io.kotest.common.KotestInternal",
      )
   )
}

// List all projects which should be included in publishing
dependencies {
//   // Assertions
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsCore)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsArrow)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsArrowFxCoroutines)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsArrowFxCoroutines)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsCompiler)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsJson)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsKonform)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsKotlinxDatetime)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsKtor)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsKtor)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsShared)
//   nmcpAggregation(projects.kotestAssertions.kotestAssertionsYaml)
//
//   // bom
//   nmcpAggregation(projects.kotestBom)
//
//   nmcpAggregation(projects.kotestCommon)
//
//   // Extensions
//   nmcpAggregation(projects.kotestExtensions)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsAllure)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsBlockhound)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsHtmlreporter)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsJunit5)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsJunitxml)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsKoin)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsMockserver)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsNow)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsSpring)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsTestcontainers)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsWiremock)

   // Framework
//   nmcpAggregation(projects.kotestFramework.kotestFrameworkEngine)
//   nmcpAggregation(projects.kotestFramework.kotestFrameworkStandalone)
   nmcpAggregation(projects.kotestFramework.kotestFrameworkSymbolProcessor)

   // Property
//   nmcpAggregation(projects.kotestProperty)
//   nmcpAggregation(projects.kotestProperty.kotestPropertyArrow)
//   nmcpAggregation(projects.kotestProperty.kotestPropertyArrowOptics)
//   nmcpAggregation(projects.kotestProperty.kotestPropertyDatetime)
//   nmcpAggregation(projects.kotestProperty.kotestPropertyLifecycle)
//   nmcpAggregation(projects.kotestProperty.kotestPropertyPermutations)
//
//   // Runner
//   nmcpAggregation(projects.kotestRunner.kotestRunnerJunit5)
//   nmcpAggregation(projects.kotestRunner.kotestRunnerJunit4)
}

configureGradleDaemonJvm(
   project = project,
   updateDaemonJvm = tasks.updateDaemonJvm,
   gradleDaemonJvmVersion = libs.versions.gradleDaemonJvm,
)
