package sbp.com.sbt.dataspace.feather.simplesecuritydriver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import sbp.com.sbt.dataspace.feather.testmodel.ActionSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

@Import(SimpleSecurityDriverConfiguration.class)
class SimpleSecurityDriverTestConfiguration {

    static final String PRODUCT_RESTRICTION = "it.services{cond = it.startAction.code == '1'}.$exists";
    static final String PRODUCT_LIMITED_RESTRICTION = "it.limitedOffer == '1'";
    static final String ACTION_SPECIAL_RESTRICTION = "it.specialOffer == '1'";
    static final String TEST_ENTITY_RESTRICTION = "it.p1 == '1'";

    @Bean
    SimpleSecurityDriverSettings simpleSecurityDriverSettings() {
        return new SimpleSecurityDriverSettings()
                .setEntityRestriction(Product.TYPE0, PRODUCT_RESTRICTION)
                .setEntityRestriction(ProductLimited.TYPE0, PRODUCT_LIMITED_RESTRICTION)
                .setEntityRestriction(ActionSpecial.TYPE0, ACTION_SPECIAL_RESTRICTION)
                .setEntityRestriction(TestEntity.TYPE0, TEST_ENTITY_RESTRICTION);
    }
}
