package sbp.com.sbt.dataspace.feather.modeldescriptionimpl2;

import com.sbt.dataspace.pdm.PdmModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(ModelDescriptionConfiguration.class)
class ModelDescriptionTestConfiguration {

    @Bean
    ModelDescriptionSettings modelDescriptionSettings() {
        return new ModelDescriptionSettings().setPdmModel(PdmModel.readModelFromClassPath());
    }
}
