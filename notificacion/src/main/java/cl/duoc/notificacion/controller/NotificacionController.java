package cl.duoc.notificacion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.notificacion.dto.NotificacionRequest;
import cl.duoc.notificacion.dto.NotificacionResponse;
import cl.duoc.notificacion.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/notificaciones")
@Slf4j
@Tag(name = "Notificaciones", description = "Operaciones del sistema de alertas y notificaciones a usuarios")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @PostMapping
    @Operation(summary = "Crear/Enviar notificación", description = "Genera una nueva alerta en el sistema (utilizado por servicios internos como Envios u Ordenes)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notificación creada con éxito",
            content = @Content(mediaType = "text/plain",
            examples = @ExampleObject(
                name = "Respuesta Exitosa",
                value = "Notificación creada con éxito"
            )))
    })
    public ResponseEntity<String> enviar(@RequestBody NotificacionRequest request) {
        log.info("POST /v1/notificaciones - Creando notificación para el usuario: {}", request.getUsuarioId());
        notificacionService.crear(request);
        return new ResponseEntity<>("Notificación creada con éxito", HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{id}")
    @Operation(summary = "Listar por usuario", description = "Recupera el historial de alertas recibidas por un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = NotificacionResponse.class)),
            examples = @ExampleObject(
                name = "Ejemplo de Alerta de Envío (Avatar Belial)",
                value = """
                        [
                          {
                            "id": 501,
                            "usuarioId": 2,
                            "nombreUsuario": "Esteban",
                            "appaternouno": "Esteban",
                            "mensaje": "Tu figura de avatar belial semidesnudo te espera en la puerta",
                            "tipo": "ENVIO",
                            "fechaEnvio": "2026-07-08T20:15:00"
                          }
                        ]
                        """
            )))
    })
    public ResponseEntity<List<NotificacionResponse>> listar(@PathVariable Long id) {
        log.info("GET /v1/notificaciones/usuario/{} - Ver notificaciones del usuario", id);
        List<NotificacionResponse> lista = notificacionService.listarPorUsuario(id);
        return ResponseEntity.ok(lista);
    }
}
