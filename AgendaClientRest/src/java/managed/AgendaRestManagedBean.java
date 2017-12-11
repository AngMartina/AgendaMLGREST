/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managed;

import client.ClienteEventos;
import client.ClienteUsuarios;
import entity.Evento;
import entity.Usuarios;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Angela
 */
@Named(value = "agendaRestManagedBean")
@SessionScoped
public class AgendaRestManagedBean implements Serializable {
    
    private Usuarios usuarioSeleccionado;
    private Evento eventoSeleccionado;
  
    private List<Evento> listaEventos;
    
    private List<String> palabrasClave;
    private List<String> preferencias;
    private String mensajeError;
    private int ordenacion;
    private int busqueda;
    private boolean modificar;
    private Date fechaOrdenacion;
    private int distanciaKm;
    
    
    
    
    @PostConstruct
    public void init(){
        usuarioSeleccionado = new Usuarios();
        eventoSeleccionado = new Evento();
        modificar = false;
        
    }
    public AgendaRestManagedBean() {
    }
    
    public Evento getEventoSeleccionado(){
        return eventoSeleccionado;
    }
    
    public Usuarios getUsuarioSeleccionado(){
        return usuarioSeleccionado;
    }

    public List<String> getPalabrasClave() {
        return palabrasClave;
    }

    public void setPalabrasClave(List<String> palabrasClave) {
        this.palabrasClave = palabrasClave;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public int getOrdenacion() {
        return ordenacion;
    }

    public void setOrdenacion(int ordenacion) {
        this.ordenacion = ordenacion;
    }

    public List<String> getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(List<String> preferencias) {
        this.preferencias = preferencias;
    }

    public List<Evento> getListaEventos() {
        return listaEventos;
    }

    public void setListaEventos(List<Evento> listaEventos) {
        this.listaEventos = listaEventos;
    }

    public int getBusquedad() {
        return busqueda;
    }

    public void setBusquedad(int busquedad) {
        this.busqueda = busquedad;
    }

    public Date getFechaOrdenacion() {
        return fechaOrdenacion;
    }

    public void setFechaOrdenacion(Date fechaOrdenacion) {
        this.fechaOrdenacion = fechaOrdenacion;
    }

    public int getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(int distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
    
    
    public List<Evento> listarEventos(){
        
        ClienteEventos clienteEvento = new ClienteEventos();
       
        
        if(usuarioSeleccionado == null || usuarioSeleccionado.getTipoUsuario() == 2 ||usuarioSeleccionado.getTipoUsuario() == 1){ // No es Periodista
            Response r = clienteEvento.eventosVisibles_XML(Response.class);
            
            if(r.getStatus()==200){
                GenericType<List<Evento>> genericType = new GenericType<List<Evento>>(){};
                List<Evento> todosEventos= r.readEntity(genericType);    
            return todosEventos;  
            
            }
        }else{ // Es periodista
            Response r = clienteEvento.findAll_XML(Response.class);
            
            if(r.getStatus()==200){
                GenericType<List<Evento>> genericType = new GenericType<List<Evento>>(){};
                List<Evento> todosEventos= r.readEntity(genericType);    
            return todosEventos; 
            }
        }
        
        return null;
    }
    
    public String seleccionUsuario(int aux){
        ClienteUsuarios clienteUsuario = new ClienteUsuarios();
        String emailUsuario = "";
        
        switch (aux) {
            case 0:
                //Si es anónimo no necesitamos tenerlo seleccionado;
                usuarioSeleccionado = null;
                break;
            case 1:
                // Usuario
                emailUsuario = "afernandez@gmail.com";
                break;
            case 2:
                // SuperUsuario
                emailUsuario = "mariag@uma.es";
                break;
            case 3:
                // Periodista
                emailUsuario = "juanlopez@yahoo.es";
                break;
            default:
                break;
        }
        
        //Si lo ha recibido coge al usuario correspondiente.
        Response r = clienteUsuario.findByEmail_XML(Response.class, emailUsuario);
            if(r.getStatus() == 200){
                GenericType<Usuarios> genericType = new GenericType<Usuarios>(){};
                usuarioSeleccionado = r.readEntity(genericType);
            }
            
        listaEventos = listarEventos();
        this.ordenacion = 2;
        this.ordenar();
        this.ordenacion = 0;
        return "listaEventos";
    }
    
    public String salir(){
        
        this.usuarioSeleccionado = null;
        this.ordenacion = 0;
        
        return "index";
    }
    
    public String crearEvento(){
        return "crearEvento";
    }
    
    public String subirEvento(){
        
        
        if(eventoSeleccionado.getFechainicio().after(eventoSeleccionado.getFechafin())){
            this.setMensajeError("La fecha de fin debe ser posterior a la fecha de inicio");
        }else{
            ClienteEventos clienteEvento = new ClienteEventos();
            this.setMensajeError("");
            
            String eventoPalabrasClave ="";

            for(String cadena: palabrasClave){
                eventoPalabrasClave += cadena;
                eventoPalabrasClave +=";";
            }

            eventoSeleccionado.setEmailusuario(usuarioSeleccionado);
            eventoSeleccionado.setPalabrasclave(eventoPalabrasClave);

            if(usuarioSeleccionado.getTipoUsuario() == 1){ //Usuario
                eventoSeleccionado.setEstado(0);
            }else { //Periodista/SuperUsuario
                eventoSeleccionado.setEstado(1);
            }
            
            if(modificar){
                clienteEvento.edit_XML(eventoSeleccionado, eventoSeleccionado.getId().toString());
            }else{
                clienteEvento.create_XML(eventoSeleccionado);
            }
            

            eventoSeleccionado = null;
            palabrasClave = null;

            return "listaEventos";
        }
        
        return "null";
        
    }
    
    public String eliminarEvento(Evento evento){
        
        ClienteEventos clienteEvento = new ClienteEventos();
        
        clienteEvento.remove(evento.getId().toString());
        
        return "null";
    }
    
    
    public String modificarEvento(Evento evento){
        eventoSeleccionado = evento;
        
        String[] palabrasClaveEvento = eventoSeleccionado.getPalabrasclave().split(";");
        palabrasClave = Arrays.asList(palabrasClaveEvento);
        
        modificar = true;
        return "crearEvento";     
    }
    public String verEventosSinValidar(){
        
        return "listaEventosSinValidar";
    }
    public List<Evento> validarEventos(){
        
        ClienteEventos clienteEventos = new ClienteEventos();
        
        Response r = clienteEventos.EventosSinValidar_XML(Response.class);
        
        if(r.getStatus() == 200){
            GenericType<List<Evento>> genericType = new GenericType<List<Evento>>(){};
            List<Evento> todosEventosSinValidar= r.readEntity(genericType);
            return todosEventosSinValidar;
        }
        
        return null;
    }
    
    public String volver(){
        eventoSeleccionado = null;
        palabrasClave = null;
        preferencias = null;
        return "listaEventos";
    }
    
    public String validarEvento(Evento evento){
        
        evento.setEstado(1);
        
        ClienteUsuarios clienteUsuario = new ClienteUsuarios();
        ClienteEventos clienteEvento = new ClienteEventos();
        clienteEvento.edit_XML(evento, evento.getId().toString());
        
        Usuarios usuarioCreador = new Usuarios();
        
        Response r = clienteUsuario.findByEmail_XML(Response.class, evento.getEmailusuario().getEmail());
        if(r.getStatus() == 200){
                GenericType<Usuarios> genericType = new GenericType<Usuarios>(){};
                usuarioCreador = r.readEntity(genericType);
        }
        
         //Actualizo al cliente con su respectiva notificación.
         String notificacion = "Se ha ACEPTADO su evento "+evento.getDescripcion()+".";
         usuarioCreador.setNotificaciones(usuarioCreador.getNotificaciones().concat("; " + notificacion));
         
         clienteUsuario.edit_XML(usuarioCreador, usuarioCreador.getId().toString());
         
        return "null";
    }
    
    public String noValidarEvento(Evento evento){
        
        ClienteUsuarios clienteUsuario = new ClienteUsuarios();
        Usuarios usuarioCreador = new Usuarios();
        
        Response r = clienteUsuario.findByEmail_XML(Response.class, evento.getEmailusuario().getEmail());
        if(r.getStatus() == 200){
                GenericType<Usuarios> genericType = new GenericType<Usuarios>(){};
                usuarioCreador = r.readEntity(genericType);
        }
        
         //Actualizo al cliente con su respectiva notificación.
         String notificacion = "Se ha RECHAZADO su evento "+evento.getDescripcion()+".";
         usuarioCreador.setNotificaciones(usuarioCreador.getNotificaciones().concat("; " + notificacion));
         
         clienteUsuario.edit_XML(usuarioCreador, usuarioCreador.getId().toString());
         
         this.eliminarEvento(evento);
        
         return "null";
    }
    
    public String verPerfil(){
        return "verPerfil";
    }
    
    public List<Evento> obtenerEventosUsuario(Usuarios usuario){
        
        ClienteEventos clienteEventos = new ClienteEventos();
        
        Response r = clienteEventos.EventosDeUsuario_XML(Response.class, usuario.getId().toString());
        if(r.getStatus() == 200){
            GenericType<List<Evento>> genericType = new GenericType<List<Evento>>(){};
                List<Evento> todosEventos= r.readEntity(genericType);  
                return todosEventos;
        }
        
        return null;
    }
    
    public String verEvento(Evento evento){
        eventoSeleccionado = evento;
        
        return "verEvento";
    }
    
    public String editarPerfil(){
        
        String[] preferenciasUsuario = usuarioSeleccionado.getPreferencias().split(";");
        preferencias = Arrays.asList(preferenciasUsuario);
        
        return "editarPerfil";
    }
    
    public String actualizarUsuario(){
       
        ClienteUsuarios clienteUsuario = new ClienteUsuarios();
           
        String preferenciasCadena ="";

        for(String cadena: preferencias){
            preferenciasCadena += cadena;
            preferenciasCadena +=";";
        }

        usuarioSeleccionado.setPreferencias(preferenciasCadena);
        
        clienteUsuario.edit_XML(usuarioSeleccionado, usuarioSeleccionado.getId().toString());
        
        return "null";
    }
    
    public String borrarUsuario(){
        
        ClienteUsuarios clienteUsuarios = new ClienteUsuarios();
        clienteUsuarios.remove(usuarioSeleccionado.getId().toString());
        
        usuarioSeleccionado = null;
        
        return "index";
    }
     public void ordenar(){
        
        if(this.ordenacion == 1){
            Collections.sort(this.listaEventos, new Comparator<Evento>() {
            @Override
            public int compare(Evento o1, Evento o2) {
                return Double.compare(o1.getPrecio(), o2.getPrecio());
            }
            });
            
        } else if(this.ordenacion == 2){
            Collections.sort(this.listaEventos, new Comparator<Evento>() {
            @Override
            public int compare(Evento o1, Evento o2) {
                return o1.getFechainicio().compareTo(o2.getFechainicio());
            }
            });
        } else if(this.ordenacion == 3){
            Collections.sort(this.listaEventos, new Comparator<Evento>() {
            @Override
            public int compare(Evento o1, Evento o2) {
                return o1.getLocalizacion().compareTo(o2.getLocalizacion());
            }
            });
        } else if(this.ordenacion == 4){
            
             listaEventos = listarEventos();
        }
        
        
        
    }
    
      public void buscarPor(){
        ClienteEventos clienteEvento = new ClienteEventos();
        List<Evento> listaResultante = new ArrayList<>();
        if(0!=this.busqueda)
            switch (this.busqueda) {
           
            case 1: //Por fecha
                if(fechaOrdenacion!=null){
                    Response r = clienteEvento.buscarEventoPorFecha_XML(Response.class, fechaOrdenacion.toString());
                    if(r.getStatus() == 200){
                        GenericType<List<Evento>> genericType = new GenericType<List<Evento>>(){};
                            listaResultante = r.readEntity(genericType);
                    }
                    
                    for(Evento evento: listaResultante){
                        if(this.usuarioSeleccionado.getTipoUsuario() != 3){
                            if(evento.getEstado() != 0){
                                listaEventos.add(evento);
                            }
                        }else{
                            listaEventos.add(evento);
                        }
                    }
                    busqueda = 0;
                }
                   
                break;
            case 2: //Por codigoPostal
                //ANGELA TRABAJARÁ AQUI
                break;
                
           
            case 3:
                 List<Evento> listaPreferencias = listaEventosPreferencia(usuarioSeleccionado);
                listaEventos = listaPreferencias;
                listaPreferencias.forEach((e) -> {
                    if(this.usuarioSeleccionado.getTipoUsuario()!=3){
                        if(e.getEstado()!=0){
                            listaEventos.add(e);
                        }
                    }else{
                        listaEventos.add(e);
                    } }
                );
                busqueda = 0;
                
                break;
           
        }
    } 
    public List<Evento> listaEventosPreferencia(Usuarios usuario){
        List<Evento> listaEventosAux = new ArrayList<>();
        boolean eventoAñadido;
        String[] preferencias = usuario.getPreferencias().split(";");
        
        for(Evento evento: listaEventosAux){
            String[] palabrasClave = evento.getPalabrasclave().split(";");
            eventoAñadido = false;
            
            for(int i = 0; i < preferencias.length-1 && !eventoAñadido; i++){
                for(int j = 0; j < palabrasClave.length-1 && !eventoAñadido; j++){
                    if(palabrasClave[j].equals(preferencias[i])){
                        listaEventos.add(evento);
                        eventoAñadido = true;
                    }
                }
            }
        }
        
        return listaEventos;
    }
    

    
    
    
}
