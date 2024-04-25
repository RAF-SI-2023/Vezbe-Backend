package rs.edu.raf.banka1.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// Potrebno je dodati @EnableCaching anotaciju u jednu od konfiguracionih klasa (@Configuration) kako bi
// Redis kesiranje funkcionisalo.
@Configuration
@EnableCaching
public class CacheConfig {

}
