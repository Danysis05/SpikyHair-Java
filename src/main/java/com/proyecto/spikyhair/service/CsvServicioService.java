package com.proyecto.spikyhair.service;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.entity.Servicios;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.ServiciosRepository;

@Service
public class CsvServicioService {

    private static final Logger log = LoggerFactory.getLogger(CsvServicioService.class);

    private final ServiciosRepository serviciosRepository;
    private final UsuarioService usuarioService;
    private final PeluqueriaService peluqueriaService;

    public CsvServicioService(ServiciosRepository serviciosRepository,
                              UsuarioService usuarioService,
                              PeluqueriaService peluqueriaService) {
        this.serviciosRepository = serviciosRepository;
        this.usuarioService = usuarioService;
        this.peluqueriaService = peluqueriaService;
    }

    /** =============================
     *  IMPORTAR SERVICIOS DESDE CSV
     *  =============================
     */
    public int cargarServiciosDesdeCsv(MultipartFile file) {

        int importados = 0;
        int repetidos = 0;

        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream())) {

            // Detectar separador automáticamente (; o ,)
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

                // Obtener peluquería del usuario autenticado
                Usuario usuario = usuarioService.getUsuarioAutenticado();
                Peluqueria peluqueria = peluqueriaService.findByUsuarioId(usuario.getId());

                if (peluqueria == null) {
                    throw new RuntimeException("El usuario no tiene una peluquería asignada.");
                }

                Long peluqueriaId = peluqueria.getId();

                while ((row = reader.readNext()) != null) {

                    if (row.length == 0 || (row.length == 1 && row[0].trim().isEmpty()))
                        continue;

                    if (first && row[0] != null)
                        row[0] = removeBom(row[0]);

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

                    if (nombre.isBlank() || duracion.isBlank() || descripcion.isBlank() || precioRaw.isBlank()) {
                        log.warn("Fila inválida, datos incompletos: {}", (Object) row);
                        continue;
                    }

                    // Normalizar precio
                    String precioNorm = precioRaw.replace("$", "").replace(".", "").replace(",", ".").trim();
                    double precio;
                    try {
                        precio = Double.parseDouble(precioNorm);
                    } catch (NumberFormatException nfe) {
                        log.warn("Precio inválido '{}', fila ignorada: {}", precioRaw, (Object) row);
                        continue;
                    }

                    // Evitar duplicados por nombre en la misma peluquería
                    boolean existe = serviciosRepository.existsByNombreIgnoreCaseAndPeluqueriaId(nombre, peluqueriaId);
                    if (existe) {
                        repetidos++;
                        log.info("Servicio repetido ignorado: {}", nombre);
                        continue;
                    }

                    Servicios servicio = new Servicios();
                    servicio.setNombre(nombre);
                    servicio.setDuracion(duracion);
                    servicio.setDescripcion(descripcion);
                    servicio.setPrecio(precio);
                    servicio.setPeluqueria(peluqueria);

                    serviciosRepository.save(servicio);
                    importados++;
                }
            }

        } catch (Exception e) {
            log.error("Error al procesar CSV de Servicios", e);
            throw new RuntimeException("Error procesando CSV: " + e.getMessage(), e);
        }

        log.info("Servicios importados: {}, repetidos: {}", importados, repetidos);

        if (repetidos > 0) {
            throw new RuntimeException(
                "Importación parcial: " + importados + " nuevos, " + repetidos + " repetidos."
            );
        }

        return importados;
    }

    // ===========================
    // Helpers
    // ===========================

    private static long contar(String s, char c) {
        return s.chars().filter(ch -> ch == c).count();
    }

    private static String removeBom(String s) {
        return s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF'
                ? s.substring(1)
                : s;
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
