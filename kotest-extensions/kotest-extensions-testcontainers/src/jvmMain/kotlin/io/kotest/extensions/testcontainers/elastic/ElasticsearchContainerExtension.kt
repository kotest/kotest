package io.kotest.extensions.testcontainers.elastic

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import io.kotest.extensions.testcontainers.AbstractContainerExtension
import io.kotest.extensions.testcontainers.ContainerLifecycleMode
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName

class ElasticsearchContainerExtension(
   container: ElasticsearchContainer,
   mode: ContainerLifecycleMode = ContainerLifecycleMode.Project,
) : AbstractContainerExtension<ElasticsearchContainer>(container, mode) {

   constructor(
      image: DockerImageName,
      mode: ContainerLifecycleMode = ContainerLifecycleMode.Project
   ) : this(ElasticsearchContainer(image), mode)
}

fun ElasticsearchContainer.client(): ElasticsearchClient {
   val restClient = RestClient.builder(HttpHost.create(httpHostAddress)).build()
   val transport = RestClientTransport(restClient, JacksonJsonpMapper())
   return ElasticsearchClient(transport)
}
