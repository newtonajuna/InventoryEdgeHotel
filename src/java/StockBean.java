
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class StockBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Stock> Stocks;
    private String ActionMessage=null;
    private Stock SelectedStock=null;
    private Long SelectedStockId;
    
    public void saveStock(Stock stock) {
        String sql = null;
        if (stock.getStockId() == 0) {
            sql = "{call sp_insert_stock(?,?,?,?,?,?)}";
        } else if (stock.getStockId() > 0) {
            sql = "{call sp_update_stock(?,?,?,?,?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (stock.getStockId() == 0) {
                cs.setInt(1,stock.getStoreId());
                cs.setLong(2,stock.getItemId());
                cs.setString(3,stock.getBatchno());
                cs.setFloat(4,stock.getCurrentqty());
                try{
                    cs.setDate(5,new java.sql.Date(stock.getItemMnfDate().getTime()));
                    cs.setDate(6,new java.sql.Date(stock.getItemExpDate().getTime()));
                }catch(NullPointerException npe){
                    cs.setDate(5,null);
                    cs.setDate(6,null);
                }
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
            } else if (stock.getStockId() > 0) {
                cs.setLong(1, stock.getStockId());
                cs.setInt(2,stock.getStoreId());
                cs.setLong(3,stock.getItemId());
                cs.setString(4,stock.getBatchno());
                cs.setFloat(5,stock.getCurrentqty());
                try{
                    cs.setDate(6,new java.sql.Date(stock.getItemMnfDate().getTime()));
                    cs.setDate(7,new java.sql.Date(stock.getItemExpDate().getTime()));
                }catch(NullPointerException npe){
                    cs.setDate(6,null);
                    cs.setDate(7,null);
                }
                cs.executeUpdate();
                //clean stock
                StockBean.deleteZeroQtyStock();
                this.setActionMessage("Saved Successfully");
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Stock NOT saved");
        }
        
    }
    
    public Stock getStock(Long StockId) {
        String sql = "{call sp_search_stock_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, StockId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Stock stock = new Stock();
                stock.setStockId(rs.getLong("stock_id"));
                stock.setStoreId(rs.getInt("store_id"));
                stock.setItemId(rs.getLong("item_id")); 
                stock.setBatchno(rs.getString("batchno"));
                stock.setCurrentqty(rs.getFloat("currentqty"));
                try{
                    stock.setItemMnfDate(new Date(rs.getDate("item_mnf_date").getTime()));
                    stock.setItemExpDate(new Date(rs.getDate("item_exp_date").getTime()));
                }catch(NullPointerException npe){
                    stock.setItemMnfDate(null);
                    stock.setItemExpDate(null);
                }
                return stock;
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

    }
    
    public Stock getStock(int StoreId,long ItemId, String BatchNo) {
        String sql = "{call sp_search_stock_by_store_item_batch(?,?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, StoreId);
            ps.setLong(2, ItemId);
            ps.setString(3, BatchNo);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                Stock stock = new Stock();
                stock.setStockId(rs.getLong("stock_id"));
                stock.setStoreId(rs.getInt("store_id"));
                stock.setItemId(rs.getLong("item_id")); 
                stock.setBatchno(rs.getString("batchno"));
                stock.setCurrentqty(rs.getFloat("currentqty"));
                try{
                    stock.setItemMnfDate(new Date(rs.getDate("item_mnf_date").getTime()));
                    stock.setItemExpDate(new Date(rs.getDate("item_exp_date").getTime()));
                }catch(NullPointerException npe){
                    stock.setItemMnfDate(null);
                    stock.setItemExpDate(null);
                }
                return stock;
            } else {
                return null;
            }
        } catch (SQLException | NullPointerException se) {
            System.err.println(se.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

    }
    
    public void addStock(int StoreId,Long ItemId, String BatchNo,float AddQty) {
        String sql = "{call sp_add_stock_by_store_item_batch(?,?,?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, StoreId);
            ps.setLong(2, ItemId);
            ps.setString(3, BatchNo);
            ps.setFloat(4, AddQty);
            ps.executeUpdate();
            //clean stock
            StockBean.deleteZeroQtyStock();
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
    
    public void subtractStock(int StoreId,Long ItemId, String BatchNo,float SubtractQty) {
        String sql = "{call sp_subtract_stock_by_store_item_batch(?,?,?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, StoreId);
            ps.setLong(2, ItemId);
            ps.setString(3, BatchNo);
            ps.setFloat(4, SubtractQty);
            ps.executeUpdate();
            //clean stock
            StockBean.deleteZeroQtyStock();
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
    
    public void deleteStock() {
         this.deleteStockById(this.getSelectedStockId());
    }
    public void deleteStockByObject(Stock stock) {
         this.deleteStockById(stock.getStockId());
    }

    public void deleteStockById(Long StockId) {
        String sql = "DELETE FROM stock WHERE stock_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, StockId);
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Stock NOT deleted");
        }
    }

    public void displayStock(Stock StockFrom, Stock StockTo) {
        StockTo.setStockId(StockFrom.getStockId());
        StockTo.setStoreId(StockFrom.getStoreId());
        StockTo.setItemId(StockFrom.getItemId());
        StockTo.setBatchno(StockFrom.getBatchno());
        StockTo.setCurrentqty(StockFrom.getCurrentqty());
        StockTo.setItemMnfDate(StockFrom.getItemMnfDate());
        StockTo.setItemExpDate(StockFrom.getItemExpDate());
    }

    public void clearStock(Stock stock) {
        stock.setStockId(0);
        stock.setStockId(0);
        stock.setStoreId(0);
        stock.setItemId(0);
        stock.setBatchno("");
        stock.setCurrentqty(0);
        //stock.setItemMnfDate(null);
        //stock.setItemExpDate(null);
    }

    /**
     * @return the Stocks
     */
    public List<Stock> getStocks() {
        String sql;
        sql = "SELECT * FROM stock";
        ResultSet rs = null;
        Stocks = new ArrayList<Stock>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setStockId(rs.getLong("stock_id"));
                stock.setStoreId(rs.getInt("store_id"));
                stock.setItemId(rs.getLong("item_id")); 
                stock.setBatchno(rs.getString("batchno"));
                stock.setCurrentqty(rs.getFloat("currentqty"));
                try{
                    stock.setItemMnfDate(new Date(rs.getDate("item_mnf_date").getTime()));
                    stock.setItemExpDate(new Date(rs.getDate("item_exp_date").getTime()));
                }catch(NullPointerException npe){
                    stock.setItemMnfDate(null);
                    stock.setItemExpDate(null);
                }
                Stocks.add(stock);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Stocks;
    }
    
    public List<Stock> getStocks(int StoreId,long ItemId) {
        String sql;
        sql = "{call sp_search_stock_by_store_item(?,?)}";
        ResultSet rs = null;
        Stocks = new ArrayList<Stock>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, StoreId);
            ps.setLong(2, ItemId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setStockId(rs.getLong("stock_id"));
                stock.setStoreId(rs.getInt("store_id"));
                stock.setItemId(rs.getLong("item_id")); 
                stock.setBatchno(rs.getString("batchno"));
                stock.setCurrentqty(rs.getFloat("currentqty"));
                try{
                    stock.setItemMnfDate(new Date(rs.getDate("item_mnf_date").getTime()));
                    stock.setItemExpDate(new Date(rs.getDate("item_exp_date").getTime()));
                }catch(NullPointerException npe){
                    stock.setItemMnfDate(null);
                    stock.setItemExpDate(null);
                }
                Stocks.add(stock);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Stocks;
    }
    
    public List<Stock> getStocksByItem(long ItemId) {
        String sql;
        sql = "{call sp_search_stock_by_item_id(?)}";
        ResultSet rs = null;
        Stocks = new ArrayList<Stock>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, ItemId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setStockId(rs.getLong("stock_id"));
                stock.setStoreId(rs.getInt("store_id"));
                stock.setItemId(rs.getLong("item_id")); 
                stock.setBatchno(rs.getString("batchno"));
                stock.setCurrentqty(rs.getFloat("currentqty"));
                try{
                    stock.setItemMnfDate(new Date(rs.getDate("item_mnf_date").getTime()));
                    stock.setItemExpDate(new Date(rs.getDate("item_exp_date").getTime()));
                }catch(NullPointerException npe){
                    stock.setItemMnfDate(null);
                    stock.setItemExpDate(null);
                }
                Stocks.add(stock);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        //GeneralSetting.setLIST_ITEMS_COUNT(Stocks.size());
        return Stocks;
    }
    
    public List<Stock> getStocks(int aStoreId) {
        String sql;
        sql = "{call sp_search_stock_by_store_id(?)}";
        ResultSet rs = null;
        Stocks = new ArrayList<Stock>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aStoreId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setStockId(rs.getLong("stock_id"));
                stock.setStoreId(rs.getInt("store_id"));
                stock.setItemId(rs.getLong("item_id")); 
                stock.setBatchno(rs.getString("batchno"));
                stock.setCurrentqty(rs.getFloat("currentqty"));
                try{
                    stock.setItemMnfDate(new Date(rs.getDate("item_mnf_date").getTime()));
                    stock.setItemExpDate(new Date(rs.getDate("item_exp_date").getTime()));
                }catch(NullPointerException npe){
                    stock.setItemMnfDate(null);
                    stock.setItemExpDate(null);
                }
                Stocks.add(stock);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Stocks;
    }
    /**
     * @param Stocks the Stocks to set
     */
    public void setStocks(List<Stock> Stocks) {
        this.Stocks = Stocks;
    }

    /**
     * @return the ActionMessage
     */
    public String getActionMessage() {
        return ActionMessage;
    }

    /**
     * @param ActionMessage the ActionMessage to set
     */
    public void setActionMessage(String ActionMessage) {
        this.ActionMessage = ActionMessage;
    }

    /**
     * @return the SelectedStock
     */
    public Stock getSelectedStock() {
        return SelectedStock;
    }

    /**
     * @param SelectedStock the SelectedStock to set
     */
    public void setSelectedStock(Stock SelectedStock) {
        this.SelectedStock = SelectedStock;
    }

    /**
     * @return the SelectedStockId
     */
    public Long getSelectedStockId() {
        return SelectedStockId;
    }

    /**
     * @param SelectedStockId the SelectedStockId to set
     */
    public void setSelectedStockId(Long SelectedStockId) {
        this.SelectedStockId = SelectedStockId;
    }
    
    public static void deleteZeroQtyStock() {
        String sql = "DELETE FROM stock WHERE currentqty=0";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.executeUpdate();
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }
    }
    
    public String getExpiryListString(Date aExpiryDate) {
        String dateString = "";
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");
        try {
            dateString = "(" + sdfr.format(aExpiryDate) + ")";
        } catch (Exception ex) {
            dateString="";
        }
        return dateString;
    }
}
