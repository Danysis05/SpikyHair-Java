package com.proyecto.spikyhair.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.spikyhair.service.EmailService;
@RestController
@RequestMapping("/correos")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/enviar-masivo")
    public String enviarMasivo() {
        emailService.enviarCorreosMasivos();
        return "Correos enviados a los usuarios de reservas";
    }
}