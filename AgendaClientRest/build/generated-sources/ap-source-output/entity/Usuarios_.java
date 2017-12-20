package entity;

import entity.Evento;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-20T12:50:24")
@StaticMetamodel(Usuarios.class)
public class Usuarios_ { 

    public static volatile SingularAttribute<Usuarios, String> apellidos;
    public static volatile SingularAttribute<Usuarios, String> preferencias;
    public static volatile SingularAttribute<Usuarios, String> notificaciones;
    public static volatile SingularAttribute<Usuarios, String> contrasenna;
    public static volatile ListAttribute<Usuarios, Evento> eventoList;
    public static volatile SingularAttribute<Usuarios, Integer> tipoUsuario;
    public static volatile SingularAttribute<Usuarios, Integer> id;
    public static volatile SingularAttribute<Usuarios, String> nombre;
    public static volatile SingularAttribute<Usuarios, String> email;

}