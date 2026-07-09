package cl.duoc.notificacion.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.notificacion.dto.NotificacionRequest;
import cl.duoc.notificacion.dto.NotificacionResponse;
import cl.duoc.notificacion.model.Notificacion;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificacionMapper {
    public Notificacion fromRequest(NotificacionRequest request){
        return Notificacion.builder()
            .usuarioId(request.getUsuarioId())
            .mensaje(request.getMensaje())
            .tipo(request.getTipo())
            .fechaEnvio(java.time.LocalDateTime.now())
            .build();
    }
    public NotificacionResponse toResponse(Notificacion notificacion, String nombre){
        return NotificacionResponse.builder()
                .id(notificacion.getId())
                .usuarioId(notificacion.getUsuarioId())
                .nombreUsuario(nombre)
                .mensaje(notificacion.getMensaje())
                .tipo(notificacion.getTipo())
                .fechaEnvio(notificacion.getFechaEnvio() != null ? notificacion.getFechaEnvio().toString() : null)
                .build();
    }


}