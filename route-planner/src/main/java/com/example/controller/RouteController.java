package com.example.controller;

import com.example.model.Route;
import com.example.model.Waypoint;
import com.example.repository.RouteRepository;
import com.example.service.RouteCalculationService;
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

    @Autowired
    private RouteCalculationService routeCalculationService;

    // Startseite
    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    // Alle Routen anzeigen
    @GetMapping("/routes")
    public String showAllRoutes(Model model) {
        List<Route> routes = routeRepository.findAll();
        model.addAttribute("routes", routes);
        return "routes";
    }

    // Route erstellen Formular anzeigen
    @GetMapping("/route/create")
    public String showCreateRouteForm(Model model) {
        model.addAttribute("route", new Route());
        return "route-create";
    }

    // Route speichern
    @PostMapping("/route/save")
    public String saveRoute(@ModelAttribute Route route) {
        routeRepository.save(route);
        return "redirect:/routes";
    }

    // Route Details anzeigen
    @GetMapping("/route/{id}")
    public String showRouteDetails(@PathVariable String id, Model model) {
        Optional<Route> route = routeRepository.findById(id);
        if (route.isPresent()) {
            model.addAttribute("route", route.get());
            return "route-details";
        } else {
            return "redirect:/routes";
        }
    }

    // Route löschen
    @PostMapping("/route/{id}/delete")
    public String deleteRoute(@PathVariable String id) {
        routeRepository.deleteById(id);
        return "redirect:/routes";
    }

    // Routenberechnung durchführen
    @PostMapping("/calculateRoute")
    public String calculateRoute(@RequestParam String routeId, Model model) {
        Optional<Route> routeOpt = routeRepository.findById(routeId);
        if (routeOpt.isPresent()) {
            Route route = routeOpt.get();
            try {
                RouteCalculationService.RouteData routeData =
                        routeCalculationService.calculateBikeRoute(route.getWaypoints());
                model.addAttribute("route", route);
                model.addAttribute("routeData", routeData);
                return "route-calculation";
            } catch (Exception e) {
                model.addAttribute("error", "Routenberechnung fehlgeschlagen: " + e.getMessage());
                return "error";
            }
        } else {
            model.addAttribute("error", "Route nicht gefunden");
            return "error";
        }
    }

    // Test-Routen erstellen (für Entwicklung)
    @GetMapping("/create-test-routes")
    public String createTestRoutes() {
        // Test Route 1
        Route route1 = new Route();
        route1.setName("Stadttour München");
        route1.setDescription("Eine schöne Tour durch München");
        
        Waypoint wp1 = new Waypoint();
        wp1.setName("Marienplatz");
        wp1.setLatitude(48.137154);
        wp1.setLongitude(11.576124);
        
        Waypoint wp2 = new Waypoint();
        wp2.setName("Englischer Garten");
        wp2.setLatitude(48.163105);
        wp2.setLongitude(11.609756);
        
        route1.setWaypoints(List.of(wp1, wp2));
        routeRepository.save(route1);

        // Test Route 2
        Route route2 = new Route();
        route2.setName("Isar Radweg");
        route2.setDescription("Entlang der Isar");
        
        Waypoint wp3 = new Waypoint();
        wp3.setName("Deutsches Museum");
        wp3.setLatitude(48.129831);
        wp3.setLongitude(11.583507);
        
        Waypoint wp4 = new Waypoint();
        wp4.setName("Flaucher");
        wp4.setLatitude(48.107079);
        wp4.setLongitude(11.572692);
        
        route2.setWaypoints(List.of(wp3, wp4));
        routeRepository.save(route2);

        return "redirect:/routes";
    }
}