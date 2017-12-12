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
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class DiscountPackageBean implements Serializable {
private List<DiscountPackage> DiscountPackages;
    private String ActionMessage=null;
    private DiscountPackage SelectedDiscountPackage=null;
    private long SelectedDiscountPackageId;
    private String SearchPackageName="";
    
    public void saveDiscountPackage(DiscountPackage discountPackage) {
        String sql = null;
        String msg="";
        String sql2 = null;
        sql2="SELECT * FROM discount_package WHERE package_name='" + discountPackage.getPackageName() + "'";
        
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        
        if (discountPackage.getDiscountPackageId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (discountPackage.getDiscountPackageId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(discountPackage.getPackageName(), 1, 50).equals("FAIL")){
            msg="Enter Discount Package Name!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if((new CustomValidator().CheckRecords(sql2)>0 && discountPackage.getDiscountPackageId()==0) || (new CustomValidator().CheckRecords(sql2)>0 && new CustomValidator().CheckRecords(sql2)!=1 && discountPackage.getDiscountPackageId()>0)){
            msg="Discount Package Name already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(discountPackage.getStartDate()==null || discountPackage.getEndDate()==null){
            msg="PleasesSelect  valid discount start and end dates...!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
            
        if (discountPackage.getDiscountPackageId() == 0) {
            sql = "{call sp_insert_discount_package(?,?,?)}";
        } else if (discountPackage.getDiscountPackageId() > 0) {
            sql = "{call sp_update_discount_package(?,?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (discountPackage.getDiscountPackageId() == 0) {
                cs.setString("in_package_name", discountPackage.getPackageName());
                cs.setTimestamp("in_start_date", new java.sql.Timestamp(discountPackage.getStartDate().getTime()));
                cs.setTimestamp("in_end_date", new java.sql.Timestamp(discountPackage.getEndDate().getTime()));
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearDiscountPackage(discountPackage);
            } else if (discountPackage.getDiscountPackageId() > 0) {
                cs.setLong("in_discount_package_id", discountPackage.getDiscountPackageId());
                cs.setString("in_package_name", discountPackage.getPackageName());
                cs.setTimestamp("in_start_date", new java.sql.Timestamp(discountPackage.getStartDate().getTime()));
                cs.setTimestamp("in_end_date", new java.sql.Timestamp(discountPackage.getEndDate().getTime()));
                cs.executeUpdate();
                this.setActionMessage("Updated Successfully");
                this.clearDiscountPackage(discountPackage);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("DiscountPackage NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("DiscountPackage NOT saved!"));
        }
    }
    }
    
    public DiscountPackage getDiscountPackage(int DiscountPackageId) {
        String sql = "{call sp_search_discount_package_by_id(?)}";
        ResultSet rs = null;    
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, DiscountPackageId);
            rs = ps.executeQuery();
            if (rs.next()) {
                DiscountPackage discountPackage = new DiscountPackage();
                discountPackage.setDiscountPackageId(rs.getInt("discount_package_id"));
                discountPackage.setPackageName(rs.getString("package_name"));
                discountPackage.setStartDate(new Date(rs.getTimestamp("start_date").getTime()));
                discountPackage.setEndDate(new Date(rs.getTimestamp("end_date").getTime()));
                return discountPackage;
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
    
    public void deleteDiscountPackage(DiscountPackage discountPackage) {
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else   
        {
        String sql = "DELETE FROM discount_package WHERE discount_package_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, discountPackage.getDiscountPackageId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearDiscountPackage(discountPackage);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("DiscountPackage NOT deleted");
        }
        }
    }

    public void displayDiscountPackage(DiscountPackage DiscountPackageFrom, DiscountPackage DiscountPackageTo) {
                DiscountPackageTo.setDiscountPackageId(DiscountPackageFrom.getDiscountPackageId());
                DiscountPackageTo.setPackageName(DiscountPackageFrom.getPackageName());
                DiscountPackageTo.setStartDate(DiscountPackageFrom.getStartDate());
                DiscountPackageTo.setEndDate(DiscountPackageFrom.getEndDate());
    }

    public void clearDiscountPackage(DiscountPackage discountPackage) {
        if(discountPackage!=null){
                discountPackage.setDiscountPackageId(0);
                discountPackage.setPackageName("");
                discountPackage.setStartDate(null);
                discountPackage.setEndDate(null);
        }
    }
    public void clearSelectedDiscountPackage(){
        this.clearDiscountPackage(this.getSelectedDiscountPackage());
    }

    /**
     * @return the DiscountPackages
     */
    public List<DiscountPackage> getDiscountPackages() {
        String sql = "{call sp_search_discount_package_by_none()}";
        ResultSet rs = null;
        DiscountPackages = new ArrayList<DiscountPackage>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                DiscountPackage discountPackage = new DiscountPackage();
                discountPackage.setDiscountPackageId(rs.getInt("discount_package_id"));
                discountPackage.setPackageName(rs.getString("package_name"));
                discountPackage.setStartDate(new Date(rs.getTimestamp("start_date").getTime()));
                discountPackage.setEndDate(new Date(rs.getTimestamp("end_date").getTime()));
                DiscountPackages.add(discountPackage);
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
        return DiscountPackages;
    }

    /**
     * @param DiscountPackages the DiscountPackages to set
     */
    public void setDiscountPackages(List<DiscountPackage> DiscountPackages) {
        this.DiscountPackages = DiscountPackages;
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
     * @return the SelectedDiscountPackageId
     */
    public long getSelectedDiscountPackageId() {
        return SelectedDiscountPackageId;
    }

    /**
     * @param SelectedDiscountPackageId the SelectedDiscountPackageId to set
     */
    public void setSelectedDiscountPackageId(long SelectedDiscountPackageId) {
        this.SelectedDiscountPackageId = SelectedDiscountPackageId;
    }

    /**
     * @return the SearchPackageName
     */
    public String getSearchPackageName() {
        return SearchPackageName;
    }

    /**
     * @param SearchPackageName the SearchPackageName to set
     */
    public void setSearchPackageName(String SearchPackageName) {
        this.SearchPackageName = SearchPackageName;
    }

    /**
     * @return the SelectedDiscountPackage
     */
    public DiscountPackage getSelectedDiscountPackage() {
        return SelectedDiscountPackage;
    }

    /**
     * @param SelectedDiscountPackage the SelectedDiscountPackage to set
     */
    public void setSelectedDiscountPackage(DiscountPackage SelectedDiscountPackage) {
        this.SelectedDiscountPackage = SelectedDiscountPackage;
    }
}
