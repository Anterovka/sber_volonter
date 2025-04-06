package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import sbp.com.sbt.dataspace.feather.simplesecuritydriver.SimpleSecurityDriverConfiguration;
import sbp.com.sbt.dataspace.feather.simplesecuritydriver.SimpleSecurityDriverSettings;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;

@Profile("sec2")
@Import(SimpleSecurityDriverConfiguration.class)
class EntitiesReadAccessJsonTestSecurity2Configuration {

    @Bean
    public SimpleSecurityDriverSettings simpleSecurityDriverSettings() {
        return new SimpleSecurityDriverSettings()
                .setEntityRestriction(Entity.TYPE0, "it.parameters.$exists");
    }
}
