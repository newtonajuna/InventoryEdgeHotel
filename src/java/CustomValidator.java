
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class CustomValidator implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String CheckPassword(String Password1,String Password2)
    {
        if(Password1.equals(Password2))
        {
            if(Password1.length()>=5 && Password1.length()<=20)
            {
                return "PASS";
            }else
            {
                return "FAIL";
            }
        }else
        {
            return "FAIL";
        }
    }
    
    public String TextSize(String TextValue, int MinValue,int MaxValue)
    {
        if(TextValue.length()>=MinValue && TextValue.length()<=MaxValue)
        {
            return "PASS";
        }else
        {
            return "FAIL";
        }
    }
    
    public long CheckRecords(String sql)
    {
        ResultSet rs = null;
        long records=0;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                records=records+1;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return records;
    }
    public long INTER_BRANCH_CheckRecords(String sql)
    {
        ResultSet rs = null;
        long records=0;
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                records=records+1;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return records;
    }
    
    public long CheckLongRange(long NumberValue, int MinValue,int MaxValue)
    {
        return 1;
    }
    
}
