package cl.duoc.productos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.productos.dto.ProductoRequest;
import cl.duoc.productos.dto.ProductoResponse;
import cl.duoc.productos.dto.ProductoUpdateRequest;
import cl.duoc.productos.model.Producto;
import cl.duoc.productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/productos")
@Slf4j
@Tag(name = "Producto", description = "Operaciones relacionadas con el catálogo y microservicio de productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar catálogo", description = "Obtiene la lista completa de productos disponibles con su respectivo stock")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catálogo listado con éxito",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)),
            examples = @ExampleObject(
                name = "Catálogo de Ejemplo",
                value = """
                        [
                          {
                            "id": 1,
                            "nombrep": "Figura Neco-Arc Chaos (edicion gato pendejo)",
                            "descripcion": "Figura de acción articulada con accesorios de Melty Blood.",
                            "precio": 45.99,
                            "categoria": "Coleccionables",
                            "vendedorId": 101
                          },
                          {
                            "id": 2,
                            "nombrep": "Poster Holográfico Kara Zor-El",
                            "descripcion": "Poster edición limitada de Supergirl en acabo brillante de alta calidad.",
                            "precio": 15.50,
                            "categoria": "Decoración",
                            "vendedorId": 102
                          }
                        ]
                        """
            )))
    })
    public ResponseEntity<List<ProductoResponse>> listarProductos() {
        log.info("GET /v1/productos - Solicitando catálogo completo con stock");
        return ResponseEntity.ok(productoService.buscarProductos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Obtiene los detalles de un producto mediante su identificador numérico")
    public ProductoResponse buscarProductoPorId(@PathVariable Long id){
        log.info("GET /v1/productos/{}", id);
        return productoService.buscarProductoPorId(id);
    }

    @GetMapping("/nombrep/{nombrep}")
    @Operation(summary = "Buscar por nombre", description = "Busca un producto específico utilizando su nombre exacto o parcial")
    public ProductoResponse buscarProductoPorNombre(@PathVariable String nombrep){
        log.info("GET /v1/productos/nombrep/{}", nombrep);
        return productoService.buscarProductoPorNombre(nombrep);
    }

    @PostMapping
    @Operation(summary = "Crear un producto", description = "Añade un nuevo producto al catálogo del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "El producto fue añadido con éxito",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductoResponse.class),
            examples = @ExampleObject(
                name = "Ejemplo de Creación Exitosa",
                value = """
                        {
                          "id": 1,
                          "nombrep": "Figura Neco-Arc Chaos (edicion gato pendejo)",
                          "descripcion": "Figura de acción articulada con accesorios de Melty Blood.",
                          "precio": 45.99,
                          "categoria": "Coleccionables",
                          "vendedorId": 101
                        }
                        """
            )))
    })
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoRequest request){
        log.info("POST /v1/productos - Creando producto: {}", request.getNombrep());
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearProducto(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Modifica las propiedades de un producto existente por su ID")
    public ResponseEntity<ProductoResponse> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoUpdateRequest request){
        log.info("PUT /v1/productos/{}", id);
        return ResponseEntity.ok().body(productoService.actualizarProducto(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Remueve permanentemente un producto del catálogo por su ID")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id){       
        log.info("DELETE /v1/productos/{}", id);
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
