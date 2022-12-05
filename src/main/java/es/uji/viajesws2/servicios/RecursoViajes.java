package es.uji.viajesws2.servicios;


import es.uji.viajesws2.modelo.GestorViajes;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


@Path("viajes")
public class RecursoViajes {
	private GestorViajes gestor;

	/**
	 * Constructor por defecto
	 */
	public RecursoViajes() {
		super();
		gestor = new GestorViajes();
		System.out.println("construyo RecursoViajes");
	}

	/**
	 * Cuando cada cliente cierra su sesion volcamos los datos en el fichero para mantenerlos actualizados
	 */
	@GET
	@Path("/guardarDatos")
	@Produces("text/plain")
	public Response guardaDatos() {
		gestor.guardaDatos();
		return Response.status(Response.Status.OK).build();
	}


	/**
	 * Devuelve los viajes disponibles con un origen dado
	 *
	 * @param origen
	 * @return JSONArray de viajes con un origen dado. Vacío si no hay viajes disponibles con ese origen
	 */
	@GET
	@Path("/consulta/{origen}")
	@Produces("application/json")
	public Response consultaViajes(@PathParam("origen") String origen) {
		JSONArray viajes = gestor.consultaViajes(origen);
		ResponseBuilder builder = Response.ok(viajes);
		return builder.build();
	}

	/**
	 * El cliente codcli reserva el viaje codviaje
	 *
	 * @param codviaje
	 * @param codcli
	 * @return JSONObject con la información del viaje. Vacío si no existe o no está disponible
	 */
	@PUT
	@Path("/reserva/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response reservaViaje(@QueryParam("codviaje") String codviaje, @QueryParam("codcli") String codcli) { // CABECERA POR COMPLETAR
		JSONObject viaje = gestor.reservaViaje(codviaje,codcli);
		ResponseBuilder builder = Response.ok(viaje);
		return builder.build();
	}

	/**
	 * El cliente codcli anula su reserva del viaje codviaje
	 *
	 * @param codviaje	codigo del viaje a anular
	 * @param codcli	codigo del cliente
	 * @return	JSON del viaje en que se ha anulado la reserva. JSON vacio si no se ha anulado
	 */
	@DELETE
	@Path("/anulacion/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response anulaReserva(@QueryParam("codviaje") String codviaje, @QueryParam("codcli") String codcli) {
		JSONObject viaje = gestor.anulaReserva(codviaje,codcli);
		ResponseBuilder builder = Response.ok(viaje);
		return builder.build();
	}

	/**
	 * El cliente codcli oferta un Viaje
	 * @param codcli
	 * @param origen
	 * @param destino
	 * @param fecha
	 * @param precio
	 * @param numplazas
	 * @return	JSONObject con los datos del viaje ofertado. Vacio si no se oferta
	 */
	@POST
	@Path("/oferta/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ofertaViaje(@QueryParam("codcli") String codcli,@QueryParam("origen") String origen,
								@QueryParam("destino") String destino,@QueryParam("fecha") String fecha,
								@QueryParam("precio") long precio,@QueryParam("numplazas") long numplazas) {
		JSONObject viaje = gestor.ofertaViaje(codcli,origen,destino,fecha,precio,numplazas);
		ResponseBuilder builder = Response.ok(viaje);
		return builder.build();
	}




	/**
	 * El cliente codcli borra un viaje que ha ofertado
	 *
	 * @param codviaje	codigo del viaje a borrar
	 * @param codcli	codigo del cliente
	 * @return	JSONObject del viaje borrado. JSON vacio si no se ha borrado
	 */
	@DELETE
	@Path("/borrado/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response borraViaje(@QueryParam("codviaje") String codviaje,@QueryParam("codcli") String codcli) {
		JSONObject viaje = gestor.borraViaje(codviaje,codcli);
		ResponseBuilder builder = Response.ok(viaje);
		return builder.build();
	}
}
