import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class CategoryBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Category> Categories;
    private String ActionMessage;
    private Category SelectedCategory = null;
    private int SelectedCategoryId;
    private String SearchCategoryName = "";
    private int TempId1;
    private String TempString1;
    private int TempId2;
    private String TempString2;

    public void saveCategory(Category cat) {
        String sql = null;
        String msg = null;

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (cat.getCategoryId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (cat.getCategoryId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (cat.getCategoryName().length() <= 0) {
            msg = "Category Name Needed...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            if (cat.getCategoryId() == 0) {
                sql = "{call sp_insert_category(?)}";
            } else if (cat.getCategoryId() > 0) {
                sql = "{call sp_update_category(?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (cat.getCategoryId() == 0) {
                    cs.setString(1, cat.getCategoryName());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearCategory(cat);
                } else if (cat.getCategoryId() > 0) {
                    cs.setInt(1, cat.getCategoryId());
                    cs.setString(2, cat.getCategoryName());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearCategory(cat);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Category NOT saved");
            }
        }

    }

    public void saveCategory1() {
        String sql = null;
        String msg = null;

        sql = "{call sp_update_category(?,?)}";
        
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            conn.setAutoCommit(false);
            cs.setInt(1, this.TempId1);
            cs.setString(2, this.TempString1);
            int x=cs.executeUpdate();
            System.out.println("X=" + x);
            //---
            cs.setInt(1, this.TempId2);
            cs.setString(2, this.TempString2);
            int y=cs.executeUpdate();
            System.out.println("Y=" + y);
            //---
            conn.commit();
            this.setActionMessage("Saved 1 Successfully");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Category 1 NOT saved");
        }
    }
    
    public void saveCategory2() {
        String sql = null;
        String msg = null;

        sql = "{call sp_update_category(?,?)}";
        
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            conn.setAutoCommit(false);
            cs.setInt(1, this.TempId2);
            cs.setString(2, this.TempString2);
            cs.executeUpdate();
            conn.commit();
            this.setActionMessage("Saved 2 Successfully");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Category 2 NOT saved");
        }
    }

    public Category getCategory(int CatId) {
        String sql = "{call sp_search_category_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, CatId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getInt("category_id"));
                cat.setCategoryName(rs.getString("category_name"));
                return cat;
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

    public void deleteCategory() {
        this.deleteCategoryById(this.SelectedCategoryId);
    }

    public void deleteCategoryByObject(Category Cat) {
        this.deleteCategoryById(Cat.getCategoryId());
    }

    public void deleteCategoryById(int CatId) {
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            String sql = "DELETE FROM category WHERE category_id=?";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, CatId);
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Category NOT deleted");
            }
        }
    }

    public void displayCategory(Category CatFrom, Category CatTo) {
        CatTo.setCategoryId(CatFrom.getCategoryId());
        CatTo.setCategoryName(CatFrom.getCategoryName());
    }

    public void clearCategory(Category Cat) {
        Cat.setCategoryId(0);
        Cat.setCategoryName("");
    }

    public List<Category> getCategories() {
        String sql;
        sql = "{call sp_search_category_by_none()}";
        ResultSet rs = null;
        Categories = new ArrayList<Category>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getInt("category_id"));
                cat.setCategoryName(rs.getString("category_name"));
                Categories.add(cat);
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
        return Categories;
    }

    /**
     * @param aCategoryName
     * @return the Categories
     */
    public List<Category> getCategoriesByCategoryName(String aCategoryName) {
        String sql;
        sql = "{call sp_search_category_by_name(?)}";
        ResultSet rs = null;
        Categories = new ArrayList<Category>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aCategoryName);
            rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getInt("category_id"));
                cat.setCategoryName(rs.getString("category_name"));
                Categories.add(cat);
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
        return Categories;
    }

    /**
     * @param Categories the Categories to set
     */
    public void setCategories(List<Category> Categories) {
        this.Categories = Categories;
    }

    /**
     * @return the SelectedCategory
     */
    public Category getSelectedCategory() {
        return SelectedCategory;
    }

    /**
     * @param SelectedCategory the SelectedCategory to set
     */
    public void setSelectedCategory(Category SelectedCategory) {
        this.SelectedCategory = SelectedCategory;
    }

    /**
     * @return the SelectedCategoryId
     */
    public int getSelectedCategoryId() {
        return SelectedCategoryId;
    }

    /**
     * @param SelectedCategoryId the SelectedCategoryId to set
     */
    public void setSelectedCategoryId(int SelectedCategoryId) {
        this.SelectedCategoryId = SelectedCategoryId;
    }

    /**
     * @return the SearchCategoryName
     */
    public String getSearchCategoryName() {
        return SearchCategoryName;
    }

    /**
     * @param SearchCategoryName the SearchCategoryName to set
     */
    public void setSearchCategoryName(String SearchCategoryName) {
        this.SearchCategoryName = SearchCategoryName;
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
     * @return the TempId1
     */
    public int getTempId1() {
        return TempId1;
    }

    /**
     * @param TempId1 the TempId1 to set
     */
    public void setTempId1(int TempId1) {
        this.TempId1 = TempId1;
    }

    /**
     * @return the TempString1
     */
    public String getTempString1() {
        return TempString1;
    }

    /**
     * @param TempString1 the TempString1 to set
     */
    public void setTempString1(String TempString1) {
        this.TempString1 = TempString1;
    }

    /**
     * @return the TempId2
     */
    public int getTempId2() {
        return TempId2;
    }

    /**
     * @param TempId2 the TempId2 to set
     */
    public void setTempId2(int TempId2) {
        this.TempId2 = TempId2;
    }

    /**
     * @return the TempString2
     */
    public String getTempString2() {
        return TempString2;
    }

    /**
     * @param TempString2 the TempString2 to set
     */
    public void setTempString2(String TempString2) {
        this.TempString2 = TempString2;
    }

}
