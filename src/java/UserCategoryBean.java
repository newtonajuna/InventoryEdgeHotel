
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
public class UserCategoryBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<UserCategory> UserCategories;
    private String ActionMessage=null;
    private UserCategory SelectedUserCategory=null;
    private int SelectedUserCategoryId;
    private String SearchUserCategoryName="";
    
    public void saveUserCategory(UserCategory usercat) {
        String sql = null;
        String msg=null;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

      if (usercat.getUserCategoryId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else if (usercat.getUserCategoryId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else{
        if (usercat.getUserCategoryId() == 0) {
            sql = "{call sp_insert_user_category(?)}";
        } else if (usercat.getUserCategoryId() > 0) {
            sql = "{call sp_update_user_category(?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (usercat.getUserCategoryId() == 0) {
                cs.setString(1, usercat.getUserCategoryName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearUserCategory(usercat);
            } else if (usercat.getUserCategoryId() > 0) {
                cs.setInt(1, usercat.getUserCategoryId());
                cs.setString(2, usercat.getUserCategoryName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearUserCategory(usercat);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("UserCategory NOT saved");
        }
     }   
        
    }
    
    public UserCategory getUserCategory(int aUserCategoryId) {
        String sql = "{call sp_search_user_category_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aUserCategoryId);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserCategory usercat = new UserCategory();
                usercat.setUserCategoryId(rs.getInt("user_category_id"));
                usercat.setUserCategoryName(rs.getString("user_category_name"));
                return usercat;
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
    
    public void deleteUserCategory() {
         this.deleteUserCategoryById(this.SelectedUserCategoryId);
    }
    
    public void deleteUserCategoryByObject(UserCategory UserCat) {
         this.deleteUserCategoryById(UserCat.getUserCategoryId());
    }

    public void deleteUserCategoryById(int aUserCategoryId) {
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
        String sql = "DELETE FROM user_category WHERE user_category_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aUserCategoryId);
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Category NOT deleted");
        }
    }
    }
    
    public void displayUserCategory(UserCategory UserCatFrom, UserCategory UserCatTo) {
        UserCatTo.setUserCategoryId(UserCatFrom.getUserCategoryId());
        UserCatTo.setUserCategoryName(UserCatFrom.getUserCategoryName());
    }

    public void clearUserCategory(UserCategory UserCat) {
        UserCat.setUserCategoryId(0);
        UserCat.setUserCategoryName("");
    }
    
    public List<UserCategory> getUserCategories() {
        String sql;
        sql="{call sp_search_user_category_by_none()}";
        ResultSet rs = null;
        UserCategories = new ArrayList<UserCategory>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                UserCategory usercat = new UserCategory();
                usercat.setUserCategoryId(rs.getInt("user_category_id"));
                usercat.setUserCategoryName(rs.getString("user_category_name"));
                UserCategories.add(usercat);
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
        return UserCategories;
    }

    public List<UserCategory> getUserCategoriesByCatSubcatName(String aCategorySubcategoryName) {
        String sql;
        sql="{call sp_search_user_category_by_name(?)}";
        ResultSet rs = null;
        UserCategories = new ArrayList<UserCategory>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aCategorySubcategoryName);
            rs = ps.executeQuery();
            while (rs.next()) {
                UserCategory usercat = new UserCategory();
                usercat.setUserCategoryId(rs.getInt("user_category_id"));
                usercat.setUserCategoryName(rs.getString("user_category_name"));
                UserCategories.add(usercat);
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
        return UserCategories;
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
     * @return the UserCategories
     */

    /**
     * @param UserCategories the UserCategories to set
     */
    public void setUserCategories(List<UserCategory> UserCategories) {
        this.UserCategories = UserCategories;
    }

    /**
     * @return the SelectedUserCategory
     */
    public UserCategory getSelectedUserCategory() {
        return SelectedUserCategory;
    }

    /**
     * @param SelectedUserCategory the SelectedUserCategory to set
     */
    public void setSelectedUserCategory(UserCategory SelectedUserCategory) {
        this.SelectedUserCategory = SelectedUserCategory;
    }

    /**
     * @return the SelectedUserCategoryId
     */
    public int getSelectedUserCategoryId() {
        return SelectedUserCategoryId;
    }

    /**
     * @param SelectedUserCategoryId the SelectedUserCategoryId to set
     */
    public void setSelectedUserCategoryId(int SelectedUserCategoryId) {
        this.SelectedUserCategoryId = SelectedUserCategoryId;
    }

    /**
     * @return the SearchUserCategoryName
     */
    public String getSearchUserCategoryName() {
        return SearchUserCategoryName;
    }

    /**
     * @param SearchUserCategoryName the SearchUserCategoryName to set
     */
    public void setSearchUserCategoryName(String SearchUserCategoryName) {
        this.SearchUserCategoryName = SearchUserCategoryName;
    }
}
