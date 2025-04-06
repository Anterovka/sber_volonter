package sbp.com.sbt.dataspace.feather.stringexpressions;

import org.springframework.context.annotation.Import;
import sbp.com.sbt.dataspace.feather.testcommon.TestCommonConfiguration;

@Import({
        TestCommonConfiguration.class,
        StringExpressionsConfiguration.class
})
class StringExpressionsTestConfiguration {
}
