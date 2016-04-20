/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.directorio;

import java.io.UnsupportedEncodingException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 *
 * @author Felipe
 */
public interface ZooKeeperMannagerInterface 
{
    
      public void createNode(String path, byte[] data) throws KeeperException,InterruptedException ;
      public Stat znode_exists(String path) throws KeeperException,InterruptedException;
       public String getData(String path) throws InterruptedException, KeeperException, UnsupportedEncodingException;
       public ZooKeeperConnection getZkConnection();
       public ZooKeeper getZk();
        public String getHost();
        public void inicializar(String host);
        
    
}
