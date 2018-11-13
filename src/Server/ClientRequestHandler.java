package Server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author lxf736
 * @version 2018-03-10
 */

public class ClientRequestHandler extends Thread {

  private Server server;
  private ExecutorService threadPool;
  private ServerSocket serverSocket;
  private boolean interrupted;

  public ClientRequestHandler(Server server, ExecutorService threadPool,
      ServerSocket serverSocket) {
    this.server = server;
    this.threadPool = threadPool;
    this.serverSocket = serverSocket;
    this.interrupted = false;
  }

  public void run() {
    while (!interrupted) {
      System.out.println("waiting for client");
      Socket socketForClient = null;
      try {
        socketForClient = this.serverSocket.accept();
        threadPool.execute(new ClientThread(this.server, socketForClient));
      } catch (IOException e) {
        interrupted = true;
      }
    }
  }

}
