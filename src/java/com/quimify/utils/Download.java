package com.quimify.utils;

// Esta clase procesa las conexiones con otros servidores.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Download {

	private final HttpURLConnection conexion;

	// Constructores:

	public Download(String url) throws IOException {
		conexion = (HttpURLConnection) new URL(url).openConnection();
		conexion.setRequestMethod("GET");
	}

	// Públicos:

	public static String formatForHTTP(String input) {
		return URLEncoder.encode(input, StandardCharsets.UTF_8);
	}

	public String getText() throws IOException {
		BufferedReader download = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

		String line;
		StringBuilder text = new StringBuilder();
		while((line = download.readLine()) != null)
			text.append(line);

		download.close();
		conexion.disconnect();

		return text.toString();
	}

}
