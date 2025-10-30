package com.example.controller;

import com.example.model.Route;
import com.example.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("routes", routeRepository.findAll());
        return "routes";
    }

    @GetMapping("/route/create")
    public String createRoute(Model model) {
        model.addAttribute("route", new Route());
        return "route-create";
    }

    @PostMapping("/route/save")
    public String saveRoute(@ModelAttribute Route route) {
        routeRepository.save(route);
        return "redirect:/routes";
    }

    @GetMapping("/route/{id}")
    public String routeDetails(@PathVariable String id, Model model) {
        model.addAttribute("route", routeRepository.findById(id).orElse(new Route()));
        return "route-details";
    }

    @PostMapping("/route/{id}/delete")
    public String deleteRoute(@PathVariable String id) {
        routeRepository.deleteById(id);
        return "redirect:/routes";
    }
}