package com.user.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.user.repository.entity.kafkaevents.EmailEvent;

@Component
public class EmailConsumer {

	private JavaMailSender mailSender;

	public EmailConsumer(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@KafkaListener(topics = "email-topic", groupId = "email-group", containerFactory = "kafkaListenerContainerFactory")
	public void consumeEmail(EmailEvent event) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(event.getEmail());
		message.setSubject("Verification Code");
		message.setText("Your verification code is: " + event.getVerificationCode());
		mailSender.send(message);
	}
}
