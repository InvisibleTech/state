package org.invisibletech;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class State {
    private static final int LONG_INDEX = 0;
    private static final int LAT_INDEX = 1;

    public static class Point {
        public final double longitude;
        public final double latitude;

        public Point(final double longitude, final double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        @Override
        public String toString() {
            return "Point [longitude=" + longitude + ", latitude=" + latitude + "]";
        }
    }

    public final String state;
    public final Point[] border;
    public final Point centroid;

    public State() {
        this(null, (Point[]) null);
    }

    public State(final String state, final Point[] border) {
        this.state = state;
        this.border = border;
        this.centroid = GeoMath.computeCentroidOfPolygon(border);
    }

    public State(final String state, final double[][] rawBorder) {
        this(state, Arrays.stream(rawBorder).map(arrPt -> new Point(arrPt[LONG_INDEX], arrPt[LAT_INDEX]))
                .<Point>toArray(size -> new Point[size]));
    }


    public static List<State> load(final InputStream inputStream) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(State.class, new StateDeserializer());

        final Gson gson = gsonBuilder.create();
        try {
            return new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .map(l -> gson.fromJson(l, State.class)).collect(Collectors.toList());
        } catch (final Exception cause) {
            throw new RuntimeException("Unable to load states data.", cause);
        }
    }

    private static class StateDeserializer implements JsonDeserializer<State> {
        @Override
        public State deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                throws JsonParseException {
            final JsonObject jsonState = json.getAsJsonObject();
            return new State(jsonState.get("state").getAsString(),
                    convertToPointList(jsonState.get("border").getAsJsonArray()));
        }

        private Point[] convertToPointList(final JsonArray jsonPoints) {
            return IntStream.range(0, jsonPoints.size()).mapToObj(i -> jsonPoints.get(i).getAsJsonArray())
                    .map(jsonPt -> new Point(jsonPt.get(LONG_INDEX).getAsDouble(), jsonPt.get(LAT_INDEX).getAsDouble()))
                    .toArray(size -> new Point[size]);
        }

    }
}
