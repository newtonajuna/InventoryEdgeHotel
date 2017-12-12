
import java.io.Serializable;
import javax.faces.bean.*;

@ManagedBean
@SessionScoped
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
    private int CategoryId;
    private String CategoryName;

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
