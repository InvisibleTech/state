package org.invisibletech;

import static java.util.Optional.ofNullable;
import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import spark.Response;

public class StateResource {
	private static Gson GSON = new Gson();

	public static void main(String[] args) {
		// TODO - Performance improvement - if tests show it is needed. Add step
		// to group clusters of states in convex hull - state can only belong to one. Then use centroid of
		// convex hull and distance to test point to select states to test for
		// containment.
		List<State> states = State.load(StateResource.class.getResourceAsStream("/data/states.json"));

		port(8080);

		exception(IllegalArgumentException.class, (exception, request, response) -> {
			invalidArgument(exception, response);
		});

		exception(NumberFormatException.class, (exception, request, response) -> {
			invalidArgument(exception, response);
		});

		post("/", (request, response) -> {
			response.type("application/json");

			Map<String, Double> requestMap = Arrays.stream(request.body().split("&")).map(arg -> arg.split("="))
					.collect(Collectors.toMap(p -> p[0], p -> Double.parseDouble(p[1])));

			return states.stream()
					.filter(state -> GeoMath.isCoordInState(state, getCoordinateParam(requestMap, "longitude"),
							getCoordinateParam(requestMap, "latitude")))
					.limit(1).map(s -> s.state).collect(Collectors.toList());

		}, GSON::toJson);
	}

	private static void invalidArgument(Exception exception, Response response) {
		response.status(400);
		response.body(GSON.toJson(Arrays.asList(exception.getClass().getSimpleName(), exception)));
	}

	private static double getCoordinateParam(Map<String, Double> requestMap, String paramName) {
		return ofNullable(requestMap.get(paramName))
				.orElseThrow(() -> new IllegalArgumentException(paramName + " not provided."));
	}
}
