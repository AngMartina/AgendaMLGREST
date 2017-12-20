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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
  
    private List<String> palabrasClave;
    private String mensajeError;
    private int ordenacion;
    private boolean modificar;
    private String email;
    
    
    
    
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    
    public String googleSignIn(){
        ClienteUsuarios clienteUsuario = new ClienteUsuarios();
        Response r = clienteUsuario.findByEmail_XML(Response.class, email);
            if(r.getStatus() == 200){
                GenericType<Usuarios> genericType = new GenericType<Usuarios>(){};
                usuarioSeleccionado = r.readEntity(genericType);
            }
        return "listaEventos";
    }
    
    public String googleSignOut(){
        ClienteUsuarios clienteUsuario = null;
        email = "";
        return "index";
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
        
        return "listaEventos";
    }
    
    public String salir(){
        usuarioSeleccionado = null;
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

    
    
    
}
