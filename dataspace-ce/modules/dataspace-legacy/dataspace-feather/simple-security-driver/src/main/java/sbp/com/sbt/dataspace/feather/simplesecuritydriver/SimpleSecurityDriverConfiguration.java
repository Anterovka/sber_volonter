package sbp.com.sbt.dataspace.feather.simplesecuritydriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;

public class SimpleSecurityDriverConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSecurityDriverConfiguration.class);

    @Bean
    public SecurityDriver getSecurityDriver(SimpleSecurityDriverSettings simpleSecurityDriverSettings) {
        LOGGER.info("{}", simpleSecurityDriverSettings);
        return new SimpleSecurityDriver(simpleSecurityDriverSettings.entityRestrictions);
    }
}
