package com.desafio.literalura.principal;

import com.desafio.literalura.dto.AutorDTO;
import com.desafio.literalura.dto.LibroDTO;
import com.desafio.literalura.dto.RespuestaLDTO;
import com.desafio.literalura.model.Autor;
import com.desafio.literalura.model.Libro;
import com.desafio.literalura.service.AutorService;
import com.desafio.literalura.service.ConsumoAPI;
import com.desafio.literalura.service.ConvierteDatos;
import com.desafio.literalura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {


    private Scanner teclado = new Scanner(System.in);

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConvierteDatos convierteDatos;

    private static final String BASE_URL = "https://gutendex.com/books/";

    public void muestraElMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulos
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un año
                    5 - Listar libros por idioma
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    System.out.println("Ingrese el titulo del libro: ");
                    String titulo = teclado.nextLine();
                    try {
                        String encodedTitulo = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
                        String json = consumoAPI.obtenerDatos(BASE_URL + "?search=" + encodedTitulo);
                        RespuestaLDTO respuestaLDTO = convierteDatos.obtenerDatos(json, RespuestaLDTO.class);
                        List<LibroDTO> libroDTO = respuestaLDTO.getLibros();
                        if (libroDTO.isEmpty()) {
                            System.out.println("Libro no Encontrado!");
                        } else {
                            boolean libroRegistrado = false;
                            for (LibroDTO librosDTO : libroDTO) {
                                if (librosDTO.getTitulo().equalsIgnoreCase(titulo)) {
                                    Optional<Libro> libroExistente = libroService.obtenerLibroPorTitulo(titulo);
                                    if (libroExistente.isPresent()) {
                                        System.out.println("El libro" + titulo + " ya existe");
                                        System.out.println("No se puede registrar el mismo libro más de una vez");
                                        libroRegistrado = true;
                                        break;
                                    } else {
                                        Libro libro = new Libro();
                                        libro.setTitulo(librosDTO.getTitulo());
                                        libro.setIdioma(librosDTO.getIdiomas().get(0));
                                        libro.setNumeroDescargas(librosDTO.getNumeroDescargas());

                                        AutorDTO autorDTO = librosDTO.getAutores().get(0);
                                        Autor autor = autorService.obtenerAutorPorNombre(autorDTO.getNombre())
                                                .orElseGet(() -> {
                                                    Autor nuevoAutor = new Autor();
                                                    nuevoAutor.setNombre(autorDTO.getNombre());
                                                    nuevoAutor.setAnoNacimiento(autorDTO.getAnoNacimiento());
                                                    nuevoAutor.setAnoFallecimiento(autorDTO.getAnoFallecimiento());
                                                    return autorService.crearAutor(nuevoAutor);
                                                });
                                        libro.setAutor(autor);

                                        libroService.crearLibro(libro);
                                        System.out.println("Libro registrado: " + libro.getTitulo());
                                        mostrarDetallesLibro(librosDTO);
                                        libroRegistrado = true;
                                        break;

                                    }
                                }
                            }
                            if (!libroRegistrado) {
                                System.out.println("No se encontró un libro con el titulo " + titulo);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener datos de la API: " + e.getMessage());
                    }
                    break;
                case 2:
                    libroService.listarLibros().forEach(l -> {
                        System.out.println("--------------LIBRO--------------");
                        System.out.println("Titulo: " + l.getTitulo());
                        if (l.getAutor() != null) {
                            System.out.println("Autor: " + l.getAutor().getNombre());
                        } else {
                            System.out.println("Autor: Desconocido");
                        }
                        System.out.println("Idioma: " + l.getIdioma());
                        System.out.println("Número de descargas: " + l.getNumeroDescargas());
                    });
                    break;

                case 3:
                    autorService.listarAutores().forEach(a -> {
                        System.out.println("-------------AUTOR--------------");
                        System.out.println("Autor: " + a.getNombre());
                        System.out.println("Fecha de nacimiento: " + a.getAnoNacimiento());
                        System.out.println("Fecha de fallecimiento: " + a.getAnoFallecimiento());
                        String libros = a.getLibros().stream()
                                .map(Libro::getTitulo)
                                .collect(Collectors.joining(", "));
                        System.out.println("Libros: [" + libros + " ]");
                    });
                    break;
                case 4:
                    //listar autores de un año
                    System.out.println("Ingrese el año vivo de autores que desea buscar: ");
                    int year = teclado.nextInt();
                    teclado.nextLine();
                    List<Autor> autoresVivos = autorService.listarAutoresVivosSegunAno(year);
                    if (autoresVivos.isEmpty()) {
                        System.out.println("No se encontraron autores vivos en el año " + year);
                    } else {
                        autoresVivos.forEach(a -> {
                            System.out.println("-------------AUTOR--------------");
                            System.out.println("Autor: " + a.getNombre());
                            System.out.println("Fecha de nacimiento: " + a.getAnoNacimiento());
                            System.out.println("Fecha de fallecimiento: " + a.getAnoFallecimiento());
                            System.out.println("Libros: " + a.getLibros().size());
                        });
                    }
                    break;
                case 5:
                    // Listar por idioma
                    System.out.println("Ingrese el idioma");
                    System.out.println("es");
                    System.out.println("en");
                    System.out.println("fr");
                    System.out.println("pt");
                    String idioma = teclado.nextLine();
                    if ("es".equalsIgnoreCase(idioma) || "en".equalsIgnoreCase(idioma) ||
                            "fr".equalsIgnoreCase(idioma) || "pt".equalsIgnoreCase(idioma)) {

                        libroService.listarLibrosPorIdioma(idioma).forEach(l -> {
                            System.out.println("----------------LIBRO----------------");
                            System.out.println("Titulo: " + l.getTitulo());
                            if (l.getAutor() != null) {
                                System.out.println("Autor: " + l.getAutor().getNombre());
                            } else {
                                System.out.println("Autor: Desconocido");
                            }

                            System.out.println("Idioma: " + l.getIdioma());
                            System.out.println("Número de descargas: " + l.getNumeroDescargas());
                        });

                    } else {
                        System.out.println("Idioma no valido. Ingrese un valor valido!");
                    }
                    break;


                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void mostrarDetallesLibro(LibroDTO libroDTO) {
        System.out.println("-------------------LIBRO----------------------");
        System.out.println("Titulo: " + libroDTO.getTitulo());
        System.out.println("Autor: " + (libroDTO.getAutores().isEmpty() ? "Desconocido" : libroDTO.getAutores()
                .stream()
                .map(AutorDTO::getNombre)
                .collect(Collectors.joining(", "))));
        System.out.println("Idioma: " + libroDTO.getIdiomas().get(0));
        System.out.println("Número de descargas: " + libroDTO.getNumeroDescargas());
    }
}
