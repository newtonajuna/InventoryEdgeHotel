
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
public class SubCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    private int SubCategoryId=0;
    private int CategoryId;
    private String CategoryName;
    private String SubCategoryName;

    /**
     * @return the SubCategoryId
     */
    public int getSubCategoryId() {
        return SubCategoryId;
    }

    /**
     * @param SubCategoryId the SubCategoryId to set
     */
    public void setSubCategoryId(int SubCategoryId) {
        this.SubCategoryId = SubCategoryId;
    }

    /**
     * @return the CategoryId
     */
    public int getCategoryId() {
        return CategoryId;
    }

    /**
     * @param CategoryId the CategoryId to set
     */
    public void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }

    /**
     * @return the SubCategoryName
     */
    public String getSubCategoryName() {
        return SubCategoryName;
    }

    /**
     * @param SubCategoryName the SubCategoryName to set
     */
    public void setSubCategoryName(String SubCategoryName) {
        this.SubCategoryName = SubCategoryName;
    }

    /**
     * @return the CategoryName
     */
    public String getCategoryName() {
        return CategoryName;
    }

    /**
     * @param CategoryName the CategoryName to set
     */
    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }
    
}
