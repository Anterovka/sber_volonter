package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import org.springframework.context.annotation.Bean;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

/**
 * Configuration of the expression type resolver
 */
public class ExpressionTypeResolverConfiguration {

    @Bean
    public ExpressionTypeResolver expressionTypeResolver(ModelDescription modelDescription) {
        return new ExpressionTypeResolver(modelDescription);
    }
}
