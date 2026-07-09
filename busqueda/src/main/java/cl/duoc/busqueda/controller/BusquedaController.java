package cl.duoc.busqueda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.busqueda.dto.BusquedaRequest;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.service.BusquedaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/busqueda")
@Slf4j
@Tag(name = "Búsqueda y Seguimiento", description = "Operaciones para registrar y consultar el estado de seguimiento de los paquetes")
public class BusquedaController {

    @Autowired
    private BusquedaService busquedaService;

    @GetMapping("/{codigo}")
    @Operation(summary = "Consultar seguimiento", description = "Busca los detalles de envío y localización de un paquete utilizando su código de tracking único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código de seguimiento localizado con éxito",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = BusquedaResponse.class),
            examples = @ExampleObject(
                name = "Ejemplo de Tracking Activo",
                value = """
                        {
                          "id": 88,
                          "codigoSeguimiento": "TRACK-NECO-005",
                          "envioId": 1,
                          "estadoEnvio": "En camino a sucursal principal",
                          "nombreProducto": "Peluche Neco-Arc",
                          "detalle": "Peluche 100% algodón",
                          "fechaActualizacionProducto": "2026-07-08T20:30:00"
                        }
                        """
            ))),
        @ApiResponse(responseCode = "404", description = "El código de seguimiento no existe en los registros",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<BusquedaResponse> buscarPorCodigo(@PathVariable String codigo) {
        log.info("GET /v1/busqueda/{} - Consultando código de seguimiento", codigo);
        BusquedaResponse respuesta = busquedaService.obtenerSeguimientoCompleto(codigo);
        return ResponseEntity.ok(respuesta);
    }
    
    @PostMapping
    @Operation(summary = "Registrar nuevo tracking", description = "Crea una orden de rastreo inicial asociada a un envío del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "El registro de seguimiento fue creado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = BusquedaResponse.class),
            examples = @ExampleObject(
                name = "Respuesta de Registro Exitoso",
                value = """
                        {
                          "id": 88,
                          "codigoSeguimiento": "TRACK-NECO-005",
                          "envioId": 1,
                          "estadoEnvio": "Registrado / Pendiente de despacho",
                          "nombreProducto": "Peluche Neco-Arc",
                          "detalle": "Peluche 100% algodón",
                          "fechaActualizacionProducto": "2026-07-08T20:30:00"
                        }
                        """
            )))
    })
    public ResponseEntity<BusquedaResponse> registrarPedido(@Valid @RequestBody BusquedaRequest request) {
        log.info("POST /v1/busqueda - Registrando nuevo código: {}", request.getCodigoSeguimiento());
        BusquedaResponse respuesta = busquedaService.registrarNuevoSeguimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
