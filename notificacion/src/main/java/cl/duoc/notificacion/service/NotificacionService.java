package cl.duoc.notificacion.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.notificacion.client.UsuarioClient;
import cl.duoc.notificacion.dto.NotificacionRequest;
import cl.duoc.notificacion.dto.NotificacionResponse;
import cl.duoc.notificacion.dto.UsuarioResponse;
import cl.duoc.notificacion.mapper.NotificacionMapper;
import cl.duoc.notificacion.model.Notificacion;
import cl.duoc.notificacion.repository.NotificacionRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificacionService {

    @Autowired
    private UsuarioClient usuarioClient;
    
    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private NotificacionMapper notificacionMapper;


    public List<NotificacionResponse> listarPorUsuario(Long id) {
        log.info("Consultando historial de notificaciones para el usuario ID: {}", id);      
        UsuarioResponse user = usuarioClient.obtenerDatosUsuario(id);
        String nombreCompleto = (user != null) ? user.getNombre() + " " + user.getAppaterno() : "Usuario Desconocido";
        
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(id);
        
        List<NotificacionResponse> respuestas = new ArrayList<>();
        
        for (Notificacion n : notificaciones) {
            NotificacionResponse dto = notificacionMapper.toResponse(n, nombreCompleto);
            respuestas.add(dto);
        }
        return respuestas;
    }

    public void crear(NotificacionRequest request) {
        log.info("Iniciando creación de notificación para usuario ID: {}", request.getUsuarioId());
        UsuarioResponse user = usuarioClient.obtenerDatosUsuario(request.getUsuarioId());
        Notificacion n = new Notificacion();
        n.setUsuarioId(request.getUsuarioId());
        n.setTipo(request.getTipo());
        n.setFechaEnvio(LocalDateTime.now());
        if(user != null) {
            String saludo = "Hola " + user.getNombre() + " " + user.getAppaterno();
            n.setMensaje(saludo + ": " + request.getMensaje());
            log.info("Notificación personalizada creada con éxito para usuario ID (owo): {}", request.getUsuarioId());
        } else {
            log.warn("Usuario ID {} no encontrado. hay que poner una notificación (owo).", request.getUsuarioId());
            n.setMensaje("Aviso para Usuario ID " + request.getUsuarioId() + ": " + request.getMensaje());
        }
        notificacionRepository.save(n);
    }
    
    /*public List<NotificacionResponse> getByUsuario(Long usuarioId) {
    String nombre = usuarioClient.getNombreById(usuarioId); 
    return notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(usuarioId).stream()
            .map(n -> notificacionMapper.toResponse(n, nombre)) 
            .collect(Collectors.toList()); 
    }*/
}