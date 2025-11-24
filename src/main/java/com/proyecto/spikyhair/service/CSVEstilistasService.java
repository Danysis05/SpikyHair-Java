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
import com.proyecto.spikyhair.entity.Estilista;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.EstilistaRepository;

@Service
public class CSVEstilistasService {

    private static final Logger log = LoggerFactory.getLogger(CSVEstilistasService.class);

    private final EstilistaRepository estilistaRepository;
    private final UsuarioService usuarioService;
    private final PeluqueriaService peluqueriaService;

    public CSVEstilistasService(
            EstilistaRepository estilistaRepository,
            UsuarioService usuarioService,
            PeluqueriaService peluqueriaService) {

        this.estilistaRepository = estilistaRepository;
        this.usuarioService = usuarioService;
        this.peluqueriaService = peluqueriaService;
    }

    /** =============================
     *  IMPORTAR ESTILISTAS DESDE CSV
     *  =============================
     */
    public int cargarEstilistasDesdeCsv(MultipartFile file) {

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

                    // Eliminar BOM en primera fila
                    if (first && row[0] != null)
                        row[0] = removeBom(row[0]);

                    // Detectar cabecera
                    if (first && esCabecera(row)) {
                        first = false;
                        continue;
                    }
                    first = false;

                    if (row.length < 2) {
                        log.warn("Fila ignorada (columnas < 2): {}", (Object) row);
                        continue;
                    }

                    String nombre = safe(row[0]);
                    String especialidad = safe(row[1]);

                    if (nombre.isBlank() || especialidad.isBlank()) {
                        log.warn("Fila inválida, datos vacíos: {}", (Object) row);
                        continue;
                    }

                    // Verificar si ya existe el estilista en esa peluquería
                    boolean existe = estilistaRepository.existsByNombreIgnoreCaseAndPeluqueriaId(nombre, peluqueriaId);
                    if (existe) {
                        repetidos++;
                        log.info("Estilista repetido ignorado: {}", nombre);
                        continue;
                    }

                    Estilista est = new Estilista();
                    est.setNombre(nombre);
                    est.setEspecialidad(especialidad);
                    est.setPeluqueria(peluqueria);

                    estilistaRepository.save(est);
                    importados++;
                }
            }

        } catch (Exception e) {
            log.error("Error al procesar CSV de Estilistas", e);
            throw new RuntimeException("Error procesando CSV: " + e.getMessage(), e);
        }

        log.info("Estilistas importados: {}, repetidos: {}", importados, repetidos);

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
        if (row.length < 2)
            return false;

        String a = row[0].toLowerCase();
        String b = row[1].toLowerCase();

        return a.contains("nombre") && b.contains("especialidad");
    }
}
