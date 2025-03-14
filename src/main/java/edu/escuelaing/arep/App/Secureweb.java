package edu.escuelaing.arep.App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class Secureweb {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Secureweb.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", String.valueOf(getPort())));
                app.run(args);
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 8080 ; //returns default port if heroku-port isn't set (i.e. on localhost)
    }
}
