package es.uji.viajesws2.modelo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

public class GestorViajes {

    private static FileWriter os;            // stream para escribir los datos en el fichero
    private static FileReader is;            // stream para leer los datos del fichero

    /**
     * Diccionario para manejar los datos en memoria.
     * La clave es el codigo único del viaje.
     */
    private static HashMap<String, Viaje> mapa;


    /**
     * Constructor del gestor de viajes
     * Crea o Lee un fichero con datos de prueba
     */
    public GestorViajes() {
        mapa = new HashMap<String, Viaje>();
        File file = new File("viajes.json");
        try {
            if (!file.exists()) {
                // Si no existe el fichero de datos, los genera y escribe
                os = new FileWriter(file);
                generaDatos();
                escribeFichero(os);
                os.close();
            }
            // Si existe el fichero o lo acaba de crear, lo lee y rellena el diccionario con los datos
            is = new FileReader(file);
            leeFichero(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Cuando cada cliente cierra su sesion volcamos los datos en el fichero para mantenerlos actualizados
     */
    public void guardaDatos() {
        File file = new File("viajes.json");
        try {
            os = new FileWriter(file);
            escribeFichero(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * escribe en el fichero un array JSON con los datos de los viajes guardados en el diccionario
     *
     * @param os stream de escritura asociado al fichero de datos
     */
    private void escribeFichero(FileWriter os) throws IOException {
        JSONArray ar = new JSONArray();
        for (Viaje clave : mapa.values()) {
            ar.add(clave.toJSON());
        }
        os.write(ar.toJSONString());
        // POR IMPLEMENTAR
    }


    /**
     * Genera los datos iniciales
     */
    private void generaDatos() {

        Viaje viaje = new Viaje("pedro", "Castellón", "Alicante", "28-05-2023", 16, 1);
        mapa.put(viaje.getCodviaje(), viaje);

        viaje = new Viaje("pedro", "Alicante", "Castellón", "29-05-2023", 16, 1);
        mapa.put(viaje.getCodviaje(), viaje);

        viaje = new Viaje("maria", "Madrid", "Valencia", "07-06-2023", 7, 2);
        mapa.put(viaje.getCodviaje(), viaje);

        viaje = new Viaje("carmen", "Sevilla", "Barcelona", "12-08-2023", 64, 1);
        mapa.put(viaje.getCodviaje(), viaje);

        viaje = new Viaje("juan", "Castellón", "Cordoba", "07-11-2023", 39, 3);
        mapa.put(viaje.getCodviaje(), viaje);

    }

    /**
     * Lee los datos del fichero en formato JSON y los añade al diccionario en memoria
     *
     * @param is stream de lectura de los datos del fichero
     */
    private void leeFichero(FileReader is) {
        JSONParser parser = new JSONParser();
        try {
            // Leemos toda la información del fichero en un array de objetos JSON
            JSONArray array = (JSONArray) parser.parse(is);
            // Rellena los datos del diccionario en memoria a partir del JSONArray
            rellenaDiccionario(array);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Rellena el diccionario a partir de los datos en un JSONArray
     *
     * @param array JSONArray con los datos de los Viajes
     */
    private void rellenaDiccionario(JSONArray array) {
        // POR IMPLEMENTAR hecho
        for (JSONObject v : (Iterable<JSONObject>) array) {
            Viaje viaje = new Viaje(v);
            mapa.put(viaje.getCodviaje(), viaje);
        }
    }

    /**
     * Devuelve los viajes disponibles con un origen dado
     *
     * @param origen
     * @return JSONArray de viajes con un origen dado. Vacío si no hay viajes disponibles con ese origen
     */
    public JSONArray consultaViajes(String origen) {
        JSONArray viajes = new JSONArray(); // Array de viajes vaco
        for (Viaje viaje : mapa.values()) { // Para cada viaje en el mapa
            if (viaje.getOrigen().equals(origen))
                viajes.add(viaje.toJSON()); // Saca los viajes del mapa y los mete en el array
        }
        System.out.println("gestor " + viajes.toJSONString());
        return viajes; // Devuelvo el array
    }


    /**
     * El cliente codcli reserva el viaje codviaje
     *
     * @param codviaje
     * @param codcli
     * @return JSONObject con la información del viaje. Vacío si no existe o no está disponible
     */
    public JSONObject reservaViaje(String codviaje, String codcli) {
        JSONObject reserva = new JSONObject();
        if (mapa.get(codviaje) == null) { // SI no existe en el mapa
            // System.out.println("No existe el viaje");
            return reserva;
        }
        else if (mapa.get(codviaje).getCodprop().equals(codcli)) { // SI cliente es el propietario
            // System.out.println("No puedes reservar tu propio viaje");
            return reserva;
        }
        else if (mapa.get(codviaje).finalizado()) {// Si Ha finalizado
            // System.out.println("No puedes reservar un viaje finalizado");
            return reserva;
        }
        else if (mapa.get(codviaje).anyadePasajero(codcli)) { // Si se puede anyadir(quedan plazas)
            reserva = mapa.get(codviaje).toJSON(); //Meto en reserva el viaje
        } else { //No se añade por que no hay plazas
            // System.out.println("No quedan plazas");
            return reserva;//
        }

        return reserva; // Devuelvo JSON Object reserva
    }

    /**
     * El cliente codcli anula su reserva del viaje codviaje
     *
     * @param codviaje codigo del viaje a anular
     * @param codcli   codigo del cliente
     * @return JSON del viaje en que se ha anulado la reserva. JSON vacio si no se ha anulado
     */
    public JSONObject anulaReserva(String codviaje, String codcli) {
        JSONObject anulacion = new JSONObject();
        if (mapa.get(codviaje) == null) // SI no existe en el mapa
            return anulacion; // Devolvemos JSON vacío
        else if (!mapa.get(codviaje).getPasajeros().contains(codcli)) { // Si el propietario y el cliente son iguales
            // System.out.println("No puedes anular la reserva a un viaje no reservado por ti");
            return anulacion;
        }
        else if (mapa.get(codviaje).finalizado()) {// Ha finalizado
            // System.out.println("No puede anularse un viaje que ya ha finalizado");
            return anulacion;
        }
        else if (mapa.get(codviaje).borraPasajero(codcli)) { //Si se puede borrar(osea que el cliente si que hizo esa reserva)
            anulacion = mapa.get(codviaje).toJSON(); // Metemos en el objeto JSON el viaje
            /*OTRA IMPLEMENTACION
            mapa.get(codviaje).getPasajeros().remove(codcli); // Eliminamos el pasajero del mapa
            mapa.get(codviaje).setNumplazas(mapa.get(codviaje).getNumplazas() + 1); // Añadimos una plaza libre
             */
        } else { //No ha podido borrarse ya que el cliente no realizo esa reserva
            return anulacion;
        }
        return anulacion;
    }

    /**
     * Devuelve si una fecha es válida y futura
     *
     * @param fecha
     * @return
     */
    private boolean es_fecha_valida(String fecha) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            LocalDate dia = LocalDate.parse(fecha, formatter);
            LocalDate hoy = LocalDate.now();

            return dia.isAfter(hoy);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha invalida: " + fecha);
            return false;
        }

    }

    /**
     * El cliente codcli oferta un Viaje
     *
     * @param codcli
     * @param origen
     * @param destino
     * @param fecha
     * @param precio
     * @param numplazas
     * @return JSONObject con los datos del viaje ofertado
     */
    public JSONObject ofertaViaje(String codcli, String origen, String destino, String fecha, long precio, long numplazas) {
        JSONObject oferta = new JSONObject();
        if (!es_fecha_valida(fecha)) { // SI la fecha no es valida
            //System.out.println("Fecha incorrecta");
            return oferta; // Devolvemos JSON vacío
        }
        if (precio < 0) { // SI el precio menos que 0
            //System.out.println("Precio incorrecto");
            return oferta; // Devolvemos JSON vacío
        }
        if (numplazas <= 0) { // Si el numero plazas menos o igual que 0
            //System.out.println("Número de plazas incorrecto");
            return oferta; // Devolvemos JSON vacío
        }
        Viaje nuevo = new Viaje(codcli, origen, destino, fecha, precio, numplazas); // Creamos nuevo viaje
        oferta = nuevo.toJSON(); //
        mapa.put(nuevo.getCodviaje(), nuevo); // Metemos en el mapa el nuevo viaje
        return oferta; // Devolvemos el OBJETO JSON
    }


    /**
     * El cliente codcli borra un viaje que ha ofertado
     *
     * @param codviaje codigo del viaje a borrar
     * @param codcli   codigo del cliente
     * @return JSONObject del viaje borrado. JSON vacio si no se ha borrado
     */
    public JSONObject borraViaje(String codviaje, String codcli) {
        JSONObject borrado = new JSONObject();
        if (mapa.get(codviaje) == null) { // SINO esta en el mapa
            //  System.out.println("El viaje no existe");
            return borrado; // Devolvemos JSON vacío
        }
        else if (!mapa.get(codviaje).getCodprop().equals(codcli)) { // Si el cliente intenta borrar un viaje que no es suyo
            // System.out.println("El viaje no puede borrarse ya que no le pertenece");
            return borrado; // Devolvemos JSON vacío
        }
        else if (mapa.get(codviaje).finalizado()) { // Si el viaje ha finalizado
            //  System.out.println("El viaje no ha podido borrarse ya que ya ha finalizado");
            return borrado; // Devolvemos JSON vacío
        }
        borrado = mapa.get(codviaje).toJSON();
        mapa.remove(codviaje, mapa.get(codviaje));
        return borrado;

    }


}