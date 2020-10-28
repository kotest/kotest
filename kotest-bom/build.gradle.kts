plugins {
   `java-platform`
   `maven-publish`
}

version = Ci.publishVersion

val bomProject = project

// Explicitly exclude subprojects that will never be published so that when configuring this project
//   we don't force their configuration and do unecessary work
val excludeFromBom = listOf("kotest-examples", "kotest-tests")
fun projectsFilter(candidateProject: Project) =
   excludeFromBom.all { !candidateProject.name.contains(it) }
      && candidateProject.name != bomProject.name

// Declare that this subproject depends on all subprojects matching the filter
// When this subproject is configured, it will force configuration of all subprojects
// so that we can declare dependencies on them
rootProject.subprojects.filter(::projectsFilter).forEach { bomProject.evaluationDependsOn(it.path) }

dependencies {
   constraints {
      rootProject.subprojects.filter { project ->
         // Only declare dependencies on projects that will have publications
         projectsFilter(project) && project.tasks.findByName("publish")?.enabled == true
      }.forEach { api(project(it.path)) }
   }
}

publishing {
   publications {
      create<MavenPublication>("KotestBom") {
         from(components["javaPlatform"])
      }
   }
}

apply(from = "$rootDir/signing-pom-details.gradle.kts")
