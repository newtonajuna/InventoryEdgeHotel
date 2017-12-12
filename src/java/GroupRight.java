
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
public class GroupRight implements Serializable {

    private static final long serialVersionUID = 1L;
    private int GroupRightId;
    private int StoreId;
    private int GroupDetailId;
    private String FunctionName;
    private String AllowView;
    private String AllowEdit;
    private String AllowAdd;
    private String AllowDelete;

    /**
     * @return the GroupRightId
     */
    public int getGroupRightId() {
        return GroupRightId;
    }

    /**
     * @param GroupRightId the GroupRightId to set
     */
    public void setGroupRightId(int GroupRightId) {
        this.GroupRightId = GroupRightId;
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
     * @return the GroupDetailId
     */
    public int getGroupDetailId() {
        return GroupDetailId;
    }

    /**
     * @param GroupDetailId the GroupDetailId to set
     */
    public void setGroupDetailId(int GroupDetailId) {
        this.GroupDetailId = GroupDetailId;
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
