/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.ad.ficheros.practica11;

/**
 *
 * @author adrianm.gib
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Practica11 {

    private static final String RUTA_DAT = "datos/catalogo_musica.dat";
    private static final String RUTA_JSON = "datos/catalogo_musica.json";

    public static void main(String[] args) {
        try {
            // Crear carpeta 'datos' y borrar fichero de pruebas anteriores
            Files.createDirectories(Paths.get("datos"));
            Files.deleteIfExists(Paths.get(RUTA_DAT));

            System.out.println("=== 1. CREATE (Creación inicial de álbumes en binario) ===");
            Album a1 = new Album(1, "Random Access Memories", 2013, 19.99, "https://privado.discografica/token_ram_991",
                    new Productor("Daft Punk", "Columbia Records"),
                    List.of(new Cancion(1, "Give Life Back to Music", 274), new Cancion(2, "Get Lucky", 369)));

            Album a2 = new Album(2, "El Mal Querer", 2018, 17.50, "https://privado.discografica/token_emq_442",
                    new Productor("El Guincho", "Sony Music"),
                    List.of(new Cancion(1, "Malamente", 150), new Cancion(2, "Pienso en tu mirá", 193)));

            guardarAlbumAppend(a1);
            guardarAlbumAppend(a2);
            leerTodos().forEach(System.out::println);

            System.out.println("\n=== 2. UPDATE / APPEND (Añadir un nuevo álbum al final sin borrar) ===");
            Album a3 = new Album(3, "Currents", 2015, 22.00, "https://privado.discografica/token_cur_773",
                    new Productor("Kevin Parker", "Interscope Records"),
                    List.of(new Cancion(1, "Let It Happen", 466), new Cancion(2, "The Less I Know the Better", 216)));

            guardarAlbumAppend(a3);

            // Cambiamos el atributo static en memoria antes de leer para demostrar su comportamiento
            Album.formatoDistribucion = "Vinilo Edición Coleccionista";
            System.out.println("-> Catálogo tras hacer append y modificar variable static:");
            leerTodos().forEach(System.out::println);

            System.out.println("\n=== 3. UPDATE (Modificar precio del álbum ID 2) ===");
            actualizarPrecio(2, 12.99);
            leerTodos().forEach(System.out::println);

            System.out.println("\n=== 4. DELETE (Eliminar álbum ID 1) ===");
            eliminarAlbum(1);
            List<Album> catalogoFinal = leerTodos();
            catalogoFinal.forEach(System.out::println);

            System.out.println("\n=== 5. EXPORTACIÓN A JSON CON GSON ===");
            exportarAJson(catalogoFinal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CREATE / APPEND: Añade un objeto al final del fichero binario
    public static void guardarAlbumAppend(Album album) throws IOException {
        File fichero = new File(RUTA_DAT);
        boolean existeConDatos = fichero.exists() && fichero.length() > 0;

        if (existeConDatos) {
            try (MiObjectOutputStream moos = new MiObjectOutputStream(new FileOutputStream(fichero, true))) {
                moos.writeObject(album);
            }
        } else {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichero))) {
                oos.writeObject(album);
            }
        }
    }

    // READ: Lee todos los álbumes secuencialmente hasta EOFException
    public static List<Album> leerTodos() throws IOException, ClassNotFoundException {
        List<Album> lista = new ArrayList<>();
        File fichero = new File(RUTA_DAT);
        if (!fichero.exists()) return lista;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero))) {
            while (true) {
                Album album = (Album) ois.readObject();
                lista.add(album);
            }
        } catch (EOFException e) {
            // Fin del fichero alcanzado correctamente
        }
        return lista;
    }

    // UPDATE: Patrón Leer -> Modificar en memoria -> Reescribir fichero completo
    public static void actualizarPrecio(int idBuscar, double nuevoPrecio) throws IOException, ClassNotFoundException {
        List<Album> albumes = leerTodos();
        for (Album a : albumes) {
            if (a.getId() == idBuscar) {
                a.setPrecio(nuevoPrecio);
                break;
            }
        }
        reescribirFichero(albumes);
    }

    // DELETE: Patrón Leer -> Filtrar en memoria -> Reescribir fichero completo
    public static void eliminarAlbum(int idEliminar) throws IOException, ClassNotFoundException {
        List<Album> albumes = leerTodos();
        albumes.removeIf(a -> a.getId() == idEliminar);
        reescribirFichero(albumes);
    }

    // Método auxiliar que sobrescribe el binario desde cero con la lista actualizada
    private static void reescribirFichero(List<Album> albumes) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_DAT, false))) {
            for (Album a : albumes) {
                oos.writeObject(a);
            }
        }
    }

    // EXPORTAR A JSON: Genera el archivo JSON formateado con Gson
    public static void exportarAJson(List<Album> albumes) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path ruta = Paths.get(RUTA_JSON);

        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            gson.toJson(albumes, writer);
        }
        System.out.println("Archivo JSON generado correctamente en: " + ruta.toAbsolutePath());
    }
}
