package com.aau.grouping_system.EmailSystem;

import java.util.Properties;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/* Class to demonstrate the use of Gmail Create Email API  */
public class CreateEmail {

	/**
	 * Create a MimeMessage using the parameters provided.
	 *
	 * @param toEmailAddress   email address of the receiver
	 * @param fromEmailAddress email address of the sender, the mailbox account
	 * @param subject          subject of the email
	 * @param bodyText         body text of the email
	 * @return the MimeMessage to be used to send email
	 * @throws MessagingException - if a wrongly formatted address is encountered.
	 */
	public static MimeMessage createEmail(String toEmailAddress,
			String fromEmailAddress,
			String subject,
			String bodyText)
			throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);

		email.setFrom(new InternetAddress(fromEmailAddress));
		email.addRecipient(jakarta.mail.Message.RecipientType.TO,
				new InternetAddress(toEmailAddress));
		email.setSubject(subject);
		email.setText(bodyText);
		return email;
	}
}