/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entity.Evento;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Emilio
 */
@Stateless
@Path("entity.evento")
public class EventoFacadeREST extends AbstractFacade<Evento> {

    @PersistenceContext(unitName = "AgendaServerRestPU")
    private EntityManager em;

    public EventoFacadeREST() {
        super(Evento.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Evento entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Evento entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Evento find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Path("findAllEvents")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Evento> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Evento> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @GET
    @Path("findEventsBy/{fechaParsear}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Evento> buscarEventoPorFecha(String fechaParsear) throws ParseException, DatatypeConfigurationException{
        
        SimpleDateFormat formateoFecha = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha = formateoFecha.parse(fechaParsear);
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fecha);
        
        XMLGregorianCalendar fechaGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        
        Query q;
        
        q = em.createQuery("SELECT e FROM Evento e WHERE :fecha BETWEEN e.fechainicio AND e.fechafin ORDER BY e.fechafin");
        q.setParameter("fecha", fechaGregorianCalendar);
        return q.getResultList();
        
    }
    @GET
    @Path("findEventsBy/{CP}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
     public List<Evento> buscarEventoPorCP(Integer cp){
        Query q;
        
        q = em.createQuery("SELECT e FROM Evento e WHERE :cp = e.codigopostal");
        q.setParameter("cp", cp);
        return q.getResultList();
        
    }
    
    @GET
    @Path("findEventsByUsuario/{email}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Evento> EventosDeUsuario(String email){
        Query q;
        q = em.createQuery("SELECT e FROM Evento e WHERE :email=e.emailusuario");
        q.setParameter("email", email);
        
        return q.getResultList();
    }
    
    @GET
    @Path("findEventsByDate")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Evento> EventosNoCaducados() throws DatatypeConfigurationException{
        
        Date fechaActual = new Date(); 
        Query q;
        
        //Si falla puede ser que tengamos que formatear la fecha actual para que sea igual.
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fechaActual);
        
        XMLGregorianCalendar fechaGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        
        
        q = em.createQuery("SELECT e FROM Evento e Where e.fechafin >= :fechaActual");
        q.setParameter("fechaActual", fechaActual);
        
        return q.getResultList();
    
    }
     
    @GET
    @Path("findEventsNoVisibled")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
       public List<Evento> eventosVisibles(){
        Query q;
        LocalDate todayLocalDate = LocalDate.now( ZoneId.of( "UTC+01:00" ) );
        java.sql.Date today = java.sql.Date.valueOf(todayLocalDate);
        
        q = em.createQuery("SELECT e FROM Evento e WHERE e.fechafin >= :today AND e.estado = 1");
        q.setParameter("today", today);
        return q.getResultList();
        
    }
    @GET
    @Path("findEventsNoValidate")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Evento> EventosSinValidar(){
        Query q;
        q = em.createQuery("SELECT e FROM Evento e WHERE e.estado= :estado");
        q.setParameter("estado", 0);
        
        return q.getResultList();
    }
    @GET
    @Path("validate/{event}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void ValidarEvento(Evento evento){
        Query q;
        q = em.createQuery("UPDATE Evento SET estado = :estado WHERE id = :id");
        q.setParameter("estado", 1);
        q.setParameter("id", evento.getId()).executeUpdate();
    }
}
