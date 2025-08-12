package by.antohakon.vetclinitclients.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public static void sendResponse(){

    }

}
