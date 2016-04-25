/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.logic;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
/**
 *
 * @author Felipe
 */

public class ZooKeeperMannager 
{
    private  ZooKeeperConnection zkConnection;
    private  ZooKeeper zk;
    private  String host;

    
    public final static String PATH_ESTADO = "/Estado";
    public final static String PATH_SERVICIOS = "/Servicios";
    
    private static ZooKeeperMannager instance;
    
    public static ZooKeeperMannager getInstance()
    {
        System.out.println("Requiriendo instancia...");
        if(instance == null)
        {
            System.out.println("Instancia null.... Creandola");
            instance = new ZooKeeperMannager("localhost");
        }
        System.out.println("Retornando instancia");
        return instance;
    }
    
    public ZooKeeperMannager(String host) 
    {
        System.out.println("Creando instancia al host " + host);
        try
        {
            
            this.host = host;
            System.out.println("Creando conexión ...");
            zkConnection = new ZooKeeperConnection();
            System.out.println("Conectando al host");
            zkConnection.connect(this.host);
            System.out.println("READY!!!");
            zk = zkConnection.getZoo();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    public void inicializar(String host)
    {
        try
        {
            this.host = host;
            zkConnection = new ZooKeeperConnection();
            zkConnection.connect(this.host);
            zk = zkConnection.getZoo();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    } 
           
  
    
    public List<String> getChildren(String path) throws KeeperException,InterruptedException 
    {
        List<String> children = new ArrayList<String>();
        if(znode_exists(path)!=null)
        {
            children = zk.getChildren(path, false);
        }
        return children;
    }
    
    //Devuelve un elemento no nulo si el nodo existe.
    //Puede obtenerse información del Stat
    public Stat znode_exists(String path) throws
      KeeperException,InterruptedException {
      return zk.exists(path, true);
   }
    
    
    public String getData(String path2) throws InterruptedException, KeeperException, UnsupportedEncodingException
    {
        final String path =path2;
        Stat stat = znode_exists(path);
        
        byte[] b = null;	
        if(stat != null) 
        {
            b = zk.getData(path, new Watcher() 
            {		
                public void process(WatchedEvent we) 
                {			
                    if (we.getType() == Event.EventType.None) 
                    {
                        switch(we.getState()) 
                        {
                            case Expired:
                            zkConnection.getConnectedSignal().countDown();
                            break;
                        }
							
                    }
                    else 
                    {
                        try
                        {
                            byte[] bn = zk.getData(path,
                            false, null);
                            String data = new String(bn,
                            "UTF-8");
                            System.out.println(data);
                            zkConnection.getConnectedSignal().countDown();

                        }
                        catch(Exception ex) 
                        {
                            ex.printStackTrace();
                        }
                    }
                }
            }, null);
				
        String data = new String(b, "UTF-8");
        //System.out.println(data);
        zkConnection.getConnectedSignal().await();
				
        } 
        else 
        {
            System.out.println("Node does not exists");
        }
        return new String(b);
    }
    

    public boolean setData(String path, byte[] newData)
    {
        boolean loLogro=true;
        try 
        {
            zk.setData(path, newData, znode_exists(path).getVersion());
        }
        catch (Exception e) 
        {
            loLogro = false;
        }
        
        return loLogro;
        
    }
    
    public ZooKeeperConnection getZkConnection() {
        return zkConnection;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public String getHost() {
        return host;
    }
    
    public void cerrarConeccion()
    {
        try 
        {
            zkConnection.close();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    /*public static void main(String[] args)
    {
        ZooKeeperMannager man = new ZooKeeperMannager("localhost");
        try 
        {
            
            System.out.println(man.obtenerInformación("/znodeServ1").toString());
            System.out.println(man.obtenerInformación("/znodeServ2").toString());
            man.zkConnection.close();
        }
        catch (Exception e) 
        {
            try
            {
            man.zkConnection.close();
            }catch(Exception e1)
            {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }*/
    
}
