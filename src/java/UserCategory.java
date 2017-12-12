
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
public class UserCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    private int UserCategoryId=0;
    private String UserCategoryName;

    /**
     * @return the UserCategoryId
     */
    public int getUserCategoryId() {
        return UserCategoryId;
    }

    /**
     * @param UserCategoryId the UserCategoryId to set
     */
    public void setUserCategoryId(int UserCategoryId) {
        this.UserCategoryId = UserCategoryId;
    }

    /**
     * @return the UserCategoryName
     */
    public String getUserCategoryName() {
        return UserCategoryName;
    }

    /**
     * @param UserCategoryName the UserCategoryName to set
     */
    public void setUserCategoryName(String UserCategoryName) {
        this.UserCategoryName = UserCategoryName;
    }
    
}
