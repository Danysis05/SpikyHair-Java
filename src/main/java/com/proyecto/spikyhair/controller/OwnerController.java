package com.proyecto.spikyhair.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import com.proyecto.spikyhair.DTO.EstilistaDto;
import com.proyecto.spikyhair.DTO.ResenasDto;
import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.EstilistaService;
import com.proyecto.spikyhair.service.PeluqueriaService;
import com.proyecto.spikyhair.service.ResenaService;
import com.proyecto.spikyhair.service.ReservasService;
import com.proyecto.spikyhair.service.ServiciosService;
import com.proyecto.spikyhair.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/owners")
public class OwnerController {
    private final UsuarioService usuarioService;
    private final PeluqueriaService peluqueriaService;
    private final EstilistaService estilistaService;
    private final ReservasService reservasService;
    private final ResenaService resenaService;
    private final ServiciosService serviciosService;

    public OwnerController(PeluqueriaService peluqueriaService, EstilistaService estilistaService, ReservasService reservasService, ResenaService resenaService, UsuarioService usuarioService, ServiciosService serviciosService) {
        this.peluqueriaService = peluqueriaService;
        this.estilistaService = estilistaService;
        this.reservasService = reservasService;
        this.resenaService = resenaService;
        this.usuarioService = usuarioService;
        this.serviciosService = serviciosService;
    }

   @GetMapping("/dashboard")
public String mostrarDashboard(
        @RequestParam(defaultValue = "0") int pageEstilistas,
        @RequestParam(defaultValue = "0") int pageServicios,
        @RequestParam(defaultValue = "0") int pageReservas,
        @RequestParam(defaultValue = "0") int pageResenas,
        Model model) {

    final int size = 6; // 6 elementos por página

    Usuario usuario = usuarioService.getUsuarioAutenticado();
    long id_usuario = usuario.getId();
    Peluqueria peluqueria = peluqueriaService.findByUsuarioId(id_usuario);
    Long peluqueriaId = peluqueria.getId();

    // Listas completas
    List<EstilistaDto> estilistas = estilistaService.getByPeluqueriaId(peluqueriaId);
    List<ServiciosDto> servicios = serviciosService.getByPeluqueriaId(peluqueriaId);
    List<ReservasDto> reservas = reservasService.findByPeluqueriaId(peluqueriaId);
    List<ResenasDto> resenas = resenaService.getByPeluqueriaId(peluqueriaId);

    // Paginación manual
    model.addAttribute("estilistas", paginar(estilistas, pageEstilistas, size));
    model.addAttribute("servicios", paginar(servicios, pageServicios, size));
    model.addAttribute("reservas", paginar(reservas, pageReservas, size));
    model.addAttribute("resenas", paginar(resenas, pageResenas, size));

    // Totales
    model.addAttribute("totalEstilistas", estilistas.size());
    model.addAttribute("totalServicios", servicios.size());
    model.addAttribute("totalReservas", reservas.size());
    model.addAttribute("totalResenas", resenas.size());

    // Info adicional
    model.addAttribute("peluqueria", peluqueria);
    model.addAttribute("usuario", usuario);
    model.addAttribute("promedioPuntacion", resenaService.obtenerPromedioPorPeluqueria(peluqueriaId));

    // Páginas actuales y totales
    model.addAttribute("pageEstilistas", pageEstilistas);
    model.addAttribute("pageServicios", pageServicios);
    model.addAttribute("pageReservas", pageReservas);
    model.addAttribute("pageResenas", pageResenas);

    model.addAttribute("totalPagesEstilistas", totalPages(estilistas.size(), size));
    model.addAttribute("totalPagesServicios", totalPages(servicios.size(), size));
    model.addAttribute("totalPagesReservas", totalPages(reservas.size(), size));
    model.addAttribute("totalPagesResenas", totalPages(resenas.size(), size));

    return "owner/dashboard";
}

private <T> List<T> paginar(List<T> lista, int page, int size) {
    int fromIndex = page * size;
    int toIndex = Math.min(fromIndex + size, lista.size());
    if(fromIndex >= lista.size()) return new ArrayList<>();
    return lista.subList(fromIndex, toIndex);
}

private int totalPages(int totalItems, int size) {
    return (int) Math.ceil((double) totalItems / size);
}

    @GetMapping("/pdf-Servicios")
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

}