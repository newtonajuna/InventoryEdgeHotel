import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class GroupDetailBean implements Serializable {
    private List<GroupDetail> GroupDetails;
    private String ActionMessage=null;
    private GroupDetail SelectedGroupDetail=null;
    private long SelectedGroupDetailId;
    private String SearchGroupName="";
    private String SearchIsActive="";
    
    public String getRightTextColor(String Allow){
        if("Yes".equals(Allow)){
            return "blue";
        }else{
            return "red";
        }
    }
    
    public void saveGroupDetail(GroupDetail groupdetail) {
        String sql = null;
        String msg="";
        String sql2 = null;
        sql2="SELECT * FROM group_detail WHERE group_name='" + groupdetail.getGroupName() + "'";
        
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (groupdetail.getGroupDetailId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (groupdetail.getGroupDetailId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(groupdetail.getIsActive(), 1, 3).equals("FAIL")){
            msg="Enter Is Active?!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(groupdetail.getGroupName(), 1, 50).equals("FAIL")){
            msg="Enter Group Name!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if((new CustomValidator().CheckRecords(sql2)>0 && groupdetail.getGroupDetailId()==0) || (new CustomValidator().CheckRecords(sql2)>0 && new CustomValidator().CheckRecords(sql2)!=1 && groupdetail.getGroupDetailId()>0)){
            msg="Group already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
            
        if (groupdetail.getGroupDetailId() == 0) {
            sql = "{call sp_insert_group_detail(?,?)}";
        } else if (groupdetail.getGroupDetailId() > 0) {
            sql = "{call sp_update_group_detail(?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (groupdetail.getGroupDetailId() == 0) {
                cs.setString("in_group_name", groupdetail.getGroupName());
                cs.setString("in_is_active", groupdetail.getIsActive());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearGroupDetail(groupdetail);
            } else if (groupdetail.getGroupDetailId() > 0) {
                cs.setLong("in_group_detail_id", groupdetail.getGroupDetailId());
                cs.setString("in_group_name", groupdetail.getGroupName());
                cs.setString("in_is_active", groupdetail.getIsActive());
                cs.executeUpdate();
                this.setActionMessage("Updated Successfully");
                this.clearGroupDetail(groupdetail);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("GroupDetail NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("GroupDetail NOT saved!"));
        }
    }
    }
    
    public GroupDetail getGroupDetail(int GroupDetailId) {
        String sql = "{call sp_search_group_detail_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, GroupDetailId);
            rs = ps.executeQuery();
            if (rs.next()) {
                GroupDetail groupdetail = new GroupDetail();
                groupdetail.setGroupDetailId(rs.getInt("group_detail_id"));
                groupdetail.setGroupName(rs.getString("group_name"));
                groupdetail.setIsActive(rs.getString("is_active"));
                return groupdetail;
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
    
    public GroupDetail findGroupDetailByName(String GroupName) {
        String sql = "{call sp_search_group_by_name(?)}";  
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, GroupName);
            rs = ps.executeQuery();
            if (rs.next()) {
                GroupDetail groupdetail = new GroupDetail();
                groupdetail.setGroupDetailId(rs.getInt("group_detail_id"));
                groupdetail.setGroupName(rs.getString("group_name"));
                groupdetail.setIsActive(rs.getString("is_active"));
                return groupdetail;
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

    public void deleteGroupDetail(GroupDetail groupdetail) {
        String sql = "DELETE FROM group_detail WHERE group_detail_id=?";
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
            ps.setInt(1, groupdetail.getGroupDetailId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearGroupDetail(groupdetail);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("GroupDetail NOT deleted");
        }
    }
    }

    public void displayGroupDetail(GroupDetail GroupDetailFrom, GroupDetail GroupDetailTo) {
                GroupDetailTo.setGroupDetailId(GroupDetailFrom.getGroupDetailId());
                GroupDetailTo.setGroupName(GroupDetailFrom.getGroupName());
                GroupDetailTo.setIsActive(GroupDetailFrom.getIsActive());
    }

    public void clearGroupDetail(GroupDetail groupdetail) {
        if(groupdetail!=null){
                groupdetail.setGroupDetailId(0);
                groupdetail.setGroupName("");
                groupdetail.setIsActive("");
        }
    }
    public void clearSelectedGroupDetail(){
        this.clearGroupDetail(this.getSelectedGroupDetail());
    }

    /**
     * @return the GroupDetails
     */
    public List<GroupDetail> getGroupDetails() {
        String sql = "{call sp_search_group_detail_by_none()}";
        ResultSet rs = null;
        GroupDetails = new ArrayList<GroupDetail>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            //ps.setString(1, this.SearchGroupName);
            rs = ps.executeQuery();
            while (rs.next()) {
                GroupDetail groupdetail = new GroupDetail();
                groupdetail.setGroupDetailId(rs.getInt("group_detail_id"));
                groupdetail.setGroupName(rs.getString("group_name"));
                groupdetail.setIsActive(rs.getString("is_active"));
                GroupDetails.add(groupdetail);
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
        return GroupDetails;
    }

    /**
     * @param GroupDetails the GroupDetails to set
     */
    public void setGroupDetails(List<GroupDetail> GroupDetails) {
        this.GroupDetails = GroupDetails;
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
     * @return the SelectedGroupDetailId
     */
    public long getSelectedGroupDetailId() {
        return SelectedGroupDetailId;
    }

    /**
     * @param SelectedGroupDetailId the SelectedGroupDetailId to set
     */
    public void setSelectedGroupDetailId(long SelectedGroupDetailId) {
        this.SelectedGroupDetailId = SelectedGroupDetailId;
    }

    /**
     * @return the SearchGroupName
     */
    public String getSearchGroupName() {
        return SearchGroupName;
    }

    /**
     * @param SearchGroupName the SearchGroupName to set
     */
    public void setSearchGroupName(String SearchGroupName) {
        this.SearchGroupName = SearchGroupName;
    }

    /**
     * @return the SearchIsActive
     */
    public String getSearchIsActive() {
        return SearchIsActive;
    }

    /**
     * @param SearchIsActive the SearchIsActive to set
     */
    public void setSearchIsActive(String SearchIsActive) {
        this.SearchIsActive = SearchIsActive;
    }

    /**
     * @return the SelectedGroupDetail
     */
    public GroupDetail getSelectedGroupDetail() {
        return SelectedGroupDetail;
    }

    /**
     * @param SelectedGroupDetail the SelectedGroupDetail to set
     */
    public void setSelectedGroupDetail(GroupDetail SelectedGroupDetail) {
        this.SelectedGroupDetail = SelectedGroupDetail;
    }
}
