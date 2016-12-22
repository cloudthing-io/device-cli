package io.cloudthing;

import com.google.common.collect.ImmutableList;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import java.util.List;

/**
 * Created by kleptoman on 21.12.16.
 */
public class CliOptionsFactory {

    public static Options getOptions() {
        Options options = new Options();
        for (Option opt :
                OPTIONS) {
            options.addOption(opt);
        }
        for (OptionGroup group :
                OPTIONS_GROUPS) {
            options.addOptionGroup(group);
        }
        return options;
    }

    private static final List<Option> OPTIONS;
    private static final List<OptionGroup> OPTIONS_GROUPS;
    static {
        ImmutableList.Builder<Option> optionBuilder = ImmutableList.builder();
        Option option = new Option("t", "tenant", true, "Tenant short name");
        option.setRequired(true);
        optionBuilder.add(option);
        option = new Option("dev", "deviceId", true, "Device ID");
        option.setRequired(true);
        optionBuilder.add(option);
        option = new Option("s", "secret", true, "Device's secret token");
        option.setRequired(true);
        optionBuilder.add(option);
        option = new Option("m", "message", true, "Payload of the event or JSON formatted data object");
        option.setRequired(false);
        optionBuilder.add(option);

        OPTIONS = optionBuilder.build();

        ImmutableList.Builder<OptionGroup> optionGroupBuilder = ImmutableList.builder();
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setRequired(true);
        optionGroup.addOption(new Option("e", "event", true, "Sending event with given eventId"));
        optionGroup.addOption(new Option("d", "data", false, "Sending data"));
        optionGroup.addOption(new Option("c", "command", false, "Waiting for commands to receive"));
        optionGroupBuilder.add(optionGroup);

        optionGroup = new OptionGroup();
        optionGroup.setRequired(false);
        optionGroup.addOption(new Option("http", "http", false, "Use HTTP protocol"));
        optionGroup.addOption(new Option("mqtt", "mqtt", false, "Use MQTT protocol"));
        optionGroupBuilder.add(optionGroup);

        OPTIONS_GROUPS = optionGroupBuilder.build();
    }
}
