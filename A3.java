import java.sql.*;
import java.util.Properties;
import java.io.*;
import java.util.*;

// ver 0.1

public class A3 {
    
/* Do not hard code this connection string. Your program must accept the connection string provided as input parameter (arg 0) to your program!
    private static final String CONNECTION_STRING ="jdbc:mysql://127.0.0.1/cs348?user=root&password=cs348&Database=cs348;";
  */ 
    
public static void GETV(int id, int row, int col, Connection con)
throws SQLException {

    // step 1: select from tabel MATRIX of matrix id, row_dim, col_dim
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    stmt1.setInt(1, id); 
    ResultSet getMatrix = stmt1.executeQuery();

    int row_dim = 0;
    int col_dim = 0;
    boolean exist_matrix = false;
    while (getMatrix.next()) { // if the inquiry matrix existed:
        row_dim = getMatrix.getInt(2); // get the second component of ResultSet 
        col_dim = getMatrix.getInt(3);
        exist_matrix = true;
    }

    // step 2: if inquiry matrix exists, and row and col is valid, then select 
    // corresponding value from table MATRIX_DATA
    if ((exist_matrix == true) && (row <= row_dim) && (col <= col_dim)) {
        String query2 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ? AND " + 
            "ROW_NUM = ? AND COL_NUM = ?";
        PreparedStatement stmt2 = con.prepareStatement(query2);
        stmt2.setInt(1, id);  
        stmt2.setInt(2, row);
        stmt2.setInt(3, col);
        ResultSet getValue = stmt2.executeQuery();
        int value = getValue.getInt(4); // get the forth component of ResultSet 
        System.out.println(value);
    }
};

public static void SETV(int id, int row, int col, int val, Connection con) 
throws SQLException {
    // step 1: select from tabel MATRIX of matrix id, row_dim, col_dim
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    stmt1.setInt(1, id); 
    ResultSet getMatrix = stmt1.executeQuery();

    int row_dim = 0;
    int col_dim = 0;
    boolean exist_matrix = false;
    while (getMatrix.next()) { // if the inquiry matrix existed:
        row_dim = getMatrix.getInt(2);
        col_dim = getMatrix.getInt(3);
        exist_matrix = true;
    }

    // step 2: if inquiry matrix exists, and row and col is valid, then modify 
    // corresponding value from table MATRIX_DATA
    if ((exist_matrix == true) && (row <= row_dim) && (col <= col_dim)) {
        String query2 = "UPDATE MATRIX_DATA " + 
            "SET VALUE = ? " +
            "WHERE MATRIX_ID = ? AND ROW_NUM = ? AND COL_NUM = ?";
        PreparedStatement stmt2 = con.prepareStatement(query2);
        stmt2.setInt(1, val);  
        stmt2.setInt(2, id);  
        stmt2.setInt(3, row);  
        stmt2.setInt(4, col);  
        stmt2.executeUpdate(); // no return value for executeUpdate()
    }

};

public static void DELETEALL(Connection con) {
    // delete all records from table MATRIX, delete all records from table MATRIX_DATA
    String query1 = "DELETE * FROM MATRIX;"
    String query2 = "DELETE * FROM MATRIX_DATA;"
    PreparedStatement stmt1 = con.prepareStatement(query1);
    PreparedStatement stmt2 = con.prepareStatement(query2);
    stmt1.executeUpdate();
    stmt2.executeUpdate();

};

public static void DELETE(int id, Connection con) {
    // delete indicated record from MATRIX, delete corresponding records from table MATRIX_DATA
    String query1 = "DELETE * FROM MATRIX WHERE MATRIX_ID = ?;"
    String query2 = "DELETE * FROM MATRIX_DATA WHERE MATRIX_ID = ?;"
    PreparedStatement stmt1 = con.prepareStatement(query1);
    PreparedStatement stmt2 = con.prepareStatement(query2);
    stmt1.setInt(1, id);
    stmt2.setInt(1, id);
    stmt1.executeUpdate();
    stmt2.executeUpdate();
}

public static void SETM(int id, int row_dim, int col_dim, Connection con) 
throws SQLException {
//System.out.println("stem");
    
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    stmt1.setInt(1, id); 
    ResultSet checkExist = stmt1.executeQuery();
    if (checkExist.next()) {
        // if the matrix exist: check if we can modify it safely: 
        row_d = checkExist.getInt(2);
        col_d = checkExist.getInt(3);
        if ((row_dim >= row_d) && (col_dim >= col_d)) {
            String query2 = "UPDATE MATRIX " + 
                    "SET ROW_DIM = ? AND COL_DIM = ? " +
                    "WHERE MATRIX_ID = ?";
            PreparedStatement stmt2 = con.prepareStatement(query2);
            stmt2.setInt(1, row_dim);  
            stmt2.setInt(2, col_dim);  
            stmt2.setInt(3, id);  
            stmt2.executeUpdate(); 
        } else if {
            // check if we can shrink matrix without losing information: 
            // TO BE CONT: 
        }
    } else {  // if the matrix doesn't exist: add new matrix into table MATRIX:
        String query = "INSERT INTO MATRIX VALUES (?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        stmt.setInt(2, row_dim);
        stmt.setInt(3, col_dim);
        stmt.executeUpdate();  
    }
};

public static void ADD(int id1, int id2, int id3, Connection con) throws SQLException {
    String query1 = "DELETE * FROM MATRIX WHERE MATRIX_ID = ?;"
    String query2 = "DELETE * FROM MATRIX_DATA WHERE MATRIX_ID = ?;"
    PreparedStatement stmt1 = con.prepareStatement(query1);
    PreparedStatement stmt2 = con.prepareStatement(query2);
    stmt1.setInt(1, id);
    stmt2.setInt(1, id);
    stmt1.executeUpdate();
    stmt2.executeUpdate();
};

public static void main(String[] args) throws                                       
ClassNotFoundException, SQLException, FileNotFoundException, NoSuchElementException
{
    // Get connection string and file name
    String CONNECTION_STRING =args[0];
    String INPUT_FILE =args[1];
//System.out.println("main_begin");
    Connection con = DriverManager.getConnection(CONNECTION_STRING);
    Scanner sc = new Scanner(new File(INPUT_FILE)); 
    while(sc.hasNext()){
        String cmd = sc.next();
//System.out.println("scan");
System.out.println(cmd);
        if (cmd.equals("GETV")) {
            String sid = sc.next();
            String srow = sc.next();
            String scol = sc.next();
            int id = Integer.parseInt(sid);
            int row = Integer.parseInt(srow);
            int col = Integer.parseInt(scol);           
            GETV(id, row, col, con);
            
        } else if (cmd.equals("SETV")) {
            String sid = sc.next();
            String srow = sc.next();
            String scol = sc.next();
            String sval = sc.next();
            int id = Integer.parseInt(sid);
            int row = Integer.parseInt(srow);
            int col = Integer.parseInt(scol);
            int val = Integer.parseInt(sval);           
            SETV(id, row, col, val, con);
            
        } else if (cmd.equals("SETM")) {
//System.out.println("setm_out");
        // sc.next() == "SETM") {
            String sid = sc.next();
            String srow_dim = sc.next();
            String scol_dim = sc.next();
            int id = Integer.parseInt(sid);
            int row_dim = Integer.parseInt(srow_dim);
            int col_dim = Integer.parseInt(scol_dim);           
            SETM(id, row_dim, col_dim, con);
//System.out.println("setm_out_end");           
        } else if (cmd.equals("DELETE")) {
        // only work for ID version;
            String sid = sc.next();
            if (sid.equals("ALL")) {
                DELETEALL(con);
            } else if {
                int id = Integer.parseInt(sid);
                DELETE(id, con);
            }
            int id = Integer.parseInt(sid);
            
        } else if (cmd.equals("ADD")) {
            String sid1 = sc.next();
            String sid2 = sc.next();
            String sid3 = sc.next();
            int id1 = Integer.parseInt(sid1);
            int id2 = Integer.parseInt(sid2);
            int id3 = Integer.parseInt(sid3);

            ADD(id1, id2, id3, con);

        } else if (cmd.equals("SUB")) {

        } else if (cmd.equals("MULT")) {

        } else if (cmd.equals("TRANSPOSE")) {

        };
    }

System.out.println("scan_end");   
    sc.close();     
    con.close();
    }
     
}
        

    


