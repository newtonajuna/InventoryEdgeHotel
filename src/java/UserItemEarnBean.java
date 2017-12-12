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
public class UserItemEarnBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<UserItemEarn> UserItemEarnsAll;
    private String ActionMessage = null;
    private UserItemEarn SelectedUserItemEarn = null;
    private UserItemEarn SelectedUserItemEarnX = null;
    private long SelectedUserItemEarnId;

    public void saveUserItemEarn(UserItemEarn uie) {
        String sql = null;
        String msg = "";
        String sql2 = null;
        if (uie.getItemSubCategoryId() > 0) {
            sql2 = "SELECT * FROM user_item_earn WHERE transaction_type_id=" + uie.getTransactionTypeId() + " and user_category_id=" + uie.getUserCategoryId() + " and item_category_id=" + uie.getItemCategoryId() + " and item_sub_category_id=" + uie.getItemSubCategoryId();
        } else {
            sql2 = "SELECT * FROM user_item_earn WHERE transaction_type_id=" + uie.getTransactionTypeId() + " and user_category_id=" + uie.getUserCategoryId() + " and item_category_id=" + uie.getItemCategoryId();
        }

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (uie.getUserItemEarnId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (uie.getUserItemEarnId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (uie.getItemCategoryId() == 0) {
            msg = "Select a valid Item Category please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (uie.getUserCategoryId() == 0) {
            msg = "Select a valid User Category please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (uie.getTransactionTypeId() == 0) {
            msg = "Select a valid Transaction Type please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (uie.getTransactionReasonId() == 0) {
            msg = "Select a valid Transaction Reason please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (uie.getEarnPerc() < 0) {
            msg = "Enter a valid Earn Percentage";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ((new CustomValidator().CheckRecords(sql2) > 0 && uie.getUserItemEarnId() == 0) || (new CustomValidator().CheckRecords(sql2) > 0 && new CustomValidator().CheckRecords(sql2) != 1 && uie.getUserItemEarnId() > 0)) {
            msg = "Transaction Type, User category, Item Category already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {

            if (uie.getUserItemEarnId() == 0) {
                sql = "{call sp_insert_user_item_earn(?,?,?,?,?,?)}";
            } else if (uie.getUserItemEarnId() > 0) {
                sql = "{call sp_update_user_item_earn(?,?,?,?,?,?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (uie.getUserItemEarnId() == 0) {
                    cs.setInt("in_transaction_type_id", uie.getTransactionTypeId());
                    cs.setInt("in_transaction_reason_id", uie.getTransactionReasonId());
                    cs.setInt("in_user_category_id", uie.getUserCategoryId());
                    cs.setInt("in_item_category_id", uie.getItemCategoryId());
                    cs.setInt("in_item_sub_category_id", uie.getItemSubCategoryId());
                    cs.setFloat("in_earn_perc", uie.getEarnPerc());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearUserItemEarn(uie);
                } else if (uie.getUserItemEarnId() > 0) {
                    cs.setLong("in_user_item_earn_id", uie.getUserItemEarnId());
                    cs.setInt("in_transaction_type_id", uie.getTransactionTypeId());
                    cs.setInt("in_transaction_reason_id", uie.getTransactionReasonId());
                    cs.setInt("in_user_category_id", uie.getUserCategoryId());
                    cs.setInt("in_item_category_id", uie.getItemCategoryId());
                    cs.setInt("in_item_sub_category_id", uie.getItemSubCategoryId());
                    cs.setFloat("in_earn_perc", uie.getEarnPerc());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearUserItemEarn(uie);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("UserItemEarn NOT saved");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("UserItemEarn NOT saved!"));
            }
        }
    }

    public UserItemEarn getUserItemEarn(long aUserItemEarnId) {
        String sql = "{call sp_search_user_item_earn_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aUserItemEarnId);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserItemEarn uie = new UserItemEarn();
                uie.setUserItemEarnId(rs.getLong("user_item_earn_id"));
                uie.setTransactionTypeId(rs.getInt("transaction_type_id"));
                uie.setTransactionReasonId(rs.getInt("transaction_reason_id"));
                uie.setUserCategoryId(rs.getInt("user_category_id"));
                uie.setItemCategoryId(rs.getInt("item_category_id"));
                uie.setItemSubCategoryId(rs.getInt("item_sub_category_id"));
                uie.setEarnPerc(rs.getFloat("earn_perc"));
                return uie;
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
    
    public UserItemEarn getUserItemEarnByTtypeTreasIcatIsubcatUcat(int aTransTypeId,int aTransReasId,int aItemCatId,int aItemSubCatId,int aUserCatId) {
        String sql = "{call sp_search_user_item_earn_by_ttype_treas_icat_isubcat_ucat(?,?,?,?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aTransTypeId);
            ps.setInt(2, aTransReasId);
            ps.setInt(3, aItemCatId);
            ps.setInt(4, aItemSubCatId);
            ps.setInt(5, aUserCatId);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserItemEarn uie = new UserItemEarn();
                uie.setUserItemEarnId(rs.getLong("user_item_earn_id"));
                uie.setTransactionTypeId(rs.getInt("transaction_type_id"));
                uie.setTransactionReasonId(rs.getInt("transaction_reason_id"));
                uie.setUserCategoryId(rs.getInt("user_category_id"));
                uie.setItemCategoryId(rs.getInt("item_category_id"));
                uie.setItemSubCategoryId(rs.getInt("item_sub_category_id"));
                uie.setEarnPerc(rs.getFloat("earn_perc"));
                return uie;
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

    public void deleteUserItemEarn(UserItemEarn uie) {
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            String sql = "DELETE FROM user_item_earn WHERE user_item_earn_id=?";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, uie.getUserItemEarnId());
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
                this.clearUserItemEarn(uie);
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("UserItemEarn NOT deleted");
            }
        }
    }

    public void displayUserItemEarn(UserItemEarn UserItemEarnFrom, UserItemEarn UserItemEarnTo) {
        UserItemEarnTo.setUserItemEarnId(UserItemEarnFrom.getUserItemEarnId());
        UserItemEarnTo.setUserCategoryId(UserItemEarnFrom.getUserCategoryId());
        UserItemEarnTo.setTransactionTypeId(UserItemEarnFrom.getTransactionTypeId());
        UserItemEarnTo.setTransactionReasonId(UserItemEarnFrom.getTransactionReasonId());
        UserItemEarnTo.setItemCategoryId(UserItemEarnFrom.getItemCategoryId());
        UserItemEarnTo.setItemSubCategoryId(UserItemEarnFrom.getItemSubCategoryId());
        UserItemEarnTo.setEarnPerc(UserItemEarnFrom.getEarnPerc());
    }

    public void clearUserItemEarn(UserItemEarn uie) {
        if (uie != null) {
            uie.setUserItemEarnId(0);
            uie.setTransactionTypeId(0);
            uie.setTransactionReasonId(0);
            uie.setUserCategoryId(0);
            uie.setItemCategoryId(0);
            uie.setItemSubCategoryId(0);
            uie.setEarnPerc(0);
        }
    }

    public void initClearUserItemEarn(UserItemEarn uie) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            if (uie != null) {
                uie.setUserItemEarnId(0);
                uie.setTransactionTypeId(0);
                uie.setTransactionReasonId(0);
                uie.setUserCategoryId(0);
                uie.setItemCategoryId(0);
                uie.setItemSubCategoryId(0);
                uie.setEarnPerc(0);
            }
        }
    }

    public void clearSelectedUserItemEarn() {
        this.clearUserItemEarn(this.getSelectedUserItemEarn());
    }

    public List<UserItemEarn> getUserItemEarnsAll() {
        String sql = "{call sp_search_user_item_earn_by_none()}";
        ResultSet rs = null;
        UserItemEarnsAll = new ArrayList<UserItemEarn>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                UserItemEarn uie = new UserItemEarn();
                uie.setUserItemEarnId(rs.getLong("user_item_earn_id"));
                uie.setTransactionTypeId(rs.getInt("transaction_type_id"));
                uie.setTransactionReasonId(rs.getInt("transaction_reason_id"));
                uie.setUserCategoryId(rs.getInt("user_category_id"));
                uie.setItemCategoryId(rs.getInt("item_category_id"));
                uie.setItemSubCategoryId(rs.getInt("item_sub_category_id"));
                uie.setEarnPerc(rs.getFloat("earn_perc"));
                UserItemEarnsAll.add(uie);
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
        return UserItemEarnsAll;
    }

    /**
     * @param UserItemEarnsAll the UserItemEarnsAll to set
     */
    public void setUserItemEarnsAll(List<UserItemEarn> UserItemEarnsAll) {
        this.UserItemEarnsAll = UserItemEarnsAll;
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
     * @return the SelectedUserItemEarnId
     */
    public long getSelectedUserItemEarnId() {
        return SelectedUserItemEarnId;
    }

    /**
     * @param SelectedUserItemEarnId the SelectedUserItemEarnId to set
     */
    public void setSelectedUserItemEarnId(long SelectedUserItemEarnId) {
        this.SelectedUserItemEarnId = SelectedUserItemEarnId;
    }

    /**
     * @return the SelectedUserItemEarn
     */
    public UserItemEarn getSelectedUserItemEarn() {
        return SelectedUserItemEarn;
    }

    /**
     * @param SelectedUserItemEarn the SelectedUserItemEarn to set
     */
    public void setSelectedUserItemEarn(UserItemEarn SelectedUserItemEarn) {
        this.SelectedUserItemEarn = SelectedUserItemEarn;
    }

    /**
     * @return the SelectedUserItemEarnX
     */
    public UserItemEarn getSelectedUserItemEarnX() {
        return SelectedUserItemEarnX;
    }

    /**
     * @param SelectedUserItemEarnX the SelectedUserItemEarnX to set
     */
    public void setSelectedUserItemEarnX(UserItemEarn SelectedUserItemEarnX) {
        this.SelectedUserItemEarnX = SelectedUserItemEarnX;
    }

}
