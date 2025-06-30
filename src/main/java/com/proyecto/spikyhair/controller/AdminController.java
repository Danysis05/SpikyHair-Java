package com.proyecto.spikyhair.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("admin/dashboard")
    public String dashboardAdmin() {
        return "dashboard";
    }
}
