
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class GeneralSetting implements Serializable {
    private static final long serialVersionUID = 1L;

    
    public boolean isTwoStringsEqual(String aString1,String aString2){
        if(aString1.equals(aString2)){
            return true;
        }else{
            return false;
        }
    }
        
    public String getStyleColorByDaysFromNow(String aCriteria, Date aDate){
        if(aCriteria.equals("DISCOUNT-EXPIRY-DATE")){
            if(CompanySetting.getDaysBetweenNowAndDate(aDate)<0){
                return "red";
            }else if(CompanySetting.getDaysBetweenNowAndDate(aDate)==0){
                return "darkorange";
            }else{
                return "black";
            }
        }
        return "";
    }

}
