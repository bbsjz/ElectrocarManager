package com.carmanager.server;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootTest
@EnableJpaRepositories(basePackages = {"com.carmanager.server.Dao"})
class ServerApplicationTests {

}
