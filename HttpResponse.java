import java.io.PrintWriter;
import java.io.IOException ;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket ;
import java.util.HashMap ;
import java.util.Map ;
import java.net.ServerSocket ;
import java.net.SocketException ;
public class HttpResponse 
{

    public static final String [] content = { "HTTP/1.1 200 OK\nContent-Type: text/html; charset=ISO-8859-1\nContent-Length: 237\n\n<html>\n<head>\n<title>Site 0, port 80</title>\n</head>\n<body>\n<p>Hello! Welcome to the first website presented by Leisure~</p>\n<form method=\"post\">\n<input type=\"submit\" name=\"site 1\" value=\"click here to 1nd site\"/>\n</form>\n</body>\n</html>" , "HTTP/1.1 200 OK\nContent-Type: text/html; charset=ISO-8859-1\nContent-Length: 237\n\n<html>\n<head>\n<title>Site 1, port 81</title>\n</head>\n<body>\n<p>Hello! Welcome to the first website presented by Leisure~</p>\n<form method=\"post\">\n<input type=\"submit\" name=\"site 0\" value=\"click here to 0nd site\"/>\n</form>\n</body>\n</html>" } ;
    public static final String EXCEPTION_MESSAGE="an exception occured while processing request" ;
    public static final String STATUS_GOOD ="All system are go well" ;
    public static final String GET_REQUEST_TYPE = "GET" ;
    public static final String POST_REQUEST_TYPE = "POST" ;
    
    private static Map < Integer , String > response ;
    private int momentSite ;
    private HttpRequest request ;

    private Map<String , String > headers ;
    
    private Socket socket ;
    private PrintWriter writer ;

    public HttpResponse ( HttpRequest req ) throws IOException 
    {
        socket = req.getConnection ( ) ;
        writer = new PrintWriter ( socket.getOutputStream() , true ) ;
        request = req ;
        
        momentSite = request.getPort()%2;
        
        headers = new HashMap< String , String > () ;

    }

    public void respond ( )
    {
        try{
            if ( getSocket() == null ) 
                throw new HttpException ( "Socket is null ..." ) ;
            else if ( getSocket().isClosed())
                throw new HttpException ( "Socket is closed..." ) ;
            //System.out.println ( "here server send message ") ;
            //System.out.println ( request.getRequestType() );
            if ( request.getRequestType().equals( GET_REQUEST_TYPE ))
            {
                writer.println ( content [ momentSite ] ) ;
                writer.flush() ;
            }
            else
            if ( request.getRequestType().equals( POST_REQUEST_TYPE ))
            {
                //System.out.println ( request.getPostData( ) );
                String value = request.getPostData().get("site+"+String.valueOf(momentSite));
                if ( value == null )
                {
                    int otherPort ;
                    //from other server ;
                    if ( momentSite == 0 ) 
                        otherPort = request.getPort() + 1 ;
                    else
                        otherPort = request.getPort() - 1 ;
                
                    Socket otherSocket =  new Socket("127.0.0.1", otherPort);  
                    //System.out.println ( otherSocket ) ;
                    //System.out.println ( otherPort );   
                    PrintWriter otherwriter = new PrintWriter ( otherSocket.getOutputStream() , true ) ;
                    otherwriter.println (  request.getHttpRequest()  );
                    otherwriter.flush ( ) ;
                    BufferedReader otherIn = new BufferedReader(new InputStreamReader(otherSocket.getInputStream()));  
                    StringBuilder receiverContent = new StringBuilder();
                    String line ;
                    while ( (line = otherIn.readLine()) != null )
                    {    

                        receiverContent.append ( line ) ;
                        receiverContent.append ( "\n" ) ;
                        //System.out.println ( line ) ;
                        if ( line.equals("</html>")) break ;
                    }
                    writer.println ( receiverContent.toString() ) ;
                    writer.flush() ;

                }
                else 
                {
                    //to other server ;
                    writer.println( content [ momentSite ] ) ;
                    writer.flush() ;
                }
            }
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
            System.err.println ( "Something bad happened while try to send data to the client" ) ;
            e.printStackTrace ( ) ;
        }
       // catch ( IOException e )
       // { 
        //    System.err.println ( "Something bad happened while try to send data to the client" ) ;
         //   e.printStackTrace ( ) ;
        //}
        finally
        {
            try {
            //    getWriter().close ( ) ;
            }
            catch ( NullPointerException e )
            {
                e.printStackTrace ( ) ;
            }
         //   catch ( IOException e ) 
          //  {
            //    e.printStackTrace ( ) ;
           // }
        }
    }
    public void writeLine ( String line ) throws IOException 
    {
        getWriter().println(line ) ;
        getWriter().flush();
    }
    public Map<String , String > getHeaders ( )
    {
        return headers ;
    }
    public String getHeader ( String key )
    {
        return headers.get ( key ) ;
    }
    public void setHeaders (Map<String , String> headers )
    {
        this.headers = headers ;
    }
    public void setHeader ( String key , String value )
    {
        this.headers.put ( key , value ) ;
    }
    public HttpRequest getRequest ()
    {
        return request ;
    }
    public Socket getSocket ( )
    {
        return socket ;
    }
    public PrintWriter getWriter ( ) 
    {
        return writer ;
    }

}
