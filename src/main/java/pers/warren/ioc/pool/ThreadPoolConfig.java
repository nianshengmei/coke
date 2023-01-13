package pers.warren.ioc.pool;

import pers.warren.ioc.annotation.Bean;
import pers.warren.ioc.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public CokeThreadPool cokeThreadPool(){
        return new CokeThreadPool();
    }
}
