import java.io.Serializable;
import javax.faces.bean.*;

@ManagedBean
@SessionScoped
public class Branch implements Serializable {
    private static final long serialVersionUID = 1L;
    private long BranchId;
    private String BranchCode;
    private String BranchName;

    /**
     * @return the BranchId
     */
    public long getBranchId() {
        return BranchId;
    }

    /**
     * @param BranchId the BranchId to set
     */
    public void setBranchId(long BranchId) {
        this.BranchId = BranchId;
    }

    /**
     * @return the BranchCode
     */
    public String getBranchCode() {
        return BranchCode;
    }

    /**
     * @param BranchCode the BranchCode to set
     */
    public void setBranchCode(String BranchCode) {
        this.BranchCode = BranchCode;
    }

    /**
     * @return the BranchName
     */
    public String getBranchName() {
        return BranchName;
    }

    /**
     * @param BranchName the BranchName to set
     */
    public void setBranchName(String BranchName) {
        this.BranchName = BranchName;
    }
}
