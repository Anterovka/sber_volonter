package sbp.com.sbt.dataspace.feather.stringexpressions;

import org.springframework.context.annotation.Bean;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;

public class StringExpressionsConfiguration {

    public static final String EXPRESSIONS_PROCESSOR_BEAN_NAME = "string-expressions::ExpressionsProcessor";

    @Bean(name = EXPRESSIONS_PROCESSOR_BEAN_NAME)
    public ExpressionsProcessor expressionsProcessor() {
        return new StringExpressionsProcessor();
    }

    @Bean
    public ModelDescriptionCheck stringExpressionsModelDescriptionCheck() {
        return new StringExpressionsModelDescriptionCheck();
    }
}
