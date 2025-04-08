package com.user.service.kafkaeventservice;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.user.repository.entity.kafkaevents.LoginEvent;

@Service
public class LoginEventProducer {
	private KafkaTemplate<String, Object> kafkaTemplate;

	public LoginEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendLoginEvent(LoginEvent event) {
		kafkaTemplate.send("login-events", event);
	}
}