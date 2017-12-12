
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class TransactionReasonBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<TransactionReason> TransactionReasons;
    private String ActionMessage=null;
    private TransactionReason SelectedTransactionReason=null;
    private int SelectedTransactionReasonId;
    private String SearchTransactionReasonName="";
    
    public void saveTransactionReason(TransactionReason tr) {
        String sql = null;
        String msg=null;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

      if (tr.getTransactionReasonId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else if (tr.getTransactionReasonId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else {
        if (tr.getTransactionReasonId() == 0) {
            sql = "{call sp_insert_transaction_reason(?,?)}";
        } else if (tr.getTransactionReasonId() > 0) {
            sql = "{call sp_update_transaction_reason(?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (tr.getTransactionReasonId() == 0) {
                cs.setInt(1, tr.getTransactionTypeId());
                cs.setString(2, tr.getTransactionReasonName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
            } else if (tr.getTransactionReasonId() > 0) {
                cs.setInt(1, tr.getTransactionReasonId());
                cs.setInt(2, tr.getTransactionTypeId());
                cs.setString(3, tr.getTransactionReasonName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("TransactionReason NOT saved");
        }
      } 
    }
    
    public TransactionReason getTransactionReason(int TraReasId) {
        String sql = "SELECT * FROM transaction_reason WHERE transaction_reason_id=?";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, TraReasId);
            rs = ps.executeQuery();
            if (rs.next()) {
                TransactionReason tr = new TransactionReason();
                tr.setTransactionReasonId(rs.getInt("transaction_reason_id"));
                tr.setTransactionTypeId(rs.getInt("transaction_type_id"));
                tr.setTransactionReasonName(rs.getString("transaction_reason_name"));
                return tr;
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
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
    
    public void deleteTransactionReason() {
         this.deleteTransactionReasonById(this.SelectedTransactionReasonId);
    }
    
    public void deleteTransactionReasonByObject(TransactionReason TraReas) {
         this.deleteTransactionReasonById(TraReas.getTransactionReasonId());
    }

    public void deleteTransactionReasonById(int TraReasId) {
        String sql = "DELETE FROM transaction_reason WHERE transaction_reason_id=?";
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, TraReasId);
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Transaction Reason NOT deleted");
        }
        }
    }
    
    public void displayTransactionReason(TransactionReason TraReasFrom, TransactionReason TraReasTo) {
        TraReasTo.setTransactionReasonId(TraReasFrom.getTransactionReasonId());
        TraReasTo.setTransactionTypeId(TraReasFrom.getTransactionTypeId());
        TraReasTo.setTransactionReasonName(TraReasFrom.getTransactionReasonName());
    }

    public void clearTransactionReason(TransactionReason TraReas) {
        TraReas.setTransactionReasonId(0);
        TraReas.setTransactionTypeId(0);
        TraReas.setTransactionTypeName("");
        TraReas.setTransactionReasonName("");
    }

    public List<TransactionReason> getTransactionReasons() {
        String sql;
        if(this.SearchTransactionReasonName.length()>0)
        {
            sql = "SELECT * FROM transaction_reason INNER JOIN transaction_type ON transaction_reason.transaction_type_id=transaction_type.transaction_type_id AND (transaction_reason_name LIKE '%" + this.SearchTransactionReasonName + "%' OR transaction_type_name LIKE '%" + this.SearchTransactionReasonName + "%') ORDER BY transaction_type_name,transaction_reason_name ASC";
        }else
        {
            sql = "SELECT * FROM transaction_reason INNER JOIN transaction_type ON transaction_reason.transaction_type_id=transaction_type.transaction_type_id ORDER BY transaction_type_name,transaction_reason_name ASC";
        }
        ResultSet rs = null;
        TransactionReasons = new ArrayList<TransactionReason>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                TransactionReason tr = new TransactionReason();
                tr.setTransactionReasonId(rs.getInt("transaction_reason_id"));
                tr.setTransactionTypeId(rs.getInt("transaction_type_id"));
                tr.setTransactionTypeName(rs.getString("transaction_type_name"));
                tr.setTransactionReasonName(rs.getString("transaction_reason_name"));
                TransactionReasons.add(tr);
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
        return TransactionReasons;
    }
    
    public List<TransactionReason> getTransactionReasons(int TraTypeId) {
        String sql;
        
        sql = "SELECT * FROM transaction_reason WHERE transaction_type_id=" + TraTypeId + " ORDER BY transaction_reason_name ASC";
        ResultSet rs = null;
        TransactionReasons = new ArrayList<TransactionReason>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                TransactionReason tr = new TransactionReason();
                tr.setTransactionReasonId(rs.getInt("transaction_reason_id"));
                tr.setTransactionTypeId(rs.getInt("transaction_type_id"));
                tr.setTransactionReasonName(rs.getString("transaction_reason_name"));
                TransactionReasons.add(tr);
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
        return TransactionReasons;
    }
        
    /**
     * @return the ActionMessage
     */
    public String getActionMessage() {
        return ActionMessage;
    }

    /**
     * @param aActionMessage the ActionMessage to set
     */
    public void setActionMessage(String aActionMessage) {
        this.ActionMessage = aActionMessage;
    }
    
    /**
     * @return the TransactionReasons
     */

    /**
     * @param TransactionReasons the TransactionReasons to set
     */
    public void setTransactionReasons(List<TransactionReason> TransactionReasons) {
        this.TransactionReasons = TransactionReasons;
    }

    /**
     * @return the SelectedTransactionReason
     */
    public TransactionReason getSelectedTransactionReason() {
        return SelectedTransactionReason;
    }

    /**
     * @param SelectedTransactionReason the SelectedTransactionReason to set
     */
    public void setSelectedTransactionReason(TransactionReason SelectedTransactionReason) {
        this.SelectedTransactionReason = SelectedTransactionReason;
    }

    /**
     * @return the SelectedTransactionReasonId
     */
    public int getSelectedTransactionReasonId() {
        return SelectedTransactionReasonId;
    }

    /**
     * @param SelectedTransactionReasonId the SelectedTransactionReasonId to set
     */
    public void setSelectedTransactionReasonId(int SelectedTransactionReasonId) {
        this.SelectedTransactionReasonId = SelectedTransactionReasonId;
    }

    /**
     * @return the SearchTransactionReasonName
     */
    public String getSearchTransactionReasonName() {
        return SearchTransactionReasonName;
    }

    /**
     * @param SearchTransactionReasonName the SearchTransactionReasonName to set
     */
    public void setSearchTransactionReasonName(String SearchTransactionReasonName) {
        this.SearchTransactionReasonName = SearchTransactionReasonName;
    }
}
