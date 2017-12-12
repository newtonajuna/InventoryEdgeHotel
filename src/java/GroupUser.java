
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
public class GroupUser implements Serializable {

    private static final long serialVersionUID = 1L;
    private int GroupUserId;
    private int GroupDetailId;
    private int UserDetailId;

    /**
     * @return the GroupUserId
     */
    public int getGroupUserId() {
        return GroupUserId;
    }

    /**
     * @param GroupUserId the GroupUserId to set
     */
    public void setGroupUserId(int GroupUserId) {
        this.GroupUserId = GroupUserId;
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
}
