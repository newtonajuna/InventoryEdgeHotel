
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
public class UserRightBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<UserRight> UserRights;
    private String ActionMessage=null;
    private UserRight SelectedUserRight=null;
    private int SelectedUserRightId;
    private String SearchUserName="";

    public void saveUserRight(UserRight userright) {
        String sql = null;
        String msg="";
        String sql2 = null;
        sql2="SELECT * FROM user_right WHERE store_id=" + userright.getStoreId() + " and user_detail_id=" + userright.getUserDetailId() + " and function_name='" + userright.getFunctionName() + "'";
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (userright.getUserRightId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (userright.getUserRightId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(userright.getStoreId()==0){
            msg="Select a valid Store please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(userright.getUserDetailId()==0){
            msg="Select a valid User please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(userright.getFunctionName(), 1, 20).equals("FAIL")){
            msg="Select a valid Function Name!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(userright.getAllowView(), 2, 3).equals("FAIL")){
            msg="Select value for Allow View!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(userright.getAllowAdd(), 2, 3).equals("FAIL")){
            msg="Select value for Allow Add!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(userright.getAllowEdit(), 2, 3).equals("FAIL")){
            msg="Select value for Allow Edit!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(userright.getAllowDelete(), 2, 3).equals("FAIL")){
            msg="Select value for Allow Delete!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if((new CustomValidator().CheckRecords(sql2)>0 && userright.getUserRightId()==0) || (new CustomValidator().CheckRecords(sql2)!=1 && userright.getUserRightId()>0)){
            msg="Function Right for this user and in this store already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
            
        if (userright.getUserRightId() == 0) {
            sql = "{call sp_insert_user_right(?,?,?,?,?,?,?)}";
        } else if (userright.getUserRightId() > 0) {
            sql = "{call sp_update_user_right(?,?,?,?,?,?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (userright.getUserRightId() == 0) {
                cs.setInt(1, userright.getStoreId());
                cs.setInt(2, userright.getUserDetailId());
                cs.setString(3, userright.getFunctionName());
                cs.setString(4, userright.getAllowView());
                cs.setString(5, userright.getAllowAdd());
                cs.setString(6, userright.getAllowEdit());
                cs.setString(7, userright.getAllowDelete());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearUserRight(userright);
            } else if (userright.getUserRightId() > 0) {
                cs.setInt(1, userright.getUserRightId());
                cs.setInt(2, userright.getStoreId());
                cs.setInt(3, userright.getUserDetailId());
                cs.setString(4, userright.getFunctionName());
                cs.setString(5, userright.getAllowView());
                cs.setString(6, userright.getAllowAdd());
                cs.setString(7, userright.getAllowEdit());
                cs.setString(8, userright.getAllowDelete());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearUserRight(userright);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("UserRight NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("UserRight NOT saved!"));
        }
    }
    }
    
    public UserRight getUserRight(int UserRightId) {
        String sql = "SELECT * FROM user_right WHERE user_right_id=?";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, UserRightId);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserRight userright = new UserRight();
                userright.setUserRightId(rs.getInt("user_detail_id"));
                userright.setStoreId(rs.getInt("store_id"));
                userright.setUserDetailId(rs.getInt("user_detail_id"));
                userright.setFunctionName(rs.getString("function_name"));
                userright.setAllowView(rs.getString("allow_view"));
                userright.setAllowAdd(rs.getString("allow_add"));
                userright.setAllowEdit(rs.getString("allow_edit"));
                userright.setAllowDelete(rs.getString("allow_delete"));
                return userright;
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

    public void deleteUserRight(UserRight userright) {
        String sql = "DELETE FROM user_right WHERE user_right_id=?";
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
            ps.setInt(1, userright.getUserRightId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearUserRight(userright);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("UserRight NOT deleted");
        }
    }
    }

    public void displayUserRight(UserRight UserRightFrom, UserRight UserRightTo) {
        UserRightTo.setUserRightId(UserRightFrom.getUserRightId());
        UserRightTo.setStoreId(UserRightFrom.getStoreId());
            UserRightTo.setStoreName(UserRightFrom.getStoreName());
        UserRightTo.setUserDetailId(UserRightFrom.getUserDetailId());
            UserRightTo.setUsername(UserRightFrom.getUsername());
            UserRightTo.setUserNames(UserRightFrom.getUserNames());
        UserRightTo.setFunctionName(UserRightFrom.getFunctionName());
        UserRightTo.setAllowView(UserRightFrom.getAllowView());
        UserRightTo.setAllowAdd(UserRightFrom.getAllowAdd());
        UserRightTo.setAllowEdit(UserRightFrom.getAllowEdit());
        UserRightTo.setAllowDelete(UserRightFrom.getAllowDelete());
    }

    public void clearUserRight(UserRight userright) {
        userright.setUserRightId(0);
        
        userright.setStoreId(0);
        userright.setStoreName("");
        
        userright.setUserDetailId(0);
        userright.setUsername("");
        userright.setUserNames("");
        
        userright.setFunctionName("");
        
        userright.setAllowView("");
        userright.setAllowAdd("");
        userright.setAllowEdit("");
        userright.setAllowDelete("");
    }

    /**
     * @return the UserRights
     */
    public List<UserRight> getUserRights() {
        String sql;
        if(this.SearchUserName.length()>0)
        {
            sql = "SELECT * FROM user_right INNER JOIN user_detail ON user_right.user_detail_id=user_detail.user_detail_id INNER JOIN store ON user_right.store_id=store.store_id AND (user_name LIKE '%" + this.SearchUserName + "%' OR first_name LIKE '%" + this.SearchUserName + "%' OR second_name LIKE '%" + this.SearchUserName + "%' OR third_name LIKE '%" + this.SearchUserName + "%' OR store_name LIKE '%" + this.SearchUserName + "%') ORDER BY first_name,second_name,third_name,store_name,function_name ASC";
        }else
        {
            sql = "SELECT * FROM user_right INNER JOIN user_detail ON user_right.user_detail_id=user_detail.user_detail_id INNER JOIN store ON user_right.store_id=store.store_id ORDER BY first_name,second_name,third_name,store_name,function_name ASC";
  
        }
        
        ResultSet rs = null;
        UserRights = new ArrayList<UserRight>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                UserRight userright = new UserRight();
                
                userright.setUserRightId(rs.getInt("user_right_id"));
                
                userright.setStoreId(rs.getInt("store_id"));
                userright.setStoreName(rs.getString("store_name"));
                
                userright.setUserDetailId(rs.getInt("user_detail_id"));
                userright.setUsername(rs.getString("user_name"));
                userright.setUserNames(rs.getString("first_name") + " " + rs.getString("second_name") + " " + rs.getString("third_name"));
                
                userright.setFunctionName(rs.getString("function_name"));
                userright.setAllowView(rs.getString("allow_view"));
                userright.setAllowAdd(rs.getString("allow_add"));
                userright.setAllowEdit(rs.getString("allow_edit"));
                userright.setAllowDelete(rs.getString("allow_delete"));
                
                UserRights.add(userright);
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
        return UserRights;
    }

    /**
     * @param UserRights the UserRights to set
     */
    public void setUserRights(List<UserRight> UserRights) {
        this.UserRights = UserRights;
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
     * @return the SelectedUserRight
     */
    public UserRight getSelectedUserRight() {
        return SelectedUserRight;
    }

    /**
     * @param SelectedUserRight the SelectedUserRight to set
     */
    public void setSelectedUserRight(UserRight SelectedUserRight) {
        this.SelectedUserRight = SelectedUserRight;
    }

    /**
     * @return the SelectedUserRightId
     */
    public int getSelectedUserRightId() {
        return SelectedUserRightId;
    }

    /**
     * @param SelectedUserRightId the SelectedUserRightId to set
     */
    public void setSelectedUserRightId(int SelectedUserRightId) {
        this.SelectedUserRightId = SelectedUserRightId;
    }

    /**
     * @return the SearchUserName
     */
    public String getSearchUserName() {
        return SearchUserName;
    }

    /**
     * @param SearchUserName the SearchUserName to set
     */
    public void setSearchUserName(String SearchUserName) {
        this.SearchUserName = SearchUserName;
    }
    
}
