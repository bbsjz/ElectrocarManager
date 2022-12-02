package com.carmanager.server;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
                String pass = "admin";

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                final String passHash = encoder.encode(pass);
                System.out.println(passHash);

                final boolean matches = encoder.matches(pass, passHash);
                System.out.println(matches);
        }
}
