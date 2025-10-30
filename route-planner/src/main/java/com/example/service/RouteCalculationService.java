package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.model.Waypoint;
import java.util.List;

@Service
public class RouteCalculationService {

    @Value("${openrouteservice.api.key:}")
    private String apiKey;

    private final String ORS_URL = "https://api.openrouteservice.org/v2/directions/";
    private final RestTemplate restTemplate = new RestTemplate();

    public RouteData calculateBikeRoute(List<Waypoint> waypoints) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("OpenRouteService API Key nicht konfiguriert! Bitte in application.properties setzen: openrouteservice.api.key=DEIN_API_KEY");
        }

        // Mindestens 2 Wegpunkte benötigt
        if (waypoints == null || waypoints.size() < 2) {
            throw new IllegalArgumentException("Mindestens 2 Wegpunkte benötigt für Routenberechnung");
        }

        // Koordinaten für API vorbereiten ([[lon,lat], [lon,lat], ...])
        double[][] coordinates = waypoints.stream()
                .map(wp -> new double[]{wp.getLongitude(), wp.getLatitude()})
                .toArray(double[][]::new);

        // API-Request erstellen
        ORSRequest request = new ORSRequest(coordinates);

        // Fahrrad-Profil
        String url = ORS_URL + "cycling-regular";

        // ✅ KORRIGIERT: Headers mit korrektem API-Key Format
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<ORSRequest> entity = new HttpEntity<>(request, headers);

        try {
            // API aufrufen
            ResponseEntity<ORSResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    ORSResponse.class
            );

            return extractRouteData(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Fehler bei der Routenberechnung: " + e.getMessage(), e);
        }
    }

    private RouteData extractRouteData(ORSResponse response) {
        RouteData routeData = new RouteData();

        if (response != null && response.features != null && response.features.length > 0) {
            ORSResponse.Feature feature = response.features[0];
            if (feature.properties != null && feature.properties.summary != null) {
                routeData.distance = feature.properties.summary.distance; // Meter
                routeData.duration = feature.properties.summary.duration; // Sekunden
            }

            // Zusätzlich: Geometrie für Karte speichern
            if (feature.geometry != null && feature.geometry.coordinates != null) {
                routeData.geometry = feature.geometry.coordinates;
            }
        }

        return routeData;
    }

    // RouteData Klasse mit erweiterten Funktionen
    public static class RouteData {
        private double distance; // in Metern
        private double duration; // in Sekunden
        private double[][] geometry; // Routen-Geometrie für Karte

        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }

        public double getDuration() { return duration; }
        public void setDuration(double duration) { this.duration = duration; }

        public double[][] getGeometry() { return geometry; }
        public void setGeometry(double[][] geometry) { this.geometry = geometry; }

        // Hilfsmethoden für Anzeige
        public String getFormattedDistance() {
            if (distance < 1000) {
                return String.format("%.0f m", distance);
            } else {
                return String.format("%.1f km", distance / 1000);
            }
        }

        public String getFormattedDuration() {
            int hours = (int) (duration / 3600);
            int minutes = (int) ((duration % 3600) / 60);

            if (hours > 0) {
                return String.format("%d h %d min", hours, minutes);
            } else {
                return String.format("%d min", minutes);
            }
        }

        // Geschwindigkeit berechnen (km/h)
        public double getSpeedKmh() {
            if (duration == 0) return 0;
            double distanceKm = distance / 1000;
            double durationHours = duration / 3600;
            return distanceKm / durationHours;
        }

        public String getFormattedSpeed() {
            return String.format("%.1f km/h", getSpeedKmh());
        }
    }

    // Request-Klasse für OpenRouteService API
    public static class ORSRequest {
        public double[][] coordinates;
        public String format = "geojson";
        public String language = "de";
        public String units = "meters";
        public boolean geometry = true;
        public boolean instructions = true;
        public boolean elevation = true;

        public ORSRequest(double[][] coordinates) {
            this.coordinates = coordinates;
        }
    }

    // Response-Klasse für OpenRouteService API
    public static class ORSResponse {
        public String type;
        public Feature[] features;
        public double[] bbox;
        public Metadata metadata;

        public static class Metadata {
            public String attribution;
            public String service;
            public long timestamp;
            public Query query;
            public Engine engine;
        }

        public static class Query {
            public double[][] coordinates;
            public String profile;
            public String format;
        }

        public static class Engine {
            public String version;
            public String build_date;
            public String graph_date;
        }

        public static class Feature {
            public String type;
            public Properties properties;
            public Geometry geometry;
            public double[] bbox;
        }

        public static class Properties {
            public Segment[] segments;
            public Summary summary;
            public int[] way_points;
        }

        public static class Summary {
            public double distance;
            public double duration;
            public double ascent;
            public double descent;
        }

        public static class Segment {
            public double distance;
            public double duration;
            public Step[] steps;
            public double ascent;
            public double descent;
        }

        public static class Step {
            public double distance;
            public double duration;
            public int type;
            public String instruction;
            public String name;
            public int[] way_points;
        }

        public static class Geometry {
            public String type;
            public double[][] coordinates;
        }
    }
}