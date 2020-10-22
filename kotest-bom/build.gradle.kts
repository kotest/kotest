plugins {
   `java-platform`
   `maven-publish`
}

version = Ci.publishVersion

val bomProjectName = project.name

rootProject.subprojects.filter { project ->
   project.name != bomProjectName
}.forEach {
   evaluationDependsOn(it.path)
}


dependencies {
   constraints {
      rootProject.subprojects.filter { project ->
         project.name != bomProjectName &&
            project.plugins.hasPlugin(MavenPublishPlugin::class)
      }.forEach {

         println(it)
         it.publishing.publications.filterIsInstance<MavenPublication>()
            .forEach { publication ->
               println(publication.artifactId)
            }
      }
   }
}

publishing {
   publications {
      create<MavenPublication>("kotest-bom") {
         from(components["javaPlatform"])
      }
   }
}

val listPublishingTasks by tasks.registering {
   doLast {
      println("List Publishing artifacts")
      rootProject.subprojects.filter { project ->
         project.tasks.findByName("publish")?.enabled == true &&
            project.name != bomProjectName
      }.forEach { project ->
         println(project)
         project.publishing.publications
            .filterIsInstance<MavenPublication>()
            .forEach {
               println(it.artifactId)

            }
      }
   }
}
