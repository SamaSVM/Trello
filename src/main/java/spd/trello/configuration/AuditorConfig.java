package spd.trello.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import spd.trello.security.SecurityAuditorAware;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditProvider")
public class AuditorConfig {

    @Bean
    public AuditorAware<String> auditProvider(){
        return new SecurityAuditorAware();
    }
}