package com.example;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.ActorRef;

public class StartStopExample {
    public static void main(String[] args) {
        ActorRef<String> system = ActorSystem.create(StartStopMain.create(), "start-stop-system");
        system.tell("start");
    }
}
