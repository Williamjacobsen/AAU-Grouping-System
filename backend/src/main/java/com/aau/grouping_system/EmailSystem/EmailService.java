package com.aau.grouping_system.EmailSystem;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Service;

/*
 * EmailService requires that credentials.json is in the EmailSystem classpath: 
 * 	(backend/src/main/resources/com/aau/grouping_system/EmailSystem/credentials.json).
 * EmailService requires that the tokens folder is in the root directory.
 * credentials.json is downloaded form the Google API platform.
 * The tokens folder is generated once the client has signed in (via. OAuth).
 *
 * EmailService is a class that contains 4 public static methods:
 * - sendEmail(to, subject, body)
 * - sendEmail(to, List<cc>, subject, body)
 * - sendEmail(List<to>, subject, body)
 * - sendEmail(List<to>, List<cc>, subject, body)
 * 
 * There are examples of usage in the 'main' function (at the end of the file).
 */

@Service
public class EmailService {
	private static final String GMAIL = "aau.p3.email@gmail.com";

	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
	private static final String TOKENS_DIR = "tokens";
	private static final String CREDENTIALS_FILE_PATH = "./credentials.json";

	/**
	 * Loads OAuth client secrets.
	 * The obtained refresh token is stored under TOKENS_DIR for reuse.
	 */
	private static Credential getCredentials(final NetHttpTransport httpTransport) throws Exception {
		try (InputStream in = EmailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
			if (in == null)
				throw new FileNotFoundException("Missing " + CREDENTIALS_FILE_PATH);

			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
					.setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIR)))
					.setAccessType("online")
					.build();

			LocalServerReceiver receiver = new LocalServerReceiver.Builder()
					.setPort(8888)
					.setCallbackPath("/Callback")
					.build();

			return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		}
	}

	/**
	 * Builds a Gmail service using the stored credentials.
	 */
	private static Gmail getGmailService() throws Exception {
		NetHttpTransport http = GoogleNetHttpTransport.newTrustedTransport();
		Credential credentials = getCredentials(http);
		return new Gmail.Builder(http, JSON_FACTORY, credentials)
				.setApplicationName("AAU Grouping System")
				.build();
	}

	/**
	 * Creates a simple text/plain MimeMessage.
	 *
	 * @param to      Single primary recipient.
	 * @param ccList  Optional list of CC recipients (can be null or empty).
	 * @param subject Email subject.
	 * @param body    Email body as plain text.
	 */
	private static MimeMessage buildMimeMessage(
			String to,
			List<String> ccList,
			String subject,
			String body) throws Exception {
		Properties props = new Properties();
		Session session = Session.getInstance(props, null);

		MimeMessage email = new MimeMessage(session);
		email.setFrom(new InternetAddress(GMAIL));
		email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));

		if (ccList != null && !ccList.isEmpty()) {
			for (String cc : ccList) {
				if (cc != null && !cc.isBlank()) {
					email.addRecipient(jakarta.mail.Message.RecipientType.CC, new InternetAddress(cc.trim()));
				}
			}
		}

		email.setSubject(subject, "UTF-8");
		email.setText(body, "UTF-8");
		return email;
	}

	/**
	 * Encodes a MimeMessage in base64url form required by the Gmail API.
	 */
	private static String encodeEmail(MimeMessage email) throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		email.writeTo(buffer);
		return Base64.encodeBase64URLSafeString(buffer.toByteArray());
	}

	/**
	 * Sends a raw MimeMessage via the Gmail API and returns the API's Message
	 * response.
	 */
	private static Message sendRaw(Gmail service, MimeMessage email) throws Exception {
		String encoded = encodeEmail(email);
		Message message = new Message().setRaw(encoded);
		// "me" means the authenticated user (the account from OAuth)
		return service.users().messages().send("me", message).execute();
	}

	/**
	 * Send a single email to one recipient (no CC).
	 */
	public static Message sendEmail(String to, String subject, String body) throws Exception {
		Gmail service = getGmailService();
		MimeMessage email = buildMimeMessage(to, null, subject, body);
		return sendRaw(service, email);
	}

	/**
	 * Sends the email to multiple recipients as separate emails.
	 * (Each recipient gets their own email so they cannot see other addresses.)
	 */
	public static List<Message> sendEmail(List<String> toList, String subject, String body)
			throws Exception {
		Gmail service = getGmailService();
		List<Message> results = new ArrayList<>();

		if (toList == null || toList.isEmpty())
			return results;

		for (String to : toList) {
			if (to == null || to.isBlank())
				continue;
			MimeMessage email = buildMimeMessage(to.trim(), null, subject, body);
			results.add(sendRaw(service, email));
		}
		return results;
	}

	/**
	 * Send a single email to one recipient, with optional CC list.
	 */
	public static Message sendEmail(
			String to,
			List<String> ccList,
			String subject,
			String body) throws Exception {
		Gmail service = getGmailService();
		MimeMessage email = buildMimeMessage(to, ccList, subject, body);
		return sendRaw(service, email);
	}

	/**
	 * Send the email to multiple recipients as separate emails, including CC (same
	 * CC list for all).
	 *
	 * Note: Each primary recipient gets their own copy; the CC list is included on
	 * each email.
	 */
	public static List<Message> sendEmail(
			List<String> toList,
			List<String> ccList,
			String subject,
			String body) throws Exception {
		Gmail service = getGmailService();
		List<Message> results = new ArrayList<>();

		if (toList == null || toList.isEmpty())
			return results;

		for (String to : toList) {
			if (to == null || to.isBlank())
				continue;
			MimeMessage email = buildMimeMessage(to.trim(), ccList, subject, body);
			results.add(sendRaw(service, email));
		}
		return results;
	}

	public static void main(String[] args) throws Exception {
		String one = "villi05.v.j@gmail.com";
		List<String> many = List.of("villi05.v.j@gmail.com",
				"pleasedontletmeknow1@example.com");
		List<String> cc = List.of("aau.p3.email@gmail.com", "villi05.v.j@gmail.com");

		// 1) One email (no CC)
		// Message m1 = sendEmail(one, "Test: single", "Hello from AAU Grouping
		// System.");

		// 2) Many emails (no CC)
		// List<Message> m2 = sendEmail(many, "Test: many", "Hello all (sent
		// separately).");

		// 3) One email with CC
		Message m3 = sendEmail(one, cc, "Test: single + CC", "CC list included.");

		// 4) Many emails with CC
		// List<Message> m4 = sendEmail(many, cc, "Test: many + CC", "Each recipient
		// gets a copy with CC.");
	}
}
