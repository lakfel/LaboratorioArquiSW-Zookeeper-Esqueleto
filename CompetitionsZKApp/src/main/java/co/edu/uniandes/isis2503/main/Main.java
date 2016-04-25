/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.main;

import co.edu.uniandes.isis2503.persistence.PersistenceManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 *
 * This class launches the web application in an embedded Jetty container. This
 * is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 *
 * @author Felipe
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp/";
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8082";
        }
        Server server = new Server(Integer.valueOf(webPort));
        WebAppContext root = new WebAppContext();
        root.setContextPath("/");
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);
        PersistenceManager.getInstance().getEntityManagerFactory();
        root.setParentLoaderPriority(true);
        server.setHandler(root);
        server.start();
        server.join();
    }

}
