package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;

public class SuperviseMain {
    public static void main(String[] args) {
        // Root Guardian
        ActorRef supervisingActor = ActorSystem.create(SupervisingActor.create(), "supervising-actor");
        supervisingActor.tell("failChild");
    }
}
