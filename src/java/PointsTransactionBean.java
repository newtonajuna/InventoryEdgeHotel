
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
public class PointsTransactionBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<PointsTransaction> PointsTransactions;
    private String ActionMessage=null;
    private PointsTransaction SelectedPointsTransaction=null;
    private long SelectedPointsTransactionId;
    private String SearchCardNumber="";
    private String TypedCardNumber;
    PointsCard NewPointsCard=null;
    PointsCardBean NewPointsCardBean=null;
    
    public void savePointsTransaction(PointsTransaction pointsTransaction) {
        String sql = null;
        String msg="";
        
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        
        if (pointsTransaction.getPointsTransactionId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"INTER BRANCH", "Add")==0) {
            msg="INTER BRANCH : YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (pointsTransaction.getPointsTransactionId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"INTER BRANCH", "Edit")==0) {
            msg="INTER BRANCH : YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(pointsTransaction.getTransactionDate()==null){
            msg="Select Transaction Date";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(pointsTransaction.getPointsCardId()==0){
            msg="Invalid Points Card Number";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(pointsTransaction.getTransactionId()==0){
            msg="Invalid Transaction Reference Please";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(pointsTransaction.getTransBranchId()==0){
            msg="Select transaction branch please";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {     
            if (pointsTransaction.getPointsTransactionId() == 0) {
                sql = "{call sp_insert_points_transaction(?,?,?,?,?,?,?,?,?,?,?)}";
            } else if (pointsTransaction.getPointsTransactionId() > 0) {
                sql = "{call sp_update_points_transaction(?,?,?,?,?,?,?,?,?,?)}";
            }
            try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (pointsTransaction.getPointsTransactionId() == 0) {
                cs.setLong("in_points_card_id", pointsTransaction.getPointsCardId());       
                cs.setDate("in_transaction_date", new java.sql.Date(pointsTransaction.getTransactionDate().getTime()));
                cs.setFloat("in_points_awarded", pointsTransaction.getPointsAwarded());
                cs.setFloat("in_points_spent", pointsTransaction.getPointsSpent());
                cs.setLong("in_transaction_id", pointsTransaction.getTransactionId());
                cs.setInt("in_trans_branch_id", pointsTransaction.getTransBranchId());
                cs.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_add_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_edit_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setFloat("in_points_spent_amount", pointsTransaction.getPointsSpentAmount());
                
                cs.executeUpdate();
                this.clearPointsTransaction(pointsTransaction);
                this.setActionMessage("Saved Successfully");
                
            } else if (pointsTransaction.getPointsTransactionId() > 0) {
                cs.setLong("in_points_transaction_id", pointsTransaction.getPointsTransactionId());
                cs.setLong("in_points_card_id", pointsTransaction.getPointsCardId());   
                cs.setDate("in_transaction_date", new java.sql.Date(pointsTransaction.getTransactionDate().getTime()));
                cs.setFloat("in_points_awarded", pointsTransaction.getPointsAwarded());
                cs.setFloat("in_points_spent", pointsTransaction.getPointsSpent());
                cs.setLong("in_transaction_id", pointsTransaction.getTransactionId());
                cs.setInt("in_trans_branch_id", pointsTransaction.getTransBranchId());
                cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_edit_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setFloat("in_points_spent_amount", pointsTransaction.getPointsSpentAmount());
                
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearPointsTransaction(pointsTransaction);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Points Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Points Transaction NOT saved!"));
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
    }
    }
    
    public void addPointsTransactionToLive(PointsTransaction pointsTransaction) {
        String sql = null;
        if(pointsTransaction.getPointsCardId()==0){
            //do nothing
        }else if(pointsTransaction.getTransactionId()==0){
            //do nothing
        }else if(pointsTransaction.getTransBranchId()==0){
            //do nothing
        }else
        {     
            sql = "{call sp_insert_points_transaction(?,?,?,?,?,?,?,?,?,?,?)}";
            try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setLong("in_points_card_id", pointsTransaction.getPointsCardId());       
                cs.setDate("in_transaction_date", new java.sql.Date(pointsTransaction.getTransactionDate().getTime()));
                cs.setFloat("in_points_awarded", pointsTransaction.getPointsAwarded());
                cs.setFloat("in_points_spent", pointsTransaction.getPointsSpent());
                cs.setLong("in_transaction_id", pointsTransaction.getTransactionId());
                cs.setInt("in_trans_branch_id", pointsTransaction.getTransBranchId());
                cs.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_add_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_edit_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setFloat("in_points_spent_amount", pointsTransaction.getPointsSpentAmount());
                cs.executeUpdate();
                //update balance
                NewPointsCardBean=new PointsCardBean();
                NewPointsCardBean.addPointsCardBalanceByCardId(pointsTransaction.getPointsCardId(), pointsTransaction.getPointsSpent()*(-1));
                NewPointsCardBean.addPointsCardBalanceByCardId(pointsTransaction.getPointsCardId(), pointsTransaction.getPointsAwarded());
                NewPointsCardBean=null;
                //this.clearPointsTransaction(pointsTransaction);  
            }catch (SQLException se) {
                System.err.println(se.getMessage());
            }catch(NullPointerException npe){
                System.err.println(npe.getMessage());
                FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            }
    }
    }
    
    public void addPointsTransactionToStage(PointsTransaction pointsTransaction) {
        String sql = null;

        if(pointsTransaction.getPointsCardId()==0){
            //do nothing
        }else if(pointsTransaction.getTransactionId()==0){
            //do nothing
        }else if(pointsTransaction.getTransBranchId()==0){
            //do nothing
        }else
        {     
            sql = "{call sp_insert_stage_points_transaction(?,?,?,?,?,?,?,?,?,?,?)}";
            try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setLong("in_points_card_id", pointsTransaction.getPointsCardId());       
                cs.setDate("in_transaction_date", new java.sql.Date(pointsTransaction.getTransactionDate().getTime()));
                cs.setFloat("in_points_awarded", pointsTransaction.getPointsAwarded());
                cs.setFloat("in_points_spent", pointsTransaction.getPointsSpent());
                cs.setLong("in_transaction_id", pointsTransaction.getTransactionId());
                cs.setInt("in_trans_branch_id", pointsTransaction.getTransBranchId());
                cs.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_add_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_edit_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setFloat("in_points_spent_amount", pointsTransaction.getPointsSpentAmount());
                cs.executeUpdate();
                //this.clearPointsTransaction(pointsTransaction);  
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Local-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
    }
    }
    
    public void movePointsTransactionsFromStageToLive() {
        String sql;
        sql = "{call sp_search_stage_points_transaction_by_none()}";
        ResultSet rs = null;

        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                PointsTransaction pointsTransaction = new PointsTransaction();
                pointsTransaction.setPointsTransactionId(rs.getLong("stage_points_transaction_id"));
                pointsTransaction.setPointsCardId(rs.getLong("points_card_id"));
                pointsTransaction.setTransactionDate(new Date(rs.getDate("transaction_date").getTime()));
                pointsTransaction.setPointsAwarded(rs.getFloat("points_awarded"));
                pointsTransaction.setPointsSpent(rs.getFloat("points_spent"));
                pointsTransaction.setTransactionId(rs.getLong("transaction_id"));
                pointsTransaction.setTransBranchId(rs.getInt("trans_branch_id"));
                pointsTransaction.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsTransaction.setAddUser(rs.getString("add_user"));
                pointsTransaction.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsTransaction.setEditUser(rs.getString("edit_user"));
                pointsTransaction.setPointsSpentAmount(rs.getFloat("points_spent_amount"));
                this.addPointsTransactionToLive(pointsTransaction);
                this.deletePointsTransactionFromStage(pointsTransaction);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Local/Inter - Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
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
    
    public void deletePointsTransactionFromStage(PointsTransaction pointsTransaction) {
        String sql = null;
        
        if(pointsTransaction.getPointsCardId()==0){
            //do nothing
        }else if(pointsTransaction.getTransactionId()==0){
            //do nothing
        }else if(pointsTransaction.getTransBranchId()==0){
            //do nothing
        }else
        {     
            sql = "{call sp_delete_stage_points_transaction_by_trans_id(?)}";
            try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setLong("in_transaction_id", pointsTransaction.getTransactionId());
                cs.executeUpdate();  
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Local-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
        }
    }
    
    public void cancelPointsTransaction(PointsTransaction pointsTransaction) {
        String sql = null;

        if(pointsTransaction.getPointsCardId()==0){
            //do nothing
        }else if(pointsTransaction.getTransactionId()==0){
            //do nothing
        }else if(pointsTransaction.getTransBranchId()==0){
            //do nothing
        }else
        {     
            sql = "{call sp_insert_points_transaction(?,?,?,?,?,?,?,?,?,?,?)}";
            try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setLong("in_points_card_id", pointsTransaction.getPointsCardId());       
                cs.setDate("in_transaction_date", new java.sql.Date(new java.util.Date().getTime()));
                cs.setFloat("in_points_awarded", pointsTransaction.getPointsAwarded()*(-1));
                cs.setFloat("in_points_spent", pointsTransaction.getPointsSpent()*(-1));
                cs.setLong("in_transaction_id", pointsTransaction.getTransactionId());
                cs.setInt("in_trans_branch_id", pointsTransaction.getTransBranchId());
                cs.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_add_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_edit_user", new GeneralUserSetting().getCurrentUserNames());
                cs.setFloat("in_points_spent_amount", pointsTransaction.getPointsSpentAmount()*(-1));
                
                cs.executeUpdate();
                this.clearPointsTransaction(pointsTransaction);  
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
    }
    }
    
    public PointsTransaction getPointsTransaction(long PointsTransactionId) {
        String sql = "{call sp_search_points_transaction_by_id(?)}";
        ResultSet rs = null;
        PointsCard pc=new PointsCard();
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, PointsTransactionId);
                rs = ps.executeQuery();            
            if (rs.next()) {
                PointsTransaction pointsTransaction = new PointsTransaction();
                pointsTransaction.setPointsTransactionId(rs.getLong("points_transaction_id"));
                pointsTransaction.setPointsCardId(rs.getLong("points_card_id"));
                pointsTransaction.setTransactionDate(new Date(rs.getDate("transaction_date").getTime()));
                pointsTransaction.setPointsAwarded(rs.getFloat("points_awarded"));
                pointsTransaction.setPointsSpent(rs.getFloat("points_spent"));
                pointsTransaction.setTransactionId(rs.getLong("transaction_id"));
                pointsTransaction.setTransBranchId(rs.getInt("trans_branch_id"));
                pointsTransaction.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsTransaction.setAddUser(rs.getString("add_user"));
                pointsTransaction.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsTransaction.setEditUser(rs.getString("edit_user"));
                pointsTransaction.setPointsSpentAmount(rs.getFloat("points_spent_amount"));
                return pointsTransaction;
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
    
    public void deletePointsTransaction(PointsTransaction pointsTransaction) {
        String sql = "DELETE FROM points_transaction WHERE points_transaction_id=?";
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, pointsTransaction.getPointsTransactionId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearPointsTransaction(pointsTransaction);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("PointsTransaction NOT deleted");
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
    }

    public void clearPointsTransaction(PointsTransaction pointsTransaction) {
                pointsTransaction.setPointsTransactionId(0);
                pointsTransaction.setPointsCardId(0);
                //pointsTransaction.setTransactionDate(new Date(rs.getDate("transaction_date").getTime()));
                pointsTransaction.setPointsAwarded(0);
                pointsTransaction.setPointsSpent(0);
                pointsTransaction.setTransactionId(0);
                pointsTransaction.setTransBranchId(0);
                //pointsTransaction.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsTransaction.setAddUser("");
                //pointsTransaction.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsTransaction.setEditUser("");
    }
    /**
     * @param CardId
     * @return the PointsTransactions
     */
    public List<PointsTransaction> getPointsTransactions(long CardId) {
        String sql;
        sql = "{call sp_search_points_transaction_by_card_id(?)}";
        ResultSet rs = null;
        PointsTransactions = new ArrayList<PointsTransaction>();
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, CardId);
            rs = ps.executeQuery();
            while (rs.next()) {
                PointsTransaction pointsTransaction = new PointsTransaction();
                pointsTransaction.setPointsTransactionId(rs.getLong("points_transaction_id"));
                pointsTransaction.setPointsCardId(rs.getLong("points_card_id"));
                pointsTransaction.setTransactionDate(new Date(rs.getDate("transaction_date").getTime()));
                pointsTransaction.setPointsAwarded(rs.getFloat("points_awarded"));
                pointsTransaction.setPointsSpent(rs.getFloat("points_spent"));
                pointsTransaction.setTransactionId(rs.getLong("transaction_id"));
                pointsTransaction.setTransBranchId(rs.getInt("trans_branch_id"));
                pointsTransaction.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                pointsTransaction.setAddUser(rs.getString("add_user"));
                pointsTransaction.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                pointsTransaction.setEditUser(rs.getString("edit_user"));
                pointsTransaction.setPointsSpentAmount(rs.getFloat("points_spent_amount"));
                PointsTransactions.add(pointsTransaction);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Connection", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            PointsTransactions.clear();
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return PointsTransactions;
    }

    /**
     * @param PointsTransactions the PointsTransactions to set
     */
    public void setPointsTransactions(List<PointsTransaction> PointsTransactions) {
        this.PointsTransactions = PointsTransactions;
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
     * @return the SelectedPointsTransaction
     */
    public PointsTransaction getSelectedPointsTransaction() {
        return SelectedPointsTransaction;
    }

    /**
     * @param SelectedPointsTransaction the SelectedPointsTransaction to set
     */
    public void setSelectedPointsTransaction(PointsTransaction SelectedPointsTransaction) {
        this.SelectedPointsTransaction = SelectedPointsTransaction;
    }

    /**
     * @return the SelectedPointsTransactionId
     */
    public long getSelectedPointsTransactionId() {
        return SelectedPointsTransactionId;
    }

    /**
     * @param SelectedPointsTransactionId the SelectedPointsTransactionId to set
     */
    public void setSelectedPointsTransactionId(long SelectedPointsTransactionId) {
        this.SelectedPointsTransactionId = SelectedPointsTransactionId;
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
