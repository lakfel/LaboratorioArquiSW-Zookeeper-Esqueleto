/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.logic;

// import java classes
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

// import zookeeper classes
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;

//Esta clase realiza la conección con ZooKeeper y guarda una referencia.
public class ZooKeeperConnection {

    private ZooKeeper zoo;
    private CountDownLatch connectedSignal = new CountDownLatch(1);

    public ZooKeeperConnection() {
        connectedSignal = new CountDownLatch(1);
    }

    // Method to connect zookeeper ensemble.
    public void connect(String host) throws IOException, InterruptedException {

        zoo = new ZooKeeper(host, 5000, new Watcher() {
            public void process(WatchedEvent we) {

                if (null != we.getState()) switch (we.getState()) {
                    case SyncConnected:
                        connectedSignal.countDown();
                        break;
                    case ConnectedReadOnly:
                        System.out.println("Estado solo lectura");
                        break;
                    case AuthFailed:
                        System.out.println("Estado Autorización fallida");
                        break;
                    case Disconnected:
                        System.out.println("Estado Desconectado");
                        break;
                    case Expired:
                        System.out.println("Estado expiró la sesión");
                        break;
                    default:
                        break;
                }
                 
            }
        });

        connectedSignal.await();
    }

    // Method to disconnect from zookeeper server
    public void close() throws InterruptedException {
        zoo.close();
    }

    public ZooKeeper getZoo() {
        
        return zoo;
    }

    public CountDownLatch getConnectedSignal() {
        return connectedSignal;
    }
    
    
}
