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
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/*
 * Yo BTW Google is gay as fuck,
 * those stupid ass bitches put a massive fucking delay on every change.
 * For every Gmail API config change it takes so fucking long to test it:
 * "Note: It may take 5 minutes to a few hours for settings to take effect".
 * And how the fuck was i supposed to know that my "Authorized redirect URIs
 * is supposed to be "http://localhost:8888/Callback"?
 * Because it is NOT in the documentation, and it is NOT on Google.
 * So i have to make a guess, wait "a few hours", then guess again, wait "a few hours", and so on.
 * And how the fuck am i supposed to know that it saves the tokens as a hidden folder,
 * That means i literally have to do ls -a at root, then rm -rf tokens every time.
 * And fuck the error details for "Access blocked: AAU-Grouping-System has not completed the Google verification process":
 * The details are literally: "Request details: access_type=offline scope=https://www.googleapis.com/auth/gmail.labels response_type=code redirect_uri=http://localhost:8888/Callback flowName=GeneralOAuthFlow client_id=617862210825-om5d931vu339p9b3h1ppeprrucuh2tj6.apps.googleusercontent.com"
 * Apparently that error message is trying to tell me to add my gmail as a test user (VERY GOOD AND DETAILED ERROR MESSAGE THANK YOU GOOGLE).
 * Now i just need to wait "a few hours" again...
 * Also apparently the engineers at google dont know how to open a file,
 * in no universe does /credentials.json read the file, (it opens a folder "/folder_name")
 * instead ./credentials.json reads the file, (./file_name.extension)
 * their documentation is literally wrong.
 * Also i have to do rm -rf tokens on that hidden folder every time, otherwise i get 403 unAuth error.
 * Nevermind, seems tokens are finally correct.
 * Btw, the code is just copied from their shitty ass documentation.
 * Suck my dick Google.
 */

/* class to demonstrate use of Gmail list labels API */
public class GmailQuickstart {
	/**
	 * Application name.
	 */
	private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
	/**
	 * Global instance of the JSON factory.
	 */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	/**
	 * Directory to store authorization tokens for this application.
	 */
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	/**
	 * Global instance of the scopes required by this quickstart.
	 * If modifying these scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);
	private static final String CREDENTIALS_FILE_PATH = "./credentials.json";

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
			throws IOException {
		// Load client secrets.
		InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline")
				.build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		// returns an authorized Credential object.
		return credential;
	}

	public static void main(String... args) throws IOException, GeneralSecurityException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME)
				.build();

		// Print the labels in the user's account.
		String user = "me";
		ListLabelsResponse listResponse = service.users().labels().list(user).execute();
		List<Label> labels = listResponse.getLabels();
		if (labels.isEmpty()) {
			System.out.println("No labels found.");
		} else {
			System.out.println("Labels:");
			for (Label label : labels) {
				System.out.printf("- %s\n", label.getName());
			}
		}
	}
}