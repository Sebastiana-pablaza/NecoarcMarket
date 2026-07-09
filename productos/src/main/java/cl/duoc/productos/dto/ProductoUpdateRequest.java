package cl.duoc.productos.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoUpdateRequest {

    @Schema(description = "Nombre del producto actualizado", example = "necoarc chaos")
    @Size(min = 2, max = 150, message = "Los nombres deben tener entre 2 y 100 caracteres")
    private String nombrep;

    @Schema(description = "descripcion actualizada del producto", example = "figura de necoarc chaos")
    @Size(min = 10, max = 100)
    private String descripcion;

    @Schema(description = "Valor actualizado del producto", example = "400.99")
    @DecimalMin(value = "0.0",inclusive = false )
    private BigDecimal precio;

    @Schema(description = "Categoría actualizada del producto(puede quedar vacía)", example = "peluche")
    private String categoria;

    
    private Boolean activo = true;

    @Schema(description = "Id de vendedor actualizado", example = "2")
    private Long vendedorId;



}
