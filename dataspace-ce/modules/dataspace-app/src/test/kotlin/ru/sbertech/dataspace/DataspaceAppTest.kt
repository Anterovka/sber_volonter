package ru.sbertech.dataspace

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import ru.sbertech.dataspace.configs.ParentCtxConfig
import sbp.com.sbt.dataspace.ModelRelease
import java.nio.file.Paths

@SpringBootTest(
    classes = [ParentCtxConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@EnabledIfSystemProperty(named = "db.postgres.url", matches = ".+")
class DataspaceAppTest {
    @LocalServerPort
    private var localServerPort = 0

    companion object {
        @DynamicPropertySource
        @JvmStatic
        fun configureTestProperties(registry: DynamicPropertyRegistry) {
            val folderPath =
                Paths
                    .get(
                        requireNotNull(
                            DataspaceAppTest::class.java.classLoader
                                ?.getResource("models")
                                ?.toURI(),
                        ),
                    ).toFile()
                    .absolutePath

            registry.apply {
                add("dataspace.app.pdmZipped") { false }
                add("dataspace.app.pathConfigDirectory") { folderPath }
            }
        }

        @JvmStatic
        @BeforeAll
        fun initModels() {
            ModelRelease.testExecution(
                ModelRelease.TestMavenModelReleaseConfiguration.createWithForceMkDirTargetDirectory(
                    "src-model",
                    "models/6",
                ),
            )
        }
    }

    @Test
    fun actuatorModelsTest() {
        val response =
            requireNotNull(
                WebClient
                    .builder()
                    .baseUrl("http://127.0.0.1:$localServerPort")
                    .build()
                    .get()
                    .uri("/actuator/models")
                    .retrieve()
                    .bodyToMono(JsonNode::class.java)
                    .block(),
            )

        (1..4).forEach { modelName ->
            assertThat(response.at("/models/model-$modelName").isMissingNode)
                .`as`("model-$modelName")
                .isFalse
        }
    }

    @Test
    fun schemaIntrospectionTest() {
        val introspectionJson =
            JsonNodeFactory
                .instance
                .objectNode()
                .put(
                    "query",
                    "{ __schema { types { name fields { name } inputFields { name } } } }",
                )

        listOf("1").forEach { modelName ->
            val response =
                requireNotNull(
                    WebClient
                        .builder()
                        .build()
                        .post()
                        .uri("http://127.0.0.1:$localServerPort/models/$modelName/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(introspectionJson), JsonNode::class.java)
                        .retrieve()
                        .bodyToMono(JsonNode::class.java)
                        .block(),
                )

            val typesNode = response.at("/data/__schema/types")
            assertThat(typesNode).isNotNull
            assertThat(typesNode.isArray).isTrue
            val typeSet =
                (typesNode as ArrayNode)
                    .iterator()
                    .asSequence()
                    .map { node -> node.at("/name") }
                    .filterNot { node -> node.isMissingNode }
                    .map { node -> node.asText() }
                    .toSet()

            assertThat(typeSet).contains("Sample", "SampleElement", "SampleDictionary")
            assertThat(typeSet).doesNotContain("SampleApiCall")
        }
    }

    @Test
    fun srcModelTest() {
        val introspectionJson =
            JsonNodeFactory
                .instance
                .objectNode()
                .put(
                    "query",
                    "{ __schema { types { name fields { name } inputFields { name type { kind } } } } }",
                )

        val response =
            requireNotNull(
                WebClient
                    .builder()
                    .build()
                    .post()
                    .uri("http://127.0.0.1:$localServerPort/models/6/graphql")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(introspectionJson), JsonNode::class.java)
                    .retrieve()
                    .bodyToMono(JsonNode::class.java)
                    .block(),
            )

        val createMyClassInputName = "_CreateMyClassInput"
        val createMyChildClassInputName = "_CreateMyChildClassInput"
        val createMyChildWithDefaultIdClassInputName = "_CreateMyChildWithDefaultIdClassInput"
        val updateMyClassInputName = "_UpdateMyClassInput"
        val updateMyChildClassInputName = "_UpdateMyChildClassInput"
        val compareMyClassInputName = "_CompareMyClassInput"

        val map =
            (response.at("/data/__schema/types") as ArrayNode)
                .elements()
                .asSequence()
                .map { typeNode ->
                    val typeName = typeNode.at("/name").asText()

                    if (setOf(
                            createMyClassInputName,
                            createMyChildClassInputName,
                            createMyChildWithDefaultIdClassInputName,
                            updateMyClassInputName,
                            updateMyChildClassInputName,
                            compareMyClassInputName,
                        ).contains(typeName)
                    ) {
                        typeName to
                            (typeNode.at("/inputFields") as ArrayNode)
                                .elements()
                                .asSequence()
                                .map { inputFieldNode ->
                                    inputFieldNode.at("/name").asText() to inputFieldNode.at("/type/kind").asText()
                                }.toMap()
                    } else {
                        null
                    }
                }.filterNotNull()
                .toMap()

        assertThat(map).containsKey(createMyClassInputName)
        assertThat(map[createMyClassInputName])
            .`as`("case $createMyClassInputName")
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "id" to "SCALAR",
                    "code" to "NON_NULL",
                    "myEnum" to "NON_NULL",
                ),
            )

        assertThat(map).containsKey(createMyChildClassInputName)
        assertThat(map[createMyChildClassInputName])
            .`as`("case $createMyChildClassInputName")
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "id" to "NON_NULL",
                    "parent" to "NON_NULL",
                    "myClass" to "NON_NULL",
                    "nyClassRef" to "NON_NULL",
                    "clientRef" to "NON_NULL",
                ),
            )

        assertThat(map).containsKey(createMyChildWithDefaultIdClassInputName)
        assertThat(map[createMyChildWithDefaultIdClassInputName])
            .`as`("case $createMyChildWithDefaultIdClassInputName")
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "parent" to "NON_NULL",
                    "nyClassRef" to "INPUT_OBJECT",
                    "clientRef" to "INPUT_OBJECT",
                ),
            )

        assertThat(map).containsKey(updateMyClassInputName)
        assertThat(map[updateMyClassInputName])
            .`as`("case $updateMyClassInputName")
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "id" to "NON_NULL",
                    "code" to "SCALAR",
                    "myEnum" to "ENUM",
                ),
            )

        assertThat(map).containsKey(updateMyChildClassInputName)
        assertThat(map[updateMyChildClassInputName])
            .`as`("case $updateMyChildClassInputName")
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "id" to "NON_NULL",
                    "parent" to "SCALAR",
                    "myClass" to "SCALAR",
                    "nyClassRef" to "INPUT_OBJECT",
                    "clientRef" to "INPUT_OBJECT",
                ),
            )

        assertThat(map).containsKey(compareMyClassInputName)
        assertThat(map[compareMyClassInputName])
            .`as`("case $compareMyClassInputName")
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "code" to "SCALAR",
                    "myEnum" to "ENUM",
                ),
            )
    }
}
