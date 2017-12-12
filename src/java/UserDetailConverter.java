
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("UserDetailConverter")
public class UserDetailConverter  implements Converter {  
 
@Override
public Object getAsObject(FacesContext context, UIComponent component, String value){

    if (value == null || value.isEmpty()) {
        return null;
    }
    int id;
    try{
        id=Integer.parseInt(value);
    }catch(NumberFormatException nfe){
        return null;
    }
    return new UserDetailBean().findUserDetail(id);

}

@Override
public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null || value == "") {
        return null;
    }
    if (!(value instanceof UserDetail)) {
        throw new ConverterException("The value is not a valid UserDetail: " + value);
    }
    
    Integer gID = ((UserDetail) value).getUserDetailId();
    return (gID != null) ? String.valueOf(gID) : null;
}
 
}  
                      