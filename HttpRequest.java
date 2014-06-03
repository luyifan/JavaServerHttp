import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter; 
public class HttpRequest implements Runnable 
{
    public static final String GET_REQUEST_TYPE = "GET" ;
    public static final String POST_REQUEST_TYPE = "POST" ;
    private Socket connection ;
    private String requestLine ;
    private String requestType ;
    private String httpRequest ;
    private int port ;
    private PrintWriter out ;
    private Map < String , String > headers ;
    private Map < String , String > getData ;
    private Map < String , String > postData ;
    public HttpRequest ( Socket connection , int port ) throws IOException , SocketException , HttpException
    {
        setPort ( port ) ;
        connection.setKeepAlive ( true ) ;
        setConnection ( connection ) ;
        setHeaders( new HashMap< String , String >() ) ;
        setGetData ( new HashMap< String , String >() );
        setPostData ( new HashMap < String , String>() ) ;
    }
    @Override 
    public void run ( ) 
    {
       
        if ( getConnection().isClosed())
            System.out.println ( "Socket is closed...") ;
        try
        {
            parseRequest ( ) ;
            HttpResponse resp = new HttpResponse ( this ) ;
            resp.respond() ;
        }
        catch (IOException e ) {
             e.printStackTrace();
        }
        catch (HttpException e )
        {
            e.printStackTrace();
        }
    }
    public void parseRequest() throws IOException , SocketException , HttpException 
    {
	   


        BufferedReader input = new BufferedReader ( new InputStreamReader ( getConnection().getInputStream() ) ) ;
        StringBuilder requestBuilder = new StringBuilder () ;
        String firstLine = input.readLine ( ) ;
        if ( firstLine == null ) 
            throw new HttpException ( "Input is returning null..." ) ;

        while ( firstLine.isEmpty() )
            firstLine = input.readLine();
        setRequestLine ( firstLine ) ;
        requestBuilder.append ( getRequestLine() ) ;
        requestBuilder.append ( "\n" ) ;
        for ( String line = input.readLine() ; line != null && !line.isEmpty() ; line = input.readLine() )
        {
            requestBuilder.append (line);
            requestBuilder.append ("\n" ) ;
            String [] items = line.split(": " ) ;
            if ( items.length == 1 ) 
                throw new HttpException ( "No key value pair in \n\t" + line ) ;
            String value = items [ 1 ] ;
            for ( int i = 2 ; i < items.length ; i++ ) 
                value += ": " + items [ i ] ;
            getHeaders().put(items [ 0 ] , value ) ;
        }
        
         if ( getRequestType().equals( POST_REQUEST_TYPE ) && getHeaders().containsKey("Content-Length"))
        {
            requestBuilder.append("\n");
            int contentLength = Integer.parseInt ( getHeaders().get("Content-Length"));
            StringBuilder b = new StringBuilder();
            for ( int i = 0 ; i < contentLength; i++ ) 
                b.append((char)input.read());
            requestBuilder.append ( b.toString() );
            String [] data = b.toString().split("&");
            getPostData().putAll( parseIntputData(data));
        }
        setHttpRequest ( requestBuilder.toString() );
    }

    private Map < String , String > parseIntputData ( String [] data )
    {
        Map<String, String > out = new HashMap<String , String> () ;
        for ( String item : data )
        {
            if ( item.indexOf("=")==-1 )
            {
                out.put( item , null ) ;
                continue ;
            }
            String value = item.substring(item.indexOf("=") + 1 ) ;
            try
            {
                value = URLDecoder.decode ( value , "UTF-8" ) ;
            }
            catch ( UnsupportedEncodingException e ) 
            {
                e.printStackTrace ( ) ;
            }
            //System.out.println ( item.substring( 0 , item.indexOf("="))) ;
            //System.out.println ( value ) ;
            out.put ( item.substring( 0 , item.indexOf("=")) , value ) ;

        }
        return out ;
    }
    public void setHeaders( Map<String , String> headers )
    {
        this.headers = headers ;
    }
    public Map<String,String> getHeaders()
    {
        return headers ;
    }
    public void setGetData ( Map < String , String > data )
    {
        this.getData = data ;
    }
    public Map< String , String > getGetData()
    {
        return getData ;
    }
    public void setPostData ( Map< String , String > data )
    {
        this.postData = data ;
    }
    public Map< String , String > getPostData()
    {
        return postData ;
    }
    public void setPort ( int port ) 
    {
        this.port = port ;
    }
    public int getPort ( ) 
    {   
        return port ;
    }
    public void setRequestLine ( String line ) throws HttpException
    {
        this.requestLine = line ;
        String [] splitty = requestLine.trim().split(" " );
        if ( splitty.length != 3 ) 
            throw new HttpException ("Request line has a number of spaces other than 3." ) ;
        
        if ( splitty[0].equalsIgnoreCase(GET_REQUEST_TYPE )) 
        {
            setRequestType( GET_REQUEST_TYPE ) ;
        }
        else
        if ( splitty [0].equalsIgnoreCase(POST_REQUEST_TYPE ))
        {
            setRequestType( POST_REQUEST_TYPE ) ;
        }
        else
        {
            throw new HttpException("Unexpected request type:" + splitty[0]);
        }
    }
    public String getRequestLine ( ) 
    {
        return requestLine ;
    }
    public void  setHttpRequest ( String httpRequest  ) 
    {
        this.httpRequest = httpRequest ;
    }   
    public String getHttpRequest ( ) 
    {
        return httpRequest ;
    }
    public void setRequestType ( String requestType )
    {
        this.requestType = requestType ;
    }
    public String getRequestType ( ) 
    {
        return requestType ;
    }
    public void setConnection ( Socket connection )
    { 
        this.connection = connection ;
    }
    public Socket getConnection ( ) 
    {
        return connection ;
    }

}
