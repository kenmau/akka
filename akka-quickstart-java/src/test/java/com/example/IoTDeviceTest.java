package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class IoTDeviceTest {

    @ClassRule public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void testReplyWithEmptyReadingIfNoTemperatureIsKnown() {
        TestProbe<IoTDevice.RespondTemperature> probe = testKit.createTestProbe(IoTDevice.RespondTemperature.class);
        ActorRef<IoTDevice.Command> deviceActor = testKit.spawn(IoTDevice.create("group", "device"));

        deviceActor.tell(new IoTDevice.ReadTemperature(42L, probe.getRef()));
        IoTDevice.RespondTemperature response = probe.receiveMessage();

        assertEquals(42L, response.requestId);
        assertEquals(Optional.empty(), response.value);
    }

    @Test
    public void testReplyWithLatestTemperatureReading() {
        TestProbe<IoTDevice.TemperatureRecorded> recordProbe = testKit.createTestProbe(IoTDevice.TemperatureRecorded.class);
        TestProbe<IoTDevice.RespondTemperature> readProbe = testKit.createTestProbe(IoTDevice.RespondTemperature.class);
        ActorRef<IoTDevice.Command> deviceActor = testKit.spawn(IoTDevice.create("group", "device"));

        deviceActor.tell(new IoTDevice.RecordTemperature(1L, 24.0, recordProbe.getRef()));
        IoTDevice.TemperatureRecorded recordResponse = recordProbe.receiveMessage();

        assertEquals(1L, recordResponse.requestId);

        deviceActor.tell(new IoTDevice.ReadTemperature(2L, readProbe.getRef()));
        IoTDevice.RespondTemperature readResponse = readProbe.receiveMessage();

        assertEquals(2L, readResponse.requestId);
        assertEquals(Optional.of(24.0), readResponse.value);

        deviceActor.tell(new IoTDevice.RecordTemperature(3L, 55.0, recordProbe.getRef()));
        assertEquals(3L, recordProbe.receiveMessage().requestId);

        deviceActor.tell(new IoTDevice.ReadTemperature(4L, readProbe.getRef()));
        IoTDevice.RespondTemperature readResponse2 = readProbe.receiveMessage();
        assertEquals(4L, readResponse2.requestId);
        assertEquals(Optional.of(55.0), readResponse2.value);

    }
}
