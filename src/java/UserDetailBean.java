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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
public class UserDetailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<UserDetail> UserDetails;
    private List<UserDetail> UserDetailObjectList;
    private String ActionMessage = null;
    private UserDetail SelectedUserDetail = null;
    private int SelectedUserDetailId;
    private String SearchUserName = "";

    public UserDetail findUserDetail(int aUserDetailId) {
        String sql = "{call sp_search_user_detail_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aUserDetailId);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserDetail userdetail = new UserDetail();
                userdetail.setUserDetailId(rs.getInt("user_detail_id"));
                userdetail.setUserName(rs.getString("user_name"));
                userdetail.setUserPassword(Security.Decrypt(rs.getString("user_password")));
                userdetail.setUserPasswordConfirm(Security.Decrypt(rs.getString("user_password")));
                userdetail.setFirstName(rs.getString("first_name"));
                userdetail.setSecondName(rs.getString("second_name"));
                userdetail.setThirdName(rs.getString("third_name"));
                userdetail.setIsUserLocked(rs.getString("is_user_locked"));
                userdetail.setIsUserGenAdmin(rs.getString("is_user_gen_admin"));
                userdetail.setUserCategoryId(rs.getInt("user_category_id"));
                //userdetail.setUserImgUrl(rs.getString("user_img_url"));
                return userdetail;
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

    public void saveUserDetail(UserDetail userdetail) {
        String sql = null;
        String msg = "";
        String sql2 = null;
        sql2 = "SELECT * FROM user_detail WHERE user_name='" + userdetail.getUserName() + "'";
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (userdetail.getUserDetailId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (userdetail.getUserDetailId() > 0 && new GeneralUserSetting().getChangePasswordAllowed() != 1 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (userdetail.getUserCategoryId() == 0) {
            msg = "Please select the User Category";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().CheckPassword(userdetail.getUserPassword(), userdetail.getUserPasswordConfirm()).equals("FAIL")) {
            msg = "Ensure that user passwords match and are between 5-to-20 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(userdetail.getUserName(), 5, 20).equals("FAIL")) {
            msg = "User Name MUST be between 5-to-20 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(userdetail.getFirstName(), 3, 100).equals("FAIL")) {
            msg = "First Name MUST be between 3-to-100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(userdetail.getSecondName(), 0, 100).equals("FAIL")) {
            msg = "Second Name cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(userdetail.getThirdName(), 0, 100).equals("FAIL")) {
            msg = "Third Name cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(userdetail.getIsUserLocked(), 2, 3).equals("FAIL")) {
            msg = "Please selct an option for Lock User? !";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(userdetail.getIsUserGenAdmin(), 2, 3).equals("FAIL")) {
            msg = "Please selct an option for General Admin !";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ((new CustomValidator().CheckRecords(sql2) > 0 && userdetail.getUserDetailId() == 0) || (new CustomValidator().CheckRecords(sql2) > 0 && new CustomValidator().CheckRecords(sql2) != 1 && userdetail.getUserDetailId() > 0)) {
            msg = "Username already exists, please enter a different username !";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (this.isLicensePackageViolated(userdetail, CompanySetting.getPackageUsers(CompanySetting.getPACKAGE_NAME()))) {
            msg = "YOUR CURRENT LICENSE PACKAGE LIMITS ADDITION OF MORE USERS, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {

            if (userdetail.getUserDetailId() == 0) {
                sql = "{call sp_insert_user_detail(?,?,?,?,?,?,?,?)}";
            } else if (userdetail.getUserDetailId() > 0) {
                sql = "{call sp_update_user_detail(?,?,?,?,?,?,?,?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (userdetail.getUserDetailId() == 0) {
                    cs.setString("in_user_name", userdetail.getUserName());
                    cs.setString("in_user_password", Security.Encrypt(userdetail.getUserPassword()));
                    cs.setString("in_first_name", userdetail.getFirstName());
                    cs.setString("in_second_name", userdetail.getSecondName());
                    cs.setString("in_third_name", userdetail.getThirdName());
                    cs.setString("in_is_user_locked", userdetail.getIsUserLocked());
                    cs.setString("in_is_user_gen_admin", userdetail.getIsUserGenAdmin());
                    cs.setInt("in_user_category_id", userdetail.getUserCategoryId());
                    //cs.setString(8, userdetail.getUserImgUrl());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearUserDetail(userdetail);

                } else if (userdetail.getUserDetailId() > 0) {
                    cs.setInt("in_user_detail_id", userdetail.getUserDetailId());
                    cs.setString("in_user_name", userdetail.getUserName());
                    cs.setString("in_user_password", Security.Encrypt(userdetail.getUserPassword()));
                    cs.setString("in_first_name", userdetail.getFirstName());
                    cs.setString("in_second_name", userdetail.getSecondName());
                    cs.setString("in_third_name", userdetail.getThirdName());
                    cs.setString("in_is_user_locked", userdetail.getIsUserLocked());
                    cs.setString("in_is_user_gen_admin", userdetail.getIsUserGenAdmin());
                    cs.setInt("in_user_category_id", userdetail.getUserCategoryId());
                    //cs.setString(9, userdetail.getUserImgUrl());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearUserDetail(userdetail);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("UserDetail NOT saved");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("UserDetail NOT saved!"));
            }
        }
    }

    public UserDetail getUserDetail(int aUserDetailId) {
        String sql = "{call sp_search_user_detail_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aUserDetailId);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserDetail userdetail = new UserDetail();
                userdetail.setUserDetailId(rs.getInt("user_detail_id"));
                userdetail.setUserName(rs.getString("user_name"));
                userdetail.setUserPassword(Security.Decrypt(rs.getString("user_password")));
                userdetail.setUserPasswordConfirm(Security.Decrypt(rs.getString("user_password")));
                userdetail.setFirstName(rs.getString("first_name"));
                userdetail.setSecondName(rs.getString("second_name"));
                userdetail.setThirdName(rs.getString("third_name"));
                userdetail.setIsUserLocked(rs.getString("is_user_locked"));
                userdetail.setIsUserGenAdmin(rs.getString("is_user_gen_admin"));
                userdetail.setUserCategoryId(rs.getInt("user_category_id"));
                //userdetail.setUserImgUrl(rs.getString("user_img_url"));
                return userdetail;
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

    public UserDetail getUserDetailByUserName(String UserName) {
        String sql = "{call sp_search_user_detail_by_username(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, UserName);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserDetail userdetail = new UserDetail();
                userdetail.setUserDetailId(rs.getInt("user_detail_id"));
                userdetail.setUserName(rs.getString("user_name"));
                userdetail.setUserPassword(Security.Decrypt(rs.getString("user_password")));
                userdetail.setUserPasswordConfirm(Security.Decrypt(rs.getString("user_password")));
                userdetail.setFirstName(rs.getString("first_name"));
                userdetail.setSecondName(rs.getString("second_name"));
                userdetail.setThirdName(rs.getString("third_name"));
                userdetail.setIsUserLocked(rs.getString("is_user_locked"));
                userdetail.setIsUserGenAdmin(rs.getString("is_user_gen_admin"));
                userdetail.setUserCategoryId(rs.getInt("user_category_id"));
                //userdetail.setUserImgUrl(rs.getString("user_img_url"));
                return userdetail;
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

    public static int getSystemUserDetailId() {
        String sql = "{call sp_search_user_detail_by_username(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, "system");
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_detail_id");
            } else {
                return 0;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return 0;
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

    public long getCountTotalActiveUserDetail() {
        String sql = "{call sp_search_user_detail_all_active()}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("total_user_count");
            } else {
                return 0;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return 1000000;
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

    public boolean isLicensePackageViolated(UserDetail aUserDetail, long PackageUsers) {
        long TotalActiveUsers = this.getCountTotalActiveUserDetail();
        if (aUserDetail.getUserDetailId() == 0) {//for new addition
            if (aUserDetail.getIsUserLocked().equals("Yes") && TotalActiveUsers > PackageUsers) {
                return true;
            } else if (aUserDetail.getIsUserLocked().equals("No") && (TotalActiveUsers + 1) > PackageUsers) {
                return true;
            } else {
                return false;
            }
        } else if (aUserDetail.getUserDetailId() > 0) {//for update
            if (aUserDetail.getIsUserLocked().equals("No")) {
                UserDetail UserDetailOld = new UserDetail();
                UserDetailOld = this.getUserDetail(aUserDetail.getUserDetailId());
                if (UserDetailOld.getIsUserLocked().equals("Yes") && (TotalActiveUsers + 1) > PackageUsers) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void deleteUserDetail(UserDetail userdetail) {
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            String sql = "DELETE FROM user_detail WHERE user_detail_id=?";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, userdetail.getUserDetailId());
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
                this.clearUserDetail(userdetail);
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("UserDetail NOT deleted");
            }
        }
    }

    public void displayUserDetail(UserDetail UserDetailFrom, UserDetail UserDetailTo) {
        UserDetailTo.setUserDetailId(UserDetailFrom.getUserDetailId());
        UserDetailTo.setUserName(UserDetailFrom.getUserName());
        UserDetailTo.setUserPassword(UserDetailFrom.getUserPassword());
        UserDetailTo.setUserPasswordConfirm(UserDetailFrom.getUserPasswordConfirm());
        UserDetailTo.setFirstName(UserDetailFrom.getFirstName());
        UserDetailTo.setSecondName(UserDetailFrom.getSecondName());
        UserDetailTo.setThirdName(UserDetailFrom.getThirdName());
        UserDetailTo.setIsUserLocked(UserDetailFrom.getIsUserLocked());
        UserDetailTo.setIsUserGenAdmin(UserDetailFrom.getIsUserGenAdmin());
        UserDetailTo.setUserCategoryId(UserDetailFrom.getUserCategoryId());
        //UserDetailTo.setUserImgUrl(UserDetailFrom.getUserImgUrl());
    }

    public void clearUserDetail(UserDetail userdetail) {
        if (userdetail != null) {
            userdetail.setUserDetailId(0);
            userdetail.setUserName("");
            userdetail.setUserPassword("");
            userdetail.setUserPasswordConfirm("");
            userdetail.setFirstName("");
            userdetail.setSecondName("");
            userdetail.setThirdName("");
            userdetail.setIsUserLocked("");
            userdetail.setIsUserGenAdmin("");
            userdetail.setUserCategoryId(0);
            //userdetail.setUserImgUrl("");
        }
    }

    public void clearUserDetailChange(UserDetail userdetail) {
        userdetail.setOldUserPassword("");
        userdetail.setNewUserPassword("");
        userdetail.setNewUserPasswordConfirm("");
    }

    /**
     * @param aSearchName
     * @return the UserDetails
     */
    public List<UserDetail> getUserDetailsByNames(String aSearchName) {
        String sql;
        sql = "{call sp_search_user_detail_by_names(?)}";
        ResultSet rs = null;
        UserDetails = new ArrayList<UserDetail>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aSearchName);
            rs = ps.executeQuery();
            while (rs.next()) {
                UserDetail userdetail = new UserDetail();
                userdetail.setUserDetailId(rs.getInt("user_detail_id"));
                userdetail.setUserName(rs.getString("user_name"));
                userdetail.setUserPassword(Security.Decrypt(rs.getString("user_password")));
                userdetail.setUserPasswordConfirm(Security.Decrypt(rs.getString("user_password")));
                userdetail.setFirstName(rs.getString("first_name"));
                userdetail.setSecondName(rs.getString("second_name"));
                userdetail.setThirdName(rs.getString("third_name"));
                userdetail.setIsUserLocked(rs.getString("is_user_locked"));
                userdetail.setIsUserGenAdmin(rs.getString("is_user_gen_admin"));
                userdetail.setUserCategoryId(rs.getInt("user_category_id"));
                //userdetail.setUserImgUrl(rs.getString("user_img_url"));
                UserDetails.add(userdetail);
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
        return UserDetails;
    }

    public List<UserDetail> getUserDetails() {
        String sql;
        sql = "{call sp_search_user_detail_by_none()}";
        ResultSet rs = null;
        UserDetails = new ArrayList<UserDetail>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                UserDetail userdetail = new UserDetail();
                userdetail.setUserDetailId(rs.getInt("user_detail_id"));
                userdetail.setUserName(rs.getString("user_name"));
                userdetail.setUserPassword(Security.Decrypt(rs.getString("user_password")));
                userdetail.setUserPasswordConfirm(Security.Decrypt(rs.getString("user_password")));
                userdetail.setFirstName(rs.getString("first_name"));
                userdetail.setSecondName(rs.getString("second_name"));
                userdetail.setThirdName(rs.getString("third_name"));
                userdetail.setIsUserLocked(rs.getString("is_user_locked"));
                userdetail.setIsUserGenAdmin(rs.getString("is_user_gen_admin"));
                userdetail.setUserCategoryId(rs.getInt("user_category_id"));
                //userdetail.setUserImgUrl(rs.getString("user_img_url"));
                UserDetails.add(userdetail);
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
        return UserDetails;
    }

    public void changePassword(UserDetail aUserDetail) {
        String msg;
        UserDetail OldUD = new GeneralUserSetting().getCurrentUser();

        UserDetail NewUD = new UserDetail();
        NewUD = aUserDetail;

        try {
            if (!NewUD.getOldUserPassword().equals(OldUD.getUserPassword())) {
                msg = "Old password is not correct...";
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
            } else if (!NewUD.getNewUserPassword().equals(NewUD.getNewUserPasswordConfirm())) {
                msg = "New passwords do not match...";
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
            } else {
                OldUD.setUserPassword(NewUD.getNewUserPassword());
                OldUD.setUserPasswordConfirm(NewUD.getNewUserPasswordConfirm());
                //update seesion
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                HttpSession httpSession = request.getSession(false);

                httpSession.setAttribute("CHANGE_PASSWORD_ALLOWED", 1);
                this.saveUserDetail(OldUD);
                httpSession.setAttribute("CHANGE_PASSWORD_ALLOWED", 0);
                this.clearUserDetailChange(NewUD);
            }

        } catch (NullPointerException npe) {
            msg = "Invalid user...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }
    }

    /**
     * @param UserDetails the UserDetails to set
     */
    public void setUserDetails(List<UserDetail> UserDetails) {
        this.UserDetails = UserDetails;
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
     * @return the SelectedUserDetail
     */
    public UserDetail getSelectedUserDetail() {
        return SelectedUserDetail;
    }

    /**
     * @param SelectedUserDetail the SelectedUserDetail to set
     */
    public void setSelectedUserDetail(UserDetail SelectedUserDetail) {
        this.SelectedUserDetail = SelectedUserDetail;
    }

    /**
     * @return the SelectedUserDetailId
     */
    public int getSelectedUserDetailId() {
        return SelectedUserDetailId;
    }

    /**
     * @param SelectedUserDetailId the SelectedUserDetailId to set
     */
    public void setSelectedUserDetailId(int SelectedUserDetailId) {
        this.SelectedUserDetailId = SelectedUserDetailId;
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

    /**
     * @return the UserDetailObjectList
     */
    public List<UserDetail> getUserDetailObjectList(String Query) {
        String sql;
        sql = "{call sp_search_user_detail_by_names(?)}";
        ResultSet rs = null;
        UserDetailObjectList = new ArrayList<UserDetail>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query.trim());
            rs = ps.executeQuery();
            while (rs.next()) {
                UserDetail userdetail = new UserDetail();
                userdetail.setUserDetailId(rs.getInt("user_detail_id"));
                userdetail.setUserName(rs.getString("user_name"));
                userdetail.setUserPassword(Security.Decrypt(rs.getString("user_password")));
                userdetail.setUserPasswordConfirm(Security.Decrypt(rs.getString("user_password")));
                userdetail.setFirstName(rs.getString("first_name"));
                userdetail.setSecondName(rs.getString("second_name"));
                userdetail.setThirdName(rs.getString("third_name"));
                userdetail.setIsUserLocked(rs.getString("is_user_locked"));
                userdetail.setIsUserGenAdmin(rs.getString("is_user_gen_admin"));
                userdetail.setUserCategoryId(rs.getInt("user_category_id"));
                //userdetail.setUserImgUrl(rs.getString("user_img_url"));
                UserDetailObjectList.add(userdetail);
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
        return UserDetailObjectList;
    }

    /**
     * @param UserDetailObjectList the UserDetailObjectList to set
     */
    public void setUserDetailObjectList(List<UserDetail> UserDetailObjectList) {
        this.UserDetailObjectList = UserDetailObjectList;
    }

}
