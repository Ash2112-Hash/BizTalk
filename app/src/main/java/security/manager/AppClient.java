package security.manager;
import android.util.Log;

import com.example.messagingapp.models.Message;
import com.example.messagingapp.models.User;
import com.example.messagingapp.singleton.MainUser;
import com.example.messagingapp.utils.Firebase_CollectionFields;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

// KDC implemented from online documentation and learning tutorials

public class AppClient {
    String selfIP;
    String destinationIP;
    String userId;
    private SecretKey decryptionKey;
    private int port;
    private KDC kdc;
    private static final int KEYLENGTH = 16;
    private SecretKey sessionKey;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private BufferedReader receiver;
    private PrintWriter sender;
    private User user;
    public AtomicBoolean connected = new AtomicBoolean(false);
    public AppClient(String userId, String destinationIP, int port, KDC kdc, User user){
        this.destinationIP = destinationIP;
        this.kdc = kdc;
        try {
            this.decryptionKey = CryptoMethods.StringToSKey(user.getSecretKey());
        }
        catch(Exception e){
            Log.e("AppClient", "problem when creating decryptionKey");
        }
        this.port = port;
        this.userId = userId;
        this.user = MainUser.getUserData();
    }


    private String authentication(PrintWriter sender, BufferedReader receiver){
        String userKey = CryptoMethods.SKeyToString(this.decryptionKey);
        String message = userId +"/" +userKey;
        sender.println(message);
        try{
            String msg = null;
            while(msg == null) {
                msg = receiver.readLine();
            }
            if(msg.equals("INVALID")){
                return "INVALID";
            }
            System.out.println("decrypting sessionKey");
            System.out.println(msg.length());
            String sessionKeyMsg = CryptoMethods.decryption(msg, decryptionKey);
            System.out.println("return sessionKey");
            return sessionKeyMsg;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }

        return "INVALID";
    }

    private Message createMessage(String messageContents, String pairID) {
        Message m = new Message();
        m.setPairID(pairID);
        m.setMessage(messageContents);
        m.setDate(Timestamp.now());
        m.setSenderEmail(user.getEmail());
        return m;
    }
    public boolean sendMessage(Message msg, String pairID)throws Exception{
        String message = msg.getMessage();
        String temp = CryptoMethods.decryption(message, sessionKey);
        String decrptedMsg = CryptoMethods.decryption(temp, decryptionKey);
        Message msg2Send = createMessage(decrptedMsg, pairID);
        AtomicBoolean state = new AtomicBoolean(false);
        db.collection(Firebase_CollectionFields.ATTR_COLLECTION_MESSAGING)
                // generate a unique UUID for the document
                .document(UUID.randomUUID().toString().substring(0,20))
                .set(msg2Send)
                .addOnSuccessListener(aVoid -> {
                            Log.d("DEBUG_MESSAGING", "Message (pairID=" + pairID +
                                    ", contents="+msg2Send.getMessage()+") written to DB");
                            state.set(true);
                        }
                )
                .addOnFailureListener(aVoid ->
                        Log.d("DEBUG_MESSAGING", "Error writing message: "+msg2Send.getMessage()));
        return state.get();
    }

// TODO: the connection only support localhost only for now, in future it will be connecting to an actual server
    public void connect(){
        if(this.connected.get()){
            return;
        }
        new Thread(()->{
            PrintWriter sender;
            BufferedReader receiver;
            try {
                InetAddress host = InetAddress.getLocalHost();
                Log.d("network", host.getHostAddress());
                Socket socket = new Socket(host.getHostName(), port);
                System.out.println(socket.getInetAddress());
                while(!socket.isConnected()){
                    System.out.println("socket not connected");
                    try {
                        Thread.sleep(3000);
                    }
                    catch(InterruptedException ex){

                    }
                }
                receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                sender = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            } catch (IOException e) {
                throw new RuntimeException();
            }
            String sessionKeyMsg = this.authentication(sender, receiver);
            if(sessionKeyMsg.equals("INVALID")){
                throw new RuntimeException("authentication failed");
            }
            SecretKey sessionKey = CryptoMethods.StringToSKey(sessionKeyMsg);
            this.sessionKey = sessionKey;


            this.receiver = receiver;
            this.sender = sender;
            this.connected.set(true);
        }).start();


    }

}
