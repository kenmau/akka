package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class IoTDevice extends AbstractBehavior<IoTDevice.Command> {

    private final String groupId;
    private final String deviceId;

    private Optional<Double> lastTemperatureReading = Optional.empty();

    public IoTDevice(ActorContext<Command> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;

        context.getLog().info("Device actor {}-{} started", groupId, deviceId);
    }

    public interface Command {}

    // Command
    public static final class ReadTemperature implements Command {
        final long requestId;
        final ActorRef<RespondTemperature> replyTo;

        public ReadTemperature(long requestId, ActorRef<RespondTemperature> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }

    // Message Response
    public static final class RespondTemperature {
        final long requestId;
        final Optional<Double> value;

        public RespondTemperature(long requestId, Optional<Double> value) {
            this.requestId = requestId;
            this.value = value;
        }
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(RecordTemperature.class, this::onRecordTemperature)
                .onMessage(ReadTemperature.class, this::onReadTemperature)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    public static Behavior<Command> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new IoTDevice(context, groupId, deviceId));
    }

    // Write Temperature
    public static final class RecordTemperature implements Command {
        final long requestId;
        final double value;
        final ActorRef<TemperatureRecorded> replyTo;

        public RecordTemperature(long requestId, double value, ActorRef<TemperatureRecorded> replyTo) {
            this.requestId = requestId;
            this.value = value;
            this.replyTo = replyTo;
        }
    }

    public static final class TemperatureRecorded {
        final long requestId;

        public TemperatureRecorded(long requestId) {
            this.requestId = requestId;
        }
    }


    // Message Handlers
    private Behavior<Command> onReadTemperature(ReadTemperature r) {
        getContext().getLog().info("Read temperature reading {} with {}", lastTemperatureReading, r.requestId);
        r.replyTo.tell(new RespondTemperature(r.requestId, lastTemperatureReading));
        return this;
    }

    private Behavior<Command> onRecordTemperature(RecordTemperature r) {
        getContext().getLog().info("Recorded temperature reading {} with {}", r.value, r.requestId);
        lastTemperatureReading = Optional.of(r.value);
        r.replyTo.tell(new TemperatureRecorded(r.requestId));
        return this;
    }

    private IoTDevice onPostStop() {
        getContext().getLog().info("Device actor {}-{} stopped", groupId, deviceId);
        return this;
    }


}
