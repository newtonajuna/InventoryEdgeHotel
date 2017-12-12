import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
@ManagedBean
@SessionScoped
public class Customer implements java.io.Serializable {
 
    private Integer custId;
    private String firstName;
    private String lastName;
    private String email;
    private Date dob;
    private String sd, msg, selectedname;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
 
    public Customer() {
    }
 
    public Customer(String firstName, String lastName, String email, Date dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dob = dob;
    }
 
    public String getSd() {
        return sd;
    }
 
    public void setSd(String sd) {
        this.sd = sd;
    }
 
    public Integer getCustId() {
        return this.custId;
    }
 
    public void setCustId(Integer custId) {
        this.custId = custId;
    }
 
    public String getFirstName() {
        return this.firstName;
    }
 
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
 
    public String getLastName() {
        return this.lastName;
    }
 
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
 
    public String getEmail() {
        return this.email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public Date getDob() {
        return this.dob;
    }
 
    public void setDob(Date dob) {
        this.dob = dob;
    }
 
    public String getMsg() {
        return msg;
    }
 
    public void setMsg(String msg) {
        this.msg = msg;
    }
 
    public String getSelectedname() {
        return selectedname;
    }
 
    public void setSelectedname(String selectedname) {
        this.selectedname = selectedname;
    }
 
    public void saveCustomer() {
        String sql = null;
        sql = "INSERT INTO customer(first_name,last_name,email) values(?,?,?)";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);){
                ps.setString(1, this.firstName);
                ps.setString(2, this.lastName);
                ps.setString(3, this.email);
                ps.execute();
                this.msg = "Member Info Saved Successfull!";
                clearAll();
                
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }   
    }
    public void updateCustomer() {
        String sql = null;
        sql = "UPDATE customer SET first_name=?,last_name=?,email=? WHERE cust_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);){
                ps.setString(1, this.firstName);
                ps.setString(2, this.lastName);
                ps.setString(3, this.email);
                ps.setInt(4, this.custId);
                ps.execute();
                this.msg = "Member Info Updated Successfull!";
                clearAll();
                
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } 
    }
    public void deleteCustomer() {

        this.msg = "Member Info Delete Successfull!";
        clearAll();
    }
 
    public List<Customer> getAllCustomers() {
        List<Customer> users = new ArrayList<Customer>();
        
        return users;
    }
 
    public void fullInfo() {
//        CustomerDao dao = new CustomerDao();
//        List<Customer> lc = dao.getCustomerById(selectedname);
//        System.out.println(lc.get(0).firstName);
//        this.custId = lc.get(0).custId;
//        this.firstName = lc.get(0).firstName;
//        this.lastName = lc.get(0).lastName;
//        this.email = lc.get(0).email;
    }
 
    private void clearAll() {
        this.firstName = "";
        this.lastName = "";
        this.sd = "";
        this.email = "";
        this.custId=0;
    }
}