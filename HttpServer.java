import java.io.IOException ;
import java.net.InetSocketAddress ;
import java.net.ServerSocket ;
import java.net.Socket ;
import java.net.SocketException ;
import java.io.PrintWriter; 
public class HttpServer {
    public static final int defaultPort = 80 ;
    private ServerSocket socket = null ;
    public int port ;
    public HttpServer(){
        this( defaultPort ) ;
    }
    
    public HttpServer ( int port ){
        setPort ( port );
    }
    public void setPort ( int port ){
        this.port = port ;
    }
    public int getPort(){
        return port ;
    }

    private void run ( String [] argv ) 
    {   
        try {

            socket = new ServerSocket ( ) ;
            System.out.println ( "Starting HTTPServer at http://127:0:0:1:" + getPort ( ) ) ;
            socket.setReuseAddress ( true ) ;
            int count = 0 ;
            socket.bind ( new InetSocketAddress ( getPort ( ) ) ) ;
            while ( true ) 
            {
                Socket connection = null ;
                try 
                {
                    connection = socket.accept() ;
                    
		    HttpRequest request = new HttpRequest ( connection , getPort ( )  ) ;
                    count ++ ;
                    //System.out.println ( count ) ;
                    Thread t = new Thread ( request );
                    t.start ( ) ;
                }
                catch ( SocketException e ) 
                {
                    System.err.println ( "Client broke connection early!" ) ;
                    e.printStackTrace ( ) ;
                }
                catch ( IOException e  )
                {   
                    System.err.println ( "IOException. Probably an HTTPRequest issue." ) ;
                    e.printStackTrace ( ) ;
                }
                catch ( HttpException e )
                {
                    System.err.println("HttpException.");
                    e.printStackTrace( ) ;
                }
                catch ( Exception e ) 
                {
                    System.err.println ( "Generic Exception !" ) ;
                    e.printStackTrace ( );
                    break;
                }
            }
        }
        catch ( Exception e ) 
        {
            System.err.println ( "Something bad happened..." ) ;
            e.printStackTrace ( ) ;
        }
        finally 
        {
            try {
                socket.close ( ) ;
            }
            catch ( IOException e ) 
            {
               System.err.println ( "Well that's not good..." ) ;
               e.printStackTrace ( ) ;
            }
        }
    
    }
    public static void main (String [] argv ) 
    {
        int port = Integer.parseInt ( argv [ 0 ] ) ;
        HttpServer httpServer = new HttpServer ( port ) ;
        httpServer.run( argv ) ;
    }
}
