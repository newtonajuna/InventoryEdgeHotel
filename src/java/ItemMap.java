
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class ItemMap implements Serializable {
    private static final long serialVersionUID = 1L;
    private long ItemMapId; 
    private long BigItemId;
    private long SmallItemId;
    private float FractionQty;
    private int Position;
    private long MapGroupId;

    /**
     * @return the ItemMapId
     */
    public long getItemMapId() {
        return ItemMapId;
    }

    /**
     * @param ItemMapId the ItemMapId to set
     */
    public void setItemMapId(long ItemMapId) {
        this.ItemMapId = ItemMapId;
    }

    /**
     * @return the BigItemId
     */
    public long getBigItemId() {
        return BigItemId;
    }

    /**
     * @param BigItemId the BigItemId to set
     */
    public void setBigItemId(long BigItemId) {
        this.BigItemId = BigItemId;
    }

    /**
     * @return the SmallItemId
     */
    public long getSmallItemId() {
        return SmallItemId;
    }

    /**
     * @param SmallItemId the SmallItemId to set
     */
    public void setSmallItemId(long SmallItemId) {
        this.SmallItemId = SmallItemId;
    }

    /**
     * @return the FractionQty
     */
    public float getFractionQty() {
        return FractionQty;
    }

    /**
     * @param FractionQty the FractionQty to set
     */
    public void setFractionQty(float FractionQty) {
        this.FractionQty = FractionQty;
    }

    /**
     * @return the Position
     */
    public int getPosition() {
        return Position;
    }

    /**
     * @param Position the Position to set
     */
    public void setPosition(int Position) {
        this.Position = Position;
    }

    /**
     * @return the MapGroupId
     */
    public long getMapGroupId() {
        return MapGroupId;
    }

    /**
     * @param MapGroupId the MapGroupId to set
     */
    public void setMapGroupId(long MapGroupId) {
        this.MapGroupId = MapGroupId;
    }
}
