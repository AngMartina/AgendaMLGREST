package entity;

import entity.Usuarios;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-04T12:32:27")
@StaticMetamodel(Evento.class)
public class Evento_ { 

    public static volatile SingularAttribute<Evento, Date> fechainicio;
    public static volatile SingularAttribute<Evento, String> descripcion;
    public static volatile SingularAttribute<Evento, Double> precio;
    public static volatile SingularAttribute<Evento, Integer> estado;
    public static volatile SingularAttribute<Evento, String> palabrasclave;
    public static volatile SingularAttribute<Evento, String> localizacion;
    public static volatile SingularAttribute<Evento, Date> fechafin;
    public static volatile SingularAttribute<Evento, Integer> id;
    public static volatile SingularAttribute<Evento, Usuarios> emailusuario;
    public static volatile SingularAttribute<Evento, String> url;
    public static volatile SingularAttribute<Evento, Integer> codigopostal;

}