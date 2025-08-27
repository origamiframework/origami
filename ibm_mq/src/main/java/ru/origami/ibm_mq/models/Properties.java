package ru.origami.ibm_mq.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderMethodName = "Builder", setterPrefix = "set")
public class Properties {

    private String host;

    private Integer port;

    private String queueManagerName;

    private String channel;

    private String username;

    private String password;

    @Builder.Default
    private boolean clientTransport = true;

    @Override
    public String toString() {
        return String.format("%s : %d. QueueManager: %s, Channel: %s, ClientTransport: %s",
                host, port, queueManagerName, channel, clientTransport);
    }
}
