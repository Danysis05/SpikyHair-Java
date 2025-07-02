package com.proyecto.spikyhair.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.RolRepository;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.ReservasService;
import com.proyecto.spikyhair.service.ServiciosService;
import com.proyecto.spikyhair.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final ServiciosService serviciosService;
    private final ReservasService reservasService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public AdminController(UsuarioService usuarioService, ServiciosService serviciosService,
                           ReservasService reservasService, UsuarioRepository usuarioRepository,
                           RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.serviciosService = serviciosService;
        this.reservasService = reservasService;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @GetMapping("/dashboard")
    public String dashboardAdmin(
            @RequestParam(required = false) String nombreUsuario,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String nombreServicio,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            Model model) {

        Usuario usuario = usuarioService.getUsuarioAutenticado();

        // FILTROS
        List<UsuarioDto> usuarios = usuarioService.filtrarUsuarios(nombreUsuario, rol);
        List<ServiciosDto> servicios = serviciosService.filtrarServicios(nombreServicio, precioMin, precioMax);
        List<ReservasDto> reservas = reservasService.getAll();

        long totalUsuarios = usuarioRepository.count();
        long totalServicios = serviciosService.count();
        long totalReservas = reservasService.count();

        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("servicios", servicios);
        model.addAttribute("reservas", reservas);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalServicios", totalServicios);
        model.addAttribute("totalReservas", totalReservas);
        model.addAttribute("servicio", new ServiciosDto());
        model.addAttribute("roles", rolRepository.findAll());

        // Conserva valores filtrados en el formulario
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("rolSeleccionado", rol);
        model.addAttribute("nombreServicio", nombreServicio);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);

        return "admin/dashboard";
    }

    @GetMapping("/dashboard/pdf")
    public void generarPdfServicios(
            @RequestParam(required = false) String nombreServicio,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            HttpServletResponse response) throws IOException, DocumentException {

        List<ServiciosDto> servicios = serviciosService.filtrarServicios(nombreServicio, precioMin, precioMax);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=servicios.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Reporte de Servicios", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {3, 2, 4, 2});

        Stream.of("Nombre", "Duración", "Descripción", "Precio")
                .forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setPadding(5);
                    table.addCell(cell);
                });

        for (ServiciosDto servicio : servicios) {
            table.addCell(servicio.getNombre());
            table.addCell(servicio.getDuracion() + " min");
            table.addCell(servicio.getDescripcion());
            table.addCell("$" + servicio.getPrecio());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/dashboard/pdf-usuarios")
public void generarPdfUsuarios(
    @RequestParam(required = false) String nombreUsuario,
    @RequestParam(required = false) String rol,
    HttpServletResponse response) throws IOException, DocumentException {

    List<UsuarioDto> usuarios = usuarioService.filtrarUsuarios(nombreUsuario, rol);

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");

    Document document = new Document();
    PdfWriter.getInstance(document, response.getOutputStream());
    document.open();

    Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    Paragraph title = new Paragraph("Reporte de Usuarios", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(20);
    document.add(title);

    PdfPTable table = new PdfPTable(4); // Nombre, Email, Teléfono, Rol
    table.setWidthPercentage(100);
    table.setWidths(new float[] {3, 4, 3, 2});

    Stream.of("Nombre", "Email", "Teléfono", "Rol")
        .forEach(header -> {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        });

    for (UsuarioDto usuario : usuarios) {
        table.addCell(usuario.getNombre());
        table.addCell(usuario.getEmail());
        table.addCell(usuario.getTelefono());
        table.addCell(usuario.getRol().getNombre()); // Asegúrate que RolDto tenga getNombre()
    }

    document.add(table);
    document.close();
}

}
