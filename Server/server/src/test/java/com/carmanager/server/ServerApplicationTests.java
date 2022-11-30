package com.carmanager.server;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@SpringBootTest
@EnableJpaRepositories(basePackages = {"com.carmanager.server.Dao"})
class ServerApplicationTests {
        @Test
        void gsonTest()
        {

//               String a="123456";
//               System.out.println(a.substring(1,a.length()-1));
                try {
                        Date d=new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss aa",Locale.ENGLISH).parse("Nov 30, 2022, 1:42:20 PM");
                        System.out.println(d);
                } catch (ParseException e) {
                        throw new RuntimeException(e);
                }


        }
}
