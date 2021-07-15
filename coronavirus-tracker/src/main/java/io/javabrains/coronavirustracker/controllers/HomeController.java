package io.javabrains.coronavirustracker.controllers;

import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.services.CoronavirusDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class HomeController {

    private final CoronavirusDataService coronavirusDataService;

    public HomeController(CoronavirusDataService coronavirusDataService) {
        this.coronavirusDataService = coronavirusDataService;
    }

    @GetMapping("/")
    public String home(Model model) {
        ArrayList<LocationStats> allStats = coronavirusDataService.getAllStats();
        long totalReportedCases = allStats.stream()
                .mapToLong(locationStats -> locationStats.getLatestTotalCases()).sum();
        long totalNewCases = allStats.stream()
                .mapToLong(locationStats -> locationStats.getDiffFromPrevDay()).sum();

        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalNewCases", totalNewCases);
        model.addAttribute("totalReportedCases", totalReportedCases);
        return "home";
    }
}
