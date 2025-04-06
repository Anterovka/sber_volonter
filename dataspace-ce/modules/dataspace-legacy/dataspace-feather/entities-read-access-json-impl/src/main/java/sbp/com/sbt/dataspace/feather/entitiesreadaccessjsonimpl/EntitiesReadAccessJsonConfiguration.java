package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;
import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;

import java.util.Optional;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

public class EntitiesReadAccessJsonConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntitiesReadAccessJsonConfiguration.class);
    public static final String EXPRESSIONS_PROCESSOR_BEAN_NAME = "entities-read-access-json-impl::ExpressionsProcessor";
    public static final String TOOLS_PROFILE = "entities-read-access-json-impl::tools";

    @Bean(name = EXPRESSIONS_PROCESSOR_BEAN_NAME)
    public ExpressionsProcessor expressionsProcessor() {
        return new ExpressionsProcessorImpl();
    }

    @Bean
    public EntitiesReadAccessJson entitiesReadAccessJson(EntitiesReadAccessJsonSettings entitiesReadAccessJsonSettings, ModelDescription modelDescription, @Qualifier(EXPRESSIONS_PROCESSOR_BEAN_NAME) ExpressionsProcessor expressionsProcessor, Optional<SecurityDriver> securityDriverOptional, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        LOGGER.info("{}", entitiesReadAccessJsonSettings);
        checkNotNull(entitiesReadAccessJsonSettings.sqlDialect, "SQL dialect");
        return new EntitiesReadAccessJsonImpl(modelDescription, expressionsProcessor, securityDriverOptional.orElse(null), namedParameterJdbcTemplate, entitiesReadAccessJsonSettings);
    }

    @Bean
    public ModelDescriptionCheck entitiesReadAccessJsonModelDescriptionCheck() {
        return new EntitiesReadAccessJsonModelDescriptionCheck();
    }

    @Bean
    @Profile(TOOLS_PROFILE)
    public EntitiesReadAccessJsonTools entitiesReadAccessJsonTools(EntitiesReadAccessJsonSettings entitiesReadAccessJsonSettings, ModelDescription modelDescription, @Qualifier(EXPRESSIONS_PROCESSOR_BEAN_NAME) ExpressionsProcessor expressionsProcessor, Optional<SecurityDriver> securityDriverOptional) {
        LOGGER.info("{}", entitiesReadAccessJsonSettings);
        checkNotNull(entitiesReadAccessJsonSettings.sqlDialect, "SQL dialect");
        return new EntitiesReadAccessJsonTools(modelDescription, expressionsProcessor, securityDriverOptional.orElse(null), entitiesReadAccessJsonSettings);
    }
}
