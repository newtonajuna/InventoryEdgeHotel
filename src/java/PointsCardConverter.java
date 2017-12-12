
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("PointsCardConverter")
public class PointsCardConverter  implements Converter {  
 
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
    return new PointsCardBean().findPointsCard(id);

}

@Override
public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null || value == "") {
        return null;
    }
    if (!(value instanceof PointsCard)) {
        throw new ConverterException("The value is not a valid PointsCard: " + value);
    }
    
    Long gID = ((PointsCard) value).getPointsCardId();
    return (gID != null) ? String.valueOf(gID) : null;
}
 
}  
                      