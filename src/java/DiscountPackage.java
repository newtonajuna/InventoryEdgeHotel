import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.*;

@ManagedBean
@SessionScoped
public class DiscountPackage implements Serializable {
    private static final long serialVersionUID = 1L;
    private int DiscountPackageId;
    private String PackageName;
    private Date StartDate;
    private Date EndDate;

    /**
     * @return the DiscountPackageId
     */
    public int getDiscountPackageId() {
        return DiscountPackageId;
    }

    /**
     * @param DiscountPackageId the DiscountPackageId to set
     */
    public void setDiscountPackageId(int DiscountPackageId) {
        this.DiscountPackageId = DiscountPackageId;
    }

    /**
     * @return the PackageName
     */
    public String getPackageName() {
        return PackageName;
    }

    /**
     * @param PackageName the PackageName to set
     */
    public void setPackageName(String PackageName) {
        this.PackageName = PackageName;
    }

    /**
     * @return the StartDate
     */
    public Date getStartDate() {
        return StartDate;
    }

    /**
     * @param StartDate the StartDate to set
     */
    public void setStartDate(Date StartDate) {
        this.StartDate = StartDate;
    }

    /**
     * @return the EndDate
     */
    public Date getEndDate() {
        return EndDate;
    }

    /**
     * @param EndDate the EndDate to set
     */
    public void setEndDate(Date EndDate) {
        this.EndDate = EndDate;
    }
    
}
