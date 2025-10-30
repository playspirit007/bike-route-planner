package com.example.service;

import com.example.model.Waypoint;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RouteCalculationService {

    public RouteData calculateBikeRoute(List<Waypoint> waypoints) {
        RouteData routeData = new RouteData();
        
        // Vereinfachte Berechnung für Codespaces (ohne externe API)
        if (waypoints.size() >= 2) {
            // Luftlinie als Schätzung
            double totalDistance = 0;
            for (int i = 1; i < waypoints.size(); i++) {
                totalDistance += calculateDistance(
                    waypoints.get(i-1).getLatitude(), waypoints.get(i-1).getLongitude(),
                    waypoints.get(i).getLatitude(), waypoints.get(i).getLongitude()
                );
            }
            
            routeData.distance = totalDistance * 1000; // km zu Meter
            routeData.duration = (totalDistance / 15) * 3600; // 15 km/h Durchschnitt
        }
        
        return routeData;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Erdradius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public static class RouteData {
        private double distance; // in Metern
        private double duration; // in Sekunden

        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }

        public double getDuration() { return duration; }
        public void setDuration(double duration) { this.duration = duration; }

        public String getFormattedDistance() {
            return String.format("%.1f km", distance / 1000);
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
    }
}