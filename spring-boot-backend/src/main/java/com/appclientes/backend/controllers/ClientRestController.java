package com.appclientes.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appclientes.backend.models.entity.UserAPI;
import com.appclientes.backend.models.service.IUserServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.appclientes.backend.models.entity.Cliente;
import com.appclientes.backend.models.service.IClienteServ;

//@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClientRestController {

    @Autowired
    private IClienteServ clienteServ;

    @Autowired
    private IUserServ userServ;

    @GetMapping("/clientes")
    public List<Cliente> index(){
        return clienteServ.findAll();

    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        Cliente cliente = null;
        Map<String, Object> response = new HashMap<>();

        try {
            cliente = clienteServ.findById(id);

        }catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la BBDD");
            response.put("error", e.getMessage().concat(" ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }



        if(cliente == null) {
            response.put("mensaje", "El cliente con ID:".concat(id.toString()).concat(" no existe en la Base de datos"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
    }


    @PostMapping("/new_user")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody UserAPI userAPI){
        UserAPI userNew = null;

        Map<String, Object> response = new HashMap<>();

        try {
            userAPI.setPassword(passwordEncoder().encode(userAPI.getPassword()));
            userNew = userServ.save(userAPI);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la BBDD");
            response.put("error", e.getMessage().concat(" ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje","El usuario ha sido creado con exito!");
        response.put("cliente", userNew);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create (@RequestBody Cliente cliente) {
        Cliente clienteNew = null;
        Map<String, Object> response = new HashMap<>();

        try {
            clienteNew = clienteServ.save(cliente);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la BBDD");
            response.put("error", e.getMessage().concat(" ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje","El cliente ha sido creado con exito!");
        response.put("cliente", clienteNew);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Cliente cliente) {

        Cliente clienteActual = clienteServ.findById(id);
        Cliente clienteUpdated = null;

        Map<String, Object> response = new HashMap<>();

        if(clienteActual == null) {
            response.put("mensaje", "El cliente con ID:".concat(id.toString()).concat(" no se pudo editar"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

        }

        try {
            clienteActual.setNombre(cliente.getNombre());
            clienteActual.setApellido(cliente.getApellido());
            clienteActual.setEmail(cliente.getEmail());

            clienteUpdated = clienteServ.save(clienteActual);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el cliente en la BBDD");
            response.put("error", e.getMessage().concat(" ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje","El cliente ha sido actualizado con exito!");
        response.put("cliente", clienteUpdated);


        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            clienteServ.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el cliente en la BBDD");
            response.put("error", e.getMessage().concat(" ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje","El cliente ha sido eliminado con exito!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
