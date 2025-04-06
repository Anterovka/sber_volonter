package ru.sbertech.dataspace.modelcontext.configs

import com.sbt.dataspace.pdm.PdmModel
import graphql.schema.GraphQLSchema
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import ru.sbertech.dataspace.entitymanager.EntityManagerFactory
import ru.sbertech.dataspace.entitymanager.default.DefaultEntityManagerFactory
import ru.sbertech.dataspace.grammar.expr.ExprGrammar
import ru.sbertech.dataspace.graphql.schema.builder.SchemaBuilder
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.pdm.aggregatesModelBuilder
import ru.sbertech.dataspace.model.pdm.dictionariesModelBuilder
import ru.sbertech.dataspace.model.pdm.modelBuilder
import ru.sbertech.dataspace.model.pdm.statusModelBuilder
import ru.sbertech.dataspace.model.pdm.systemModelBuilder
import ru.sbertech.dataspace.security.config.ChildSecurityConfig
import ru.sbertech.dataspace.security.graphql.GqlSecureConfiguration
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.sql.dialect.postgres.PostgresDialect
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import java.io.File
import java.nio.file.FileSystems
import java.util.zip.ZipFile
import javax.sql.DataSource

@SpringBootConfiguration
@Import(
    PropertyPlaceholderAutoConfiguration::class,
    CustomGraphQlAutoConfiguration::class,
    DataSourceAutoConfiguration::class,
    DataspaceLegacyConfiguration::class,
    ChildSecurityConfig::class,
    GqlSecureConfiguration::class,
)
class ChildCtxConfig {
    @Bean
    fun pdmModel(
        @Value("\${child.model.path:model-path}") modelPath: String,
        @Value("\${child.model.pdm.zip:false}") isPdmZipped: Boolean,
    ): PdmModel {
        val file: File?
        val pdmModel =
            if (isPdmZipped) {
                val fileName = "pdm.zip"
                file = File(modelPath + FileSystems.getDefault().separator + fileName)
                checkFileExist(file, fileName)
                extractZipFileAndLoadModel(file)
            } else {
                val fileName = "pdm.xml"
                file = File(modelPath + FileSystems.getDefault().separator + fileName)
                checkFileExist(file, fileName)
                LOGGER.info("Loading model from $modelPath/$fileName")
                PdmModel.readModelByPath(file)
            }

        return pdmModel
    }

    private fun checkFileExist(
        file: File,
        fileName: String,
    ) {
        if (!file.exists()) LOGGER.error("File $fileName does not exist!")
    }

    @Bean
    fun model(
        pdmModel: PdmModel,
        @Value("\${child.model.path:model-path}") modelPath: String,
    ): Model {
        val model =
            modelBuilder(pdmModel)
                .also {
                    val modelErrors = it.validate()
                    if (!modelErrors.isEmpty()) {
                        throw IllegalStateException(
                            "Errors occurred while building the model $modelPath: [${modelErrors.joinToString(", ")}]",
                        )
                    }
                }.build()

        systemModelBuilder(pdmModel).build(model)
        aggregatesModelBuilder(pdmModel).build(model)
        dictionariesModelBuilder(pdmModel).build(model)
        statusModelBuilder(pdmModel)?.build(model)

        return model
    }

    @Bean
    fun entityManagerFactory(model: Model): EntityManagerFactory = DefaultEntityManagerFactory(model, PostgresDialect())

    @Bean
    @ConditionalOnMissingBean(GraphQLSchema::class)
    fun graphQLSchema(
        dataSource: DataSource,
        model: Model,
        graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
        securityRulesFetcher: SecurityRulesFetcher?,
        @Value("\${dataspace.uow.isManyAggregatesAllowed:false}") isManyAggregatesAllowed: Boolean,
    ): GraphQLSchema =
        SchemaBuilder(
            model,
            DefaultEntityManagerFactory(model, PostgresDialect()),
            dataSource,
            ExprGrammar(),
            graphQLDataFetcherHelper,
            securityRulesFetcher,
            isManyAggregatesAllowed,
        ).build()

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ChildCtxConfig::class.java)
    }

    private fun extractZipFileAndLoadModel(zipFile: File): PdmModel {
        try {
            ZipFile(zipFile).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    zip.getInputStream(entry).use { input ->
                        LOGGER.info("Unzipped ${entry.name} from ${zipFile.absolutePath} to InputStream")
                        if (entry.name.equals("pdm.xml")) {
                            LOGGER.info("Loading model from ${zipFile.absolutePath}")
                            return PdmModel.readModelByInputStream(input)
                        }
                    }
                }
                throw IllegalStateException("pdm.xml not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException("An error occurred while loading the pdm.xml from ${zipFile.absolutePath}: ${e.message}")
        }
    }
}
