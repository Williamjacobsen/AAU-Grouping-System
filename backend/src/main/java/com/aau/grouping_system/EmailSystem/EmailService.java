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
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class EmailService {

	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
	private static final String TOKENS_DIR = "tokens";
	private static final String CREDENTIALS_CLASSPATH = "./credentials.json"; // put in src/main/resources
	private static final String APP_NAME = "AAU Grouping System";
	private static final int OAUTH_PORT = 8888;
	private static final String OAUTH_CALLBACK = "/Callback";

	private EmailService() {
	}

	/** Send a simple plain-text email. Returns the Gmail message ID. */
	public static String sendText(String from, String to, String subject, String body) throws Exception {
		NetHttpTransport http = GoogleNetHttpTransport.newTrustedTransport();
		Credential cred = authorize(http);

		Gmail gmail = new Gmail.Builder(http, JSON_FACTORY, cred)
				.setApplicationName(APP_NAME)
				.build();

		// Build minimal MimeMessage
		Session session = Session.getInstance(new Properties());
		MimeMessage email = new MimeMessage(session);
		email.setFrom(new InternetAddress(from));
		email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject, "UTF-8");
		email.setText(body == null ? "" : body, "UTF-8");

		// Encode & send
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		email.writeTo(buffer);
		String encodedEmail = Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.toByteArray());

		Message message = new Message().setRaw(encodedEmail);
		Message result = gmail.users().messages().send("me", message).execute();
		return result.getId();
	}

	// --- helpers ---

	private static Credential authorize(NetHttpTransport http) throws Exception {
		try (InputStream in = EmailService.class.getResourceAsStream(CREDENTIALS_CLASSPATH)) {
			if (in == null)
				throw new FileNotFoundException("Missing " + CREDENTIALS_CLASSPATH + " on classpath");
			GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(http, JSON_FACTORY, secrets, SCOPES)
					.setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIR)))
					.setAccessType("offline")
					.build();

			LocalServerReceiver receiver = new LocalServerReceiver.Builder()
					.setPort(OAUTH_PORT)
					.setCallbackPath(OAUTH_CALLBACK)
					.build();

			return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		}
	}

	public static void main(String[] args) throws Exception {
		String id = sendText(
				"aau.p3.email@gmail.com",
				"villi05.v.j@gmail.com",
				"Test message",
				"lorem ipsum.");
		System.out.println("Sent! id = " + id);
	}
}
