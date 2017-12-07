package io.cloudthing;

import io.cloudthing.communication.*;
import io.cloudthing.sdk.device.data.DataChunk;
import io.cloudthing.sdk.device.data.GenericDataPayload;
import io.cloudthing.sdk.device.data.ICloudThingMessage;
import io.cloudthing.sdk.device.data.convert.JsonPayloadConverter;
import io.cloudthing.sdk.device.utils.CredentialCache;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {
        Options options = CliOptionsFactory.getOptions();


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("device-cli", options);

            System.exit(1);
            return;
        }
        CredentialCache.getInstance().setCredentials(cmd.getOptionValue("tenant"),
                cmd.getOptionValue("deviceId"), cmd.getOptionValue("secret"));

        if (cmd.hasOption("command")) {
            subscribe();
        } else {

            //publish(cmd);
//
//            List<DataChunk> voltageMessage = toMessage("voltage", 230, 10, 300);
//            send(voltageMessage,cmd);
//
//            List<DataChunk> currentMessage = toMessage("current", 1, 1,300);
//            send(currentMessage,cmd);
//
//            List<DataChunk> demandMessage = toMessage("power", 60, 5, 300);
//            send(demandMessage,cmd);

//            List<DataChunk> humMessage = toMessage("hum", 60, 20, 300);
//            send(humMessage,cmd);

//            List<DataChunk> tempMessage = toMessage("temp", 25, 5, 300);
//            send(tempMessage,cmd);

            List<DataChunk> reedMessage = toMessage("window", 600);
            send(reedMessage,cmd);
        }
    }

    private static void publish(CommandLine cmd)  {
        boolean isData = cmd.hasOption("data");

        IMessagePublisher publisher = null;
        try {
            publisher = getPublisher(cmd, isData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isData) {
            publisher.setEventId(cmd.getOptionValue("event"));
        }
        publisher.setMessagePayload(cmd.getOptionValue("message"));
        try {
            publisher.sendMessage(new ICloudThingMessage() {
                @Override
                public byte[] toBytes() throws Exception {
                    return new byte[0];
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void publish(IMessagePublisher publisher, ICloudThingMessage message) {
        try {
            publisher.sendMessage(message);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static IMessagePublisher getPublisher(CommandLine cmd, boolean data) throws Exception {
        boolean useHttp = cmd.hasOption("http");
        IMessagePublisherBuilder builder = useHttp ? HttpMessagePublisher.builder() : MqttMessagePublisher.builder();
        return builder.setMessageType(data ? IMessagePublisher.MessageType.DATA : IMessagePublisher.MessageType.EVENT)
                .build();
    }

    private static void subscribe() throws Exception {
        MqttCommandSubscriber subscriber = new MqttCommandSubscriber();
        subscriber.subscribe();
        System.out.println("Ctrl+C to terminate");
        for (;;) {
            Thread.sleep(1000);
        }
    }

    private static List<DataChunk> toMessage(String keyName, double rawVal, double tolerance, int timeResolution) {
        List<DataChunk> result = new ArrayList<>();
        long start = 1512345600;
        long end = /*1511743800;*/1512950400;

        for (long i=start; i < end; i=i+timeResolution) {
            Random random = new Random();
            double value = rawVal - tolerance + +random.nextInt((int)tolerance*2) + random.nextDouble();

            result.add(new DataChunk(keyName, value, i));
        }
        return result;
    }


    private static List<DataChunk> toMessage(String keyName, int timeResolution) {
        List<DataChunk> result = new ArrayList<>();
        long start = 1512345600;
        long end = 1512950400;

        for (long i=start; i < end; i=i+timeResolution) {

            Random random = new Random();
            boolean value = random.nextBoolean();

            result.add(new DataChunk(keyName, value, i));
        }
        return result;
    }

    private static void send(List<DataChunk> list, CommandLine cmd) throws Exception {

        JsonPayloadConverter jsonPayloadConverter = new JsonPayloadConverter();
        GenericDataPayload payload = new GenericDataPayload();
        payload.setConverter(jsonPayloadConverter);

        for (DataChunk s : list) {

            //System.out.println(s);
            payload.add(s);
        }

        payload.toString();

        publish(getPublisher(cmd,true), payload);

    }
}
