/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managed;

import client.ClienteEvento;
import entity.Evento;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Angela
 */
@Named(value = "agendaRestManagedBean")
@SessionScoped
public class AgendaRestManagedBean implements Serializable {

    /**
     * Creates a new instance of AgendaRestManagedBean
     */
    public AgendaRestManagedBean() {
    }
    
    public List<Evento> listarEventos(){
        ClienteEvento clienteE = new ClienteEvento();
        Response r = clienteE.findAll_XML(Response.class);
        if(r.getStatus()==200){
            GenericType<List<Evento>> genericType = new GenericType<List<Evento>>(){};
            List<Evento> todosEventos= r.readEntity(genericType);    
            return todosEventos;        
        }
        return null;
    }
    
}
