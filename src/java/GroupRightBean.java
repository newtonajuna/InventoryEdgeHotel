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
public class GroupRightBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<GroupRight> GroupRights;
    private String ActionMessage = null;
    private GroupRight SelectedGroupRight = null;
    private int SelectedGroupRightId;
    private String SearchUserName = "";
    private int SelectedGroupDetailId;
    private int SelectedStoreId;
    private List<GroupRight> GroupRightsForEdit;
    private List<GroupRight> ActiveGroupRightsForCurrentStoreUser;
    GroupRight groupright1;
    GroupRight groupright2;
    String CurrentFunctionName;
    private String[] FunctionArrayList;

    public void saveGroupRight() {
        String msg = null;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (this.SelectedGroupDetailId == 0 || this.SelectedStoreId == 0) {
            //do nothing
            this.setActionMessage("Group Rights NOT saved!");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Group Rights NOT saved!"));
        } else {
            int ListItemIndex = 0;
            int ListItemNo = this.GroupRightsForEdit.size();
            while (ListItemIndex < ListItemNo) {
                this.addGroupRight(this.GroupRightsForEdit.get(ListItemIndex));
                ListItemIndex = ListItemIndex + 1;
            }
            this.retrieveGroupRightsForEdit();
            this.setActionMessage("Group Rights saved successfully");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Group Rights saved successfully"));
        }
    }

    public void addGroupRight(GroupRight groupright) {
        String sql = null;
        String msg = "";

        if (groupright.getGroupRightId() == 0) {
            sql = "{call sp_insert_group_right(?,?,?,?,?,?,?)}";
        } else if (groupright.getGroupRightId() > 0) {
            sql = "{call sp_update_group_right(?,?,?,?,?,?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (groupright.getGroupRightId() == 0) {
                cs.setInt("in_store_id", groupright.getStoreId());
                cs.setInt("in_group_detail_id", groupright.getGroupDetailId());
                cs.setString("in_function_name", groupright.getFunctionName());
                cs.setString("in_allow_view", groupright.getAllowView());
                cs.setString("in_allow_add", groupright.getAllowAdd());
                cs.setString("in_allow_edit", groupright.getAllowEdit());
                cs.setString("in_allow_delete", groupright.getAllowDelete());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearGroupRight(groupright);
            } else if (groupright.getGroupRightId() > 0) {
                cs.setInt("in_group_right_id", groupright.getGroupRightId());
                cs.setInt("in_store_id", groupright.getStoreId());
                cs.setInt("in_group_detail_id", groupright.getGroupDetailId());
                cs.setString("in_function_name", groupright.getFunctionName());
                cs.setString("in_allow_view", groupright.getAllowView());
                cs.setString("in_allow_add", groupright.getAllowAdd());
                cs.setString("in_allow_edit", groupright.getAllowEdit());
                cs.setString("in_allow_delete", groupright.getAllowDelete());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearGroupRight(groupright);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("GroupRight NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("GroupRight NOT saved!"));
        }

    }

    public GroupRight getGroupRight(int GrpRightId) {
        String sql = "{call sp_search_group_right_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, GrpRightId);
            rs = ps.executeQuery();
            if (rs.next()) {
                GroupRight groupright = new GroupRight();
                groupright.setGroupRightId(rs.getInt("group_right_id"));
                groupright.setStoreId(rs.getInt("store_id"));
                groupright.setGroupDetailId(rs.getInt("group_detail_id"));
                groupright.setFunctionName(rs.getString("function_name"));
                groupright.setAllowView(rs.getString("allow_view"));
                groupright.setAllowAdd(rs.getString("allow_add"));
                groupright.setAllowEdit(rs.getString("allow_edit"));
                groupright.setAllowDelete(rs.getString("allow_delete"));
                return groupright;
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

    public void deleteGroupRight(GroupRight groupright) {
        String sql = "DELETE FROM group_right WHERE group_right_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, groupright.getGroupRightId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearGroupRight(groupright);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("GroupRight NOT deleted");
        }
    }

    public void displayGroupRight(GroupRight GroupRightFrom, GroupRight GroupRightTo) {
        GroupRightTo.setGroupRightId(GroupRightFrom.getGroupRightId());
        GroupRightTo.setStoreId(GroupRightFrom.getStoreId());
        GroupRightTo.setGroupDetailId(GroupRightFrom.getGroupDetailId());
        GroupRightTo.setFunctionName(GroupRightFrom.getFunctionName());
        GroupRightTo.setAllowView(GroupRightFrom.getAllowView());
        GroupRightTo.setAllowAdd(GroupRightFrom.getAllowAdd());
        GroupRightTo.setAllowEdit(GroupRightFrom.getAllowEdit());
        GroupRightTo.setAllowDelete(GroupRightFrom.getAllowDelete());
    }

    public void clearGroupRight(GroupRight groupright) {
        groupright.setGroupRightId(0);
        groupright.setStoreId(0);
        groupright.setGroupDetailId(0);
        groupright.setFunctionName("No");
        groupright.setAllowView("No");
        groupright.setAllowAdd("No");
        groupright.setAllowEdit("No");
        groupright.setAllowDelete("No");
    }

    /**
     * @return the GroupRights
     */
    public List<GroupRight> getGroupRights() {
        String sql;

        if (this.getSelectedGroupDetailId() != 0 && this.getSelectedStoreId() != 0) {
            sql = "SELECT * FROM group_right gr WHERE gr.group_detail_id=" + this.getSelectedGroupDetailId() + " AND gr.store_id=" + this.getSelectedStoreId() + "";
        } else if (this.getSelectedGroupDetailId() != 0 && this.getSelectedStoreId() == 0) {
            sql = "SELECT * FROM group_right gr WHERE gr.group_detail_id=" + this.getSelectedGroupDetailId() + "";
        } else if (this.getSelectedGroupDetailId() == 0 && this.getSelectedStoreId() != 0) {
            sql = "SELECT * FROM group_right gr WHERE gr.store_id=" + this.getSelectedStoreId() + "";
        } else {
            sql = "SELECT * FROM group_right gr ORDER BY group_detail_id ASC";
        }

        ResultSet rs = null;
        GroupRights = new ArrayList<GroupRight>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                GroupRight groupright = new GroupRight();
                groupright.setGroupRightId(rs.getInt("group_right_id"));
                groupright.setStoreId(rs.getInt("store_id"));
                groupright.setGroupDetailId(rs.getInt("group_detail_id"));
                groupright.setFunctionName(rs.getString("function_name"));
                groupright.setAllowView(rs.getString("allow_view"));
                groupright.setAllowAdd(rs.getString("allow_add"));
                groupright.setAllowEdit(rs.getString("allow_edit"));
                groupright.setAllowDelete(rs.getString("allow_delete"));
                GroupRights.add(groupright);
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
        return GroupRights;
    }

    public GroupRight getGroupRightByGroupStoreFunction(int GrpDtlId, int StrId, String FuncName) {
        String sql = "{call sp_search_group_right_by_group_id_store_id_function(?,?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, GrpDtlId);
            ps.setInt(2, StrId);
            ps.setString(3, FuncName);
            rs = ps.executeQuery();
            if (rs.next()) {
                GroupRight groupright = new GroupRight();
                groupright.setGroupRightId(rs.getInt("group_right_id"));
                groupright.setStoreId(rs.getInt("store_id"));
                groupright.setGroupDetailId(rs.getInt("group_detail_id"));
                groupright.setFunctionName(rs.getString("function_name"));
                groupright.setAllowView(rs.getString("allow_view"));
                groupright.setAllowAdd(rs.getString("allow_add"));
                groupright.setAllowEdit(rs.getString("allow_edit"));
                groupright.setAllowDelete(rs.getString("allow_delete"));
                return groupright;
            } else {
                return null;
            }
        } catch (SQLException | NullPointerException se) {
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

    public List<GroupRight> retrieveGroupRightsForEdit() {
        this.GroupRightsForEdit = new ArrayList<GroupRight>();
        if (this.SelectedGroupDetailId == 0 || this.SelectedStoreId == 0) {
            return null;
        } else {
            int n = this.getFunctionArrayList().length;
            for (int i = 0; i < n; i++) {
                groupright1 = new GroupRight();
                groupright2 = new GroupRight();
                groupright1.setGroupDetailId(this.SelectedGroupDetailId);
                groupright1.setStoreId(this.SelectedStoreId);
                this.CurrentFunctionName = this.FunctionArrayList[i];
                groupright2 = this.getGroupRightByGroupStoreFunction(this.SelectedGroupDetailId, this.SelectedStoreId, this.CurrentFunctionName);
                if (groupright2 != null) {
                    groupright1.setGroupRightId(groupright2.getGroupRightId());
                    groupright1.setFunctionName(groupright2.getFunctionName());
                    groupright1.setAllowView(groupright2.getAllowView());
                    groupright1.setAllowAdd(groupright2.getAllowAdd());
                    groupright1.setAllowEdit(groupright2.getAllowEdit());
                    groupright1.setAllowDelete(groupright2.getAllowDelete());
                } else {
                    groupright1.setGroupRightId(0);
                    groupright1.setFunctionName(this.CurrentFunctionName);
                    groupright1.setAllowView("No");
                    groupright1.setAllowAdd("No");
                    groupright1.setAllowEdit("No");
                    groupright1.setAllowDelete("No");
                }
                this.GroupRightsForEdit.add(groupright1);
                groupright1 = null;
                groupright2 = null;
            }
            return this.GroupRightsForEdit;
        }
    }

    /**
     * @param GroupRights the GroupRights to set
     */
    public void setGroupRights(List<GroupRight> GroupRights) {
        this.GroupRights = GroupRights;
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
     * @return the SelectedGroupRight
     */
    public GroupRight getSelectedGroupRight() {
        return SelectedGroupRight;
    }

    /**
     * @param SelectedGroupRight the SelectedGroupRight to set
     */
    public void setSelectedGroupRight(GroupRight SelectedGroupRight) {
        this.SelectedGroupRight = SelectedGroupRight;
    }

    /**
     * @return the SelectedGroupRightId
     */
    public int getSelectedGroupRightId() {
        return SelectedGroupRightId;
    }

    /**
     * @param SelectedGroupRightId the SelectedGroupRightId to set
     */
    public void setSelectedGroupRightId(int SelectedGroupRightId) {
        this.SelectedGroupRightId = SelectedGroupRightId;
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
     * @return the SelectedGroupDetailId
     */
    public int getSelectedGroupDetailId() {
        return SelectedGroupDetailId;
    }

    /**
     * @param SelectedGroupDetailId the SelectedGroupDetailId to set
     */
    public void setSelectedGroupDetailId(int SelectedGroupDetailId) {
        this.SelectedGroupDetailId = SelectedGroupDetailId;
    }

    /**
     * @return the SelectedStoreId
     */
    public int getSelectedStoreId() {
        return SelectedStoreId;
    }

    /**
     * @param SelectedStoreId the SelectedStoreId to set
     */
    public void setSelectedStoreId(int SelectedStoreId) {
        this.SelectedStoreId = SelectedStoreId;
    }

    public List<GroupRight> getCurrentGroupRights(int aStoreId, long aUserDetailId) {
        String sql = "{call sp_search_group_right_get_active_rights_by_store_user(?,?)}";
        ResultSet rs = null;
        List<GroupRight> NewCurrentGroupRights = new ArrayList<GroupRight>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aStoreId);
            ps.setLong(2, aUserDetailId);
            rs = ps.executeQuery();
            while (rs.next()) {
                GroupRight groupright = new GroupRight();
                groupright.setGroupRightId(rs.getInt("group_right_id"));
                groupright.setStoreId(rs.getInt("store_id"));
                groupright.setGroupDetailId(rs.getInt("group_detail_id"));
                groupright.setFunctionName(rs.getString("function_name"));
                groupright.setAllowView(rs.getString("allow_view"));
                groupright.setAllowAdd(rs.getString("allow_add"));
                groupright.setAllowEdit(rs.getString("allow_edit"));
                groupright.setAllowDelete(rs.getString("allow_delete"));
                NewCurrentGroupRights.add(groupright);
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
        return NewCurrentGroupRights;
    }

    public int IsUserFunctionAccessAllowed(UserDetail aUserDetail, List<GroupRight> aGroupRights, String aFunctionName, String aAllow) {

        return 0;
    }

    public String getFunctionByTransType(String aTransTypeName, String aTransReasonName) {
        //System.out.println("INaTransTypeName:" + aTransTypeName);
        //System.out.println("INaTransReasonName:" + aTransReasonName);
        /*
         1	PURCHASE INVOICE
         2	SALE INVOICE
         3	DISPOSE
         4	TRANSFER
         5	ITEM
         6	PAYMENT
         7	UNPACK
         8	PURCHASE ORDER
         9	GOODS RECEIVED
         10	SALE QUOTATION
         11	SALE ORDER
         12	GOODS DELIVERY
         13	TRANSFER REQUEST
         {and others}
         */
        String x = "";
        try {
            switch (aTransTypeName) {
                case "PURCHASE INVOICE":
                    x = "PURCHASE INVOICE";
                    break;
                case "SALE INVOICE":
                    if (aTransReasonName.equals("RETAIL SALE INVOICE")) {
                        x = "RETAIL SALE INVOICE";
                    } else if (aTransReasonName.equals("WHOLE SALE INVOICE")) {
                        x = "WHOLE SALE INVOICE";
                    } else if (aTransReasonName.equals("COST-PRICE SALE INVOICE")) {
                        x = "COST-PRICE SALE INVOICE";
                    } else if (aTransReasonName.equals("EXEMPT SALE INVOICE")) {
                        x = "EXEMPT SALE INVOICE";
                    }
                    break;
                case "DISPOSE":
                    x = "DISPOSE";
                    break;
                case "TRANSFER":
                    x = "TRANSFER";
                    break;
                case "ITEM":
                    x = "ITEM";
                    break;
                case "PAYMENT":
                    x = "PAYMENT";
                    break;
                case "UNPACK":
                    x = "UNPACK";
                    break;
                case "PURCHASE ORDER":
                    x = "PURCHASE ORDER";
                    break;
                case "GOODS RECEIVED":
                    x = "GOODS RECEIVED";
                    break;
                case "SALE QUOTATION":
                    x = "SALE QUOTATION";
                    break;
                case "SALE ORDER":
                    x = "SALE ORDER";
                    break;
                case "GOODS DELIVERY":
                    x = "GOODS DELIVERY";
                    break;
                case "TRANSFER REQUEST":
                    x = "TRANSFER REQUEST";
                    break;
                default:
                    x = aTransTypeName;
                    break;
            }
        } catch (NullPointerException npe) {

        }
        return x;
    }

    public int IsUserGroupsFunctionAccessAllowed(UserDetail aUserDetail, List<GroupRight> aGroupRights, String aFunctionName, String aAllow) {
        //System.out.println("aUserDetail:" + aUserDetail.getUserDetailId() + " : " + aUserDetail.getUserName());
        //System.out.println("aFunctionName:" + aFunctionName);
        //System.out.println("aAllow:" + aAllow);
        //first check is person is general admin
        if (aUserDetail == null) {
            return 0;
        }
        if ("Yes".equals(aUserDetail.getIsUserGenAdmin()) && "No".equals(aUserDetail.getIsUserLocked()) && "system".equals(aUserDetail.getUserName()) && "SETTING".equals(aFunctionName)) {
            return 1;
        }
        if ("Yes".equals(aUserDetail.getIsUserGenAdmin()) && "No".equals(aUserDetail.getIsUserLocked()) && !"system".equals(aUserDetail.getUserName())) {
            return 1;
        }
        //for non-admins      
        int ListItemIndex = 0;
        int ListItemNo = aGroupRights.size();
        int IsNegativeRightSeen = 0;
        int IsPositiveRightSeen = 0;

        while (ListItemIndex < ListItemNo) {
            if (aFunctionName.equals(aGroupRights.get(ListItemIndex).getFunctionName())) {
                if (aAllow.equals("View")) {
                    if ("Yes".equals(aGroupRights.get(ListItemIndex).getAllowView()) && IsPositiveRightSeen == 0) {
                        IsPositiveRightSeen = 1;
                    } else if ("No".equals(aGroupRights.get(ListItemIndex).getAllowView()) && IsNegativeRightSeen == 0) {
                        IsNegativeRightSeen = 1;
                    }
                } else if (aAllow.equals("Add")) {
                    if ("Yes".equals(aGroupRights.get(ListItemIndex).getAllowAdd()) && IsPositiveRightSeen == 0) {
                        IsPositiveRightSeen = 1;
                    } else if ("No".equals(aGroupRights.get(ListItemIndex).getAllowAdd()) && IsNegativeRightSeen == 0) {
                        IsNegativeRightSeen = 1;
                    }
                } else if (aAllow.equals("Edit")) {
                    if ("Yes".equals(aGroupRights.get(ListItemIndex).getAllowEdit()) && IsPositiveRightSeen == 0) {
                        IsPositiveRightSeen = 1;
                    } else if ("No".equals(aGroupRights.get(ListItemIndex).getAllowEdit()) && IsNegativeRightSeen == 0) {
                        IsNegativeRightSeen = 1;
                    }
                } else if (aAllow.equals("delete")) {
                    if ("Yes".equals(aGroupRights.get(ListItemIndex).getAllowDelete()) && IsPositiveRightSeen == 0) {
                        IsPositiveRightSeen = 1;
                    } else if ("No".equals(aGroupRights.get(ListItemIndex).getAllowDelete()) && IsNegativeRightSeen == 0) {
                        IsNegativeRightSeen = 1;
                    }
                }
            }
            ListItemIndex = ListItemIndex + 1;
        }

        if (IsPositiveRightSeen == 1 && IsNegativeRightSeen == 0) {
            return 1;//Allow Function Access
        } else {
            return 0;//Dissallow Function Access
        }
    }

    public int IsUserGroupsFunctionAccessAllowed2(UserDetail aUserDetail, List<GroupRight> aGroupRights, String aFunctionName, String aAllow) {
        //first check is person is general admin
        if (aUserDetail == null) {
            return 0;
        }
        if ("Yes".equals(aUserDetail.getIsUserGenAdmin()) && "No".equals(aUserDetail.getIsUserLocked())) {
            return 1;
        }
        //for non-admins
        int ListItemIndex = 0;
        int ListItemNo = aGroupRights.size();
        int IsNegativeRightSeen = 0;
        int IsPositiveRightSeen = 0;

        while (ListItemIndex < ListItemNo) {
            if (aFunctionName.equals(aGroupRights.get(ListItemIndex).getFunctionName())) {
                if (aAllow.equals("View")) {
                    if ("Yes".equals(aGroupRights.get(ListItemIndex).getAllowView()) && IsPositiveRightSeen == 0) {
                        IsPositiveRightSeen = 1;
                    } else if ("No".equals(aGroupRights.get(ListItemIndex).getAllowView()) && IsNegativeRightSeen == 0) {
                        IsNegativeRightSeen = 1;
                    }
                } else if (aAllow.equals("Add")) {
                    if ("Yes".equals(aGroupRights.get(ListItemIndex).getAllowAdd()) && IsPositiveRightSeen == 0) {
                        IsPositiveRightSeen = 1;
                    } else if ("No".equals(aGroupRights.get(ListItemIndex).getAllowAdd()) && IsNegativeRightSeen == 0) {
                        IsNegativeRightSeen = 1;
                    }
                } else if (aAllow.equals("Edit")) {
                    if ("Yes".equals(aGroupRights.get(ListItemIndex).getAllowEdit()) && IsPositiveRightSeen == 0) {
                        IsPositiveRightSeen = 1;
                    } else if ("No".equals(aGroupRights.get(ListItemIndex).getAllowEdit()) && IsNegativeRightSeen == 0) {
                        IsNegativeRightSeen = 1;
                    }
                } else if (aAllow.equals("delete")) {
                    if ("Yes".equals(aGroupRights.get(ListItemIndex).getAllowDelete()) && IsPositiveRightSeen == 0) {
                        IsPositiveRightSeen = 1;
                    } else if ("No".equals(aGroupRights.get(ListItemIndex).getAllowDelete()) && IsNegativeRightSeen == 0) {
                        IsNegativeRightSeen = 1;
                    }
                }
            }
            ListItemIndex = ListItemIndex + 1;
        }

        if (IsPositiveRightSeen == 1 && IsNegativeRightSeen == 0) {
            return 1;//Allow Function Access
        } else {
            return 0;//Dissallow Function Access
        }
    }

    /**
     * @return the GroupRightsForEdit
     */
    public List<GroupRight> getGroupRightsForEdit() {
        return GroupRightsForEdit;
    }

    /**
     * @param GroupRightsForEdit the GroupRightsForEdit to set
     */
    public void setGroupRightsForEdit(List<GroupRight> GroupRightsForEdit) {
        this.GroupRightsForEdit = GroupRightsForEdit;
    }

    /**
     * @return the ActiveGroupRightsForCurrentStoreUser
     */
    public List<GroupRight> getActiveGroupRightsForCurrentStoreUser() {
        return ActiveGroupRightsForCurrentStoreUser;
    }

    /**
     * @param ActiveGroupRightsForCurrentStoreUser the
     * ActiveGroupRightsForCurrentStoreUser to set
     */
    public void setActiveGroupRightsForCurrentStoreUser(List<GroupRight> ActiveGroupRightsForCurrentStoreUser) {
        this.ActiveGroupRightsForCurrentStoreUser = ActiveGroupRightsForCurrentStoreUser;
    }

    /**
     * @return the FunctionArrayList
     */
    public String[] getFunctionArrayList() {
        int n = 28;
        FunctionArrayList = new String[n];
        FunctionArrayList[0] = "ITEM";
        FunctionArrayList[1] = "SALE QUOTATION";
        FunctionArrayList[2] = "SALE ORDER";
        FunctionArrayList[3] = "RETAIL SALE INVOICE";
        FunctionArrayList[4] = "WHOLE SALE INVOICE";
        FunctionArrayList[5] = "COST-PRICE SALE INVOICE";
        FunctionArrayList[6] = "EXEMPT SALE INVOICE";
        FunctionArrayList[7] = "GOODS DELIVERY";
        FunctionArrayList[8] = "PURCHASE ORDER";
        FunctionArrayList[9] = "GOODS RECEIVED";
        FunctionArrayList[10] = "PURCHASE INVOICE";
        FunctionArrayList[11] = "PAYMENT";
        FunctionArrayList[12] = "INTER BRANCH";
        FunctionArrayList[13] = "SPEND POINT";
        FunctionArrayList[14] = "DISPOSE";
        FunctionArrayList[15] = "TRANSFER REQUEST";
        FunctionArrayList[16] = "TRANSFER";
        FunctionArrayList[17] = "SETTING";
        FunctionArrayList[18] = "TRANSACTOR";
        FunctionArrayList[19] = "UNPACK";
        FunctionArrayList[20] = "DISCOUNT";
        FunctionArrayList[21] = "REPORT-ITEMS";
        FunctionArrayList[22] = "REPORT-STOCK";
        FunctionArrayList[23] = "REPORT-TRANSACTORS";
        FunctionArrayList[24] = "REPORT-TRANSACTIONS";
        FunctionArrayList[25] = "REPORT-PAYMENTS";
        FunctionArrayList[26] = "REPORT-ROOMINGLIST";
        FunctionArrayList[27] = "GUESTFOLIO";
        return FunctionArrayList;
    }

    /**
     * @param FunctionArrayList the FunctionArrayList to set
     */
    public void setFunctionArrayList(String[] FunctionArrayList) {
        this.FunctionArrayList = FunctionArrayList;
    }

    public String getFcnName(int aTransTypeId, int aTransReasonId) {
        /*
         TTI	TTN                     TRI	TRN
         1	PURCHASE INVOICE	1	PURCHASE INVOICE
         2	SALE INVOICE            2	RETAIL SALE INVOICE
         2	SALE INVOICE            10	WHOLE SALE INVOICE
         2	SALE INVOICE            11	COST-PRICE SALE INVOICE
         2	SALE INVOICE            17	EXEMPT SALE INVOICE
         3	DISPOSE                 3	EXPIRED
         3	DISPOSE         	4	LOST
         3	DISPOSE                 5	DAMAGE
         4	TRANSFER                6	ISSUE TRANSFER
         4	TRANSFER                7	RETURN TRANSFER
         5	ITEM                    8	ITEM
         7	UNPACK                  9	UNPACK
         8	PURCHASE ORDER          12	PURCHASE ORDER
         9	GOODS RECEIVED          13	GOODS RECEIVED
         10	SALE QUOTATION          14	RETAIL SALE QUOTATION
         10	SALE QUOTATION          15	WHOLE SALE QUOTATION
         11	SALE ORDER              16	SALE ORDER
         12	GOODS DELIVERY          18	GOODS DELIVERY
         13	TRANSFER REQUEST	19	ISSUE TRANSFER REQUEST
         13	TRANSFER REQUEST	20	RETURN TRANSFER REQUEST
         */
        String aFCN = "";
        if (aTransTypeId == 1) {
            if (aTransReasonId == 1) {
                aFCN = "PURCHASE INVOICE";
            }
        } else if (aTransTypeId == 2) {
            if (aTransReasonId == 2) {
                aFCN = "RETAIL SALE INVOICE";
            } else if (aTransReasonId == 10) {
                aFCN = "WHOLE SALE INVOICE";
            } else if (aTransReasonId == 11) {
                aFCN = "COST-PRICE SALE INVOICE";
            } else if (aTransReasonId == 17) {
                aFCN = "EXEMPT SALE INVOICE";
            }
        } else if (aTransTypeId == 3) {
            if (aTransReasonId == 3) {
                aFCN = "DISPOSE";
            } else if (aTransReasonId == 4) {
                aFCN = "DISPOSE";
            } else if (aTransReasonId == 5) {
                aFCN = "DISPOSE";
            }
        } else if (aTransTypeId == 4) {
            if (aTransReasonId == 6) {
                aFCN = "TRANSFER";
            } else if (aTransReasonId == 7) {
                aFCN = "TRANSFER";
            }
        } else if (aTransTypeId == 5) {
            if (aTransReasonId == 8) {
                aFCN = "ITEM";
            }
        } else if (aTransTypeId == 6) {
            aFCN = "PAYMENT";
        } else if (aTransTypeId == 7) {
            if (aTransReasonId == 9) {
                aFCN = "UNPACK";
            }
        } else if (aTransTypeId == 8) {
            if (aTransReasonId == 12) {
                aFCN = "PURCHASE ORDER";
            }
        } else if (aTransTypeId == 9) {
            if (aTransReasonId == 13) {
                aFCN = "GOODS RECEIVED";
            }
        } else if (aTransTypeId == 10) {
            if (aTransReasonId == 14) {
                aFCN = "SALE QUOTATION";
            } else if (aTransReasonId == 15) {
                aFCN = "SALE QUOTATION";
            }
        } else if (aTransTypeId == 11) {
            if (aTransReasonId == 16) {
                aFCN = "SALE ORDER";
            }
        } else if (aTransTypeId == 12) {
            if (aTransReasonId == 18) {
                aFCN = "GOODS DELIVERY";
            }
        } else if (aTransTypeId == 13) {
            if (aTransReasonId == 19) {
                aFCN = "TRANSFER REQUEST";
            } else if (aTransReasonId == 20) {
                aFCN = "TRANSFER REQUEST";
            }
        } else {
            aFCN = "";
        }
        return aFCN;
    }

}
