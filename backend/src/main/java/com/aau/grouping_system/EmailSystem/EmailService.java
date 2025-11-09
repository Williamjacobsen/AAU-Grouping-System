package com.aau.grouping_system.EmailSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/* How to use:
 * emailService.builder()
 *  .to("email@example.com")
 *  .subject("subject")
 *  .text("some text")
 *  .send();
 */

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${EMAIL}")
	private String email;

	public Builder builder() {
		return new Builder(this);
	}

	private void sendEmail(Builder builder) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(email);
		helper.setTo(builder.to);
		if (builder.cc != null && builder.cc.length > 0) {
			helper.setCc(builder.cc);
		}
		helper.setSubject(builder.subject);
		helper.setText(builder.text, builder.isHtml);

		mailSender.send(message);
	}

	public class Builder {
		private final EmailService service;
		private String[] to;
		private String[] cc;
		private String subject;
		private String text;
		private boolean isHtml = false;

		private Builder(EmailService service) {
			this.service = service;
		}

		public Builder to(String... to) {
			this.to = to;
			return this;
		}

		public Builder cc(String... cc) {
			this.cc = cc;
			return this;
		}

		public Builder subject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder text(String text) {
			this.text = text;
			return this;
		}

		public Builder isHtml(boolean isHtml) {
			this.isHtml = isHtml;
			return this;
		}

		public void send() throws MessagingException {
			if (to == null || to.length == 0) {
				throw new IllegalArgumentException("At least one recipient is required");
			}
			service.sendEmail(this);
		}
	}
}