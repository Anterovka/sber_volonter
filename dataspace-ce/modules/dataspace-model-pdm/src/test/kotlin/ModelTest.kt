import com.sbt.dataspace.pdm.PdmModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import ru.sbertech.dataspace.model.dictionaries.hasDictionariesExtension
import ru.sbertech.dataspace.model.dictionaries.isDictionary
import ru.sbertech.dataspace.model.pdm.dictionariesModelBuilder
import ru.sbertech.dataspace.model.pdm.modelBuilder
import ru.sbertech.dataspace.model.type.EntityType
import sbp.com.sbt.dataspace.ModelRelease

class ModelTest {
    @Test
    fun `Simple test`() {
        ModelRelease.testExecution(
            ModelRelease.TestMavenModelReleaseConfiguration.create("", ""),
        )

        val model =
            modelBuilder(PdmModel.readModelFromClassPath())
                .also { assertThat(it.validate()).isEmpty() }
                .build()
        // TODO ссылки
//        val aggregatesModel = aggregatesModelBuilder(PdmModel.readModelFromClassPath()).build(model)
//        assertThat(aggregatesModel).isNotNull

        dictionariesModelBuilder(PdmModel.readModelFromClassPath()).build(model)
        assertThat(model.hasDictionariesExtension).isTrue()
        assertThat((model.type("RootDictionary") as EntityType).isDictionary).isTrue()
        assertThat((model.type("TestEntity") as EntityType).isDictionary).isFalse()
    }
}
