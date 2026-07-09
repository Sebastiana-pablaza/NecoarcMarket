package cl.duoc.inventario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.inventario.dto.InventarioRequest;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.dto.ProductoDetalleDTO;
import cl.duoc.inventario.service.InventarioService;
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
@RequestMapping("/v1/inventario")
@Slf4j
@Tag(name = "Inventario", description = "Operaciones de control de stock, abastecimiento y validación de unidades")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping("/abastecer")
    @Operation(summary = "Abastecer stock", description = "Incrementa o inicializa la cantidad física de unidades de un producto en el inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock abastecido con éxito",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = InventarioResponse.class),
            examples = @ExampleObject(
                name = "Ejemplo de Stock Abastecido",
                value = """
                        {
                          "id": 1,
                          "productoId": 1,
                          "cantidad": 150,
                          "ultimaActualizacion": "2026-07-08T21:45:00"
                        }
                        """
            )))
    })
    public ResponseEntity<InventarioResponse> abastecer(@Valid @RequestBody InventarioRequest request) {
        // 🟢 AÑADIDO: Log que faltaba para registrar el abastecimiento en la consola de Docker
        log.info("POST /v1/inventario/abastecer - Abasteciendo stock. Producto ID: {}, Cantidad: +{}", request.getProductoId(), request.getCantidad());
        return ResponseEntity.ok(inventarioService.agregarStock(request.getProductoId(), request.getCantidad()));
    }

    @PutMapping("/producto/{productoId}/descontar")
    @Operation(summary = "Descontar stock", description = "Disminuye las unidades disponibles de un producto (utilizado tras confirmar una compra)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock descontado con éxito",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = InventarioResponse.class)))
    })
    public ResponseEntity<InventarioResponse> descontarStock(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        log.info("PUT /v1/inventario/producto/{}/descontar?cantidad={}", productoId, cantidad);
        InventarioResponse response = inventarioService.descontarStock(productoId, cantidad);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/producto/{id}")
    @Operation(summary = "Obtener stock plano", description = "Recupera la entidad de inventario directa asociada al ID del producto, sin datos cruzados")
    public ResponseEntity<InventarioResponse> obtenerStockPlano(@PathVariable Long id) {
        log.info("GET /v1/inventario/producto/{} - Solicitud de stock plano para Productos", id);
        InventarioResponse response = inventarioService.obtenerStockPorProducto(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/producto/{id}/detalle")
    @Operation(summary = "Obtener detalle completo", description = "Recupera el stock cruzando información con el Microservicio de Productos para traer el nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Información cruzada obtenida con éxito",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductoDetalleDTO.class),
            examples = @ExampleObject(
                name = "Ejemplo de Detalle Cruzado",
                value = """
                        {
                          "productoId": 2,
                          "nombre": "necoarc chaos",
                          "cantidad": 100
                        }
                        """
            )))
    })
    public ResponseEntity<ProductoDetalleDTO> obtenerDetalleCompleto(@PathVariable Long id) {
        log.info("GET /v1/inventario/producto/{}/detalle - Solicitud de información cruzada", id);
        ProductoDetalleDTO detalle = inventarioService.obtenerDetalleCompleto(id);
        return ResponseEntity.ok(detalle);
    }

    @DeleteMapping("/producto/{productoId}")
    @Operation(summary = "Eliminar registro de inventario", description = "Remueve de forma permanente el registro de control de stock asignado a un producto")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Long productoId) {
        log.info("DELETE /v1/inventario/producto/{}", productoId);
        inventarioService.eliminarInventarioPorProducto(productoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/producto/{productoId}/stock")
    @Operation(summary = "Validar stock suficiente", description = "Retorna un valor booleano indicando si el inventario posee las unidades solicitadas o más")
    public ResponseEntity<Boolean> tieneStock(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        log.info("GET /v1/inventario/producto/{}/stock?cantidad={} - Validando disponibilidad", productoId, cantidad);
        return ResponseEntity.ok(
                inventarioService.tieneStockSuficiente(productoId, cantidad));
    }
}
