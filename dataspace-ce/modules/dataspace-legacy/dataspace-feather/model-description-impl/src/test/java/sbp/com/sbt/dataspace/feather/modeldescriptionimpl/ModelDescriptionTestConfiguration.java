package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(ModelDescriptionConfiguration.class)
class ModelDescriptionTestConfiguration {

    @Bean
    ModelDescriptionSettings modelDescriptionSettings() {
        return new ModelDescriptionSettings()
                .setModelResourceName("/sbp/com/sbt/dataspace/feather/testmodel/model.xml");
    }
}
