
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class PointsCardBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<PointsCard> PointsCards;
    private String ActionMessage=null;
    private PointsCard SelectedPointsCard=null;
    private long SelectedPointsCardId;
    private String SearchCardNumber="";
    private String TypedCardNumber;
   
    public String getNewPointsCardNumber() {
        String p1=Integer.toString((int) Math.floor(1+Math.random()*9));
        String p2=Integer.toString((int) Math.floor(10+Math.random()*90));
        String p3=Integer.toString((int) Math.floor(100+Math.random()*899));
        String p4=Integer.toString((int) Math.floor(1000+Math.random()*8999));
        return p1 + p2 + p3 + p4;
    }   
    public void savePointsCard(PointsCard pointsCard) {
        String sql = null;
        String msg="";
        String sql2 = null;
        String sql3 = null;
        String sql4 = null;
        String sql5 = null;
        //first get new random card number
        pointsCard.setCardNumber(this.getNewPointsCardNumber());
        
        sql2="SELECT * FROM points_card WHERE card_number='" + pointsCard.getCardNumber() + "'";
        sql3="SELECT * FROM points_card WHERE card_number='" + pointsCard.getCardNumber() + "' AND points_card_id!=" + pointsCard.getPointsCardId();
        sql4="SELECT * FROM points_card WHERE card_holder='" + pointsCard.getCardHolder() + "'";
        sql5="SELECT * FROM points_card WHERE card_holder='" + pointsCard.getCardHolder() + "' AND points_card_id!=" + pointsCard.getPointsCardId();
        
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        
        if (pointsCard.getPointsCardId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"INTER BRANCH", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (pointsCard.getPointsCardId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"INTER BRANCH", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getCardNumber(), 10, 10).equals("FAIL")){
            msg="Card Number MUST be 10 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getCardHolder(), 3, 100).equals("FAIL")){
            msg="Card Holder Names MUST be between 3-to-100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(pointsCard.getRegBranchId()==0){
            msg="Please select a valid Registration Branch";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getPhone(), 0, 100).equals("FAIL")){
            msg="Phone cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getEmail(), 0, 100).equals("FAIL")){
            msg="Email cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getWebsite(), 0, 100).equals("FAIL")){
            msg="Website cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getCpname(), 0, 100).equals("FAIL")){
            msg="Contact person's name cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getCptitle(), 0, 100).equals("FAIL")){
            msg="Contact person's title cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getCpphone(), 0, 100).equals("FAIL")){
            msg="Contact person's phone cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getCpemail(), 0, 100).equals("FAIL")){
            msg="Contact person's email cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getPhysicalAddress(), 0, 250).equals("FAIL")){
            msg="Physical address cannot exceed 2550 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getTaxIdentity(), 0, 100).equals("FAIL")){
            msg="Tax Identity cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(pointsCard.getAccountDetails(), 0, 250).equals("FAIL")){
            msg="Account details cannot exceed 250 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if((new CustomValidator().INTER_BRANCH_CheckRecords(sql2)>0 && pointsCard.getPointsCardId()==0) || (new CustomValidator().INTER_BRANCH_CheckRecords(sql3)>0 && pointsCard.getPointsCardId()>0)){
            msg="Card Number already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if((new CustomValidator().INTER_BRANCH_CheckRecords(sql4)>0 && pointsCard.getPointsCardId()==0) || (new CustomValidator().INTER_BRANCH_CheckRecords(sql5)>0 && pointsCard.getPointsCardId()>0)){
            msg="Card Holder already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {     
            if (pointsCard.getPointsCardId() == 0) {
                sql = "{call sp_insert_points_card(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            } else if (pointsCard.getPointsCardId() > 0) {
                sql = "{call sp_update_points_card(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            }
            try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (pointsCard.getPointsCardId() == 0) {
                cs.setString("in_card_number", pointsCard.getCardNumber());
                cs.setString("in_card_holder", pointsCard.getCardHolder());
                cs.setString("in_email", pointsCard.getEmail());
                cs.setString("in_phone", pointsCard.getPhone());
                cs.setString("in_physical_address", pointsCard.getPhysicalAddress());
                cs.setString("in_tax_identity", pointsCard.getTaxIdentity());
                cs.setString("in_account_details", pointsCard.getAccountDetails());
                cs.setString("in_website", pointsCard.getWebsite());
                cs.setString("in_cpname", pointsCard.getCpname());
                cs.setString("in_cptitle", pointsCard.getCptitle());
                cs.setString("in_cpphone", pointsCard.getCpphone());
                cs.setString("in_cpemail", pointsCard.getCpemail());
                cs.setInt("in_reg_branch_id", pointsCard.getRegBranchId());
                cs.setFloat("in_points_balance", pointsCard.getPointsBalance());
                cs.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_add_user",new GeneralUserSetting().getCurrentUserNames());
                cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_edit_user", new GeneralUserSetting().getCurrentUserNames());
                cs.executeUpdate();
                
                this.clearPointsCard(pointsCard);
                this.setActionMessage("Saved Successfully");
                
            } else if (pointsCard.getPointsCardId() > 0) {
                cs.setLong("in_points_card_id", pointsCard.getPointsCardId());
                cs.setString("in_card_number", pointsCard.getCardNumber());
                cs.setString("in_card_holder", pointsCard.getCardHolder());
                cs.setString("in_email", pointsCard.getEmail());
                cs.setString("in_phone", pointsCard.getPhone());
                cs.setString("in_physical_address", pointsCard.getPhysicalAddress());
                cs.setString("in_tax_identity", pointsCard.getTaxIdentity());
                cs.setString("in_account_details", pointsCard.getAccountDetails());
                cs.setString("in_website", pointsCard.getWebsite());
                cs.setString("in_cpname", pointsCard.getCpname());
                cs.setString("in_cptitle", pointsCard.getCptitle());
                cs.setString("in_cpphone", pointsCard.getCpphone());
                cs.setString("in_cpemail", pointsCard.getCpemail());
                cs.setInt("in_reg_branch_id", pointsCard.getRegBranchId());
                cs.setFloat("in_points_balance", pointsCard.getPointsBalance());
                cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_edit_user", new GeneralUserSetting().getCurrentUserNames());
                
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearPointsCard(pointsCard);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("PointsCard NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("PointsCard NOT saved!"));
        }catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
    }
    }
    
    public PointsCard findPointsCard(long PointsCardId) {
        String sql = "{call sp_search_points_card_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, PointsCardId);
            rs = ps.executeQuery();
            if (rs.next()) {
                PointsCard pointsCard = new PointsCard();
                pointsCard.setPointsCardId(rs.getLong("points_card_id"));
                pointsCard.setCardNumber(rs.getString("card_number"));
                pointsCard.setCardHolder(rs.getString("card_holder"));
                pointsCard.setEmail(rs.getString("email"));
                pointsCard.setPhone(rs.getString("phone"));
                pointsCard.setPhysicalAddress(rs.getString("physical_address"));
                pointsCard.setTaxIdentity(rs.getString("tax_identity"));
                pointsCard.setAccountDetails(rs.getString("account_details"));
                pointsCard.setWebsite(rs.getString("website"));
                pointsCard.setCpname(rs.getString("cpname"));
                pointsCard.setCptitle(rs.getString("cptitle"));
                pointsCard.setCpphone(rs.getString("cpphone"));
                pointsCard.setCpemail(rs.getString("cpemail"));
                pointsCard.setRegBranchId(rs.getInt("reg_branch_id"));
                pointsCard.setPointsBalance(rs.getFloat("points_balance"));
                pointsCard.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsCard.setAddUser(rs.getString("add_user"));
                pointsCard.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsCard.setEditUser(rs.getString("edit_user"));
                return pointsCard;
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

    }
    
    public PointsCard getPointsCard(long PointsCardId) {
        String sql = "{call sp_search_points_card_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, PointsCardId);
            rs = ps.executeQuery();
            if (rs.next()) {
                PointsCard pointsCard = new PointsCard();
                pointsCard.setPointsCardId(rs.getLong("points_card_id"));
                pointsCard.setCardNumber(rs.getString("card_number"));
                pointsCard.setCardHolder(rs.getString("card_holder"));
                pointsCard.setEmail(rs.getString("email"));
                pointsCard.setPhone(rs.getString("phone"));
                pointsCard.setPhysicalAddress(rs.getString("physical_address"));
                pointsCard.setTaxIdentity(rs.getString("tax_identity"));
                pointsCard.setAccountDetails(rs.getString("account_details"));
                pointsCard.setWebsite(rs.getString("website"));
                pointsCard.setCpname(rs.getString("cpname"));
                pointsCard.setCptitle(rs.getString("cptitle"));
                pointsCard.setCpphone(rs.getString("cpphone"));
                pointsCard.setCpemail(rs.getString("cpemail"));
                pointsCard.setRegBranchId(rs.getInt("reg_branch_id"));
                pointsCard.setPointsBalance(rs.getFloat("points_balance"));
                pointsCard.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsCard.setAddUser(rs.getString("add_user"));
                pointsCard.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsCard.setEditUser(rs.getString("edit_user"));
                return pointsCard;
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            return null;
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

    }
    
    public PointsCard getPointsCardByCardNumber(String PointsCardNumber) {
        String sql = "{call sp_search_points_card_by_card_number(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, PointsCardNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                PointsCard pointsCard = new PointsCard();
                pointsCard.setPointsCardId(rs.getLong("points_card_id"));
                pointsCard.setCardNumber(rs.getString("card_number"));
                pointsCard.setCardHolder(rs.getString("card_holder"));
                pointsCard.setEmail(rs.getString("email"));
                pointsCard.setPhone(rs.getString("phone"));
                pointsCard.setPhysicalAddress(rs.getString("physical_address"));
                pointsCard.setTaxIdentity(rs.getString("tax_identity"));
                pointsCard.setAccountDetails(rs.getString("account_details"));
                pointsCard.setWebsite(rs.getString("website"));
                pointsCard.setCpname(rs.getString("cpname"));
                pointsCard.setCptitle(rs.getString("cptitle"));
                pointsCard.setCpphone(rs.getString("cpphone"));
                pointsCard.setCpemail(rs.getString("cpemail"));
                pointsCard.setRegBranchId(rs.getInt("reg_branch_id"));
                pointsCard.setPointsBalance(rs.getFloat("points_balance"));
                pointsCard.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsCard.setAddUser(rs.getString("add_user"));
                pointsCard.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsCard.setEditUser(rs.getString("edit_user"));
                return pointsCard;
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            return null;
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
    
     public boolean IsPointsCardNumberValid(String PointsCardNumber) {
        String sql = "{call sp_search_points_card_by_card_number(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, PointsCardNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return false;
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            return false;
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    
    public void deletePointsCard(PointsCard pointsCard) {
        String sql = "DELETE FROM points_card WHERE points_card_id=?";
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        
        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"INTER BRANCH", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, pointsCard.getPointsCardId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearPointsCard(pointsCard);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("PointsCard NOT deleted");
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
        }
    }

    public void displayPointsCard(PointsCard PointsCardFrom, PointsCard PointsCardTo) {
                PointsCardTo.setPointsCardId(PointsCardFrom.getPointsCardId());
                PointsCardTo.setCardNumber(PointsCardFrom.getCardNumber());
                PointsCardTo.setCardHolder(PointsCardFrom.getCardHolder());
                PointsCardTo.setEmail(PointsCardFrom.getEmail());
                PointsCardTo.setPhone(PointsCardFrom.getPhone());
                PointsCardTo.setPhysicalAddress(PointsCardFrom.getPhysicalAddress());
                PointsCardTo.setTaxIdentity(PointsCardFrom.getTaxIdentity());
                PointsCardTo.setAccountDetails(PointsCardFrom.getAccountDetails());
                PointsCardTo.setWebsite(PointsCardFrom.getWebsite());
                PointsCardTo.setCpname(PointsCardFrom.getCpname());
                PointsCardTo.setCptitle(PointsCardFrom.getCptitle());
                PointsCardTo.setCpphone(PointsCardFrom.getCpphone());
                PointsCardTo.setCpemail(PointsCardFrom.getCpemail());
                PointsCardTo.setRegBranchId(PointsCardFrom.getRegBranchId());
                PointsCardTo.setPointsBalance(PointsCardFrom.getPointsBalance());
                PointsCardTo.setAddUser(PointsCardFrom.getAddUser());
                PointsCardTo.setEditUser(PointsCardFrom.getEditUser());
    }

    public void clearPointsCard(PointsCard pointsCard) {
        pointsCard.setPointsCardId(0);
        pointsCard.setCardNumber("");
        pointsCard.setCardHolder("");
        pointsCard.setEmail("");
        pointsCard.setPhone("");
        pointsCard.setPhysicalAddress("");
        pointsCard.setTaxIdentity("");
        pointsCard.setAccountDetails("");
        pointsCard.setWebsite("");
        pointsCard.setCpname("");
        pointsCard.setCptitle("");
        pointsCard.setCpphone("");
        pointsCard.setCpemail("");
        pointsCard.setRegBranchId(0);
        pointsCard.setPointsBalance(0);
        //pointsCard.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
        pointsCard.setAddUser("");
        //pointsCard.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
        pointsCard.setEditUser("");
    }
    
    public void initClearPointsCard(PointsCard pointsCard) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            pointsCard.setPointsCardId(0);
            pointsCard.setCardNumber("");
            pointsCard.setCardHolder("");
            pointsCard.setEmail("");
            pointsCard.setPhone("");
            pointsCard.setPhysicalAddress("");
            pointsCard.setTaxIdentity("");
            pointsCard.setAccountDetails("");
            pointsCard.setWebsite("");
            pointsCard.setCpname("");
            pointsCard.setCptitle("");
            pointsCard.setCpphone("");
            pointsCard.setCpemail("");
            pointsCard.setRegBranchId(0);
            pointsCard.setPointsBalance(0);
            //pointsCard.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
            pointsCard.setAddUser("");
            //pointsCard.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
            pointsCard.setEditUser("");
        }
    }

    public List<PointsCard> getPointsCards() {
        String sql;
        sql = "{call sp_search_points_card_by_card_number_holder_names(?)}";
        ResultSet rs = null;
        PointsCards = new ArrayList<PointsCard>();
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, this.SearchCardNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                PointsCard pointsCard = new PointsCard();
                pointsCard.setPointsCardId(rs.getLong("points_card_id"));
                pointsCard.setCardNumber(rs.getString("card_number"));
                pointsCard.setCardHolder(rs.getString("card_holder"));
                pointsCard.setEmail(rs.getString("email"));
                pointsCard.setPhone(rs.getString("phone"));
                pointsCard.setPhysicalAddress(rs.getString("physical_address"));
                pointsCard.setTaxIdentity(rs.getString("tax_identity"));
                pointsCard.setAccountDetails(rs.getString("account_details"));
                pointsCard.setWebsite(rs.getString("website"));
                pointsCard.setCpname(rs.getString("cpname"));
                pointsCard.setCptitle(rs.getString("cptitle"));
                pointsCard.setCpphone(rs.getString("cpphone"));
                pointsCard.setCpemail(rs.getString("cpemail"));
                pointsCard.setRegBranchId(rs.getInt("reg_branch_id"));
                pointsCard.setPointsBalance(rs.getFloat("points_balance"));
                pointsCard.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsCard.setAddUser(rs.getString("add_user"));
                pointsCard.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsCard.setEditUser(rs.getString("edit_user"));
                PointsCards.add(pointsCard);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            PointsCards.clear();
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return PointsCards;
    }
    
    public List<PointsCard> getPointsCardsByObjectList(String Query) {
        String sql;
        sql = "{call sp_search_points_card_by_card_number_holder_names(?)}";
        ResultSet rs = null;
        PointsCards = new ArrayList<PointsCard>();
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                PointsCard pointsCard = new PointsCard();
                pointsCard.setPointsCardId(rs.getLong("points_card_id"));
                pointsCard.setCardNumber(rs.getString("card_number"));
                pointsCard.setCardHolder(rs.getString("card_holder"));
                pointsCard.setEmail(rs.getString("email"));
                pointsCard.setPhone(rs.getString("phone"));
                pointsCard.setPhysicalAddress(rs.getString("physical_address"));
                pointsCard.setTaxIdentity(rs.getString("tax_identity"));
                pointsCard.setAccountDetails(rs.getString("account_details"));
                pointsCard.setWebsite(rs.getString("website"));
                pointsCard.setCpname(rs.getString("cpname"));
                pointsCard.setCptitle(rs.getString("cptitle"));
                pointsCard.setCpphone(rs.getString("cpphone"));
                pointsCard.setCpemail(rs.getString("cpemail"));
                pointsCard.setRegBranchId(rs.getInt("reg_branch_id"));
                pointsCard.setPointsBalance(rs.getFloat("points_balance"));
                pointsCard.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsCard.setAddUser(rs.getString("add_user"));
                pointsCard.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsCard.setEditUser(rs.getString("edit_user"));
                PointsCards.add(pointsCard);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            PointsCards.clear();
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return PointsCards;
    }

    /**
     * @param PointsCards the PointsCards to set
     */
    public void setPointsCards(List<PointsCard> PointsCards) {
        this.PointsCards = PointsCards;
    }
    public void updatePointsCardBalance(String CardNumber, float Points) {
            String sql = null;
            sql = "{call sp_update_points_card_balance(?,?)}";
            try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setString("in_card_number", CardNumber);
                cs.setFloat("in_points", Points);
                if (CardNumber.length()!=0 || Points!=0) {
                    cs.executeUpdate();
                }
            }catch (SQLException se) {
                System.err.println(se.getMessage());
            }catch(NullPointerException npe){
                System.err.println(npe.getMessage());
                FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            }
    }
    public void addPointsCardBalanceByCardNo(String CardNumber, float Points) {
            String sql = null;
            sql = "{call sp_add_points_card_balance_by_card_no(?,?)}";
            try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setString("in_card_number", CardNumber);
                cs.setFloat("in_points", Points);
                if (CardNumber.length()!=0 || Points!=0) {
                    cs.executeUpdate();
                }
            }catch (SQLException se) {
                System.err.println(se.getMessage());
            }catch(NullPointerException npe){
                System.err.println(npe.getMessage());
                FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            }
    }
    public void addPointsCardBalanceByCardId(long CardId, float Points) {
            String sql = null;
            
            sql = "{call sp_add_points_card_balance_by_card_id(?,?)}";
            try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setLong("in_points_card_id", CardId);
                cs.setFloat("in_points", Points);
                if (CardId!=0 && Points!=0) {
                    cs.executeUpdate();
                }
            }catch (SQLException se) {
                System.err.println(se.getMessage());
            }catch(NullPointerException npe){
                System.err.println(npe.getMessage());
                FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            }
    }
    public void subtractPointsCardBalance(String CardNumber, float Points) {
            String sql = null;
            sql = "{call sp_subtract_points_card_balance(?,?)}";
            try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setString("in_card_number", CardNumber);
                cs.setFloat("in_points", Points);
                if (CardNumber.length()!=0 || Points!=0) {
                    cs.executeUpdate();
                }
            }catch (SQLException se) {
                System.err.println(se.getMessage());
            }catch(NullPointerException npe){
                System.err.println(npe.getMessage());
                FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            }
    }
    
    /**
     * @return the ActionMessage
     */
    public String getActionMessage() {
        return ActionMessage;
    }

    /**
     * @param ActionMessage the ActionMessage to set
     */
    public void setActionMessage(String ActionMessage) {
        this.ActionMessage = ActionMessage;
    }

    /**
     * @return the SelectedPointsCard
     */
    public PointsCard getSelectedPointsCard() {
        return SelectedPointsCard;
    }

    /**
     * @param SelectedPointsCard the SelectedPointsCard to set
     */
    public void setSelectedPointsCard(PointsCard SelectedPointsCard) {
        this.SelectedPointsCard = SelectedPointsCard;
    }

    /**
     * @return the SelectedPointsCardId
     */
    public long getSelectedPointsCardId() {
        return SelectedPointsCardId;
    }

    /**
     * @param SelectedPointsCardId the SelectedPointsCardId to set
     */
    public void setSelectedPointsCardId(long SelectedPointsCardId) {
        this.SelectedPointsCardId = SelectedPointsCardId;
    }

    /**
     * @return the SearchCardNumber
     */
    public String getSearchCardNumber() {
        return SearchCardNumber;
    }

    /**
     * @param SearchCardNumber the SearchCardNumber to set
     */
    public void setSearchCardNumber(String SearchCardNumber) {
        this.SearchCardNumber = SearchCardNumber;
    }

    /**
     * @return the TypedCardNumber
     */
    public String getTypedCardNumber() {
        return TypedCardNumber;
    }

    /**
     * @param TypedCardNumber the TypedCardNumber to set
     */
    public void setTypedCardNumber(String TypedCardNumber) {
        this.TypedCardNumber = TypedCardNumber;
    }
}
