package fr.demo.state.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    public void sendMail(String message) {
        LOGGER.info("************* Sending message : \"{}\" *************", message);
    }
}
