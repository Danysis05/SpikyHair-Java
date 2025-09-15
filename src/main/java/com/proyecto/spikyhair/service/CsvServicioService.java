package com.proyecto.spikyhair.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.proyecto.spikyhair.entity.Servicios;
import com.proyecto.spikyhair.repository.ServiciosRepository;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CsvServicioService {

    private static final Logger log = LoggerFactory.getLogger(CsvServicioService.class);
    private final ServiciosRepository serviciosRepository;

    public CsvServicioService(ServiciosRepository serviciosRepository) {
        this.serviciosRepository = serviciosRepository;
    }

    /** Importar servicios desde un CSV */
    public int cargarServiciosDesdeCsv(MultipartFile file) {
        int importados = 0;

        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream())) {
            bis.mark(2_000_000);
            String probe = new String(bis.readNBytes(4096), StandardCharsets.UTF_8);
            char sep = contar(probe, ';') > contar(probe, ',') ? ';' : ',';
            bis.reset();

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(sep)
                    .withIgnoreQuotations(false)
                    .build();

            try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(bis, StandardCharsets.UTF_8))
                    .withCSVParser(parser)
                    .build()) {

                String[] row;
                boolean first = true;

                while ((row = reader.readNext()) != null) {
                    if (row.length == 0 || (row.length == 1 && row[0].trim().isEmpty())) continue;

                    if (first && row[0] != null) row[0] = removeBom(row[0]);
                    if (first && esCabecera(row)) {
                        first = false;
                        continue;
                    }
                    first = false;

                    if (row.length < 4) {
                        log.warn("Fila ignorada (columnas < 4): {}", (Object) row);
                        continue;
                    }

                    String nombre = safe(row[0]);
                    String duracion = safe(row[1]);
                    String descripcion = safe(row[2]);
                    String precioRaw = safe(row[3]);

                    String precioNorm = precioRaw.replace("$", "").replace(".", "").replace(",", ".").trim();
                    double precio;
                    try {
                        precio = Double.parseDouble(precioNorm);
                    } catch (NumberFormatException nfe) {
                        log.warn("Precio inválido '{}', fila ignorada: {}", precioRaw, (Object) row);
                        continue;
                    }

                    Servicios servicio = new Servicios();
                    servicio.setNombre(nombre);
                    servicio.setDuracion(duracion);
                    servicio.setDescripcion(descripcion);
                    servicio.setPrecio(precio);

                    serviciosRepository.save(servicio);
                    importados++;
                }
            }
        } catch (Exception e) {
            log.error("Error al procesar CSV con OpenCSV", e);
            throw new RuntimeException("Error al procesar CSV con OpenCSV: " + e.getMessage(), e);
        }

        log.info("Servicios importados: {}", importados);
        return importados;
    }

    /** Exportar servicios a CSV */
    public void exportarServiciosCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=servicios.csv");

            try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
                // Cabecera
                writer.writeNext(new String[]{"Nombre", "Duración", "Descripción", "Precio"});

                // Datos
                List<Servicios> lista = serviciosRepository.findAll();
                for (Servicios s : lista) {
                    writer.writeNext(new String[]{
                            s.getNombre(),
                            s.getDuracion(),
                            s.getDescripcion(),
                            String.valueOf(s.getPrecio())
                    });
                }
            }
        } catch (IOException e) {
            log.error("Error al exportar CSV", e);
            throw new RuntimeException("Error al exportar CSV: " + e.getMessage(), e);
        }
    }

    // Helpers
    private static long contar(String s, char c) {
        return s.chars().filter(ch -> ch == c).count();
    }

    private static String removeBom(String s) {
        return s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF' ? s.substring(1) : s;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static boolean esCabecera(String[] row) {
        if (row.length < 4) return false;
        String a = row[0].toLowerCase();
        String b = row[1].toLowerCase();
        String c = row[2].toLowerCase();
        String d = row[3].toLowerCase();
        return a.contains("nombre") && b.contains("duracion") && c.contains("descripcion") && d.contains("precio");
    }
}
