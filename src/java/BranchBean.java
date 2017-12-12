import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class BranchBean implements Serializable {
private List<Branch> Branches;
    private String ActionMessage=null;
    private Branch SelectedBranch=null;
    private long SelectedBranchId;
    private String SearchBranchCode="";
    private String SearchBranchName="";
    
    public void saveBranch(Branch branch) {
        String sql = null;
        String msg="";
        String sql2 = null;
        sql2="SELECT * FROM branch WHERE branch_code='" + branch.getBranchCode() + "' OR branch_name='" + branch.getBranchName() + "'";
        
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        
        if (branch.getBranchId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"INTER BRANCH", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (branch.getBranchId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"INTER BRANCH", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(branch.getBranchName(), 1, 100).equals("FAIL")){
            msg="Enter Branch Branch Name!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(branch.getBranchCode(), 1, 20).equals("FAIL")){
            msg="Enter Branch Branch Code!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if((new CustomValidator().INTER_BRANCH_CheckRecords(sql2)>0 && branch.getBranchId()==0) || (new CustomValidator().INTER_BRANCH_CheckRecords(sql2)>0 && new CustomValidator().INTER_BRANCH_CheckRecords(sql2)!=1 && branch.getBranchId()>0)){
            msg="Branch Code OR BranchName already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
            
        if (branch.getBranchId() == 0) {
            sql = "{call sp_insert_branch(?,?)}";
        } else if (branch.getBranchId() > 0) {
            sql = "{call sp_update_branch(?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (branch.getBranchId() == 0) {
                cs.setString("in_branch_code", branch.getBranchCode());
                cs.setString("in_branch_name", branch.getBranchName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearBranch(branch);
            } else if (branch.getBranchId() > 0) {
                cs.setLong("in_branch_id", branch.getBranchId());
                cs.setString("in_branch_code", branch.getBranchCode());
                cs.setString("in_branch_name", branch.getBranchName());
                cs.executeUpdate();
                this.setActionMessage("Updated Successfully");
                this.clearBranch(branch);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Branch NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Branch NOT saved!"));
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            this.setActionMessage("Branch NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
    }
    }
    
    public Branch getBranch(int BranchId) {
        String sql = "{call sp_search_branch_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, BranchId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Branch branch = new Branch();
                branch.setBranchId(rs.getLong("branch_id"));
                branch.setBranchCode(rs.getString("branch_code"));
                branch.setBranchName(rs.getString("branch_name"));
                return branch;
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
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
    
    public boolean IsCompanyBranchInvalid() {
        String sql = "{call sp_search_branch_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, CompanySetting.getBranchId());
            rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return true;
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            return true;
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
    
    public static Branch findBranchByCode(String BranchCode) {
        String sql = "{call sp_search_branch_by_code(?)}";  
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, BranchCode);
            rs = ps.executeQuery();
            if (rs.next()) {
                Branch branch = new Branch();
                branch.setBranchId(rs.getLong("branch_id"));
                branch.setBranchCode(rs.getString("branch_code"));
                branch.setBranchName(rs.getString("branch_name"));
                return branch;
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
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

    public void deleteBranch(Branch branch) {
        String sql = "DELETE FROM branch WHERE branch_id=?";
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
            ps.setLong(1, branch.getBranchId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearBranch(branch);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Branch NOT deleted");
        }catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
        }
        }
    }

    public void displayBranch(Branch BranchFrom, Branch BranchTo) {
                BranchTo.setBranchId(BranchFrom.getBranchId());
                BranchTo.setBranchCode(BranchFrom.getBranchCode());
                BranchTo.setBranchName(BranchFrom.getBranchName());
    }

    public void clearBranch(Branch branch) {
        if(branch!=null){
                branch.setBranchId(0);
                branch.setBranchCode("");
                branch.setBranchName("");
        }
    }
    public void clearSelectedBranch(){
        this.clearBranch(this.getSelectedBranch());
    }

    /**
     * @return the Branches
     */
    public List<Branch> getBranches() {
        String sql = "{call sp_search_branch_by_none()}";
        ResultSet rs = null;
        Branches = new ArrayList<Branch>();
        try (
                Connection conn = DBConnection.getINTER_BRANCH_MySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            //ps.setString(1, this.SearchBranchCode);
            rs = ps.executeQuery();
            while (rs.next()) {
                Branch branch = new Branch();
                branch.setBranchId(rs.getLong("branch_id"));
                branch.setBranchCode(rs.getString("branch_code"));
                branch.setBranchName(rs.getString("branch_name"));
                Branches.add(branch);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } catch(NullPointerException npe){
            System.err.println(npe.getMessage());
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Inter-Branch Database Connectivity is either LOST or PROBLEMATIC, contact admin...!"));
            Branches.clear();
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Branches;
    }

    /**
     * @param Branches the Branches to set
     */
    public void setBranches(List<Branch> Branches) {
        this.Branches = Branches;
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
     * @return the SelectedBranchId
     */
    public long getSelectedBranchId() {
        return SelectedBranchId;
    }

    /**
     * @param SelectedBranchId the SelectedBranchId to set
     */
    public void setSelectedBranchId(long SelectedBranchId) {
        this.SelectedBranchId = SelectedBranchId;
    }

    /**
     * @return the SearchBranchCode
     */
    public String getSearchBranchCode() {
        return SearchBranchCode;
    }

    /**
     * @param SearchBranchCode the SearchBranchCode to set
     */
    public void setSearchBranchCode(String SearchBranchCode) {
        this.SearchBranchCode = SearchBranchCode;
    }

    /**
     * @return the SearchBranchName
     */
    public String getSearchBranchName() {
        return SearchBranchName;
    }

    /**
     * @param SearchBranchName the SearchBranchName to set
     */
    public void setSearchBranchName(String SearchBranchName) {
        this.SearchBranchName = SearchBranchName;
    }

    /**
     * @return the SelectedBranch
     */
    public Branch getSelectedBranch() {
        return SelectedBranch;
    }

    /**
     * @param SelectedBranch the SelectedBranch to set
     */
    public void setSelectedBranch(Branch SelectedBranch) {
        this.SelectedBranch = SelectedBranch;
    }
}
