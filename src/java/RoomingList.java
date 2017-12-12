
import java.util.Date;
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
public class RoomingList {

    private int RoomOccupancyId;
    private String RoomNumber;
    private String GuestName;
    private String Org_Self;
    private String RoomType;
    private Date StartDate;
    private Date EndDate;
    private int NumberOfPeople;
    private String RoomOccupancyStatus;
    private String Remarks;
    private int transaction_id;
    private float rate;
    private int item_id;
    private long room_id;
    
    private int bill_transactor_id;
    private String org_self;

    public long getRoom_id() {
        return room_id;
    }

    public void setRoom_id(long room_id) {
        this.room_id = room_id;
    }

    public int getBill_transactor_id() {
        return bill_transactor_id;
    }

    public void setBill_transactor_id(int bill_transactor_id) {
        this.bill_transactor_id = bill_transactor_id;
    }

    public String getOrg_self() {
        return org_self;
    }

    public void setOrg_self(String org_self) {
        this.org_self = org_self;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }

    public String getRoomOccupancyStatus() {
        return RoomOccupancyStatus;
    }

    public void setRoomOccupancyStatus(String RoomOccupancyStatus) {
        this.RoomOccupancyStatus = RoomOccupancyStatus;
    }

    public String getRoomType() {
        return RoomType;
    }

    public void setRoomType(String RoomType) {
        this.RoomType = RoomType;
    }

    public int getRoomOccupancyId() {
        return RoomOccupancyId;
    }

    public void setRoomOccupancyId(int RoomOccupancyId) {
        this.RoomOccupancyId = RoomOccupancyId;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String RoomNumber) {
        this.RoomNumber = RoomNumber;
    }

    public String getGuestName() {
        return GuestName;
    }

    public void setGuestName(String GuestName) {
        this.GuestName = GuestName;
    }

    public String getOrg_Self() {
        return Org_Self;
    }

    public void setOrg_Self(String Org_Self) {
        this.Org_Self = Org_Self;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date StartDate) {
        this.StartDate = StartDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date EndDate) {
        this.EndDate = EndDate;
    }

    public int getNumberOfPeople() {
        return NumberOfPeople;
    }

    public void setNumberOfPeople(int NumberOfPeople) {
        this.NumberOfPeople = NumberOfPeople;
    }
}
