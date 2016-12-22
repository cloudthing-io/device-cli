package io.cloudthing;

import io.cloudthing.communication.*;
import io.cloudthing.sdk.device.utils.CredentialCache;
import org.apache.commons.cli.*;

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
            publish(cmd);
        }
    }

    private static void publish(CommandLine cmd) throws Exception {
        boolean isData = cmd.hasOption("data");

        IMessagePublisher publisher = getPublisher(cmd, isData);
        if (!isData) {
            publisher.setEventId(cmd.getOptionValue("event"));
        }
        publisher.setMessagePayload(cmd.getOptionValue("message"));
        try {
            publisher.sendMessage();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
}
