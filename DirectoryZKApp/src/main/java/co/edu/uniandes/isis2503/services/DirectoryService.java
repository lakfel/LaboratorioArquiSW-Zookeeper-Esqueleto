/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.services;


import co.edu.uniandes.isis2503.logic.ZooKeeperMannager;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.zookeeper.data.Stat;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Felipe
 */
@Path("/directorio")
@Produces(MediaType.APPLICATION_JSON)
public class DirectoryService 
{
    
    ZooKeeperMannager zooKeeperMannager;
    
    @PostConstruct
    public void init() 
    {
        try 
        {    
            zooKeeperMannager = ZooKeeperMannager.getInstance();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    @POST
    @Path("/estadoPagina")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoPagina(JSONObject datos)
    {
        JSONObject rta = new JSONObject();
        try
        {
            String aplicacion = datos.getString("nombrePagina");
            String newData = datos.getString("nuevoEstado");
            if(zooKeeperMannager.setData("/"+aplicacion+ZooKeeperMannager.PATH_ESTADO, newData.getBytes()))
                rta.put("_estado", "Estado actualizado");
            else
                rta.put("_estado", "Error: No fue posible actualizar el estado");
        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }
    
    
    @POST
    @Path("/servicioPagina")
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarServicio(JSONObject datos)
    {
        JSONObject rta = new JSONObject();
        try
        {
            String nombrePagina = datos.getString("nombrePagina");
            String nombreServicio = datos.getString("nombreServicio");
            String rutaServicio = datos.getString("rutaServicio");
            zooKeeperMannager.createNode("/"+nombrePagina+ZooKeeperMannager.PATH_SERVICIOS+"/"+nombreServicio, rutaServicio.getBytes());
            rta.put("_estado", "Servicio creado");
            
        }
        catch(Exception e)
        {
            try
            {
                rta.put("_estado", "No se creó el servició");
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }
                    
            e.printStackTrace();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }
    
    @POST
    @Path("/eliminarServicio")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarServicio(JSONObject datos)
    {
        JSONObject rta = new JSONObject();
        try
        {
            String nombrePagina = datos.getString("nombrePagina");
            String nombreServicio = datos.getString("nombreServicio");
            String rutaServicio = datos.getString("rutaServicio");
            zooKeeperMannager.deleteNode("/"+nombreServicio+ZooKeeperMannager.PATH_SERVICIOS+"/"+nombreServicio);
            rta.put("_estado", "Servicio eliminado");
            
        }
        catch(Exception e)
        {
            try
            {
                rta.put("_estado", "No se eliminó el servicio");
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }
                    
            e.printStackTrace();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }
    
    @GET
    @Path("/servicios/{path}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response darServicios(@PathParam("path")String path)
    {
        JSONObject rta = new JSONObject();
        try
        {
            List<String> servicios = zooKeeperMannager.getChildren("/"+path+ZooKeeperMannager.PATH_SERVICIOS);
            for (int i = 0; i < servicios.size(); i++) 
            {
                rta.put("-servicio_"+i, servicios.get(i));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }
    
    @GET
    @Path("/urlPagina/{nombrePagina}/{servicio}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarURL (@PathParam("nombrePagina") String nombrePagina, @PathParam("servicio") String servicio)
    {
        JSONObject rta = new JSONObject();
        String url = "";
        boolean existePagina = false;
        try
        {
            url = new String(zooKeeperMannager.getData("/"+nombrePagina));
            existePagina = true;
        }
        catch(Exception e)
        {
            try
            {    
                rta.put("_estado", "La pagna no existe");
                
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }
        }
        
        if(existePagina)
        {
            try
            {
                rta.put("_estadoPagina",new String(zooKeeperMannager.getData("/"+nombrePagina+ZooKeeperMannager.PATH_ESTADO)));
                url = new String(zooKeeperMannager.getData("/"+nombrePagina)) +  "/" + new String(zooKeeperMannager.getData("/"+nombrePagina+ZooKeeperMannager.PATH_SERVICIOS+"/"+servicio));
                rta.put("_url",url);
            }
            catch(Exception e)
            {
                try
                {    
                   // rta.put("_estado", "La pagna existe, pero el servicio no se encuentra");
                    rta.put("_url", url);

                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }
    
}
