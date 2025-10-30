package com.example.controller;

import com.example.model.Route;
import com.example.model.Waypoint;
import com.example.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class RouteController {

    @Autowired
    private RouteRepository routeRepository;

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/routes")
    public String showRoutes(Model model) {
        try {
            List<Route> routes = routeRepository.findAll();
            System.out.println("Anzahl Routen: " + routes.size());
            model.addAttribute("routes", routes);
            return "routes";
        } catch (Exception e) {
            System.out.println("FEHLER in showRoutes: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Fehler beim Laden der Routen: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/route/create")
    public String showCreateRouteForm(Model model) {
        model.addAttribute("route", new Route());
        return "route-create";
    }

    @PostMapping("/route/save")
    public String saveRoute(@ModelAttribute Route route) {
        try {
            System.out.println("Speichere Route: " + route.getName());
            routeRepository.save(route);
            return "redirect:/routes";
        } catch (Exception e) {
            System.out.println("FEHLER in saveRoute: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/";
        }
    }

    @GetMapping("/route/{id}")
    public String showRoute(@PathVariable String id, Model model) {
        try {
            System.out.println("=== ROUTE DETAILS ===");
            System.out.println("Route ID: " + id);

            Optional<Route> route = routeRepository.findById(id);

            if (route.isPresent()) {
                Route r = route.get();
                System.out.println("Route gefunden: " + r.getName());
                System.out.println("Wegpunkte: " + (r.getWaypoints() != null ? r.getWaypoints().size() : 0));

                model.addAttribute("route", r);
                return "route-details";
            } else {
                System.out.println("Route nicht gefunden für ID: " + id);
                model.addAttribute("error", "Route mit ID " + id + " nicht gefunden");
                return "error";
            }
        } catch (Exception e) {
            System.out.println("❌ SCHWERER FEHLER in showRoute: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Interner Server Fehler: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/route/{id}/delete")
    public String deleteRoute(@PathVariable String id) {
        try {
            System.out.println("🗑️ Lösche Route mit ID: " + id);
            routeRepository.deleteById(id);
            System.out.println("Route erfolgreich gelöscht");
            return "redirect:/routes";
        } catch (Exception e) {
            System.out.println("Fehler beim Löschen: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/routes";
        }
    }

    @GetMapping("/create-test-routes")
    public String createTestRoutes() {
        try {
            // Test Route 1
            Route route1 = new Route("Stadttour München", "Eine schöne Tour durch München");
            route1.addWaypoint(new Waypoint("Marienplatz", 48.137154, 11.576124));
            route1.addWaypoint(new Waypoint("Englischer Garten", 48.163105, 11.609756));
            routeRepository.save(route1);

            // Test Route 2
            Route route2 = new Route("Isar Radweg", "Entlang der Isar");
            route2.addWaypoint(new Waypoint("Deutsches Museum", 48.129831, 11.583507));
            route2.addWaypoint(new Waypoint("Flaucher", 48.107079, 11.572692));
            routeRepository.save(route2);

            System.out.println("Test-Routen erstellt");
            return "redirect:/routes";
        } catch (Exception e) {
            System.out.println("Fehler beim Erstellen der Test-Routen: " + e.getMessage());
            return "redirect:/routes";
        }
    }

    @GetMapping("/error")
    public String showError(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("error", message != null ? message : "Ein unbekannter Fehler ist aufgetreten");
        return "error";
    }
}