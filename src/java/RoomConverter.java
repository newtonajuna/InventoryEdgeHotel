
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bajuna
 */
@FacesConverter("RoomConverter")
public class RoomConverter   implements Converter {  
 
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
    return new RoomBean().findRoom(id);

}

@Override
public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null || value == "") {
        return null;
    }
    if (!(value instanceof Room)) {
        throw new ConverterException("The value is not a valid Room: " + value);
    }
    
    Integer gID = ((Room) value).getRoomId();
    return (gID != null) ? String.valueOf(gID) : null;
}
}
