package com.vandeenensupport.httpkafka;
/**
 * provides an http endpoint at /ad-hoc/&lt;topicname> where json POST bodies are put into a Kafka queue
 */

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/ad-hoc")
@Produces(MediaType.APPLICATION_JSON)
public class AdHocResource {
    private KafkaProducer producer;

    public AdHocResource(KafkaProducer producer) {
        this.producer = producer;
    }

    @POST
    @Path("{topic}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response produce(String e, @PathParam("topic") String topic, @QueryParam("keyfield") String keyfield) throws JsonProcessingException {

        if(keyfield == null) throw new BadInput("add ?keyfield=<name of keyfield>");
        GsonBuilder builder = new GsonBuilder();
        try {
            Map o = builder.create().fromJson(e, Map.class);
            Object k  = o.get(keyfield);
            if(k==null) throw new BadInput("Input has no field " + keyfield);
            producer.send(new ProducerRecord(topic, k.toString().getBytes(), e.getBytes()));
        }
        catch(JsonSyntaxException ex){
            throw new BadInput("input is not json");
        }
        return Response.ok().build();
    }

    public static class BadInput extends WebApplicationException {
        public BadInput(String message) {
            super(Response.status(Response.Status.BAD_REQUEST)
                    .entity(message).type(MediaType.TEXT_PLAIN).build());
            System.err.println("BadInput "+message);
        }
    }

}
