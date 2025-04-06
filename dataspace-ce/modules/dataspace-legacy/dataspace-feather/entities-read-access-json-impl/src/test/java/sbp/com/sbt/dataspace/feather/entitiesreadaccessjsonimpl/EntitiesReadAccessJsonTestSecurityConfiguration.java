package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import sbp.com.sbt.dataspace.feather.simplesecuritydriver.SimpleSecurityDriverConfiguration;
import sbp.com.sbt.dataspace.feather.simplesecuritydriver.SimpleSecurityDriverSettings;
import sbp.com.sbt.dataspace.feather.testmodel.Action;
import sbp.com.sbt.dataspace.feather.testmodel.ActionParameterSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.RequestPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

@Profile("sec")
@Import(SimpleSecurityDriverConfiguration.class)
class EntitiesReadAccessJsonTestSecurityConfiguration {

    @Bean
    public SimpleSecurityDriverSettings simpleSecurityDriverSettings() {
        return new SimpleSecurityDriverSettings()
                .setEntityRestriction(Entity.TYPE0, "it.name $like 'entity%'")
                .setEntityRestriction(Product.TYPE0, "it.code $like 'product%' && (it.creatorCode == 1 || it.parameters{type = ActionParameter}.request{type = RequestPlus}.$count == 1)")
                .setEntityRestriction(ProductLimited.TYPE0, "it.limitedOffer $like 'limitedOffer%'")
                .setEntityRestriction(ActionParameterSpecial.TYPE0, "it.specialOffer $like 'specialOffer%'")
                .setEntityRestriction(RequestPlus.TYPE0, "it.initiator.lastName == 'Ivanov'")
                .setEntityRestriction(Service.TYPE0, "it.startAction.algorithmCode == 1")
                .setEntityRestriction(Action.TYPE0, "it.algorithmCode == 1");
    }
}
