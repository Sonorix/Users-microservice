/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.users.ms.dao;

import com.users.ms.model.Database;
import com.users.ms.model.UserDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;


public class UserDao {
    public void createUser(UserDto user) {
        String sql = "INSERT INTO users (nombre, email, contraseña, tipo_usuario, telefono) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try {
            
            
            if ( verifyEmailExistence(user.getEmail()) ) throw new Exception("Email already registred");
            
            
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            
            user.setPassword(hashedPassword);
            
            Connection conn = new Database().getConn();
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, user.getNombre());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getTipoUsuario());
            stmt.setString(5, user.getTelefono());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new Exception("Database insert operation did not affect any rows");
            }
                
            stmt.close();
            conn.close();
            
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }
    
    public UserDto getUserByEmail(String email) {
        String sql = "SELECT nombre, email, contraseña, tipo_usuario, telefono FROM users WHERE email = ? LIMIT 1";
        
        try {
            Connection conn = new Database().getConn();
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                UserDto user = new UserDto(
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("contraseña"),
                    rs.getString("tipo_usuario"),
                    rs.getString("telefono")
                );
                
                rs.close();
                stmt.close();
                conn.close();
                
                return user;
            }
            
            rs.close();
            stmt.close();
            conn.close();
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user: " + e.getMessage(), e);
        }
    }
    
    private boolean verifyEmailExistence(String email) throws Exception {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1"; 
        
        try {
            Connection conn = new Database().getConn();
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next();
            
        } catch (Exception e) {
            throw new RuntimeException("Unable to verify email");
        }
    }
}
