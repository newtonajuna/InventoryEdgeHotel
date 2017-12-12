
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class ReportStockBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ActionMessage=null;
    List<ReportStock> ActiveReportStock=new ArrayList<ReportStock>();
    List<ReportStockSummary> ActiveReportStockSummary=new ArrayList<ReportStockSummary>();
    List<ReportStock> ActiveReportStockTotal=new ArrayList<ReportStock>();
    List<ReportStockSummary> ActiveReportStockTotalSummary=new ArrayList<ReportStockSummary>();
    List<ReportStock> ActiveReportStockAll=new ArrayList<ReportStock>();
    List<ReportStockSummary> ActiveReportStockAllSummary=new ArrayList<ReportStockSummary>();
    private double TotalCostValue;
    private double TotalWholesaleValue;
    private double TotalRetailsaleValue;
    
    public List<ReportStock> getActiveReportStock(ReportStock aReportStock,boolean RETRIEVE_REPORT){
        String sql;   
        sql = "{call sp_report_stock_in(?,?,?,?,?)}";
        ResultSet rs = null;
        this.ActiveReportStock.clear();
        if(aReportStock!=null && RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try{
                        ps.setInt(1,aReportStock.getStoreId());
                    }catch(NullPointerException npe){
                        ps.setInt(1,0);
                    }
                    try{
                        ps.setInt(2,aReportStock.getCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(2,0);
                    }
                    try{
                        ps.setInt(3,aReportStock.getSubCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(3,0);
                    }
                    try{
                        ps.setDate(4,new java.sql.Date(aReportStock.getItemExpDate().getTime()));
                    }catch(NullPointerException npe){
                        ps.setDate(4,null);
                    }
                    try{
                        ps.setDate(5,new java.sql.Date(aReportStock.getItemExpDate2().getTime()));
                    }catch(NullPointerException npe){
                        ps.setDate(5,null);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ActiveReportStock.add(this.getReportStockFromResultset(rs));
                    }
                    this.setActionMessage("");
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
        return this.ActiveReportStock;
    }
    public long getActiveReportStockCount(){
        return this.ActiveReportStock.size();
    }
    
    public List<ReportStock> getActiveReportStockTotal(ReportStock aReportStock,boolean RETRIEVE_REPORT){
        String sql;   
        sql = "{call sp_report_stock_total(?,?,?)}";
        ResultSet rs = null;
        this.ActiveReportStockTotal.clear();
        if(aReportStock!=null && RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try{
                        ps.setInt(1,aReportStock.getStoreId());
                    }catch(NullPointerException npe){
                        ps.setInt(1,0);
                    }
                    try{
                        ps.setInt(2,aReportStock.getCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(2,0);
                    }
                    try{
                        ps.setInt(3,aReportStock.getSubCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(3,0);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ActiveReportStockTotal.add(this.getReportStockTotalFromResultset(rs));
                    }
                    this.setActionMessage("");
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
        return this.ActiveReportStockTotal;
    }
    public long getActiveReportStockTotalCount(){
        return this.ActiveReportStockTotal.size();
    }
    
    public List<ReportStock> getActiveReportStockAll(ReportStock aReportStock,boolean RETRIEVE_REPORT){
        String sql;   
        sql = "{call sp_report_stock_all(?,?,?)}";
        ResultSet rs = null;
        this.ActiveReportStockAll.clear();
        if(aReportStock!=null && RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try{
                        ps.setInt(1,aReportStock.getCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(1,0);
                    }
                    try{
                        ps.setInt(2,aReportStock.getSubCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(2,0);
                    }
                    try{
                        ps.setString(3,aReportStock.getReorderFilter());
                    }catch(NullPointerException npe){
                        ps.setString(3,"");
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ActiveReportStockAll.add(this.getReportStockAllFromResultset(rs));
                    }
                    this.setActionMessage("");
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
        return this.ActiveReportStockAll;
    }
    public long getActiveReportStockAllCount(){
        return this.ActiveReportStockAll.size();
    }

    public List<ReportStockSummary> getActiveReportStockSummary(ReportStock aReportStock,boolean RETRIEVE_REPORT){
        String sql;   
        sql = "{call sp_report_stock_in_summary(?,?,?,?,?)}";
        ResultSet rs = null;
        this.ActiveReportStockSummary.clear();
        if(aReportStock!=null && RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try{
                        ps.setInt(1,aReportStock.getStoreId());
                    }catch(NullPointerException npe){
                        ps.setInt(1,0);
                    }
                    try{
                        ps.setInt(2,aReportStock.getCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(2,0);
                    }
                    try{
                        ps.setInt(3,aReportStock.getSubCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(3,0);
                    }
                    try{
                        ps.setDate(4,new java.sql.Date(aReportStock.getItemExpDate().getTime()));
                    }catch(NullPointerException npe){
                        ps.setDate(4,null);
                    }
                    try{
                        ps.setDate(5,new java.sql.Date(aReportStock.getItemExpDate2().getTime()));
                    }catch(NullPointerException npe){
                        ps.setDate(5,null);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ActiveReportStockSummary.add(this.getReportStockSummaryFromResultset(rs));
                    }
                    this.setActionMessage("");
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
        return this.ActiveReportStockSummary;
    }
    
    public long getActiveReportStockSummaryCount(){
        return this.ActiveReportStockSummary.size();
    }
    
    public List<ReportStockSummary> getActiveReportStockTotalSummary(ReportStock aReportStock,boolean RETRIEVE_REPORT){
        String sql;   
        sql = "{call sp_report_stock_total_summary(?,?,?)}";
        ResultSet rs = null;
        this.ActiveReportStockTotalSummary.clear();
        if(aReportStock!=null && RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try{
                        ps.setInt(1,aReportStock.getStoreId());
                    }catch(NullPointerException npe){
                        ps.setInt(1,0);
                    }
                    try{
                        ps.setInt(2,aReportStock.getCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(2,0);
                    }
                    try{
                        ps.setInt(3,aReportStock.getSubCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(3,0);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ActiveReportStockTotalSummary.add(this.getReportStockSummaryFromResultset(rs));
                    }
                    //refresh totals
                    this.calculateTotalStockValues();
                    
                    this.setActionMessage("");
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
        }else{
            this.setTotalCostValue(0);
            this.setTotalWholesaleValue(0);
            this.setTotalRetailsaleValue(0);
        }
        return this.ActiveReportStockTotalSummary;
    }
    
    public long getActiveReportStockTotalSummaryCount(){
        return this.ActiveReportStockTotalSummary.size();
    }
    
    public List<ReportStockSummary> getActiveReportStockAllSummary(ReportStock aReportStock,boolean RETRIEVE_REPORT){
        String sql;   
        sql = "{call sp_report_stock_all_summary(?,?,?)}";
        ResultSet rs = null;
        this.ActiveReportStockAllSummary.clear();
        if(aReportStock!=null && RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try{
                        ps.setInt(1,aReportStock.getCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(1,0);
                    }
                    try{
                        ps.setInt(2,aReportStock.getSubCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(2,0);
                    }
                    try{
                        ps.setString(3,aReportStock.getReorderFilter());
                    }catch(NullPointerException npe){
                        ps.setString(3,"");
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ActiveReportStockAllSummary.add(this.getReportStockAllSummaryFromResultset(rs));
                    }
                    this.setActionMessage("");
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
        return this.ActiveReportStockAllSummary;
    }
    
    public long getActiveReportStockAllSummaryCount(){
        return this.ActiveReportStockAllSummary.size();
    }
    
    public ReportStock getReportStockFromResultset(ResultSet aResultSet) {
        try{
                ReportStock reportStock = new ReportStock();
                try{
                    reportStock.setItemId(aResultSet.getLong("item_id"));
                }catch(NullPointerException npe){
                    reportStock.setItemId(0);
                }
                try{
                    reportStock.setItemCode(aResultSet.getString("item_code"));
                }catch(NullPointerException npe){
                    reportStock.setItemCode("");
                }
                try{
                    reportStock.setDescription(aResultSet.getString("description"));
                }catch(NullPointerException npe){
                    reportStock.setDescription("");
                }
                try{
                    reportStock.setCategoryId(aResultSet.getInt("category_id"));
                }catch(NullPointerException npe){
                    reportStock.setCategoryId(0);
                }
                try{
                    reportStock.setSubCategoryId(aResultSet.getInt("sub_category_id"));
                }catch(NullPointerException npe){
                    reportStock.setSubCategoryId(0);
                }
                try{
                    reportStock.setUnitId(aResultSet.getInt("unit_id"));
                }catch(NullPointerException npe){
                    reportStock.setUnitId(0);
                }
                try{
                    reportStock.setReorderLevel(aResultSet.getInt("reorder_level"));
                }catch(NullPointerException npe){
                    reportStock.setReorderLevel(0);
                }
                try{
                    reportStock.setUnitCostPrice(aResultSet.getInt("unit_cost_price"));
                }catch(NullPointerException npe){
                    reportStock.setUnitCostPrice(0);
                }
                try{
                    reportStock.setUnitRetailsalePrice(aResultSet.getFloat("unit_retailsale_price"));
                }catch(NullPointerException npe){
                    reportStock.setUnitRetailsalePrice(0);
                }
                try{
                    reportStock.setUnitWholesalePrice(aResultSet.getFloat("unit_wholesale_price"));
                }catch(NullPointerException npe){
                    reportStock.setUnitWholesalePrice(0);
                }
                try{
                    reportStock.setIsSuspended(aResultSet.getString("is_suspended"));
                }catch(NullPointerException npe){
                    reportStock.setIsSuspended("");
                }
                try{
                    reportStock.setVatRated(aResultSet.getString("vat_rated"));
                }catch(NullPointerException npe){
                    reportStock.setVatRated("");
                }
                try{
                    reportStock.setBatchno(aResultSet.getString("batchno"));
                }catch(NullPointerException npe){
                    reportStock.setBatchno("");
                }
                try{
                    reportStock.setStoreName(aResultSet.getString("store_name"));
                }catch(NullPointerException npe){
                    reportStock.setStoreName("");
                }
                try{
                    reportStock.setCategoryName(aResultSet.getString("category_name"));
                }catch(NullPointerException npe){
                    reportStock.setCategoryName("");
                }
                try{
                    reportStock.setUnitName(aResultSet.getString("unit_name"));
                }catch(NullPointerException npe){
                    reportStock.setUnitName("");
                }
                try{
                    reportStock.setSubCategoryName(aResultSet.getString("sub_category_name"));
                }catch(NullPointerException npe){
                    reportStock.setSubCategoryName("");
                }
                try{
                reportStock.setCurrentQty(aResultSet.getFloat("currentqty"));
                 }catch(NullPointerException npe){
                    reportStock.setItemMnfDate(null);
                }
                try{
                    reportStock.setItemMnfDate(new Date(aResultSet.getDate("item_mnf_date").getTime()));
                }catch(NullPointerException npe){
                    reportStock.setItemMnfDate(null);
                }
                try{
                    reportStock.setItemExpDate(new Date(aResultSet.getDate("item_exp_date").getTime()));
                }catch(NullPointerException npe){
                    reportStock.setItemExpDate(null);
                }
                try{
                    reportStock.setStoreId(aResultSet.getInt("store_id"));
                }catch(NullPointerException npe){
                    reportStock.setStoreId(0);
                }
               
                return reportStock;
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }

    }
    public ReportStock getReportStockTotalFromResultset(ResultSet aResultSet) {
        try{
                ReportStock reportStock = new ReportStock();
                try{
                    reportStock.setItemId(aResultSet.getLong("item_id"));
                }catch(NullPointerException npe){
                    reportStock.setItemId(0);
                }
                try{
                    reportStock.setDescription(aResultSet.getString("description"));
                }catch(NullPointerException npe){
                    reportStock.setDescription("");
                }
                try{
                    reportStock.setCategoryId(aResultSet.getInt("category_id"));
                }catch(NullPointerException npe){
                    reportStock.setCategoryId(0);
                }
                try{
                    reportStock.setSubCategoryId(aResultSet.getInt("sub_category_id"));
                }catch(NullPointerException npe){
                    reportStock.setSubCategoryId(0);
                }
                try{
                    reportStock.setUnitId(aResultSet.getInt("unit_id"));
                }catch(NullPointerException npe){
                    reportStock.setUnitId(0);
                }
                try{
                    reportStock.setReorderLevel(aResultSet.getInt("reorder_level"));
                }catch(NullPointerException npe){
                    reportStock.setReorderLevel(0);
                }
                try{
                    reportStock.setStoreName(aResultSet.getString("store_name"));
                }catch(NullPointerException npe){
                    reportStock.setStoreName("");
                }
                try{
                    reportStock.setCategoryName(aResultSet.getString("category_name"));
                }catch(NullPointerException npe){
                    reportStock.setCategoryName("");
                }
                try{
                    reportStock.setUnitName(aResultSet.getString("unit_name"));
                }catch(NullPointerException npe){
                    reportStock.setUnitName("");
                }
                try{
                    reportStock.setSubCategoryName(aResultSet.getString("sub_category_name"));
                }catch(NullPointerException npe){
                    reportStock.setSubCategoryName("");
                }
                try{
                    reportStock.setCurrentQty(aResultSet.getFloat("currentqty"));
                 }catch(NullPointerException npe){
                    reportStock.setCurrentQty(0);
                }
                try{
                    reportStock.setStoreId(aResultSet.getInt("store_id"));
                }catch(NullPointerException npe){
                    reportStock.setStoreId(0);
                }
                
                try{
                    reportStock.setCostValue(aResultSet.getFloat("cost_value"));
                 }catch(NullPointerException npe){
                    reportStock.setCostValue(0);
                }
                try{
                    reportStock.setRetailsaleValue(aResultSet.getFloat("retailsale_value"));
                 }catch(NullPointerException npe){
                    reportStock.setRetailsaleValue(0);
                }
                try{
                    reportStock.setWholesaleValue(aResultSet.getFloat("wholesale_value"));
                 }catch(NullPointerException npe){
                    reportStock.setWholesaleValue(0);
                }
                
               
                return reportStock;
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }
    }
    
    public ReportStock getReportStockAllFromResultset(ResultSet aResultSet) {
        try{
                ReportStock reportStock = new ReportStock();
                try{
                    reportStock.setItemId(aResultSet.getLong("item_id"));
                }catch(NullPointerException npe){
                    reportStock.setItemId(0);
                }
                try{
                    reportStock.setDescription(aResultSet.getString("description"));
                }catch(NullPointerException npe){
                    reportStock.setDescription("");
                }
                try{
                    reportStock.setCategoryId(aResultSet.getInt("category_id"));
                }catch(NullPointerException npe){
                    reportStock.setCategoryId(0);
                }
                try{
                    reportStock.setSubCategoryId(aResultSet.getInt("sub_category_id"));
                }catch(NullPointerException npe){
                    reportStock.setSubCategoryId(0);
                }
                try{
                    reportStock.setUnitId(aResultSet.getInt("unit_id"));
                }catch(NullPointerException npe){
                    reportStock.setUnitId(0);
                }
                try{
                    reportStock.setReorderLevel(aResultSet.getInt("reorder_level"));
                }catch(NullPointerException npe){
                    reportStock.setReorderLevel(0);
                }
                try{
                    reportStock.setCategoryName(aResultSet.getString("category_name"));
                }catch(NullPointerException npe){
                    reportStock.setCategoryName("");
                }
                try{
                    reportStock.setUnitName(aResultSet.getString("unit_name"));
                }catch(NullPointerException npe){
                    reportStock.setUnitName("");
                }
                try{
                    reportStock.setSubCategoryName(aResultSet.getString("sub_category_name"));
                }catch(NullPointerException npe){
                    reportStock.setSubCategoryName("");
                }
                try{
                reportStock.setCurrentQty(aResultSet.getFloat("currentqty"));
                 }catch(NullPointerException npe){
                    reportStock.setItemMnfDate(null);
                }
               
                return reportStock;
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }

    }
    
    public ReportStockSummary getReportStockSummaryFromResultset(ResultSet aResultSet) {
        try{
            if(aResultSet!=null){
                ReportStockSummary reportStockSummary = new ReportStockSummary();
                try{
                    reportStockSummary.setStoreName(aResultSet.getString("store_name"));
                }catch(NullPointerException npe){
                    reportStockSummary.setStoreName("");
                }
                try{
                    reportStockSummary.setCategoryName(aResultSet.getString("category_name"));
                }catch(NullPointerException npe){
                     reportStockSummary.setCategoryName("");
                }
                try{
                    reportStockSummary.setSubCategoryName(aResultSet.getString("sub_category_name"));
                }catch(NullPointerException npe){
                    reportStockSummary.setSubCategoryName("");
                }
                try{
                    reportStockSummary.setSumCurrentQty(aResultSet.getFloat("sum_currentqty"));
                }catch(NullPointerException npe){
                    reportStockSummary.setSumCurrentQty(0);
                }
                try{
                    reportStockSummary.setSumCostValue(aResultSet.getFloat("sum_cost_value"));
                }catch(NullPointerException npe){
                    reportStockSummary.setSumCostValue(0);
                }
                try{
                    reportStockSummary.setSumWholesaleValue(aResultSet.getFloat("sum_wholesale_value"));
                }catch(NullPointerException npe){
                     reportStockSummary.setSumWholesaleValue(0);
                }
                try{
                    reportStockSummary.setSumRetailsaleValue(aResultSet.getFloat("sum_retailsale_value"));
                }catch(NullPointerException npe){
                    reportStockSummary.setSumRetailsaleValue(0);
                }
                
                return reportStockSummary;
            }else{
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }
    }
    public ReportStockSummary getReportStockAllSummaryFromResultset(ResultSet aResultSet) {
        try{
            if(aResultSet!=null){
                ReportStockSummary reportStockSummary = new ReportStockSummary();
                reportStockSummary.setCategoryName(aResultSet.getString("category_name"));
                reportStockSummary.setSubCategoryName(aResultSet.getString("sub_category_name"));
                reportStockSummary.setSumCurrentQty(aResultSet.getFloat("sum_currentqty"));
                return reportStockSummary;
            }else{
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }
    }
    public void calculateTotalStockValues() {
        List<ReportStockSummary> rss=new ArrayList<ReportStockSummary>();
        rss=this.ActiveReportStockTotalSummary;
        int ListItemIndex=0;
        int ListItemNo=rss.size();
        this.setTotalCostValue(0);
        this.setTotalWholesaleValue(0);
        this.setTotalRetailsaleValue(0);
        while(ListItemIndex<ListItemNo){
            this.setTotalCostValue(this.getTotalCostValue() + rss.get(ListItemIndex).getSumCostValue());
            this.setTotalWholesaleValue(this.getTotalWholesaleValue() + rss.get(ListItemIndex).getSumWholesaleValue());
            this.setTotalRetailsaleValue(this.getTotalRetailsaleValue() + rss.get(ListItemIndex).getSumRetailsaleValue());
            ListItemIndex=ListItemIndex+1;
        }
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
     * @return the TotalCostValue
     */
    public double getTotalCostValue() {
        return TotalCostValue;
    }

    /**
     * @param TotalCostValue the TotalCostValue to set
     */
    public void setTotalCostValue(double TotalCostValue) {
        this.TotalCostValue = TotalCostValue;
    }

    /**
     * @return the TotalWholesaleValue
     */
    public double getTotalWholesaleValue() {
        return TotalWholesaleValue;
    }

    /**
     * @param TotalWholesaleValue the TotalWholesaleValue to set
     */
    public void setTotalWholesaleValue(double TotalWholesaleValue) {
        this.TotalWholesaleValue = TotalWholesaleValue;
    }

    /**
     * @return the TotalRetailsaleValue
     */
    public double getTotalRetailsaleValue() {
        return TotalRetailsaleValue;
    }

    /**
     * @param TotalRetailsaleValue the TotalRetailsaleValue to set
     */
    public void setTotalRetailsaleValue(double TotalRetailsaleValue) {
        this.TotalRetailsaleValue = TotalRetailsaleValue;
    }
    
}
