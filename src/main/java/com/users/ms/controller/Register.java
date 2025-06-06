/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.users.ms.controller;

import com.users.ms.dao.UserDao;
import com.users.ms.model.UserDto;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
@WebServlet(name = "Register", urlPatterns = {"/register"})
public class Register extends HttpServlet {
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject data;
        
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (JsonReader jsonReader = Json.createReader(request.getInputStream())) {
            data = jsonReader.readObject();
            
            String[] requiredFields = {"nombre", "email", "password", "tipoUsuario", "telefono"};
            
            validateFields(data, requiredFields);
               
            String nombre = data.getString("nombre");            
            String email = data.getString("email");
            String password = data.getString("password");
            String tipoUsuario = data.getString("tipoUsuario");
            String telefono = data.getString("telefono");
            
            UserDto userDto = new UserDto(nombre, email, password, tipoUsuario, telefono);
            new UserDao().createUser(userDto);
            
            data = Json.createObjectBuilder()
                    .add("message", "User created succesfully")
                    .build();
            
            try (PrintWriter out = response.getWriter()) {
                out.print(data.toString());
                out.flush();
            }
                
            
            
        } catch (Exception e) {
            handleError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Controller for user registration";
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
