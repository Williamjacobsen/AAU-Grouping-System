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
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SendMessage {

	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
	private static final String TOKENS_DIR = "tokens";
	private static final String CREDENTIALS_FILE_PATH = "./credentials.json"; // on classpath

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
		try (InputStream in = SendMessage.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
			if (in == null)
				throw new FileNotFoundException("Missing " + CREDENTIALS_FILE_PATH);
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
					.setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIR)))
					.setAccessType("offline")
					.build();

			LocalServerReceiver receiver = new LocalServerReceiver.Builder()
					.setPort(8888)
					.setCallbackPath("/Callback")
					.build();

			return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		}
	}

	public static Message sendEmail(String from, String to) throws Exception {
		NetHttpTransport http = GoogleNetHttpTransport.newTrustedTransport();
		Credential cred = getCredentials(http);

		Gmail service = new Gmail.Builder(http, JSON_FACTORY, cred)
				.setApplicationName("AAU Grouping System")
				.build();

		Properties props = new Properties();
		Session session = Session.getInstance(props, null);
		MimeMessage email = new MimeMessage(session);
		email.setFrom(new InternetAddress(from));
		email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject("Test message");
		email.setText("lorem ipsum.");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		email.writeTo(buffer);
		String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());

		Message message = new Message().setRaw(encodedEmail);
		return service.users().messages().send("me", message).execute();
	}

	public static void main(String[] args) throws Exception {
		String from = "aau.p3.email@gmail.com";
		String to = "villi05.v.j@gmail.com";

		Message m = sendEmail(from, to);
		if (m != null) {
			System.out.println("Sent! Message ID: " + m.getId());
		} else {
			System.out.println("Send failed (null response). Check logs.");
		}
	}
}
