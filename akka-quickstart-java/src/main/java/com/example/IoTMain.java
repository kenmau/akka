package com.example;

import akka.actor.typed.ActorSystem;

public class IoTMain {
    public static void main (String[] args) {
        ActorSystem.create(IoTSupervisor.create(), "iot-system");
    }

}
