package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import org.springframework.context.annotation.Import;
import sbp.com.sbt.dataspace.feather.testcommon.TestCommonConfiguration;

@Import({
        TestCommonConfiguration.class,
        ExpressionTypeResolverConfiguration.class
})
class ExpressionTypeResolverTestConfiguration {
}
