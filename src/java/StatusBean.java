/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class StatusBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ItemAddedStatus;
    private String ItemNotAddedStatus;
    private int ShowItemAddedStatus;//0 or 1
    private int ShowItemNotAddedStatus;//0 or 1

    public void clearStatus() {
        this.setItemAddedStatus("");
        this.setItemNotAddedStatus("");
        this.setShowItemAddedStatus(0);
        this.setShowItemNotAddedStatus(0);
    }

    public void initClearStatus() {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            this.setItemAddedStatus("");
            this.setItemNotAddedStatus("");
            this.setShowItemAddedStatus(0);
            this.setShowItemNotAddedStatus(0);
        }
    }

    /**
     * @return the ItemAddedStatus
     */
    public String getItemAddedStatus() {
        return ItemAddedStatus;
    }

    /**
     * @param ItemAddedStatus the ItemAddedStatus to set
     */
    public void setItemAddedStatus(String ItemAddedStatus) {
        this.ItemAddedStatus = ItemAddedStatus;
    }

    /**
     * @return the ItemNotAddedStatus
     */
    public String getItemNotAddedStatus() {
        return ItemNotAddedStatus;
    }

    /**
     * @param ItemNotAddedStatus the ItemNotAddedStatus to set
     */
    public void setItemNotAddedStatus(String ItemNotAddedStatus) {
        this.ItemNotAddedStatus = ItemNotAddedStatus;
    }

    /**
     * @return the ShowItemAddedStatus
     */
    public int getShowItemAddedStatus() {
        return ShowItemAddedStatus;
    }

    /**
     * @param ShowItemAddedStatus the ShowItemAddedStatus to set
     */
    public void setShowItemAddedStatus(int ShowItemAddedStatus) {
        this.ShowItemAddedStatus = ShowItemAddedStatus;
    }

    /**
     * @return the ShowItemNotAddedStatus
     */
    public int getShowItemNotAddedStatus() {
        return ShowItemNotAddedStatus;
    }

    /**
     * @param ShowItemNotAddedStatus the ShowItemNotAddedStatus to set
     */
    public void setShowItemNotAddedStatus(int ShowItemNotAddedStatus) {
        this.ShowItemNotAddedStatus = ShowItemNotAddedStatus;
    }

}
