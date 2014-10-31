import java.sql.*;
import java.util.Properties;
import java.io.*;
import java.util.*;

public class A3 {
	
/* Do not hard code this connection string. Your program must accept the connection string provided as input parameter (arg 0) to your program!
    private static final String CONNECTION_STRING ="jdbc:mysql://127.0.0.1/cs348?user=root&password=cs348&Database=cs348;";
  */  
    
    public static void main(String[] args) throws
                             ClassNotFoundException,SQLException,FileNotFoundException
    {
        // Get connection string and file name
	String CONNECTION_STRING =args[0];
	String INPUT_FILE =args[1];

        Connection con = DriverManager.getConnection(CONNECTION_STRING);
	Scanner sc = new Scanner(new File(INPUT_FILE));	
	while(sc.hasNext()){
		System.out.println(sc.next());
	}





 	sc.close();     
        con.close();
    }
    
    
}
    	

	


