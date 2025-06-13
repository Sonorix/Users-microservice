/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.users.ms.controller;

import com.users.ms.dao.UserDao;
import com.users.ms.model.UserDto;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Administrador
 */
@WebServlet(name = "Login", urlPatterns = {"/login"})
public class Login extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // Este método no se utiliza para la API REST
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // No sirve
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject data;
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (JsonReader jsonReader = Json.createReader(request.getInputStream())) {
            data = jsonReader.readObject();
            
            String[] requiredFields = {"email", "password"};
            
            validateFields(data, requiredFields);
               
            String email = data.getString("email");            
            String password = data.getString("password");
            
            // Obtener usuario por email
            UserDto user = new UserDao().getUserByEmail(email);
            
            // Verificar si el usuario existe
            if (user == null) {
                throw new Exception("Invalid email or password");
            }
            
            // Verificar contraseña
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw new Exception("Invalid email or password");
            }
            
            // Construir respuesta exitosa - sin incluir la contraseña en la respuesta
            JsonObject userResponse = Json.createObjectBuilder()
                    .add("nombre", user.getNombre())
                    .add("email", user.getEmail())
                    .add("tipoUsuario", user.getTipoUsuario())
                    .add("telefono", user.getTelefono())
                    .build();
                    
            data = Json.createObjectBuilder()
                    .add("message", "Login successful")
                    .add("user", userResponse)
                    .build();
            
            try (PrintWriter out = response.getWriter()) {
                out.print(data.toString());
                out.flush();
            }
                
        } catch (Exception e) {
            handleError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Controller for user login";
    }
    
    private void validateFields(JsonObject data, String[] requiredFields) {
        
        List<String> missing = new ArrayList<>();
        
        for (String field : requiredFields) {
            if (!data.containsKey(field)) {
                missing.add(field);
                continue;
            }
            
            if (data.getString(field).isEmpty())
                missing.add(field);
        }
        
        if (!missing.isEmpty()) throw new IllegalArgumentException("Some required fields are missing or are empty: " + String.join(", ", missing));
    }
    
    private void handleError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        
        JsonObject comeback = Json.createObjectBuilder()
                .add("message", message)
                .build();
        
        try (PrintWriter out = response.getWriter()) {
            out.print(comeback.toString());
            out.flush();
        }
         
    }
}
