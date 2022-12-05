package es.uji.viajesws2.servicios;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author 
 * Instancia los objetos que ofrecen los servicios web que queremos que despliegue el servidor (p.e. Tomcat)
 * Se almacenan en un conjunto que es devuelto por el método gestSingletons
 *
 */

@ApplicationPath("/servicios") // Innecesario si se incluye como elemento servlet-mapping en el fichero web.xml
public class AplicacionViajes extends Application {
   private Set<Object> singletons = new HashSet<Object>();

   public AplicacionViajes() {
      singletons.add(new RecursoViajes());
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
