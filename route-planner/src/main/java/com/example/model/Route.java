package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "routes")
public class Route {
    @Id
    private String id;
    private String name;
    private String description;
    private List<Waypoint> waypoints;

    public Route() {}
    public Route(String name, String description, List<Waypoint> waypoints) {
        this.name = name;
        this.description = description;
        this.waypoints = waypoints;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Waypoint> getWaypoints() { return waypoints; }
    public void setWaypoints(List<Waypoint> waypoints) { this.waypoints = waypoints; }
}
EOF

cat > src/main/java/com/example/model/Waypoint.java << 'EOF'
package com.example.model;

public class Waypoint {
    private String name;
    private double latitude;
    private double longitude;

    public Waypoint() {}
    public Waypoint(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}