import java.io.Serializable;
import javax.faces.bean.*;

@ManagedBean
@SessionScoped
public class GroupDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    private int GroupDetailId;
    private String GroupName;
    private String IsActive;

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
     * @return the GroupName
     */
    public String getGroupName() {
        return GroupName;
    }

    /**
     * @param GroupName the GroupName to set
     */
    public void setGroupName(String GroupName) {
        this.GroupName = GroupName;
    }

    /**
     * @return the IsActive
     */
    public String getIsActive() {
        return IsActive;
    }

    /**
     * @param IsActive the IsActive to set
     */
    public void setIsActive(String IsActive) {
        this.IsActive = IsActive;
    }
}
