package com.vandeenensupport.httpkafka;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;
    // among other imports for Dropwizard, you'll need these specific ones:
import java.util.EnumSet;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;


public class KafkaApplication extends Application<KafkaConfiguration> implements Managed {
    public static void main(String[] args) throws Exception {
        new KafkaApplication().run(args);
    }

    private KafkaProducer producer;

    @Override
    public void initialize(Bootstrap<KafkaConfiguration> bootstrap) {
    }

    @Override
    public void run(KafkaConfiguration configuration, Environment environment) {
        producer = new KafkaProducer(configuration.producer);
        environment.jersey().register(new AdHocResource(producer));
        configureCors(environment);
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public void stop() throws Exception {
        producer.close();
    }



    private void configureCors(Environment environment) {
        Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
    }
}
