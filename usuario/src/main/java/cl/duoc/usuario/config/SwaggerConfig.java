package cl.duoc.usuario.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/*
Esta clase sirve para personalizar la informacion que aparece en la
documentacion de la API.
*/
@Configuration //Le indica a spring que contiene configuraciones y beans.
public class SwaggerConfig {

    /* Le dice a spring que debe crear un objeto y guardarlo en un contenedor de spring
    y SpringDoc detectara este bean de tipo OpenAPI para construir la documentacion.
    */
    @Bean 
    public OpenAPI customOpenAPI(){
        return new OpenAPI() //Se crea la instancia de OpenAPI
                .servers(List.of(
                    new Server().url("http://localhost:8090").description("API Gateway Central")
                ))
                .info(new Info() //Se define la informacion de la documentacion
                    .title("NecoArcMarket - Servicio de Usuarios API")
                    .version("2.0")
                    .description("Documentación de los endpoints para la gestión y administración de perfiles de usuario."));
    }
}
