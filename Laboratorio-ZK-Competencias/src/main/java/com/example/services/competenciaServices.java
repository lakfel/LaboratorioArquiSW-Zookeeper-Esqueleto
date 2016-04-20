/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.main.Main;
import com.example.main.PersistenceManager;
import com.example.models.Competencia;
import com.example.models.CompetitorDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Felipe
 */
@Path("/competencias")
@Produces(MediaType.APPLICATION_JSON)
public class competenciaServices 
{
    @PersistenceContext(unitName = "CompetenciasPU")
    EntityManager entityManager;
    
    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() 
    {
        Query q = entityManager.createQuery("select u from Competencia u order by u.name ASC"); 
        List<Competencia> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
       
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createContest(Competencia competencia) {
        JSONObject rta = new JSONObject();
        Competencia competenciaTmp = new Competencia();
        competenciaTmp.setName(competencia.getName());
        competenciaTmp.setCity(competencia.getCity());
        competenciaTmp.setCountry(competencia.getCountry());
        competenciaTmp.setPrize(competencia.getPrize());
        competenciaTmp.setYear(competencia.getYear());
        competenciaTmp.setWinnerId(competencia.getWinnerId());
     
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(competenciaTmp);
            entityManager.getTransaction().commit();
            entityManager.refresh(competenciaTmp);
            rta.put("competencia_id", competenciaTmp.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            competenciaTmp = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    } 

    
}
