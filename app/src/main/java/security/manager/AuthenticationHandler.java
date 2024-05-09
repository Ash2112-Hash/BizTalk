package security.manager;

public class AuthenticationHandler extends Thread{
    public boolean authenticated = false;
    public AuthenticationHandler(String userId){

    }
    @Override
    public void run(){
        //here is the authentication process
        //after this thread join, the authentication is done, the result will be used by main thread
        //if true, a socket connection will be established, user from our size can send message over socket,
        //user also must be able to save their chatlog
        //the main thread then if successfull, let KDC distribute a session key (or at login)
        //when user press save message, it will be sent with encryption
        //need to add IP address of the other end of the messaging or im fucked
    }

}
