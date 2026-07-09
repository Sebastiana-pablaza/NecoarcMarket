package cl.duoc.resena.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.resena.dto.ResenaRequest;
import cl.duoc.resena.dto.ResenaResponse;
import cl.duoc.resena.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/resenas")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Gestión de reseñas de productos para el sistema")
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    @Operation(summary = "Crear una reseña", description = "Registra una nueva calificación y comentario para un producto específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "La reseña fue creada exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ResenaResponse.class),
            examples = @ExampleObject(
                name = "Ejemplo de Reseña Creada (Neco-Arc)",
                value = """
                        {
                          "id": 101,
                          "productoId": 1,
                          "usuarioId": 5,
                          "calificacion": 5,
                          "comentario": "¡Me encanta el peluche de Neco-Arc! Llegó súper rápido y el diseño pixel art de la caja está genial. Burenyuu~",
                          "fechaCreacion": "2026-07-08T23:30:00"
                        }
                        """
            ))),
        @ApiResponse(responseCode = "400", description = "Error de validación en los datos de entrada",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ResenaResponse> guardar(@Valid @RequestBody ResenaRequest request) {
        log.info("POST /v1/resenas - Guardando reseña para el producto ID: {}", request.getProductoId());
        return new ResponseEntity<>(resenaService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener reseñas de un producto", description = "Busca y lista todas las reseñas asociadas al ID de un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseñas del producto listadas con éxito",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ResenaResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado o sin reseñas",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ResenaResponse>> obtener(@PathVariable Long productoId) {
        log.info("GET /v1/resenas/producto/{} - Obteniendo reseñas del producto", productoId);
        return ResponseEntity.ok(resenaService.listarPorProducto(productoId));
    }
}