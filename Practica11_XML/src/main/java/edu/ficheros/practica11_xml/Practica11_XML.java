/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package edu.ficheros.practica11_xml;

/**
 *
 * @author adria
 */
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.List;

public class Practica11_XML {

    public static void main(String[] args) throws Exception {

        // 1. Instanciar objetos con sus listas de lunas anidadas
        List<Planeta> sistemaSolar = List.of(
                new Planeta("Tierra", "Mediano", List.of(new Luna("Luna"))),
                new Planeta("Marte", "Pequeno", List.of(new Luna("Fobos"), new Luna("Deimos"))),
                new Planeta("Venus", "Mediano", List.of()) // Sin lunas
        );

        // 2. Crear la factoría y el árbol DOM vacío en memoria
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = dbf.newDocumentBuilder().newDocument();

        // 3. Crear y añadir el elemento raíz <sistemaSolar>
        Element raiz = doc.createElement("sistemaSolar");
        doc.appendChild(raiz);

        // 4. Recorrer los planetas y construir su jerarquía
        for (Planeta p : sistemaSolar) {
            // Crear nodo <planeta> con el atributo tamano
            Element planetaElem = doc.createElement("planeta");
            planetaElem.setAttribute("tamano", p.getTamano());

            // Añadir el subelemento <nombre> del planeta
            Element nombreElem = doc.createElement("nombre");
            nombreElem.setTextContent(p.getNombre());
            planetaElem.appendChild(nombreElem);

            // Recorrer las lunas del planeta y añadir cada una como elemento hijo <luna>
            for (Luna l : p.getLunas()) {
                Element lunaElem = doc.createElement("luna");
                lunaElem.setTextContent(l.getNombre());
                planetaElem.appendChild(lunaElem);
            }

            // Enganchar el planeta completo a la raíz
            raiz.appendChild(planetaElem);
        }

        // 5. Configurar el transformador para dar formato legible (indentación)
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        // 6. Volcar el árbol DOM al fichero físico
        transformer.transform(
                new DOMSource(doc),
                new StreamResult(new File("sistema_solar.xml"))
        );

        System.out.println("XML generado correctamente.");
    }
}
