
import java.io.Serializable;
import javax.faces.bean.*;

@ManagedBean
@SessionScoped
public class Test implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer TestInteger;
    private Float TestFloat;
    
    public void clearTest(){
        setTestInteger(null);
        setTestFloat(null);
    }
    
    public void clearTest2(){
        setTestInteger(1000);
        setTestFloat(Float.valueOf(1000));
    }

    /**
     * @return the TestInteger
     */
    public Integer getTestInteger() {
        return TestInteger;
    }

    /**
     * @param TestInteger the TestInteger to set
     */
    public void setTestInteger(Integer TestInteger) {
        this.TestInteger = TestInteger;
    }

    /**
     * @return the TestFloat
     */
    public Float getTestFloat() {
        return TestFloat;
    }

    /**
     * @param TestFloat the TestFloat to set
     */
    public void setTestFloat(Float TestFloat) {
        this.TestFloat = TestFloat;
    }
 
}
