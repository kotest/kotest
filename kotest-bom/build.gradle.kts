plugins {
   `java-platform`
   `maven-publish`
}

version = Ci.publishVersion

val bomProject = project
val excludeFromBom = listOf("kotest-examples", "kotest-tests")
fun projectsFilter(candidateProject: Project) =
   excludeFromBom.all { !candidateProject.name.contains(it) }
      && candidateProject.name != bomProject.name

rootProject.subprojects.filter(::projectsFilter).forEach { bomProject.evaluationDependsOn(it.path) }

dependencies {
   constraints {
      rootProject.subprojects.filter { project ->
         project.tasks.findByName("publish")?.enabled == true &&
            projectsFilter(project)
      }.forEach { api(project(it.path)) }
   }
}

publishing {
   publications {
      create<MavenPublication>("Bom") {
         from(components["javaPlatform"])
      }
   }
}

apply(from = "$rootDir/signing-pom-details.gradle.kts")
