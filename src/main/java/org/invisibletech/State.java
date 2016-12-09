package org.invisibletech;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class State {
	public static final int LONG_INDEX = 0;
	public static final int LAT_INDEX = 1;

	private static Logger LOGGER = LoggerFactory.getLogger(State.class);

	public final String state;
	public final double[][] border;

	public State() {
		this(null, null);
	}

	public State(String state, double[][] border) {
		this.state = state;
		this.border = border;
	}

	/*
	 * Helper to load State data from given URI. Let Gson and Java NIO do the
	 * work
	 */
	public static List<State> load(InputStream inputStream) {
		Gson gson = new Gson();
		try {
			return new BufferedReader(new InputStreamReader(inputStream)).lines()
					.map(l -> gson.fromJson(l, State.class)).collect(Collectors.toList());
		} catch (Exception cause) {
			LOGGER.error("Unable to load states data for url " + inputStream);
			throw new RuntimeException("Unable to load states data.", cause);
		}
	}

}
