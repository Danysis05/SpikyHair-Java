package com.proyecto.spikyhair.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
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
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.spikyhair.DTO.PeluqueriaDto;
import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.RolRepository;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.PeluqueriaService;
import com.proyecto.spikyhair.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final PeluqueriaService peluqueriaService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public AdminController(
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository, PeluqueriaService peluqueriaService) {

        this.usuarioService = usuarioService;
        this.peluqueriaService = peluqueriaService;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

@GetMapping("/dashboard")
public String dashboardAdmin(
        @RequestParam(required = false) String nombreUsuario,
        @RequestParam(required = false) String rol,
        @RequestParam(required = false) String q,        // <-- nuevo: búsqueda universal
        Model model) {

    Usuario usuario = usuarioService.getUsuarioAutenticado();

    // Normalización de parámetros
    String query = (q != null && !q.trim().isEmpty()) ? q.trim() : null;

    // FILTROS UNIVERSALES + FILTROS EXISTENTES
    List<UsuarioDto> usuarios;
    usuarios = usuarioService.buscarPorQuery(query);
    // Peluquerías
     List<PeluqueriaDto> peluquerias = peluqueriaService.buscarPorQuery(q);

    long totalUsuarios = usuarioRepository.count();
    long totalPeluquerias = peluqueriaService.count();
    long totalUsuariosSistema = usuarioService.countUsers();
    long totalAdmins = usuarioService.countAdmins();
    long totalOwners = usuarioService.countOwners();
    int currentYear = java.time.LocalDate.now().getYear();

    // Atributos
    model.addAttribute("usuario", usuario);
    model.addAttribute("usuarios", usuarios);
    model.addAttribute("peluquerias", peluquerias);
    model.addAttribute("totalPeluquerias", totalPeluquerias);
    model.addAttribute("totalAdmins", totalAdmins);
    model.addAttribute("totalUsers", totalUsuariosSistema);
    model.addAttribute("totalOwners", totalOwners);
    model.addAttribute("totalUsuarios", totalUsuarios);
    model.addAttribute("servicio", new ServiciosDto());
    model.addAttribute("roles", rolRepository.findAll());

    // Mantener valores en los filtros
    model.addAttribute("nombreUsuario", nombreUsuario);
    model.addAttribute("rolSeleccionado", rol);

    // Búsqueda universal
    model.addAttribute("q", query);

    // Estadísticas
    model.addAttribute("mesesUsuarios", usuarioService.obtenerMesesUsuarios());
    model.addAttribute("usuariosPorMes", usuarioService.obtenerUsuariosPorMes(currentYear));

    return "admin/dashboard";
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

    // Título
    Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    Paragraph title = new Paragraph("Reporte de Usuarios", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(20);
    document.add(title);

    // Tabla de usuarios
    PdfPTable table = new PdfPTable(4); // Nombre, Email, Teléfono, Rol
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3, 4, 3, 2});

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

    // === GRÁFICO 1: Registros por mes ===
    int currentYear = java.time.LocalDate.now().getYear();
    List<String> meses = usuarioService.obtenerMesesUsuarios(); // ["Enero", "Febrero", ...]
    List<Long> registrosPorMes = usuarioService.obtenerUsuariosPorMes(currentYear); // [10, 20, 15, ...]

    DefaultCategoryDataset datasetMeses = new DefaultCategoryDataset();
    for (int i = 0; i < meses.size(); i++) {
        datasetMeses.addValue(registrosPorMes.get(i), "Usuarios", meses.get(i));
    }

    JFreeChart chartMeses = ChartFactory.createBarChart(
            "Usuarios registrados por mes",
            "Mes",
            "Cantidad",
            datasetMeses
    );

    // Personalización
    CategoryPlot plot1 = chartMeses.getCategoryPlot();
    BarRenderer renderer1 = (BarRenderer) plot1.getRenderer();
    renderer1.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    renderer1.setDefaultItemLabelsVisible(true);
    plot1.setRangeGridlinesVisible(true);

    // Convertir a imagen y agregar al PDF
    BufferedImage chartImage1 = chartMeses.createBufferedImage(500, 300);
    ByteArrayOutputStream chartBaos1 = new ByteArrayOutputStream();
    ImageIO.write(chartImage1, "png", chartBaos1);
    Image chartPdfImage1 = Image.getInstance(chartBaos1.toByteArray());
    chartPdfImage1.setAlignment(Element.ALIGN_CENTER);
    chartPdfImage1.setSpacingBefore(20);
    document.add(chartPdfImage1);

    // === GRÁFICO 2: Usuarios vs Administradores ===
    long totalAdmins = usuarioService.countAdmins();
    long totalUsuarios = usuarioService.countUsers();

    DefaultCategoryDataset datasetRoles = new DefaultCategoryDataset();
    datasetRoles.addValue(totalUsuarios, "Usuarios", "Usuarios");
    datasetRoles.addValue(totalAdmins, "Administradores", "Administradores");

    JFreeChart chartRoles = ChartFactory.createBarChart(
            "Usuarios vs Administradores",
            "Rol",
            "Cantidad",
            datasetRoles
    );

    CategoryPlot plot2 = chartRoles.getCategoryPlot();
    BarRenderer renderer2 = (BarRenderer) plot2.getRenderer();
    renderer2.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    renderer2.setDefaultItemLabelsVisible(true);
    plot2.setRangeGridlinesVisible(true);

    BufferedImage chartImage2 = chartRoles.createBufferedImage(400, 300);
    ByteArrayOutputStream chartBaos2 = new ByteArrayOutputStream();
    ImageIO.write(chartImage2, "png", chartBaos2);
    Image chartPdfImage2 = Image.getInstance(chartBaos2.toByteArray());
    chartPdfImage2.setAlignment(Element.ALIGN_CENTER);
    chartPdfImage2.setSpacingBefore(20);
    document.add(chartPdfImage2);

    document.close();
}




}
