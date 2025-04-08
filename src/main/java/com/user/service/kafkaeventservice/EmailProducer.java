package com.user.service.kafkaeventservice;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.user.repository.entity.kafkaevents.EmailEvent;


@Service
public class EmailProducer {
	private KafkaTemplate<String, Object> kafkaTemplate;

	public EmailProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendVerificationEmail(String email, String code) {
		EmailEvent event = new EmailEvent(email, code);
		kafkaTemplate.send("email-topic", event);
	}

}
