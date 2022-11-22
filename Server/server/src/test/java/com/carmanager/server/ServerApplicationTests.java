package com.carmanager.server;

import com.carmanager.server.Entity.DateMove;
import com.carmanager.server.Service.DateMoveService;
import com.carmanager.server.Utils.DateUtils;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Date;
import java.util.List;

@SpringBootTest
@EnableJpaRepositories(basePackages = {"com.carmanager.server.Dao"})
class ServerApplicationTests {

	@Autowired
	DateMoveService service;

	@Test
	void contextLoads() {
		Gson gson=new Gson();
		List<DateMove> list=service.getAll();
		String json=gson.toJson(list);
		System.out.println(json);
	}

	@Test
	void textDate()
	{
		Date date=new Date();
		System.out.println(DateUtils.toYearAndMonthAndDate(date));
		System.out.println(DateUtils.toHourAndMinute(date));
	}

}
