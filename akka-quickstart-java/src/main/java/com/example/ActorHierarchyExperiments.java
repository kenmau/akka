package com.example;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.ActorRef;

public class ActorHierarchyExperiments {
    public static void main(String[] args) {
        ActorRef<String> testSystem = ActorSystem.create(Main.create(), "testSystem");
        testSystem.tell("start");
    }
}
