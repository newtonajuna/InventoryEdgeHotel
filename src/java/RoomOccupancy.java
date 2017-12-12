
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bajuna
 */
public class RoomOccupancy {

    private int RoomId;
    private int RoomPackageId;
    private int TransactorId;
    private Date StartDate;
    private Date EndDate;
    private String RoomOccupancyStatus;
    private String Remarks;
    private Transactor transactor;
    private Room room;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Transactor getTransactor() {
        return transactor;
    }

    public void setTransactor(Transactor transactor) {
        this.transactor = transactor;
    }

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int RoomId) {
        this.RoomId = RoomId;
    }

    public int getRoomPackageId() {
        return RoomPackageId;
    }

    public void setRoomPackageId(int RoomPackageId) {
        this.RoomPackageId = RoomPackageId;
    }

    public int getTransactorId() {
        return TransactorId;
    }

    public void setTransactorId(int TransactorId) {
        this.TransactorId = TransactorId;
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

    public String getRoomOccupancyStatus() {
        return RoomOccupancyStatus;
    }

    public void setRoomOccupancyStatus(String RoomOccupancyStatus) {
        this.RoomOccupancyStatus = RoomOccupancyStatus;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }
}
