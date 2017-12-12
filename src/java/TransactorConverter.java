
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("TransactorConverter")
public class TransactorConverter  implements Converter {  
 
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
    return new TransactorBean().findTransactor(id);

}

@Override
public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null || value == "") {
        return null;
    }
    if (!(value instanceof Transactor)) {
        throw new ConverterException("The value is not a valid Transactor: " + value);
    }
    
    Long gID = ((Transactor) value).getTransactorId();
    return (gID != null) ? String.valueOf(gID) : null;
}
 
}  
                      