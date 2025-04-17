package io.kotest.extensions.testcontainers.kafka//import co.elastic.clients.elasticsearch._types.Refresh
//import co.elastic.clients.elasticsearch.core.IndexRequest
//import co.elastic.clients.elasticsearch.core.SearchRequest
//import co.elastic.clients.elasticsearch.indices.CreateIndexRequest
//import io.kotest.core.extensions.install
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
//import org.testcontainers.utility.DockerImageName
//
//class ElasticTestContainerExtensionTest : FunSpec() {
//   init {
//
//      val container = install(
//         ElasticsearchContainerExtension(
//            DockerImageName.parse("elasticsearch:7.17.6")
//               .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch")
//         )
//      )
//
//      val client = container.client()
//
//      test("elastic happy path") {
//
//         val create = CreateIndexRequest.Builder()
//            .index("foo")
//            .build()
//         client.indices().create(create)
//
//         val req = IndexRequest.Builder<Map<String, String>>()
//         req.index("foo")
//         req.id("123")
//         req.document(mapOf("name" to "billy the butcher"))
//         req.refresh(Refresh.True)
//
//         client.index(req.build())
//
//         val sreq = SearchRequest.Builder().index("foo").build()
//         client.search(sreq, Map::class.java).hits().hits().size shouldBe 1
//      }
//   }
//}
