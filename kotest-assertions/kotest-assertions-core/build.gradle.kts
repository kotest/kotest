plugins {
   id("kotest-jvm-conventions")
   id("kotest-js-conventions")
   id("kotest-wasi-conventions")
   id("kotest-native-conventions")
   id("kotest-android-native-conventions")
   id("kotest-watchos-device-conventions")
   id("kotest-publishing-conventions")
}

// kotest-assertions-core is an aggregator artifact. The matcher implementations live in
// kotest-assertions-core-logic, the dot-notation assertions in kotest-assertions-core-standard,
// and the infix assertions in kotest-assertions-core-infix. This module re-exports them so that
// depending on kotest-assertions-core continues to provide the full assertion API, exactly as
// before the split.
//
// Tests stay in kotest-assertions-core-logic: its test source set depends on
// kotest-framework-engine (for the spec DSL), which transitively provides the full assertion API
// (all calling conventions), so tests can use any style while logic's *main* source depends only
// on kotest-assertions-shared.
kotlin {

   sourceSets {

      commonMain {
         dependencies {
            api(projects.kotestAssertions.kotestAssertionsCoreLogic)
            api(projects.kotestAssertions.kotestAssertionsCoreStandard)
            api(projects.kotestAssertions.kotestAssertionsCoreInfix)
         }
      }
   }
}
