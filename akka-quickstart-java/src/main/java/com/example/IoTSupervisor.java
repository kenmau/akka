package com.example;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class IoTSupervisor extends AbstractBehavior<Void> {

    private IoTSupervisor(ActorContext<Void> context) {
        super(context);
        context.getLog().info("IoT Application started");
    }

    public static Behavior<Void> create() {
        return Behaviors.setup(IoTSupervisor::new);
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private IoTSupervisor onPostStop() {
        getContext().getLog().info("IoT Application stoppoed");
        return this;
    }
}
