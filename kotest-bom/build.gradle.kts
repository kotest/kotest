plugins {
   `java-platform`
   id("kotest-publishing-conventions")
}

val bomProject = project

// Explicitly exclude subprojects that will never be published so that when configuring this project
//   we don't force their configuration and do unnecessary work
val excludeFromBom = listOf("kotest-examples", "kotest-tests")
fun projectsFilter(candidateProject: Project) =
   excludeFromBom.all { !candidateProject.name.contains(it) } &&
      candidateProject.name != bomProject.name

// Declare that this subproject depends on all subprojects matching the filter
// When this subproject is configured, it will force configuration of all subprojects
// so that we can declare dependencies on them
rootProject.subprojects.filter(::projectsFilter).forEach { bomProject.evaluationDependsOn(it.path) }

dependencies {
   constraints {
      rootProject.subprojects
         .filter { project ->
            // Only declare dependencies on projects that will have publications
            projectsFilter(project) && project.tasks.findByName("publish")?.enabled == true
         }
         .forEach { project ->
            project.publishing.publications
               .withType<MavenPublication>()
               // Skip platform artifacts (like *-linuxx64, *-macosx64)
               // It leads to inconsistent bom when publishing from different platforms
               // (e.g. on linux it will include only linuxx64 artifacts and no macosx64)
               // It shouldn't be a problem as usually consumers need to use generic *-native artifact
               // Gradle will choose correct variant by using metadata attributes
               .matching { publication -> publication.artifacts.none { it.extension == "klib" } }
               .all {
                  api("${groupId}:${artifactId}:${version}")
               }
         }
   }
}

publishing {
   publications {
      create<MavenPublication>("KotestBom") {
         from(components["javaPlatform"])
      }
   }
}
