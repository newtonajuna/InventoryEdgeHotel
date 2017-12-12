
import java.io.Serializable;
import javax.faces.bean.*;

@ManagedBean
@SessionScoped
public class PayMethod implements Serializable {

    private static final long serialVersionUID = 1L;
    private int PayMethodId;
    private String PayMethodName;
    private float Surchage;

    /**
     * @return the PayMethodId
     */
    public int getPayMethodId() {
        return PayMethodId;
    }

    /**
     * @param PayMethodId the PayMethodId to set
     */
    public void setPayMethodId(int PayMethodId) {
        this.PayMethodId = PayMethodId;
    }

    /**
     * @return the PayMethodName
     */
    public String getPayMethodName() {
        return PayMethodName;
    }

    /**
     * @param PayMethodName the PayMethodName to set
     */
    public void setPayMethodName(String PayMethodName) {
        this.PayMethodName = PayMethodName;
    }

    public float getSurchage() {
        return Surchage;
    }

    public void setSurchage(float Surchage) {
        this.Surchage = Surchage;
    }
}
