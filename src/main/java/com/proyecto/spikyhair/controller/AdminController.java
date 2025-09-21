package com.proyecto.spikyhair.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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

    public AdminController(
            UsuarioService usuarioService,
            ServiciosService serviciosService,
            ReservasService reservasService,
            UsuarioRepository usuarioRepository,
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

    // Normalizar filtros vacíos
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
    long reservasRealizadas = reservasService.contarRealizadas();
    long reservasPendientes = reservasService.contarPendientes();
    long totalUsuariosSistema = usuarioService.countUsers();
    long totalAdmins = usuarioService.countAdmins();
    int currentYear = java.time.LocalDate.now().getYear();
    Double ingresosTotales = reservasService.calcularIngresosTotales();

    // === RESERVAS POR MES ===
    List<String> meses = reservasService.obtenerMeses(); // ["Enero", "Febrero", ...]
    List<Long> reservasPorMes = reservasService.obtenerReservasPorMes(); // [10, 20, 15, ...]
    Map<String, List<?>> estadisticasServicios = reservasService.obtenerEstadisticasServicios();
    

    model.addAttribute("usuario", usuario);
    model.addAttribute("usuarios", usuarios);
    model.addAttribute("totalAdmins", totalAdmins);
    model.addAttribute("totalUsers", totalUsuariosSistema);
    model.addAttribute("servicios", servicios);
    model.addAttribute("reservas", reservas);
    model.addAttribute("totalUsuarios", totalUsuarios);
    model.addAttribute("totalServicios", totalServicios);
    model.addAttribute("totalReservas", totalReservas);
    model.addAttribute("servicio", new ServiciosDto());
    model.addAttribute("roles", rolRepository.findAll());

    // Datos para los gráficos
    model.addAttribute("meses", meses);
    model.addAttribute("reservasPorMes", reservasPorMes);

    // Conserva valores filtrados en el formulario
    model.addAttribute("nombreUsuario", nombreUsuario);
    model.addAttribute("rolSeleccionado", rol);
    model.addAttribute("nombreServicio", nombreServicio);
    model.addAttribute("precioMin", precioMin);
    model.addAttribute("precioMax", precioMax);
    model.addAttribute("estado", estado);
    model.addAttribute("reservasRealizadas", reservasRealizadas);
    model.addAttribute("reservasPendientes", reservasPendientes);
    model.addAttribute("mesesUsuarios", usuarioService.obtenerMesesUsuarios());
    model.addAttribute("usuariosPorMes", usuarioService.obtenerUsuariosPorMes(currentYear));
    model.addAttribute("serviciosLabels", estadisticasServicios.get("labels"));
    model.addAttribute("serviciosValues", estadisticasServicios.get("values"));
    model.addAttribute("ingresosTotales", ingresosTotales);
    

    return "admin/dashboard";
}

@GetMapping("/dashboard/pdf")
public void generarPdfServicios(
        @RequestParam(required = false) String nombreServicio,
        @RequestParam(required = false) Double precioMin,
        @RequestParam(required = false) Double precioMax,
        HttpServletResponse response) throws IOException, DocumentException {

    List<ServiciosDto> servicios = serviciosService.filtrarServicios(nombreServicio, precioMin, precioMax);

    // Obtener estadísticas de servicios
    Map<String, List<?>> estadisticasServicios = reservasService.obtenerEstadisticasServicios();
    List<String> labels = (List<String>) estadisticasServicios.get("labels"); // nombres de servicios
    List<Long> values = (List<Long>) estadisticasServicios.get("values");   // número de reservas

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=servicios.pdf");

    Document document = new Document();
    PdfWriter.getInstance(document, response.getOutputStream());
    document.open();

    // Título
    Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    Paragraph title = new Paragraph("Reporte de Servicios", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(20);
    document.add(title);

    // Tabla de servicios
    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3, 2, 4, 2});

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

    // Crear gráfico de barras de servicios más reservados
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (int i = 0; i < labels.size(); i++) {
        dataset.addValue(values.get(i), "Reservas", labels.get(i));
    }

    JFreeChart chart = ChartFactory.createBarChart(
            "Servicios más reservados",
            "Servicio",
            "Número de reservas",
            dataset
    );

    // Personalizar renderer moderno
    CategoryPlot plot = chart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    renderer.setDefaultItemLabelsVisible(true);

    chart.getCategoryPlot().setRangeGridlinesVisible(true);

    // Convertir gráfico a imagen
    BufferedImage chartImage = chart.createBufferedImage(500, 300);
    ByteArrayOutputStream chartBaos = new ByteArrayOutputStream();
    ImageIO.write(chartImage, "png", chartBaos);
    Image chartPdfImage = Image.getInstance(chartBaos.toByteArray());

    chartPdfImage.setAlignment(Element.ALIGN_CENTER);
    chartPdfImage.setSpacingBefore(20);
    document.add(chartPdfImage);

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


@GetMapping("/dashboard/pdf-reservas")
public void generarPdfReservas(
        @RequestParam(required = false) String nombreUsuario,
        @RequestParam(required = false) String nombreServicio,
        @RequestParam(required = false) String estado,
        HttpServletResponse response) throws Exception {

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=reservas.pdf");

    // 🔹 Obtener reservas filtradas (con tu servicio)
    List<ReservasDto> reservas = reservasService.filtrarReservas(nombreUsuario, nombreServicio, estado);

    Document document = new Document();
    PdfWriter.getInstance(document, response.getOutputStream());
    document.open();

    // ======================
    // 📊 INGRESOS POR MES
    // ======================
    int currentYear = LocalDate.now().getYear();
    List<Double> ingresosPorMes = reservasService.obtenerIngresosPorMes(currentYear);

    DefaultCategoryDataset datasetIngresos = new DefaultCategoryDataset();
    // 🔹 Usar los 12 meses en orden fijo (enero-diciembre)
    String[] nombresMeses = {
        "Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
    };

    for (int i = 0; i < 12; i++) {
        datasetIngresos.addValue(ingresosPorMes.get(i), "Ingresos", nombresMeses[i]);
    }

    JFreeChart chartIngresos = ChartFactory.createBarChart(
            "Ingresos por Mes - " + currentYear,
            "Mes",
            "Ingresos ($)",
            datasetIngresos
    );

    BufferedImage chartImage1 = chartIngresos.createBufferedImage(500, 300);
    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
    ImageIO.write(chartImage1, "png", baos1);
    Image chartPdfImage1 = Image.getInstance(baos1.toByteArray());
    chartPdfImage1.setAlignment(Element.ALIGN_CENTER);
    chartPdfImage1.setSpacingBefore(20);
    document.add(chartPdfImage1);

    // ======================
    // 📊 TOP CLIENTES
    // ======================
    Map<String, Long> clientesTop = reservasService.obtenerClientesTop();

    DefaultCategoryDataset datasetClientes = new DefaultCategoryDataset();
    clientesTop.forEach((nombre, cantidad) -> datasetClientes.addValue(cantidad, "Reservas", nombre));

    JFreeChart chartClientes = ChartFactory.createBarChart(
            "Clientes con más Reservas",
            "Cliente",
            "Cantidad de Reservas",
            datasetClientes
    );

    BufferedImage chartImage2 = chartClientes.createBufferedImage(500, 300);
    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
    ImageIO.write(chartImage2, "png", baos2);
    Image chartPdfImage2 = Image.getInstance(baos2.toByteArray());
    chartPdfImage2.setAlignment(Element.ALIGN_CENTER);
    chartPdfImage2.setSpacingBefore(20);
    document.add(chartPdfImage2);

    // ======================
    // 📋 TABLA DE RESERVAS
    // ======================
    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setSpacingBefore(20);
    table.addCell("Usuario");
    table.addCell("Servicio");
    table.addCell("Fecha");
    table.addCell("Estado");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    for (ReservasDto reserva : reservas) {
        table.addCell(reserva.getUsuario() != null ? reserva.getUsuario().getNombre() : "N/A");
        table.addCell(reserva.getServicioNombre() != null ? reserva.getServicioNombre() : "N/A");
        table.addCell(reserva.getFecha() != null ? reserva.getFecha().format(formatter) : "N/A");
        table.addCell(reserva.getEstado() != null ? reserva.getEstado() : "N/A");
    }

    document.add(table);
    document.close();
}









}
