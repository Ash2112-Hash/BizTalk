package security.manager;

import android.util.Log;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

//this is for testing the socket connections and security features only
public class AuthenticationServer extends Thread {

    private String selfIp;
    private KDC kdc;
    public static final int[] ports = {1234, 4567, 8888, 9999, 9876};

    public boolean serverStart = false;

    public AuthenticationServer( KDC kdc){

        //will use different port later
        System.out.println("AuthenticationServer currently only supports one port in queue");
        try {
            this.selfIp = Inet4Address.getLocalHost().getHostAddress();
        }
        catch (Exception e){
            Log.e("Security", "Problem obtaining host IP");
        }
        System.out.println("Server IP:"+selfIp);
        ;
        this.kdc = kdc;

    }
    public boolean validateUser(String user_Id){
        return true;
    }
    private void serve(Socket socket, PrintWriter sender, BufferedReader receiver){
        try {
            String msg = receiver.readLine();
            String[] message = msg.split("/");
            if(!validateUser(message[0])){
                sender.println("INVALID");
                System.out.println("Server rejected such connection");
            }
            else{
                SecretKey encryptionKey = CryptoMethods.StringToSKey(message[1]); // find user key

                SecretKey sessionKey = kdc.getSessionKey(); // get user session key
                String sessionKey_msg = CryptoMethods.SKeyToString(sessionKey); //
                //encrypt user session key with user key

                String msgToSent = CryptoMethods.encryption(sessionKey_msg, encryptionKey);;
                sender.println(msgToSent);
                System.out.println("Server send user session Key length:" + msgToSent.length());

            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        try {
            socket.close();
        }
        catch(Exception exp){
            System.out.println("unable to close socket");
        }
    }
    @Override
    public void run() {
        this.serverStart = true;
// due to the time constraint, not going to implement recycling of the ports, too much work on threads
        for (int port : ports) {
            try {
                ServerSocket socket;
                socket = new ServerSocket(port);
                while (true) {

                    PrintWriter sender;
                    BufferedReader receiver;

                    try {
                        System.out.println("waiting for connection");
                        Socket clientSocket = socket.accept();
                        System.out.println("connected accepted, IP: " + clientSocket.getInetAddress());
                        receiver = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        sender = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                        new Thread(() -> {
                            serve(clientSocket, sender, receiver);
                        }).start();

                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }

                }
            } catch (Exception exp) {
                System.out.println(exp.getMessage());
                this.interrupt();
            }
        }
    }


}
