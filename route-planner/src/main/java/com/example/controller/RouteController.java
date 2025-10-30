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
    public String showAllRoutes(Model model) {
        List<Route> routes = routeRepository.findAll();
        model.addAttribute("routes", routes);
        return "routes";
    }

    @GetMapping("/route/create")
    public String showCreateRouteForm(Model model) {
        model.addAttribute("route", new Route());
        return "route-create";
    }

    @PostMapping("/route/save")
    public String saveRoute(@ModelAttribute Route route) {
        routeRepository.save(route);
        return "redirect:/routes";
    }

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

    @PostMapping("/route/{id}/delete")
    public String deleteRoute(@PathVariable String id) {
        routeRepository.deleteById(id);
        return "redirect:/routes";
    }

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

        return "redirect:/routes";
    }
}