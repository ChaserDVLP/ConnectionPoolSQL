
package com.mycompany.ad_connectionpools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolDataSource;

//CONNECTION POOLS
    /*Si las conexiones no se liberan correctamente pueden hacer que la aplicacion se cuelgue,
    para asegurar las conexiones de forma eficiente se usa un Pool de Conexiones para mejorar
    la eficiencia en una aplicación distribuida*/

//PREPARED STATEMENT
    /*Las Prepared Statement nos protegen de las inyecciones de SQL al parametrizar la consulta y además
    su uso mejora el rendimiento entre un 20% y 30% a nivel de BBDD.*/

public class AD_ConnectionPools {
    
    static final String DB_URL = "jdbc:mysql://localhost:3306/jcvd";
    static final String USER = "admin";
    static final String PASS = "1234";
    
    //Creamos un pool de conexiones
    static PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
    static PreparedStatement sentencia = null;
    static Connection conn = null;
    
    //Método Insertar con Pool
    static public void insertar(String nombre, String genero, String fechaLanzamiento, 
            String compañia, float precio) throws SQLException {
        
        //Si el id es autogenerado debemos pasar Null como primer valor
        String query = "INSERT INTO videojuegos VALUES (null,?,?,?,?,?);";
        
        try {
            conn = pds.getConnection();
            sentencia = conn.prepareStatement(query);
            
            sentencia.setString(1, nombre);
            sentencia.setString(2, genero);
            sentencia.setString(3, fechaLanzamiento);
            sentencia.setString(4, compañia);
            sentencia.setFloat(5, precio);
            
            if (sentencia.executeUpdate() > 0) { //Devuelve la cantidad de filas afectadas
                System.out.println("Se ha insertado el registro.");
            } else {
                System.out.println("Fallo al insertar");
            }
            
        } catch (SQLException e) {
            System.out.println("Error al conectar la BBDD: "+e);
            e.printStackTrace();
            
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
    }
    
    //Método eliminar con Pool 
    static public void eliminarRegistro(String nombre) throws SQLException {
        
        String query = "DELETE FROM videojuegos WHERE Nombre = ?;";
        
        try {
            conn = pds.getConnection();
            sentencia = conn.prepareStatement(query);
            
            sentencia.setString(1, nombre);
            
            if (sentencia.executeUpdate() > 0) { 
                System.out.println("Se ha eliminado el registro");
                
            } else {
                System.out.println("fallo al eliminar");
            }
            
        } catch (SQLException e) {
            System.out.println("Error al conectar la BBDD: "+e);
            e.printStackTrace();
        
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
        
    }
    
    //Buscar un registro pasado como parametro
    static public void buscarPorNombre(String nombreBuscado) throws SQLException {
        
        String query = "SELECT * FROM videojuegos WHERE Nombre = ? ";
        
        try {
            conn = pds.getConnection();
            sentencia = conn.prepareStatement(query);
            sentencia.setString(1, nombreBuscado);
            
            //Al ser una consulta necesitamos el ResultSet
            ResultSet rs = sentencia.executeQuery(); //No le pasamos query porque lo estamos pasando antes
            
            while (rs.next()) {                
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Nombre: " +rs.getString("Nombre"));
                System.out.println("Genero: " + rs.getString("Genero"));
                System.out.println("Fecha Lanzamiento: " + rs.getDate("FechaLanzamiento"));
                System.out.println("Compañía: " + rs.getString("Compañia"));
                System.out.println("Precio: " + rs.getFloat("Precio")+"\n");
            }
            
        } catch (SQLException e) {
            System.out.println("Error al conectar la BBDD: "+e);
            e.printStackTrace();
            
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
        
    }

    public static void main(String[] args) {
        
        //Controlamos la excepción que se genera al cerrar la conexión
        try {
            
            //Lo declaramos solo una vez en el main, no en todos los métodos
            pds.setConnectionFactoryClassName( "com.mysql.cj.jdbc.Driver");
            pds.setURL(DB_URL);
            pds.setUser(USER);
            pds.setPassword(PASS);
            
            insertar("prueba", "test", "2010/10/10", "test", 50);
            eliminarRegistro("prueba");
            buscarPorNombre("Valorant");
        
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión");
            e.printStackTrace();
        }
    }
}
