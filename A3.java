import java.sql.*;
import java.util.Properties;
import java.io.*;
import java.util.*;

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
    if (getMatrix.next()) { // if the inquiry matrix existed:
        row_dim = getMatrix.getInt(2); // get the second component of ResultSet 
        col_dim = getMatrix.getInt(3);
        exist_matrix = true;
    } else {
        // System.out.println("Error: the Matrix doesn't exist.");
        System.out.println("ERROR");
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
        float value = 0;
        if (getValue.next()) {
            // if data exist: 
            value = getValue.getFloat(4); // get the forth component of ResultSet 
        }
        System.out.println(value);
    } else {
        // System.out.println("Error: entry index is out of range.");
        System.out.println("ERROR");
    }
};


public static void SETV(int id, int row, int col, float val, Connection con) 
throws SQLException {
    // step 1: select from tabel MATRIX of matrix id, row_dim, col_dim
    // check if the matrix we want to modify exists and if the entry we want to 
    // modify is within the range of the matrix
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    stmt1.setInt(1, id); 
    ResultSet getMatrix = stmt1.executeQuery();

    int row_dim = 0;
    int col_dim = 0;
    boolean exist_matrix = false;
    if (getMatrix.next()) { // if the inquiry matrix existed:
        row_dim = getMatrix.getInt(2);
        col_dim = getMatrix.getInt(3);
        exist_matrix = true;
    } else {
        // System.out.println("Error: The matrix you want to modify doesn't exist."); 
        System.out.println("ERROR"); 
    }
    
    // step 2: if inquiry matrix exists, and row and col is valid, then modify 
    // corresponding value from table MATRIX_DATA
    if (exist_matrix && (row <= row_dim) && (col <= col_dim)) {
        // Step 2.1: decide if the entry we want to set is already exist:
        String query2 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ? AND " + 
            "ROW_NUM = ? AND COL_NUM = ?";
        PreparedStatement stmt2 = con.prepareStatement(query2);
        stmt2.setInt(1, id);  
        stmt2.setInt(2, row);  
        stmt2.setInt(3, col);  
        ResultSet checkExist = stmt2.executeQuery();
        if (checkExist.next()) {
            // S2.2: if the corresponding entry of the matrix exist, then we modify it
            if (val == 0) {
                // if we set new value to 0, we simply delete the old record: 
                //      To DO: check if the matrix still has any value, if there is
                //          no value remains, then delete the matrix.     
                query2 = "DELETE * FROM MATRIX_DATA WHERE MATRIX_ID = ? AND " + 
                "ROW_NUM = ? AND COL_NUM = ?";
                stmt2 = con.prepareStatement(query2);
                stmt2.setInt(1, id);  
                stmt2.setInt(2, row);  
                stmt2.setInt(3, col);  
                stmt2.executeUpdate();
            } else {
                // otherwise modify the old record:
                String query3 = "UPDATE MATRIX_DATA " + 
                "SET VALUE = ? " +
                "WHERE (MATRIX_ID = ?) AND (ROW_NUM = ?) AND (COL_NUM = ?)";
                PreparedStatement stmt3 = con.prepareStatement(query3);
                stmt3.setFloat(1, val);  
                stmt3.setInt(2, id);  
                stmt3.setInt(3, row);  
                stmt3.setInt(4, col);  
                stmt3.executeUpdate(); // no return value for executeUpdate()
            }
        } else {
            if (val != 0) {
                // if the new value is not 0:
                // S2.3: otherwise we need to insert a new record for that entry of matrix
                String query4 = "INSERT INTO MATRIX_DATA VALUES (?, ?, ?, ?);";
                PreparedStatement stmt4 = con.prepareStatement(query4);
                stmt4.setInt(1, id);  
                stmt4.setInt(2, row);  
                stmt4.setInt(3, col);  
                stmt4.setFloat(4, val); 
                stmt4.executeUpdate(); 
            }  // if the new value is 0, then we do nothing
        };
        System.out.println("DONE");  
    } else {
        //System.out.println("Error: the entry you want to modify is out of range.");
        System.out.println("ERROR");  
    };
};


public static void DELETEALL(Connection con) 
throws SQLException {
    // delete all records from table MATRIX, delete all records from table MATRIX_DATA
    //String query1 = "DELETE FROM MATRIX;";
    String query1 = "TRUNCATE TABLE MATRIX;";
    String query2 = "TRUNCATE TABLE MATRIX_DATA;";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    PreparedStatement stmt2 = con.prepareStatement(query2);
    stmt1.executeUpdate();
    stmt2.executeUpdate();
    System.out.println("DONE");  
};


public static void DELETE(int id, Connection con) 
throws SQLException {
    // delete indicated record from MATRIX, delete corresponding records from table MATRIX_DATA
    String query1 = "DELETE FROM MATRIX WHERE MATRIX_ID = ?;";
    String query2 = "DELETE FROM MATRIX_DATA WHERE MATRIX_ID = ?;";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    PreparedStatement stmt2 = con.prepareStatement(query2);
    stmt1.setInt(1, id);
    stmt2.setInt(1, id);
    stmt1.executeUpdate();
    stmt2.executeUpdate();
    System.out.println("DONE");  
};


public static void SETM(int id, int row_dim, int col_dim, Connection con) 
throws SQLException {
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    stmt1.setInt(1, id); 
    ResultSet checkExist = stmt1.executeQuery();
    if (checkExist.next()) {
        // if the matrix exist: check if we can modify it safely: 
        int row_d = checkExist.getInt(2);
        int col_d = checkExist.getInt(3);   
        if ((row_dim >= row_d) && (col_dim >= col_d)) {
            String query2 = "UPDATE MATRIX " + 
                    "SET ROW_DIM = ?, COL_DIM = ? " +
                    "WHERE MATRIX_ID = ?";
            PreparedStatement stmt2 = con.prepareStatement(query2);
            stmt2.setInt(1, row_dim);  
            stmt2.setInt(2, col_dim);  
            stmt2.setInt(3, id);  
            stmt2.executeUpdate(); 
            System.out.println("DONE");  
        } else {
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
        System.out.println("DONE");  
    }
};


public static void COPY_MATRIX(int id1, int id2, Connection con) throws SQLException {
// helper function: copy the matrix=id2 into matrix=id1:
// check if id1 already exist, if yes, delete all content 
// then createa new id1 with same dimension of id2:
// id2 is not allow to equal to id1
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?;";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    stmt1.setInt(1, id1);
    ResultSet getMatrix1 = stmt1.executeQuery();
    if (getMatrix1.next()) {
        DELETE(id1, con); // if such matrix, delete it and its data
    };
    String query2 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?;";
    PreparedStatement stmt2 = con.prepareStatement(query2);
    stmt2.setInt(1, id2);
    ResultSet getMatrix2 = stmt2.executeQuery();
    if (getMatrix2.next()) {
        int row_dim = getMatrix2.getInt(2);   
        int col_dim = getMatrix2.getInt(3);
        // create a new matrix = id1:
        // copy the framework of id2 into id1:
        query2 = "INSERT INTO MATRIX VALUES (?, ?, ?);";
        stmt2 = con.prepareStatement(query2);
        stmt2.setInt(1, id1);
        stmt2.setInt(2, row_dim);
        stmt2.setInt(3, col_dim);
        stmt2.executeUpdate();    
        // copy the content of id2 into id1: 
        String query3 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ?;";
        PreparedStatement stmt3 = con.prepareStatement(query3);
        stmt3.setInt(1, id2);
        ResultSet getValue = stmt3.executeQuery();
        while (getValue.next()) {
            int row_num = getValue.getInt(2);
            int col_num = getValue.getInt(3);
            float val = getValue.getFloat(4);
            String query7 = "INSERT INTO MATRIX_DATA VALUES (?, ?, ?, ?);";
            PreparedStatement stmt7 = con.prepareStatement(query7);
            stmt7.setInt(1, id1); // put summing result into matrix=id1
            stmt7.setInt(2, row_num);
            stmt7.setInt(3, col_num);
            stmt7.setFloat(4, val);
            stmt7.executeUpdate();
        }
    } else {
        // System.out.println("ERROR");  
    } 

};


public static void ADD(int id1, int id2, int id3, Connection con) throws SQLException {
// matrix summation: add matrix2 and matrix3, put the result into matrix1.
 
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?;";
    String query2 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?;";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    PreparedStatement stmt2 = con.prepareStatement(query2); 
    stmt1.setInt(1, id2);
    stmt2.setInt(1, id3);
    ResultSet getMatrix1 = stmt1.executeQuery();
    ResultSet getMatrix2 = stmt2.executeQuery();

    if (getMatrix1.next() && getMatrix2.next()) {
        // if both matrics exists: 
        // Note: It's important to use .next() to move cursor to the first line of ResultSet
        int row_dim_1 = getMatrix1.getInt(2);   
        int col_dim_1 = getMatrix1.getInt(3);
        int row_dim_2 = getMatrix2.getInt(2);
        int col_dim_2 = getMatrix2.getInt(3);
        if ((row_dim_1 == row_dim_2) && (col_dim_1 == col_dim_2)) {
        // two matrics have same dimension, addition is valid: 
            // create a new matrix with id=100000 and use it as temp storage: 
            String query0 = "INSERT INTO MATRIX VALUES (?, ?, ?);";
            int id0 = 100000;
            PreparedStatement stmt0 = con.prepareStatement(query0);
            stmt0.setInt(1, id0);
            stmt0.setInt(2, row_dim_1);
            stmt0.setInt(3, col_dim_1);
            stmt0.executeUpdate();     

            for (int i=1; i<=row_dim_1; ++i) { // start from row 1 not row 0
                String query3 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ? AND " + 
                    "ROW_NUM = ?;";
                String query4 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ? AND " +
                    "ROW_NUM = ?;";

                PreparedStatement stmt3 = con.prepareStatement(query3);
                PreparedStatement stmt4 = con.prepareStatement(query4);
                stmt3.setInt(1, id2);
                stmt4.setInt(1, id3);
                stmt3.setInt(2, i);
                stmt4.setInt(2, i);
                ResultSet getValue1 = stmt3.executeQuery();
                ResultSet getValue2 = stmt4.executeQuery();
                while (getValue1.next()) {
                    int row_num = getValue1.getInt(2); // actually row_num = i
//System.out.println(row_num == i);   // testing purpose
                    int col_num = getValue1.getInt(3);
                    float val = getValue1.getFloat(4);
                    // Note: here I just assume there is a matrix = id1; 
                    // may need to check if id1 already contains data in it. 
                    String query5 = "INSERT INTO MATRIX_DATA VALUES (?, ?, ?, ?);";
                    PreparedStatement stmt5 = con.prepareStatement(query5);
                    stmt5.setInt(1, id0); // put summing result into matrix=id1
                    stmt5.setInt(2, row_num);
                    stmt5.setInt(3, col_num);
                    stmt5.setFloat(4, val);
                    stmt5.executeUpdate();
                }
                while(getValue2.next()) {
                    int row_num = getValue2.getInt(2); // actually row_num = i
                    int col_num = getValue2.getInt(3);
                    float val = getValue2.getFloat(4);
                    String query6 = "SELECT * FROM MATRIX_DATA WHERE (MATRIX_ID = ?) " +
                        "AND (ROW_NUM = ?) AND (COL_NUM = ?);";
                    PreparedStatement stmt6 = con.prepareStatement(query6);
                    stmt6.setInt(1, id0); // extract the previous value in matrix=id1
                    stmt6.setInt(2, row_num);
                    stmt6.setInt(3, col_num);
                    ResultSet getOldValue = stmt6.executeQuery();
                    
                    if (getOldValue.next()) {
                        // check if there is any value at the current index, 
                        // if yes, sum up old value with new value
                        float oldVal = getOldValue.getFloat(4);
                        val = oldVal + val;
                        String query7 = "UPDATE MATRIX_DATA " + 
                        "SET VALUE = ? " +
                        "WHERE (MATRIX_ID = ?) AND (ROW_NUM = ?) AND (COL_NUM = ?)";
                        PreparedStatement stmt7 = con.prepareStatement(query7);
                        stmt7.setFloat(1, val);
                        stmt7.setInt(2, id0); // put summing result into matrix=id1
                        stmt7.setInt(3, row_num);
                        stmt7.setInt(4, col_num);
                        stmt7.executeUpdate();
                    } else {
                        // current index don't have value, put new value into it
                        String query7 = "INSERT INTO MATRIX_DATA VALUES (?, ?, ?, ?);";
                        PreparedStatement stmt7 = con.prepareStatement(query7);
                        stmt7.setInt(1, id0); // put summing result into matrix=id1
                        stmt7.setInt(2, row_num);
                        stmt7.setInt(3, col_num);
                        stmt7.setFloat(4, val);
                        stmt7.executeUpdate();
                    }
                }
            }
            // copy the matrix=id0 into matrix of id1: 
            COPY_MATRIX(id1, id0, con); 
            DELETE(id0, con);
            System.out.println("DONE");  
        } else {
            // System.out.println("Error: Dimension mismatch.");
            System.out.println("ERROR");  
        } 
    } else {
        // System.out.println("Error: selected matrix doesn't exist.");
        System.out.println("ERROR");  
    }
};


public static void MULT(int id1, int id2, int id3, Connection con) throws SQLException {
// multiply matrix2 and matrix3, store the result in matrix1 
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?;";
    String query2 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?;";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    PreparedStatement stmt2 = con.prepareStatement(query2); 
    stmt1.setInt(1, id2);
    stmt2.setInt(1, id3);
    ResultSet getMatrix1 = stmt1.executeQuery();
    ResultSet getMatrix2 = stmt2.executeQuery();

    if (getMatrix1.next() && getMatrix2.next()) {
        // if both matrics exists: 
        // Note: It's important to use .next() to move cursor to the first line of ResultSet
        int row_dim_1 = getMatrix1.getInt(2);   
        int col_dim_1 = getMatrix1.getInt(3);
        int row_dim_2 = getMatrix2.getInt(2);
        int col_dim_2 = getMatrix2.getInt(3);

        if (col_dim_1 == row_dim_2) {
            // if the multiplication is valid in dimension:
            // create a new matrix with id=100000 and use it as temp storage: 
            String query0 = "INSERT INTO MATRIX VALUES (?, ?, ?);";
            int id0 = 100000;
            PreparedStatement stmt0 = con.prepareStatement(query0);
            stmt0.setInt(1, id0);
            stmt0.setInt(2, row_dim_1);
            stmt0.setInt(3, col_dim_2);
            stmt0.executeUpdate();     
 
            for (int i=1; i<=row_dim_1; ++i) {
                String query3 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ? AND " + 
                    "ROW_NUM = ?;";
                PreparedStatement stmt3 = con.prepareStatement(query3);
                stmt3.setInt(1, id2);
                stmt3.setInt(2, i);
                ResultSet getValue1 = stmt3.executeQuery();

                for (int j=1; j<=col_dim_2; ++j) {
                    float res = 0; // this used to keep the final value of index (i,j)
                    while(getValue1.next()) { 
//System.out.println("MULT while");                        
                        int col_1 = getValue1.getInt(3);
                        float val_1 = getValue1.getFloat(4);
//System.out.println(val_1);                        
                        String query4 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ? " + 
                        "AND COL_NUM = ? AND ROW_NUM = ?;";
                        PreparedStatement stmt4 = con.prepareStatement(query4);
                        stmt4.setInt(1, id2);
                        stmt4.setInt(2, j);
                        stmt4.setInt(3, col_1);
                        ResultSet getValue2 = stmt4.executeQuery();
                        while (getValue2.next()) {
                            float val_2 = getValue2.getFloat(4);
//System.out.println(val_2);                             
                            res = res + val_1*val_2;
//System.out.println(res);                            
                        }
                    }
//System.out.println(res);                    
                    // check if res == 0; if yes, do nothing; otherwise
                    // insert the result for index (i,j)
                    if (res != 0) {
//System.out.println("MULT insert");                       
                        String query5 = "INSERT INTO MATRIX_DATA VALUES (?, ?, ?, ?);";
                        PreparedStatement stmt5 = con.prepareStatement(query5);
                        stmt5.setInt(1, id1); // put result into matrix=id1
                        stmt5.setInt(2, i); // put the result into index (i,j)
                        stmt5.setInt(3, j); 
                        stmt5.setFloat(4, res);
                        stmt5.executeUpdate();
                    }
//System.out.println("MULT finish");                      
                }
            } 
            // copy the matrix=id0 into matrix of id1: 
            COPY_MATRIX(id1, id0, con);  
            DELETE(id0, con);   
            System.out.println("DONE");         
        } else {
            // System.out.println("ERROR: dimension mismatch");  
            System.out.println("ERROR");  
        }
    }
};


public static void TRANS(int id1, int id2, Connection con) throws SQLException {
// transpose matrix2, store the result as matrix1

    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    stmt1.setInt(1, id2); 
    ResultSet getMatrix = stmt1.executeQuery();
    if (getMatrix.next()) {
        // if the selected matrix exist:
        // create a new matrix with id=100000 and use it as temp storage: 
        int row_dim = getMatrix.getInt(2); 
        int col_dim = getMatrix.getInt(3);
        String query0 = "INSERT INTO MATRIX VALUES (?, ?, ?);";
        int id0 = 100000;
        PreparedStatement stmt0 = con.prepareStatement(query0);
        stmt0.setInt(1, id0);
        stmt0.setInt(2, col_dim);
        stmt0.setInt(3, row_dim);
        stmt0.executeUpdate();      
        
        for (int i=1; i<=row_dim; ++i) {
            String query2 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ? AND " + 
                    "ROW_NUM = ?;";
            PreparedStatement stmt2 = con.prepareStatement(query2);
            stmt2.setInt(1, id2);
            stmt2.setInt(2, i);
            ResultSet getValue = stmt2.executeQuery();
            while (getValue.next()) {
                int row_num = getValue.getInt(2); // actually row_num = i
                int col_num = getValue.getInt(3);
                float val = getValue.getFloat(4);
                String query3 = "INSERT INTO MATRIX_DATA VALUES (?, ?, ?, ?);";
                PreparedStatement stmt3 = con.prepareStatement(query3);
                stmt3.setInt(1, id1); // put result into matrix=id1
                stmt3.setInt(2, col_num);
                stmt3.setInt(3, row_num); // exchange row_num and col_num
                stmt3.setFloat(4, val);
                stmt3.executeUpdate();
            };
        };
        // copy the matrix=id0 into matrix of id1: 
        COPY_MATRIX(id1, id0, con); 
        DELETE(id0, con); 
        System.out.println("DONE");  

    } else {
        // System.out.println("Error: selected matrix doesn't exist.");
        System.out.println("ERROR");  
    };
};
       

public static void NEG(int id1, int id2, Connection con) throws SQLException {
// negative all element in matrix2, and store result as matrix1
    String query1 = "SELECT * FROM MATRIX WHERE MATRIX_ID = ?";
    PreparedStatement stmt1 = con.prepareStatement(query1);
    stmt1.setInt(1, id2); 
    ResultSet getMatrix = stmt1.executeQuery();
    if (getMatrix.next()) {
        // if the selected matrix exist:
        int row_dim = getMatrix.getInt(2); 
        int col_dim = getMatrix.getInt(3);
        
        // create a new matrix with id=100000 and use it as temp storage: 
        String query0 = "INSERT INTO MATRIX VALUES (?, ?, ?);";
        int id0 = 100000;
        PreparedStatement stmt0 = con.prepareStatement(query0);
        stmt0.setInt(1, id0);
        stmt0.setInt(2, row_dim);
        stmt0.setInt(3, col_dim);
        stmt0.executeUpdate();         

        for (int i=1; i<=row_dim; ++i) {
            String query2 = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID = ? AND " + 
                    "ROW_NUM = ?;";
            PreparedStatement stmt2 = con.prepareStatement(query2);
            stmt2.setInt(1, id2);
            stmt2.setInt(2, i);
            ResultSet getValue = stmt2.executeQuery();
            while (getValue.next()) {
                int row_num = getValue.getInt(2); // actually row_num = i
                int col_num = getValue.getInt(3);
                float val = -(getValue.getFloat(4)); // negative the value
                String query3 = "INSERT INTO MATRIX_DATA VALUES (?, ?, ?, ?);";
                PreparedStatement stmt3 = con.prepareStatement(query3);
                stmt3.setInt(1, id1); // put result into matrix=id1
                stmt3.setInt(2, row_num);
                stmt3.setInt(3, col_num); 
                stmt3.setFloat(4, val);
                stmt3.executeUpdate();
            };
        }
        // copy the matrix=id0 into matrix of id1: 
        COPY_MATRIX(id1, id0, con);  
        DELETE(id0, con);
        // No DONE should be printed
    } else {
        // System.out.println("Error: selected matrix doesn't exist.");
        System.out.println("ERROR");  
    };
};


public static void main(String[] args) throws                                       
ClassNotFoundException, SQLException, FileNotFoundException, NoSuchElementException
{
    // Get connection string and file name
    String CONNECTION_STRING =args[0];
    String INPUT_FILE =args[1];
    Connection con = DriverManager.getConnection(CONNECTION_STRING);
    Scanner sc = new Scanner(new File(INPUT_FILE)); 
    while(sc.hasNext()){
        String cmd = sc.next();
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
            float val = Float.parseFloat(sval);          
            SETV(id, row, col, val, con);
            
        } else if (cmd.equals("SETM")) {
            String sid = sc.next();
            String srow_dim = sc.next();
            String scol_dim = sc.next();
            int id = Integer.parseInt(sid);
            int row_dim = Integer.parseInt(srow_dim);
            int col_dim = Integer.parseInt(scol_dim);           
            SETM(id, row_dim, col_dim, con);
        } else if (cmd.equals("DELETE")) {
            String sid = sc.next();
            if (sid.equals("ALL")) {
                DELETEALL(con);
            } else {
                int id = Integer.parseInt(sid);
                DELETE(id, con);
            }

        } else if (cmd.equals("ADD")) {
            String sid1 = sc.next();
            String sid2 = sc.next();
            String sid3 = sc.next();
            int id1 = Integer.parseInt(sid1);
            int id2 = Integer.parseInt(sid2);
            int id3 = Integer.parseInt(sid3);
            ADD(id1, id2, id3, con);

        } else if (cmd.equals("SUB")) {
            String sid1 = sc.next();
            String sid2 = sc.next();
            String sid3 = sc.next();
            int id1 = Integer.parseInt(sid1);
            int id2 = Integer.parseInt(sid2);
            int id3 = Integer.parseInt(sid3);
            int temp = 100001; // pick a number for temporary storage
            NEG(temp, id3, con);
            ADD(id1, id2, temp, con);
            DELETE(temp, con);

        } else if (cmd.equals("MULT")) {
            String sid1 = sc.next();
            String sid2 = sc.next();
            String sid3 = sc.next();
            int id1 = Integer.parseInt(sid1);
            int id2 = Integer.parseInt(sid2);
            int id3 = Integer.parseInt(sid3);
            MULT(id1, id2, id3, con);

        } else if (cmd.equals("TRANSPOSE")) {
            String sid1 = sc.next();
            String sid2 = sc.next();
            int id1 = Integer.parseInt(sid1);
            int id2 = Integer.parseInt(sid2);
            TRANS(id1, id2, con);

        } else if (cmd.equals("SQL")) {
            String query = sc.nextLine();
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet getValue = stmt.executeQuery();
            getValue.next();
            String res = getValue.getString(1); 
            System.out.println(res);  
        }
    }

    sc.close();     
    con.close();
    }
}
        

    