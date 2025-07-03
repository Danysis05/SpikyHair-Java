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
            @RequestParam(required = false) String estado,
            Model model) {

        Usuario usuario = usuarioService.getUsuarioAutenticado();
            if (nombreUsuario != null && nombreUsuario.trim().isEmpty()) nombreUsuario = null;
    if (rol != null && rol.trim().isEmpty()) rol = null;
    if (nombreServicio != null && nombreServicio.trim().isEmpty()) nombreServicio = null;
    if (estado != null && estado.trim().isEmpty()) estado = null;

        // FILTROS
        List<UsuarioDto> usuarios = usuarioService.filtrarUsuarios(nombreUsuario, rol);
        List<ServiciosDto> servicios = serviciosService.filtrarServicios(nombreServicio, precioMin, precioMax);
        List<ReservasDto> reservas = reservasService.filtrarReservas(nombreUsuario, nombreServicio, estado);

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
        model.addAttribute("estado", estado);

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

        Stream.of("Nombre", "Duración", "Descripción", "Precio").forEach(header -> {
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

        Stream.of("Nombre", "Email", "Teléfono", "Rol").forEach(header -> {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        });

        for (UsuarioDto usuario : usuarios) {
            table.addCell(usuario.getNombre());
            table.addCell(usuario.getEmail());
            table.addCell(usuario.getTelefono());
            table.addCell(usuario.getRol().getNombre());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/dashboard/pdf-reservas")
public void generarPdfReservas(
        @RequestParam(required = false) String nombreUsuario,
        @RequestParam(required = false) String nombreServicio,
        @RequestParam(required = false) String estado,
        HttpServletResponse response) throws IOException, DocumentException {

    // Limpiar filtros vacíos
    if (nombreUsuario != null && nombreUsuario.trim().isEmpty()) nombreUsuario = null;
    if (nombreServicio != null && nombreServicio.trim().isEmpty()) nombreServicio = null;
    if (estado != null && estado.trim().isEmpty()) estado = null;

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=reservas.pdf");

    try {
        List<ReservasDto> reservas = reservasService.filtrarReservas(nombreUsuario, nombreServicio, estado);

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Reporte de Reservas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        if (reservas == null || reservas.isEmpty()) {
            document.add(new Paragraph("No se encontraron reservas para los filtros seleccionados."));
        } else {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 3, 2, 2});

            Stream.of("Usuario", "Servicio", "Fecha", "Estado").forEach(header -> {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            });

            for (ReservasDto reserva : reservas) {
                table.addCell(reserva.getUsuario() != null ? reserva.getUsuario().getNombre() : "N/A");
                table.addCell(reserva.getServicio() != null ? reserva.getServicio().getNombre() : "N/A");
                table.addCell(reserva.getFecha() != null ? reserva.getFecha() : "N/A");
                table.addCell(reserva.getEstado() != null ? reserva.getEstado() : "N/A");
            }

            document.add(table);
        }

        document.close();
    } catch (Exception e) {
        // Para evitar respuesta corrupta
        Document errorDoc = new Document();
        PdfWriter.getInstance(errorDoc, response.getOutputStream());
        errorDoc.open();
        errorDoc.add(new Paragraph("Error al generar el reporte: " + e.getMessage()));
        errorDoc.close();
    }
}


}
