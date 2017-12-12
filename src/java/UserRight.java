
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
public class UserRight implements Serializable {

    private static final long serialVersionUID = 1L;
    private int UserRightId;
    private int StoreId;
    private String StoreName;
    private int UserDetailId;
    private String Username;
    private String UserNames;
    private String FunctionName;
    private String AllowView;
    private String AllowEdit;
    private String AllowAdd;
    private String AllowDelete;

    /**
     * @return the UserRightId
     */
    public int getUserRightId() {
        return UserRightId;
    }

    /**
     * @param UserRightId the UserRightId to set
     */
    public void setUserRightId(int UserRightId) {
        this.UserRightId = UserRightId;
    }

    /**
     * @return the StoreId
     */
    public int getStoreId() {
        return StoreId;
    }

    /**
     * @param StoreId the StoreId to set
     */
    public void setStoreId(int StoreId) {
        this.StoreId = StoreId;
    }

    /**
     * @return the StoreName
     */
    public String getStoreName() {
        return StoreName;
    }

    /**
     * @param StoreName the StoreName to set
     */
    public void setStoreName(String StoreName) {
        this.StoreName = StoreName;
    }

    /**
     * @return the UserDetailId
     */
    public int getUserDetailId() {
        return UserDetailId;
    }

    /**
     * @param UserDetailId the UserDetailId to set
     */
    public void setUserDetailId(int UserDetailId) {
        this.UserDetailId = UserDetailId;
    }

    /**
     * @return the Username
     */
    public String getUsername() {
        return Username;
    }

    /**
     * @param Username the Username to set
     */
    public void setUsername(String Username) {
        this.Username = Username;
    }

    /**
     * @return the UserNames
     */
    public String getUserNames() {
        return UserNames;
    }

    /**
     * @param UserNames the UserNames to set
     */
    public void setUserNames(String UserNames) {
        this.UserNames = UserNames;
    }

    /**
     * @return the FunctionName
     */
    public String getFunctionName() {
        return FunctionName;
    }

    /**
     * @param FunctionName the FunctionName to set
     */
    public void setFunctionName(String FunctionName) {
        this.FunctionName = FunctionName;
    }

    /**
     * @return the AllowView
     */
    public String getAllowView() {
        return AllowView;
    }

    /**
     * @param AllowView the AllowView to set
     */
    public void setAllowView(String AllowView) {
        this.AllowView = AllowView;
    }

    /**
     * @return the AllowEdit
     */
    public String getAllowEdit() {
        return AllowEdit;
    }

    /**
     * @param AllowEdit the AllowEdit to set
     */
    public void setAllowEdit(String AllowEdit) {
        this.AllowEdit = AllowEdit;
    }

    /**
     * @return the AllowAdd
     */
    public String getAllowAdd() {
        return AllowAdd;
    }

    /**
     * @param AllowAdd the AllowAdd to set
     */
    public void setAllowAdd(String AllowAdd) {
        this.AllowAdd = AllowAdd;
    }

    /**
     * @return the AllowDelete
     */
    public String getAllowDelete() {
        return AllowDelete;
    }

    /**
     * @param AllowDelete the AllowDelete to set
     */
    public void setAllowDelete(String AllowDelete) {
        this.AllowDelete = AllowDelete;
    }
    
}
