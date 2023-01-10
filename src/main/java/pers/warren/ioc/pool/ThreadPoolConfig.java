package pers.warren.ioc.pool;

import pers.warren.ioc.annotation.Bean;
import pers.warren.ioc.annotation.Configuration;
import pers.warren.ioc.condition.ConditionalOnMissingProperties;
import pers.warren.ioc.condition.ConditionalProperty;

@Configuration
public class ThreadPoolConfig {

    @Bean
    @ConditionalOnMissingProperties(@ConditionalProperty("coke.pool.type-name"))
    public CokeThreadPool cokeThreadPool(){
        return CokeThreadPool.threadPool;
    }
}
