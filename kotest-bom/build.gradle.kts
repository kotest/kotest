plugins {
   `java-platform`
   id("kotest-publishing-conventions")
}

configurations.api.configure {
   dependencyConstraints.addAllLater(
      kotestBomService.coordinates.map { coords ->
         logger.info("[$path] adding ${coords.size} coords: $coords")
         coords
            .distinct()
            .sorted()
            .map {
               project.dependencies.constraints.create(it)
            }
      }
   )
}

publishing {
   publications {
      create<MavenPublication>("KotestBom") {
         from(components["javaPlatform"])
      }
   }
}
