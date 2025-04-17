plugins {
   `java-platform`
   id("kotest-publishing-conventions")
}

configurations.api.configure {
   // lazily add the coords from all subprojects to the kotest-bom
   dependencyConstraints.addAllLater(
      kotestBomService.coordinates.map { coords ->
         logger.info("[$path] adding ${coords.size} coords to kotest-bom: $coords")
         coords
            .distinct()
            .sorted()
            .map { coord ->
               project.dependencies.constraints.create(coord)
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
