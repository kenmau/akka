package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

class StartStopMain extends AbstractBehavior<String> {
    static Behavior<String> create() {
        return Behaviors.setup(StartStopMain::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("start", this::start)
                .build();
    }

    private StartStopMain(ActorContext<String> context) {
        super(context);
    }

    private Behavior<String> start() {
        ActorRef<String> actor1 = getContext().spawn(StartStopActor1.create(), "actor1");
        actor1.tell("stop");
        return this;
    }
}
