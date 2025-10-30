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

    // ... bestehende Methoden ...

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
}