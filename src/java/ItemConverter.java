
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("ItemConverter")
public class ItemConverter  implements Converter {  
 
@Override
public Object getAsObject(FacesContext context, UIComponent component, String value){

    if (value == null || value.isEmpty()) {
        return null;
    }
    Long id;
    try{
        id=Long.parseLong(value);
    }catch(NumberFormatException nfe){
        return null;
    }
    return new ItemBean().findItem(id);

}

@Override
public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null || value == "") {
        return null;
    }
    if (!(value instanceof Item)) {
        throw new ConverterException("The value is not a valid Item: " + value);
    }
    
    Long gID = ((Item) value).getItemId();
    return (gID != null) ? String.valueOf(gID) : null;
}
 
}  
                      