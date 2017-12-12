
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
public class SubCategoryBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SubCategory> SubCategories;
    private String ActionMessage=null;
    private SubCategory SelectedSubCategory=null;
    private int SelectedSubCategoryId;
    private String SearchSubCategoryName="";
    
    public void saveSubCategory(SubCategory subcat) {
        String sql = null;
        String msg=null;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

      if (subcat.getSubCategoryId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else if (subcat.getSubCategoryId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else if (subcat.getCategoryId()==0 || subcat.getSubCategoryName().length()<=0) {
            msg="Category and Subcategory cannot be empty...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else{
        if (subcat.getSubCategoryId() == 0) {
            sql = "{call sp_insert_sub_category(?,?)}";
        } else if (subcat.getSubCategoryId() > 0) {
            sql = "{call sp_update_sub_category(?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (subcat.getSubCategoryId() == 0) {
                cs.setInt(1, subcat.getCategoryId());
                cs.setString(2, subcat.getSubCategoryName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearSubCategory(subcat);
            } else if (subcat.getSubCategoryId() > 0) {
                cs.setInt(1, subcat.getSubCategoryId());
                cs.setInt(2, subcat.getCategoryId());
                cs.setString(3, subcat.getSubCategoryName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearSubCategory(subcat);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("SubCategory NOT saved");
        }
     }   
        
    }
    
    public SubCategory getSubCategory(int aSubCategoryId) {
        String sql = "{call sp_search_sub_category_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aSubCategoryId);
            rs = ps.executeQuery();
            if (rs.next()) {
                SubCategory subcat = new SubCategory();
                subcat.setSubCategoryId(rs.getInt("sub_category_id"));
                subcat.setCategoryId(rs.getInt("category_id"));
                subcat.setSubCategoryName(rs.getString("sub_category_name"));
                return subcat;
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
    
    public void deleteSubCategory() {
         this.deleteSubCategoryById(this.SelectedSubCategoryId);
    }
    
    public void deleteSubCategoryByObject(SubCategory SubCat) {
         this.deleteSubCategoryById(SubCat.getSubCategoryId());
    }

    public void deleteSubCategoryById(int aSubCategoryId) {
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
        String sql = "DELETE FROM sub_category WHERE sub_category_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aSubCategoryId);
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Category NOT deleted");
        }
    }
    }
    
    public void displaySubCategory(SubCategory SubCatFrom, SubCategory SubCatTo) {
        SubCatTo.setSubCategoryId(SubCatFrom.getSubCategoryId());
        SubCatTo.setCategoryId(SubCatFrom.getCategoryId());
        SubCatTo.setSubCategoryName(SubCatFrom.getSubCategoryName());
    }

    public void clearSubCategory(SubCategory SubCat) {
        SubCat.setSubCategoryId(0);
        SubCat.setCategoryId(0);
        SubCat.setCategoryName("");
        SubCat.setSubCategoryName("");
    }
    
    public List<SubCategory> getSubCategories() {
        String sql;
        sql="{call sp_search_sub_category_by_none()}";
        ResultSet rs = null;
        SubCategories = new ArrayList<SubCategory>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                SubCategory subcat = new SubCategory();
                subcat.setSubCategoryId(rs.getInt("sub_category_id"));
                subcat.setCategoryId(rs.getInt("category_id"));
                subcat.setCategoryName(rs.getString("category_name"));
                subcat.setSubCategoryName(rs.getString("sub_category_name"));
                SubCategories.add(subcat);
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
        return SubCategories;
    }

    public List<SubCategory> getSubCategoriesByCatSubcatName(String aCategorySubcategoryName) {
        String sql;
        sql="{call sp_search_sub_category_by_name(?)}";
        ResultSet rs = null;
        SubCategories = new ArrayList<SubCategory>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aCategorySubcategoryName);
            rs = ps.executeQuery();
            while (rs.next()) {
                SubCategory subcat = new SubCategory();
                subcat.setSubCategoryId(rs.getInt("sub_category_id"));
                subcat.setCategoryId(rs.getInt("category_id"));
                subcat.setCategoryName(rs.getString("category_name"));
                subcat.setSubCategoryName(rs.getString("sub_category_name"));
                SubCategories.add(subcat);
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
        return SubCategories;
    }
    
    public List<SubCategory> getSubCategoriesByCategoryId(int aCategoryId) {
        String sql;
        sql="{call sp_search_sub_category_by_category_id(?)}";
        ResultSet rs = null;
        SubCategories = new ArrayList<SubCategory>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aCategoryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                SubCategory subcat = new SubCategory();
                subcat.setSubCategoryId(rs.getInt("sub_category_id"));
                subcat.setCategoryId(rs.getInt("category_id"));
                subcat.setSubCategoryName(rs.getString("sub_category_name"));
                SubCategories.add(subcat);
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
        return SubCategories;
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
     * @return the SubCategories
     */

    /**
     * @param SubCategories the SubCategories to set
     */
    public void setSubCategories(List<SubCategory> SubCategories) {
        this.SubCategories = SubCategories;
    }

    /**
     * @return the SelectedSubCategory
     */
    public SubCategory getSelectedSubCategory() {
        return SelectedSubCategory;
    }

    /**
     * @param SelectedSubCategory the SelectedSubCategory to set
     */
    public void setSelectedSubCategory(SubCategory SelectedSubCategory) {
        this.SelectedSubCategory = SelectedSubCategory;
    }

    /**
     * @return the SelectedSubCategoryId
     */
    public int getSelectedSubCategoryId() {
        return SelectedSubCategoryId;
    }

    /**
     * @param SelectedSubCategoryId the SelectedSubCategoryId to set
     */
    public void setSelectedSubCategoryId(int SelectedSubCategoryId) {
        this.SelectedSubCategoryId = SelectedSubCategoryId;
    }

    /**
     * @return the SearchSubCategoryName
     */
    public String getSearchSubCategoryName() {
        return SearchSubCategoryName;
    }

    /**
     * @param SearchSubCategoryName the SearchSubCategoryName to set
     */
    public void setSearchSubCategoryName(String SearchSubCategoryName) {
        this.SearchSubCategoryName = SearchSubCategoryName;
    }
}
