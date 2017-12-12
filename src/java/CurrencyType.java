
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bajuna
 */
@ManagedBean
@SessionScoped
public class CurrencyType implements Serializable {

    private static final long serialVersionUID = 1L;
    private int CurrencyTypeId;
    private String CurrencyTypeName;
    private float ExchangeRate;

    public int getCurrencyTypeId() {
        return CurrencyTypeId;
    }

    public void setCurrencyTypeId(int CurrencyTypeId) {
        this.CurrencyTypeId = CurrencyTypeId;
    }

    public String getCurrencyTypeName() {
        return CurrencyTypeName;
    }

    public void setCurrencyTypeName(String CurrencyTypeName) {
        this.CurrencyTypeName = CurrencyTypeName;
    }

    public float getExchangeRate() {
        return ExchangeRate;
    }

    public void setExchangeRate(float ExchangeRate) {
        this.ExchangeRate = ExchangeRate;
    }
}
