
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
public class TransItemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TransItem> TransItems;
    private String ActionMessage = null;
    private TransItem SelectedTransItem = null;
    private int SelectedTransactionItemId;
    private String SearchTransItem = "";
    private List<TransItem> ActiveTransItems = new ArrayList<TransItem>();
    List<TransItem> ReportTransItem = new ArrayList<TransItem>();
    private String ItemString = "";

    public void saveTransItems(Trans aTrans, List<TransItem> aActiveTransItems, long TransactionId) {
        List<TransItem> ati = aActiveTransItems;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        while (ListItemIndex < ListItemNo) {
            ati.get(ListItemIndex).setTransactionId(TransactionId);
            this.saveTransItem(aTrans, ati.get(ListItemIndex));
            ListItemIndex = ListItemIndex + 1;
        }
    }

    public void saveTransItem(Trans aTrans, TransItem transitem) {
        String sql = null;
        String sql2 = null;
        String msg = "";
        TransactionTypeBean TransTypeBean = new TransactionTypeBean();
        TransactionType TransType = new TransactionType();
        StockBean StkBean = new StockBean();

        if (1 == 2) {
        } else {

            if (transitem.getTransactionItemId() == 0) {
                sql = "{call sp_insert_transaction_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            } else if (transitem.getTransactionItemId() > 0) {
                sql = "{call sp_update_transaction_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (transitem.getTransactionItemId() == 0) {
                    //clean batch
                    if (transitem.getBatchno() == null) {
                        transitem.setBatchno("");
                    }

                    cs.setString("in_is_trade_discount_vat_liable", CompanySetting.getIsTradeDiscountVatLiable());
                    cs.setLong("in_transaction_id", transitem.getTransactionId());
                    cs.setLong("in_item_id", transitem.getItemId());
                    cs.setString("in_batchno", transitem.getBatchno());
                    try {
                        cs.setDate("in_item_expiry_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                        cs.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_item_expiry_date", null);
                        cs.setDate("in_item_mnf_date", null);
                    }
                    cs.setFloat("in_item_qty", transitem.getItemQty());
                    cs.setFloat("in_unit_price", transitem.getUnitPrice());
                    cs.setFloat("in_unit_trade_discount", transitem.getUnitTradeDiscount());
                    cs.setFloat("in_unit_vat", transitem.getUnitVat());
                    cs.setFloat("in_amount", transitem.getAmount());
                    cs.setString("in_vat_rated", transitem.getVatRated());
                    cs.setFloat("in_vat_perc", transitem.getVatPerc());
                    cs.setFloat("in_unit_price_inc_vat", transitem.getUnitPriceIncVat());
                    cs.setFloat("in_unit_price_exc_vat", transitem.getUnitPriceExcVat());
                    cs.setFloat("in_amount_inc_vat", transitem.getAmountIncVat());
                    cs.setFloat("in_amount_exc_vat", transitem.getAmountExcVat());
                    cs.setInt("in_currency_type_id", aTrans.getCurrencyTypeId());
                    cs.setString("in_remarks", transitem.getRemarks());

                    if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        cs.setString("in_stock_effect", "D");
                    } else if ("TRANSFER".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        cs.setString("in_stock_effect", "B");
                    } else if ("DISPOSE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        cs.setString("in_stock_effect", "D");
                    } else if ("UNPACK".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        cs.setString("in_stock_effect", "D");
                    } else if ("GOODS RECEIVED".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        cs.setString("in_stock_effect", "C");
                    } else {
                        cs.setString("in_stock_effect", "");
                    }
                    //for profit margin
                    cs.setFloat("in_unit_cost_price", transitem.getUnitCostPrice());
                    cs.setFloat("in_unit_profit_margin", transitem.getUnitProfitMargin());
                    //for user earning
                    if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && ("RETAIL SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType()) || "WHOLE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType()))) {
                        UserItemEarn bUserItemEarn = new UserItemEarn();
                        Item bItem = new Item();
                        UserDetail bUserDetail = new UserDetail();
                        int bTransTypeId, bTransReasId, bItemCatId, bItemSubCatId, bUserCatId;
                        bTransTypeId = new GeneralUserSetting().getCurrentTransactionTypeId();
                        bTransReasId = new GeneralUserSetting().getCurrentTransactionReasonId();
                        bItem = new ItemBean().getItem(transitem.getItemId());
                        bItemCatId = bItem.getCategoryId();
                        bUserDetail = new UserDetailBean().getUserDetail(aTrans.getTransactionUserDetailId());
                        bUserCatId = bUserDetail.getUserCategoryId();
                        try {
                            bItemSubCatId = bItem.getSubCategoryId();
                        } catch (NullPointerException npe) {
                            bItemSubCatId = 0;
                        }
                        try {
                            //System.out.println(bTransTypeId + "," + bTransReasId + "," + bItemCatId + "," + bItemSubCatId + "," + bUserCatId);
                            bUserItemEarn = new UserItemEarnBean().getUserItemEarnByTtypeTreasIcatIsubcatUcat(bTransTypeId, bTransReasId, bItemCatId, bItemSubCatId, bUserCatId);
                        } catch (NullPointerException npe) {
                            bUserItemEarn = null;
                        }
                        if (null != bUserItemEarn) {
                            cs.setFloat("in_earn_perc", bUserItemEarn.getEarnPerc());
                            cs.setFloat("in_earn_amount", (float) (bUserItemEarn.getEarnPerc() * transitem.getAmountIncVat() * 0.01));
                        } else {
                            cs.setFloat("in_earn_perc", 0);
                            cs.setFloat("in_earn_amount", 0);
                        }
                    } else {
                        cs.setFloat("in_earn_perc", 0);
                        cs.setFloat("in_earn_amount", 0);
                    }
                    //save
                    cs.executeUpdate();
                    //repeat for the unpacked ones
                    if ("UNPACK".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        cs.setLong("in_item_id", transitem.getItemId2());
                        cs.setFloat("in_item_qty", transitem.getItemQty2());
                        cs.setString("in_stock_effect", "C");
                        cs.executeUpdate();
                    }

                    //update stock
                    TransType = TransTypeBean.getTransactionType(new GeneralUserSetting().getCurrentTransactionTypeId());
                    if (new ItemBean().getItem(transitem.getItemId()).getItemType().equals("PRODUCT")) {
                        if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "DISPOSE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                            sql2 = "{call sp_subtract_stock_by_store_item_batch(?,?,?,?)}";
                            try (
                                    Connection conn2 = DBConnection.getMySQLConnection();
                                    CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                                cs2.setLong("in_item_id", transitem.getItemId());
                                cs2.setString("in_batchno", transitem.getBatchno());
                                cs2.setFloat("in_qty", transitem.getItemQty());
                                cs2.executeUpdate();
                            }
                        }
                        if ("GOODS RECEIVED".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                            if (StkBean.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), transitem.getItemId(), transitem.getBatchno()) != null) {
                                //update/add
                                sql2 = "{call sp_add_stock_by_store_item_batch(?,?,?,?)}";
                                try (
                                        Connection conn2 = DBConnection.getMySQLConnection();
                                        CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                    cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                                    cs2.setLong("in_item_id", transitem.getItemId());
                                    cs2.setString("in_batchno", transitem.getBatchno());
                                    cs2.setFloat("in_qty", transitem.getItemQty());
                                    cs2.executeUpdate();
                                }
                            } else {
                                //insert
                                sql2 = "{call sp_insert_stock(?,?,?,?,?,?)}";
                                try (
                                        Connection conn2 = DBConnection.getMySQLConnection();
                                        CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                    cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                                    cs2.setLong("in_item_id", transitem.getItemId());
                                    cs2.setString("in_batchno", transitem.getBatchno());
                                    cs2.setFloat("in_currentqty", transitem.getItemQty());
                                    try {
                                        cs2.setDate("in_item_exp_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                                        cs2.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                                    } catch (NullPointerException npe) {
                                        cs2.setDate("in_item_exp_date", null);
                                        cs2.setDate("in_item_mnf_date", null);
                                    }
                                    cs2.executeUpdate();
                                }
                            }
                        }

                        //TRANSFER - 1. Subtract stock from the source store
                        if ("TRANSFER".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                            sql2 = "{call sp_subtract_stock_by_store_item_batch(?,?,?,?)}";
                            try (
                                    Connection conn2 = DBConnection.getMySQLConnection();
                                    CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                                cs2.setLong("in_item_id", transitem.getItemId());
                                cs2.setString("in_batchno", transitem.getBatchno());
                                cs2.setFloat("in_qty", transitem.getItemQty());
                                cs2.executeUpdate();
                            }
                        }
                        //TRANSFER - 2. Add/Insert stock to the destination store
                        if ("TRANSFER".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                            if (StkBean.getStock(new GeneralUserSetting().getCurrentStore2Id(), transitem.getItemId(), transitem.getBatchno()) != null) {
                                //update/add
                                sql2 = "{call sp_add_stock_by_store_item_batch(?,?,?,?)}";
                                try (
                                        Connection conn2 = DBConnection.getMySQLConnection();
                                        CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                    cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore2Id());
                                    cs2.setLong("in_item_id", transitem.getItemId());
                                    cs2.setString("in_batchno", transitem.getBatchno());
                                    cs2.setFloat("in_qty", transitem.getItemQty());
                                    cs2.executeUpdate();
                                }
                            } else {
                                //insert
                                sql2 = "{call sp_insert_stock(?,?,?,?,?,?)}";
                                try (
                                        Connection conn2 = DBConnection.getMySQLConnection();
                                        CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                    cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore2Id());
                                    cs2.setLong("in_item_id", transitem.getItemId());
                                    cs2.setString("in_batchno", transitem.getBatchno());
                                    cs2.setFloat("in_currentqty", transitem.getItemQty());
                                    try {
                                        cs2.setDate("in_item_exp_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                                        cs2.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                                    } catch (NullPointerException npe) {
                                        cs2.setDate("in_item_exp_date", null);
                                        cs2.setDate("in_item_mnf_date", null);
                                    }
                                    cs2.executeUpdate();
                                }
                            }
                        }
                        //UNPACK - 1. Subtract stock from the source BigItem
                        if ("UNPACK".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                            sql2 = "{call sp_subtract_stock_by_store_item_batch(?,?,?,?)}";
                            try (
                                    Connection conn2 = DBConnection.getMySQLConnection();
                                    CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                                cs2.setLong("in_item_id", transitem.getItemId());
                                cs2.setString("in_batchno", transitem.getBatchno());
                                cs2.setFloat("in_qty", transitem.getItemQty());
                                cs2.executeUpdate();
                            }
                        }
                        //UNPACK - 2. Add/Insert stock to the destination small item
                        if ("UNPACK".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                            if (StkBean.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), transitem.getItemId2(), transitem.getBatchno()) != null) {
                                //update/add
                                sql2 = "{call sp_add_stock_by_store_item_batch(?,?,?,?)}";
                                try (
                                        Connection conn2 = DBConnection.getMySQLConnection();
                                        CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                    cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                                    cs2.setLong("in_item_id", transitem.getItemId2());
                                    cs2.setString("in_batchno", transitem.getBatchno());
                                    cs2.setFloat("in_qty", transitem.getItemQty2());
                                    cs2.executeUpdate();
                                }
                            } else {
                                //insert
                                sql2 = "{call sp_insert_stock(?,?,?,?,?,?)}";
                                try (
                                        Connection conn2 = DBConnection.getMySQLConnection();
                                        CallableStatement cs2 = conn2.prepareCall(sql2);) {
                                    cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                                    cs2.setLong("in_item_id", transitem.getItemId2());
                                    cs2.setString("in_batchno", transitem.getBatchno());
                                    cs2.setFloat("in_currentqty", transitem.getItemQty2());
                                    try {
                                        cs2.setDate("in_item_exp_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                                        cs2.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                                    } catch (NullPointerException npe) {
                                        cs2.setDate("in_item_exp_date", null);
                                        cs2.setDate("in_item_mnf_date", null);
                                    }
                                    cs2.executeUpdate();
                                }
                            }
                        }
                    }

                    TransType = null;
                    TransTypeBean = null;
                    StkBean = null;

                } else if (transitem.getTransactionItemId() > 0) {
                    //do nothing; this is for edit
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("TransItem NOT saved");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("TransItem NOT saved!"));
            }
        }
    }

    public void updateTransItem(TransItem transitem) {
        int success = 0;
        String sql = "{call sp_update_transaction_item2(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.setLong("in_transaction_item_id", transitem.getTransactionItemId());
            cs.setFloat("in_item_qty", transitem.getItemQty());
            cs.setFloat("in_unit_price", transitem.getUnitPrice());
            cs.setFloat("in_unit_trade_discount", transitem.getUnitTradeDiscount());
            cs.setFloat("in_unit_vat", transitem.getUnitVat());
            cs.setFloat("in_amount", transitem.getAmount());
            cs.setFloat("in_unit_price_inc_vat", transitem.getUnitPriceIncVat());
            cs.setFloat("in_unit_price_exc_vat", transitem.getUnitPriceExcVat());
            cs.setFloat("in_amount_inc_vat", transitem.getAmountIncVat());
            cs.setFloat("in_amount_exc_vat", transitem.getAmountExcVat());
            //for profit margin
            cs.setFloat("in_unit_cost_price", transitem.getUnitCostPrice());
            cs.setFloat("in_unit_profit_margin", transitem.getUnitProfitMargin());
            //for user earning
            //get the previously used earn rate/perc and update for the new qty
            cs.setFloat("in_earn_perc", transitem.getEarnPerc());
            cs.setFloat("in_earn_amount", (float) (transitem.getEarnPerc() * transitem.getAmountIncVat() * 0.01));
            //save
            cs.executeUpdate();
            success = 1;
        } catch (SQLException se) {
            success = 0;
            System.err.println("UpdateTransaction:" + se.getMessage());

        }
    }

    public void saveTransItemAutoUnpack(TransItem transitem) {
        String sql = null;
        String sql2 = null;
        StockBean StkBean = new StockBean();
        sql = "{call sp_insert_transaction_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {

            //clean batch
            if (transitem.getBatchno() == null) {
                transitem.setBatchno("");
            }

            cs.setString("in_is_trade_discount_vat_liable", "");
            cs.setLong("in_transaction_id", transitem.getTransactionId());
            cs.setLong("in_item_id", transitem.getItemId());
            cs.setString("in_batchno", transitem.getBatchno());
            try {
                cs.setDate("in_item_expiry_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                cs.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
            } catch (NullPointerException npe) {
                cs.setDate("in_item_expiry_date", null);
                cs.setDate("in_item_mnf_date", null);
            }
            cs.setFloat("in_item_qty", transitem.getItemQty());
            cs.setFloat("in_unit_price", 0);
            cs.setFloat("in_unit_trade_discount", 0);
            cs.setFloat("in_unit_vat", 0);
            cs.setFloat("in_amount", 0);
            try {
                cs.setDate("in_item_expiry_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                cs.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
            } catch (NullPointerException npe) {
                cs.setDate("in_item_expiry_date", null);
                cs.setDate("in_item_mnf_date", null);
            }
            cs.setString("in_vat_rated", "");
            cs.setFloat("in_vat_perc", 0);
            cs.setFloat("in_unit_price_inc_vat", 0);
            cs.setFloat("in_unit_price_exc_vat", 0);
            cs.setFloat("in_amount_inc_vat", 0);
            cs.setFloat("in_amount_exc_vat", 0);
            cs.setString("in_stock_effect", "D");
            //for profit margin
            cs.setFloat("in_unit_cost_price", 0);
            cs.setFloat("in_unit_profit_margin", 0);
            //for user earning
            cs.setFloat("in_earn_perc", 0);
            cs.setFloat("in_earn_amount", 0);
            //save
            cs.executeUpdate();

            //repeat for the unpacked ones
            cs.setLong("in_item_id", transitem.getItemId2());
            cs.setFloat("in_item_qty", transitem.getItemQty2());
            cs.setString("in_stock_effect", "C");
            cs.executeUpdate();

            //update stock
            //UNPACK - 1. Subtract stock from the source BigItem
            sql2 = "{call sp_subtract_stock_by_store_item_batch(?,?,?,?)}";
            try (
                    Connection conn2 = DBConnection.getMySQLConnection();
                    CallableStatement cs2 = conn2.prepareCall(sql2);) {
                cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                cs2.setLong("in_item_id", transitem.getItemId());
                cs2.setString("in_batchno", transitem.getBatchno());
                cs2.setFloat("in_qty", transitem.getItemQty());
                cs2.executeUpdate();
            }
            //UNPACK - 2. Add/Insert stock to the destination small item
            if (StkBean.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), transitem.getItemId2(), transitem.getBatchno()) != null) {
                //update/add
                sql2 = "{call sp_add_stock_by_store_item_batch(?,?,?,?)}";
                try (
                        Connection conn2 = DBConnection.getMySQLConnection();
                        CallableStatement cs2 = conn2.prepareCall(sql2);) {
                    cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                    cs2.setLong("in_item_id", transitem.getItemId2());
                    cs2.setString("in_batchno", transitem.getBatchno());
                    cs2.setFloat("in_qty", transitem.getItemQty2());
                    cs2.executeUpdate();
                }
            } else {
                //insert
                sql2 = "{call sp_insert_stock(?,?,?,?,?,?)}";
                try (
                        Connection conn2 = DBConnection.getMySQLConnection();
                        CallableStatement cs2 = conn2.prepareCall(sql2);) {
                    cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                    cs2.setLong("in_item_id", transitem.getItemId2());
                    cs2.setString("in_batchno", transitem.getBatchno());
                    cs2.setFloat("in_currentqty", transitem.getItemQty2());
                    try {
                        cs2.setDate("in_item_exp_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                        cs2.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs2.setDate("in_item_exp_date", null);
                        cs2.setDate("in_item_mnf_date", null);
                    }
                    cs2.executeUpdate();
                }
            }
            StkBean = null;
        } catch (SQLException se) {
            System.err.println("saveTransItemAutoUnpack:" + se.getMessage());
        }
    }

    public boolean updateTransItems(long aTransactionId, long aTransactionHistId, List<TransItem> aNewTransItems) {
        try {
            //get trans items that was moved to the history table
            List<TransItem> aHistTransItems = new ArrayList<TransItem>();
            this.setTransItemsHistoryByIDs(aTransactionId, aTransactionHistId, aHistTransItems);

            //1. Reverse and update all trans items whoose qty has changed
            int NewListItemIndex = 0;
            int HistListItemIndex = 0;
            int NewListItemNo = aNewTransItems.size();
            int HistListItemNo = aHistTransItems.size();
            float aDiffHistNewQty = 0;
            while (NewListItemIndex < NewListItemNo) {
                HistListItemIndex = 0;
                while (HistListItemIndex < HistListItemNo) {
                    if (aNewTransItems.get(NewListItemIndex).getItemId() == aHistTransItems.get(HistListItemIndex).getItemId() && aNewTransItems.get(NewListItemIndex).getBatchno().equals(aHistTransItems.get(HistListItemIndex).getBatchno())) {
                        aDiffHistNewQty = aHistTransItems.get(HistListItemIndex).getItemQty() - aNewTransItems.get(NewListItemIndex).getItemQty();
                        break;
                    }
                    HistListItemIndex = HistListItemIndex + 1;
                }
                //2. Reverse and update individual trans item whoose qty has changed
                if (aDiffHistNewQty > 0 || aDiffHistNewQty < 0) {
                    this.reverseTransItem(aNewTransItems.get(NewListItemIndex), aDiffHistNewQty);
                    this.updateTransItem(aNewTransItems.get(NewListItemIndex));
                }
                NewListItemIndex = NewListItemIndex + 1;
            }
            return true;
        } catch (Exception e) {
            System.err.println("UpdateTransItems:" + e.getMessage());
            return false;
        }
    }

    public void reverseTransItem(TransItem transitem, float aDiffHistNewQty) {
        String sql = null;
        String sql2 = null;
        StockBean StkBean = new StockBean();
        Stock Stk = new Stock();

        if (transitem.getTransactionItemId() == 0 || new ItemBean().getItem(transitem.getItemId()).getItemType().equals("SERVICE")) {
            //do nothing
        } else {
            //1. reverse stock
            Stk = StkBean.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), transitem.getItemId(), transitem.getBatchno());
            //for additive transactions, if diff is +ve, subtract; if diff is -ve Add
            //originally "PURCHASE INVOICE" but changed to "GOODS RECEIVED"
            if ("GOODS RECEIVED".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                if (aDiffHistNewQty > 0) {
                    //subtract stock
                    if (Stk != null) {
                        //update/subtract
                        sql2 = "{call sp_subtract_stock_by_store_item_batch(?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setLong("in_item_id", transitem.getItemId());
                            cs2.setString("in_batchno", transitem.getBatchno());
                            cs2.setFloat("in_qty", aDiffHistNewQty);
                            cs2.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(TransItemBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        //insert
                        sql2 = "{call sp_insert_stock(?,?,?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setLong("in_item_id", transitem.getItemId());
                            cs2.setString("in_batchno", transitem.getBatchno());
                            cs2.setFloat("in_currentqty", (-1 * aDiffHistNewQty));
                            try {
                                cs2.setDate("in_item_exp_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                                cs2.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                            } catch (NullPointerException npe) {
                                cs2.setDate("in_item_exp_date", null);
                                cs2.setDate("in_item_mnf_date", null);
                            }
                            cs2.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(TransItemBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else if (aDiffHistNewQty < 0) {
                    //add stock
                    aDiffHistNewQty = (-1) * aDiffHistNewQty;//remove the -ve from the quantity
                    if (Stk != null) {
                        //update/add
                        sql2 = "{call sp_add_stock_by_store_item_batch(?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setLong("in_item_id", transitem.getItemId());
                            cs2.setString("in_batchno", transitem.getBatchno());
                            cs2.setFloat("in_qty", aDiffHistNewQty);
                            cs2.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(TransItemBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        //insert
                        sql2 = "{call sp_insert_stock(?,?,?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setLong("in_item_id", transitem.getItemId());
                            cs2.setString("in_batchno", transitem.getBatchno());
                            cs2.setFloat("in_currentqty", aDiffHistNewQty);
                            try {
                                cs2.setDate("in_item_exp_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                                cs2.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                            } catch (NullPointerException npe) {
                                cs2.setDate("in_item_exp_date", null);
                                cs2.setDate("in_item_mnf_date", null);
                            }
                            cs2.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(TransItemBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            }

            if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "DISPOSE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                if (aDiffHistNewQty > 0) {
                    //add stock
                    if (Stk != null) {
                        //update/add
                        sql2 = "{call sp_add_stock_by_store_item_batch(?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setLong("in_item_id", transitem.getItemId());
                            cs2.setString("in_batchno", transitem.getBatchno());
                            cs2.setFloat("in_qty", aDiffHistNewQty);
                            cs2.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(TransItemBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        //insert
                        sql2 = "{call sp_insert_stock(?,?,?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setLong("in_item_id", transitem.getItemId());
                            cs2.setString("in_batchno", transitem.getBatchno());
                            cs2.setFloat("in_currentqty", aDiffHistNewQty);
                            try {
                                cs2.setDate("in_item_exp_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                                cs2.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                            } catch (NullPointerException npe) {
                                cs2.setDate("in_item_exp_date", null);
                                cs2.setDate("in_item_mnf_date", null);
                            }
                            cs2.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(TransItemBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (aDiffHistNewQty < 0) {
                    //subtract stock
                    aDiffHistNewQty = (-1) * aDiffHistNewQty;//remove the -ve from the quantity

                    if (Stk != null) {
                        //update/subtract
                        sql2 = "{call sp_subtract_stock_by_store_item_batch(?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setLong("in_item_id", transitem.getItemId());
                            cs2.setString("in_batchno", transitem.getBatchno());
                            cs2.setFloat("in_qty", aDiffHistNewQty);
                            cs2.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(TransItemBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        //insert
                        sql2 = "{call sp_insert_stock(?,?,?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setLong("in_item_id", transitem.getItemId());
                            cs2.setString("in_batchno", transitem.getBatchno());
                            cs2.setFloat("in_currentqty", (-1 * aDiffHistNewQty));
                            try {
                                cs2.setDate("in_item_exp_date", new java.sql.Date(transitem.getItemExpryDate().getTime()));
                                cs2.setDate("in_item_mnf_date", new java.sql.Date(transitem.getItemMnfDate().getTime()));
                            } catch (NullPointerException npe) {
                                cs2.setDate("in_item_exp_date", null);
                                cs2.setDate("in_item_mnf_date", null);
                            }
                            cs2.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(TransItemBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            }
            StkBean = null;
            Stk = null;
        }
    }

    public TransItem getTransItem(long aTransactionItemId) {
        String sql = "{call sp_search_transaction_item_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionItemId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransItemFromResultSet(rs);
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

    public TransItem getTransItemFromResultSet(ResultSet aResultSet) {
        try {
            TransItem transitem = new TransItem();
            try {
                transitem.setTransactionItemId(aResultSet.getLong("transaction_item_id"));
            } catch (NullPointerException npe) {
                transitem.setTransactionItemId(0);
            }
            try {
                transitem.setTransactionId(aResultSet.getLong("transaction_id"));
            } catch (NullPointerException npe) {
                transitem.setTransactionId(0);
            }
            try {
                transitem.setItemId(aResultSet.getLong("item_id"));
            } catch (NullPointerException npe) {
                transitem.setItemId(0);
            }
            try {
                transitem.setBatchno(aResultSet.getString("batchno"));
            } catch (NullPointerException npe) {
                transitem.setBatchno("");
            }
            try {
                transitem.setItemQty(aResultSet.getFloat("item_qty"));
            } catch (NullPointerException npe) {
                transitem.setItemQty(0);
            }

            try {
                transitem.setUnitPrice(aResultSet.getFloat("unit_price"));
            } catch (NullPointerException npe) {
                transitem.setUnitPrice(0);
            }

            try {
                transitem.setItemExpryDate(new Date(aResultSet.getDate("item_expiry_date").getTime()));
                transitem.setItemMnfDate(new Date(aResultSet.getDate("item_mnf_date").getTime()));
            } catch (NullPointerException npe) {
                transitem.setItemExpryDate(null);
                transitem.setItemMnfDate(null);
            }

            try {
                transitem.setUnitTradeDiscount(aResultSet.getFloat("unit_trade_discount"));
            } catch (NullPointerException npe) {
                transitem.setUnitTradeDiscount(0);
            }

            try {
                transitem.setUnitVat(aResultSet.getFloat("unit_vat"));
            } catch (NullPointerException npe) {
                transitem.setUnitVat(0);
            }

            try {
                transitem.setAmount(aResultSet.getFloat("amount"));
            } catch (NullPointerException npe) {
                transitem.setAmount(0);
            }

            try {
                transitem.setVatRated(aResultSet.getString("vat_rated"));
            } catch (NullPointerException npe) {
                transitem.setVatRated("");
            }

            try {
                transitem.setVatPerc(aResultSet.getFloat("vat_perc"));
            } catch (NullPointerException npe) {
                transitem.setVatPerc(0);
            }

            try {
                transitem.setUnitPriceIncVat(aResultSet.getFloat("unit_price_inc_vat"));
            } catch (NullPointerException npe) {
                transitem.setUnitPriceIncVat(0);
            }

            try {
                transitem.setUnitPriceExcVat(aResultSet.getFloat("unit_price_exc_vat"));
            } catch (NullPointerException npe) {
                transitem.setUnitPriceExcVat(0);
            }

            try {
                transitem.setAmountIncVat(aResultSet.getFloat("amount_inc_vat"));
            } catch (NullPointerException npe) {
                transitem.setAmountIncVat(0);
            }

            try {
                transitem.setAmountExcVat(aResultSet.getFloat("amount_exc_vat"));
            } catch (NullPointerException npe) {
                transitem.setAmountExcVat(0);
            }

            try {
                transitem.setStockEffect(aResultSet.getString("stock_effect"));
            } catch (NullPointerException npe) {
                transitem.setStockEffect("");
            }

            try {
                transitem.setIsTradeDiscountVatLiable(aResultSet.getString("is_trade_discount_vat_liable"));
            } catch (NullPointerException npe) {
                transitem.setIsTradeDiscountVatLiable("");
            }

            //for report only
            try {
                transitem.setDescription(aResultSet.getString("description"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setDescription("");
            }
            try {
                transitem.setUnitSymbol(aResultSet.getString("unit_symbol"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setUnitSymbol("");
            }
            try {
                transitem.setTransactionDate(new Date(aResultSet.getDate("transaction_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                transitem.setTransactionDate(null);
            }
            try {
                transitem.setAddDate(new Date(aResultSet.getTimestamp("add_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                transitem.setAddDate(null);
            }
            try {
                transitem.setEditDate(new Date(aResultSet.getTimestamp("edit_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                transitem.setEditDate(null);
            }
            try {
                transitem.setStoreName(aResultSet.getString("store_name"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setStoreName("");
            }
            try {
                transitem.setStoreName2(aResultSet.getString("store_name2"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setStoreName2("");
            }
            try {
                transitem.setTransactorNames(aResultSet.getString("transactor_names"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setTransactorNames("");
            }
            try {
                transitem.setBillTransactorName(aResultSet.getString("bill_transactor_names"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setBillTransactorName("");
            }
            try {
                transitem.setTransactionTypeName(aResultSet.getString("transaction_type_name"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setTransactionTypeName("");
            }
            try {
                transitem.setTransactionReasonName(aResultSet.getString("transaction_reason_name"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setTransactionReasonName("");
            }
            try {
                transitem.setAddUserDetailName(aResultSet.getString("add_user_detail_name"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setAddUserDetailName("");
            }
            try {
                transitem.setEditUserDetailName(aResultSet.getString("edit_user_detail_name"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setEditUserDetailName("");
            }
            try {
                transitem.setTransactionUserDetailName(aResultSet.getString("transaction_user_detail_name"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setTransactionUserDetailName("");
            }
            try {
                transitem.setUnitCostPrice(aResultSet.getFloat("unit_cost_price"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setUnitCostPrice(0);
            }
            try {
                transitem.setUnitProfitMargin(aResultSet.getFloat("unit_profit_margin"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setUnitProfitMargin(0);
            }
            try {
                transitem.setEarnPerc(aResultSet.getFloat("earn_perc"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setEarnPerc(0);
            }
            try {
                transitem.setEarnAmount(aResultSet.getFloat("earn_amount"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setEarnAmount(0);
            }
            try {
                transitem.setTransactionUserDetailId(aResultSet.getInt("transaction_user_detail_id"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setTransactionUserDetailId(0);
            }

            try {
                transitem.setBillTransactorId(aResultSet.getLong("bill_transactor_id"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setBillTransactorId(0);
            }
            try {
                transitem.setCurrencyTypeId(aResultSet.getInt("currency_type_id"));
            } catch (NullPointerException | SQLException npe) {
                transitem.setCurrencyTypeId(0);
            }
            return transitem;
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }

    }

    public TransItem getTransItemFromResultSetBillReport(ResultSet aResultSet) {
        TransItem transitem = new TransItem();
        try {
            transitem.setCategoryName(aResultSet.getString("category_name"));
        } catch (NullPointerException | SQLException npe) {
            transitem.setCategoryName("");
        }
        try {
            transitem.setSumAmountExcVat(aResultSet.getFloat("sum_amount_exc_vat"));
        } catch (NullPointerException | SQLException npe) {
            transitem.setSumAmountExcVat(0);
        }
        try {
            transitem.setSumAmountIncVat(aResultSet.getFloat("sum_amount_inc_vat"));
        } catch (NullPointerException | SQLException npe) {
            transitem.setSumAmountIncVat(0);
        }
        return transitem;
    }

    public List<TransItem> getReportTransItem(Trans aTrans, TransItem aTransItem) {
        String sql;
        sql = "{call sp_report_transaction_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTransItem.clear();
        if (aTrans != null && aTransItem != null) {
            if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() == null
                    && aTrans.getAddDate() == null && aTrans.getAddDate2() == null
                    && aTrans.getEditDate() == null && aTrans.getEditDate2() == null && aTransItem.getTransactionId() == 0) {
                this.ActionMessage = (("Atleast one date range(TransactionDate,AddDate,EditDate) is needed..."));
            } else if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() != null) {
                this.ActionMessage = (("Transaction Date(From) is needed..."));
            } else if (aTrans.getTransactionDate() != null && aTrans.getTransactionDate2() == null) {
                this.ActionMessage = (("Transaction Date(T0) is needed..."));
            } else if (aTrans.getAddDate() == null && aTrans.getAddDate2() != null) {
                this.ActionMessage = (("Add Date(From) is needed..."));
            } else if (aTrans.getAddDate() != null && aTrans.getAddDate2() == null) {
                this.ActionMessage = (("Add Date(To) is needed..."));
            } else if (aTrans.getEditDate() == null && aTrans.getEditDate2() != null) {
                this.ActionMessage = (("Edit Date(From) is needed..."));
            } else if (aTrans.getEditDate() != null && aTrans.getEditDate2() == null) {
                this.ActionMessage = (("Edit Date(To) is needed..."));
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aTrans.getTransactionDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aTrans.getTransactionDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aTrans.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setInt(4, aTrans.getStore2Id());
                    } catch (NullPointerException npe) {
                        ps.setInt(4, 0);
                    }
                    try {
                        ps.setLong(5, aTrans.getTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(5, 0);
                    }
                    try {
                        ps.setInt(6, aTrans.getTransactionTypeId());
                    } catch (NullPointerException npe) {
                        ps.setInt(6, 0);
                    }
                    try {
                        ps.setInt(7, aTrans.getTransactionReasonId());
                    } catch (NullPointerException npe) {
                        ps.setInt(7, 0);
                    }
                    try {
                        ps.setInt(8, aTrans.getAddUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(8, 0);
                    }
                    try {
                        ps.setTimestamp(9, new java.sql.Timestamp(aTrans.getAddDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(9, null);
                    }
                    try {
                        ps.setTimestamp(10, new java.sql.Timestamp(aTrans.getAddDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(10, null);
                    }
                    try {
                        ps.setInt(11, aTrans.getEditUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(11, 0);
                    }
                    try {
                        ps.setTimestamp(12, new java.sql.Timestamp(aTrans.getEditDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(12, null);
                    }
                    try {
                        ps.setTimestamp(13, new java.sql.Timestamp(aTrans.getEditDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(13, null);
                    }
                    try {
                        ps.setLong(14, aTransItem.getTransactionId());
                    } catch (NullPointerException npe) {
                        ps.setLong(14, 0);
                    }
                    try {
                        ps.setLong(15, aTransItem.getItemId());
                    } catch (NullPointerException npe) {
                        ps.setLong(15, 0);
                    }
                    try {
                        ps.setInt(16, aTrans.getTransactionUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(16, 0);
                    }
                    try {
                        ps.setLong(17, aTrans.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(17, 0);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ReportTransItem.add(this.getTransItemFromResultSet(rs));
                    }
                    this.ActionMessage = ((""));
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
        }
        return this.ReportTransItem;
    }

    public List<TransItem> getReportTransItemUserEarn(Trans aTrans) {
        String sql;
        sql = "{call sp_report_transaction_item_user_earn(?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTransItem.clear();
        if (aTrans != null) {
            if (aTrans.getTransactionDate() == null || aTrans.getTransactionDate2() == null) {
                this.ActionMessage = (("Select date range(Transaction Date)..."));
            } else if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() != null) {
                this.ActionMessage = (("Transaction Date(From) is needed..."));
            } else if (aTrans.getTransactionDate() != null && aTrans.getTransactionDate2() == null) {
                this.ActionMessage = (("Transaction Date(T0) is needed..."));
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aTrans.getTransactionDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aTrans.getTransactionDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aTrans.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setInt(4, aTrans.getTransactionTypeId());
                    } catch (NullPointerException npe) {
                        ps.setInt(4, 0);
                    }
                    try {
                        ps.setInt(5, aTrans.getTransactionReasonId());
                    } catch (NullPointerException npe) {
                        ps.setInt(5, 0);
                    }
                    try {
                        ps.setInt(6, aTrans.getTransactionUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(6, 0);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ReportTransItem.add(this.getTransItemFromResultSet(rs));
                    }
                    this.ActionMessage = ((""));
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
        }
        return this.ReportTransItem;
    }

    public List<TransItem> getReportBillTransItemsSummary(long aTransId) {
        String sql;
        sql = "{call sp_report_bill_items_summary(?)}";
        ResultSet rs = null;
        this.ReportTransItem.clear();
        if (aTransId > 0) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try {
                    ps.setLong(1, aTransId);
                } catch (NullPointerException npe) {
                    ps.setLong(1, 0);
                }

                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                while (rs.next()) {
                    this.ReportTransItem.add(this.getTransItemFromResultSetBillReport(rs));
                }
                this.ActionMessage = ((""));
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
        return this.ReportTransItem;
    }

    public void ViewTransItemPriceHistory(long aItemId) {
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("ITEM_ID", 0);
        try {
            if (aItemId != 0) {
                httpSession.setAttribute("ITEM_ID", aItemId);
                //open the view in a dialog
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("modal", true);
                options.put("draggable", true);
                options.put("resizable", true);
                options.put("contentWidth", 600);
                options.put("contentHeight", 500);
                options.put("scrollable", true);
                org.primefaces.context.RequestContext.getCurrentInstance().openDialog("ReportPurchasePriceHistory.xhtml", options, null);
            }
        } catch (NullPointerException npe) {
        }
    }

    public List<TransItem> getReportTransItemPriceHistory() {
        String sql;
        sql = "{call sp_report_transaction_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTransItem.clear();
        long aItemId = 0;
        try {
            aItemId = new GeneralUserSetting().getCurrentItemId();
        } catch (NullPointerException npe) {
            aItemId = 0;
        }
        if (aItemId != 0) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setDate(1, null);
                ps.setDate(2, null);
                ps.setInt(3, 0);
                ps.setInt(4, 0);
                ps.setLong(5, 0);
                ps.setInt(6, 1);//1 for purchase
                ps.setInt(7, 0);
                ps.setInt(8, 0);
                ps.setTimestamp(9, null);
                ps.setTimestamp(10, null);
                ps.setInt(11, 0);
                ps.setTimestamp(12, null);
                ps.setTimestamp(13, null);
                ps.setLong(14, 0);
                ps.setLong(15, aItemId);
                ps.setInt(16, 0);
                ps.setLong(17, 0);
                rs = ps.executeQuery();
                while (rs.next()) {
                    this.ReportTransItem.add(this.getTransItemFromResultSet(rs));
                }
                this.ActionMessage = ((""));
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
        return this.ReportTransItem;
    }

    public long getReportTransItemCount() {
        return this.ReportTransItem.size();
    }

    public void deleteTransItem(TransItem transitem) {
        String sql = "DELETE FROM transaction_item WHERE transaction_item_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, transitem.getTransactionItemId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            //this.clearTransItem(transitem);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("TransItem NOT deleted");
        }
    }

    public void displayTransItem(TransItem TransItemFrom, TransItem TransItemTo) {
        TransItemTo.setTransactionItemId(TransItemFrom.getTransactionItemId());
        TransItemTo.setTransactionId(TransItemFrom.getTransactionId());
        TransItemTo.setItemId(TransItemFrom.getItemId());
        TransItemTo.setBatchno(TransItemFrom.getBatchno());
        TransItemTo.setItemQty(TransItemFrom.getItemQty());
        TransItemTo.setUnitPrice(TransItemFrom.getUnitPrice());
        TransItemTo.setItemExpryDate(TransItemFrom.getItemExpryDate());
        TransItemTo.setItemMnfDate(TransItemFrom.getItemMnfDate());
        //add for unit vat, etc
    }

    public List<TransItem> getTransItemsByTransactionId(long aTransactionId) {
        String sql;
        sql = "{call sp_search_transaction_item_by_transaction_id(?)}";
        ResultSet rs = null;
        TransItems = new ArrayList<TransItem>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransItems.add(this.getTransItemFromResultSet(rs));
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
        return TransItems;
    }

    public void resetTransactionItem(int aResetType, List<TransItem> aTransItems) {//1-TransId only;2-TransId & all amounts EXC qty
        List<TransItem> ati = aTransItems;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        while (ListItemIndex < ListItemNo) {
            if (aResetType == 1 || aResetType == 2) {
                ati.get(ListItemIndex).setTransactionItemId(0);
            }
            if (aResetType == 2) {
                ati.get(ListItemIndex).setUnitPrice(0);
                ati.get(ListItemIndex).setUnitPriceIncVat(0);
                ati.get(ListItemIndex).setUnitPriceExcVat(0);
                ati.get(ListItemIndex).setAmount(0);
                ati.get(ListItemIndex).setAmountIncVat(0);
                ati.get(ListItemIndex).setAmountExcVat(0);
            }
            ListItemIndex = ListItemIndex + 1;
        }
    }

    public void assignTransItemsByTransactionId(long aTransactionId, List<TransItem> aTransItems) {
        String sql;
        sql = "{call sp_search_transaction_item_by_transaction_id(?)}";
        ResultSet rs = null;
        //TransItems = new ArrayList<TransItem>();
        aTransItems.clear();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            rs = ps.executeQuery();
            while (rs.next()) {
                aTransItems.add(this.getTransItemFromResultSet(rs));
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
    }

    public List<TransItem> getTransItemsByTransactionId2(long aTransactionId) {
        String sql;
        sql = "{call sp_search_transaction_item_by_transaction_id2(?)}";
        ResultSet rs = null;
        TransItems = new ArrayList<TransItem>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransItems.add(this.getTransItemFromResultSet(rs));
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
        return TransItems;
    }

    public void setTransItemsHistoryByIDs(long aTransactionId, long aTransactionHistId, List<TransItem> hTransItems) {
        String sql;
        sql = "{call sp_search_transaction_item_hist_by_ids(?,?)}";
        ResultSet rs = null;
        hTransItems.clear();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            ps.setLong(2, aTransactionHistId);
            rs = ps.executeQuery();
            while (rs.next()) {
                hTransItems.add(this.getTransItemFromResultSet(rs));
            }
        } catch (SQLException se) {
            System.err.println("setTransItemsHistoryByIDs:" + se.getMessage());
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

    public void setTransItemsByTransactionId(List<TransItem> aTransItems, long aTransactionId) {
        String sql;
        sql = "{call sp_search_transaction_item_by_transaction_id(?)}";
        ResultSet rs = null;
        aTransItems.clear();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            rs = ps.executeQuery();
            while (rs.next()) {
                aTransItems.add(this.getTransItemFromResultSet(rs));
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
    }

    public void setTransItemsByTransactionNumber(List<TransItem> aTransItems, String aTransactionNumber) {
        String sql;
        sql = "{call sp_search_transaction_item_by_transaction_number(?)}";
        ResultSet rs = null;
        aTransItems.clear();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aTransactionNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                aTransItems.add(this.getTransItemFromResultSet(rs));
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
    }

    public void updateTransItemBatchDates(TransItem aTransItemToUpdate) {
        //get item's stock details
        try {
            StockBean sb = new StockBean();
            Stock st = new Stock();
            st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), aTransItemToUpdate.getItemId(), aTransItemToUpdate.getBatchno());
            aTransItemToUpdate.setItemExpryDate(st.getItemExpDate());
            aTransItemToUpdate.setItemMnfDate(st.getItemMnfDate());
        } catch (NullPointerException npe) {
            aTransItemToUpdate.setItemExpryDate(null);
            aTransItemToUpdate.setItemMnfDate(null);
        }

    }

    public void addTransItem(Trans aTrans, StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem NewTransItem, Item aSelectedItem) {
        float IncludedVat;
        float ExcludedVat;
        float VatPercent;
        //note sales quote and sales order are in another function
        if (!"SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && aSelectedItem.getItemType().equals("SERVICE")) {
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("A SERVICE Item is not valid for this Transaction!");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(1);
        } else {
            try {
                //update vat perc to be used
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && ("COST-PRICE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType()) || "EXEMPT SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType()))) {
                    VatPercent = 0;
                } else {
                    VatPercent = CompanySetting.getVatPerc();
                }

                if (NewTransItem.getBatchno() == null) {
                    NewTransItem.setBatchno("");
                }

                //Update Override prices
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && NewTransItem.isOverridePrices()) {
                    NewTransItem.setUnitPrice(NewTransItem.getUnitPrice2());
                    NewTransItem.setVatRated(NewTransItem.getVatRated2());
                    NewTransItem.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount2());
                }

                //get item's stock details
                //get and check number of batches
                StockBean sb = new StockBean();
                Stock st = new Stock();
                st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), NewTransItem.getItemId(), NewTransItem.getBatchno());

                //unpacking - AutoUnPackModule
                //process items that might need to be unpacked
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) & "Yes".equals(CompanySetting.getIsAllowAutoUnpack())) {
                    float TotalQtyFromList = 0;
                    try {
                        TotalQtyFromList = this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno());
                    } catch (NullPointerException npe) {
                        TotalQtyFromList = 0;
                    }
                    float TotalQtyFromListAndNew = TotalQtyFromList + NewTransItem.getItemQty();

                    float SmallItemQtyNeeded = 0;
                    try {
                        SmallItemQtyNeeded = TotalQtyFromListAndNew - st.getCurrentqty();
                    } catch (NullPointerException npe) {
                        SmallItemQtyNeeded = TotalQtyFromListAndNew;
                    }
                    //Unpackaging if needed
                    if (st == null || st.getCurrentqty() == 0) {//this item and batch does not exist in this store OR its quatity is 0 (out of stock)
                        if (this.autoUnpackItem(NewTransItem, SmallItemQtyNeeded) == 1) {
                        } else {
                        }
                    } else if ((NewTransItem.getItemQty() + this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno())) > st.getCurrentqty()) {
                        //float TotalQtyFromList=this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno());
                        if (this.autoUnpackItem(NewTransItem, SmallItemQtyNeeded) == 1) {
                        } else {
                        }
                    }
                    //re-calculate stock for small item after unpackaing
                    st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), NewTransItem.getItemId(), NewTransItem.getBatchno());
                }

                //reset messages
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(0);
                int StockFail1 = 0, StockFail2 = 0, StockFail3 = 0;

                if (aSelectedItem.getItemType().equals("PRODUCT")) {
                    if (st == null || st.getCurrentqty() == 0) {//this item and batch does not exist in this store OR its quatity is 0 (out of stock)
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("SELECTED ITEM and BATCH - DOES NOT EXIST or IS OUT OF STOCK !");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                        StockFail1 = 1;
                    }
                }
                if (aSelectedItem.getItemType().equals("PRODUCT")) {
                    if ((NewTransItem.getItemQty() + this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno())) > st.getCurrentqty()) {
                        //check if supplied qty + existing qty is more than total current stock qty
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("SELECTED ITEM and BATCH - INSUFFICIENT STOCK !");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                        StockFail2 = 1;
                    }
                }
                if (NewTransItem.getItemQty() <= 0) {
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("ENTER ITEM QUANTITY !");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(1);
                    StockFail3 = 1;
                }

                if (StockFail1 == 0 && StockFail2 == 0 && StockFail3 == 0) {
                    TransItem ti = new TransItem();
                    ti.setCurrencyTypeId(aTrans.getCurrencyTypeId());
                    ti.setTransactionItemId(NewTransItem.getTransactionItemId());
                    ti.setTransactionId(NewTransItem.getTransactionId());
                    ti.setItemId(NewTransItem.getItemId());
                    ti.setBatchno(NewTransItem.getBatchno());
                    ti.setItemQty(NewTransItem.getItemQty());
                    ti.setUnitPrice(NewTransItem.getUnitPrice());
                    ti.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount());
                    ti.setAmount(NewTransItem.getAmount());
                    ti.setVatRated(NewTransItem.getVatRated());
                    ti.setRemarks(NewTransItem.getRemarks());

                    //for UNPACK
                    ti.setItemId2(NewTransItem.getItemId2());
                    ti.setItemQty2(NewTransItem.getItemQty2());
                    //Check if this is a vatable item
                    if ("STANDARD".equals(NewTransItem.getVatRated()) && ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()))) {
                        //this is a vatable item
                        ti.setVatPerc(VatPercent);
                        //Check if VAT is Inclusive or Excuksive
                        if ("Yes".equals(CompanySetting.getIsVatInclusive())) {
                            //VAT - Inclusive
                            if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                                ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                                IncludedVat = (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) - (100 * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) / (100 + VatPercent));
                                ti.setUnitVat(IncludedVat);
                                ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);

                            } else {
                                //do nothing; IncVat=IncVat
                                ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                                IncludedVat = NewTransItem.getUnitPrice() - (100 * NewTransItem.getUnitPrice() / (100 + VatPercent));
                                ti.setUnitVat(IncludedVat);
                                ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);
                            }
                        } else {
                            //VAT - Exclusive
                            ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                            if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                                ExcludedVat = (VatPercent / 100) * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount());
                            } else {
                                ExcludedVat = (VatPercent / 100) * NewTransItem.getUnitPrice();
                            }
                            ti.setUnitVat(ExcludedVat);
                            ti.setUnitPriceIncVat(NewTransItem.getUnitPrice() + ExcludedVat);
                        }

                    } else {
                        //this ISNT a vatable item
                        ti.setVatPerc(0);
                        ti.setUnitVat(0);
                        ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                        ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                    }
                    if (aSelectedItem.getItemType().equals("PRODUCT")) {
                        ti.setItemExpryDate(st.getItemExpDate());
                        ti.setItemMnfDate(st.getItemMnfDate());
                    } else {
                        ti.setItemExpryDate(null);
                        ti.setItemMnfDate(null);
                    }
                    ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                    ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());

                    //for profit margin
                    if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        ti.setUnitCostPrice(aSelectedItem.getUnitCostPrice());
                        ti.setUnitProfitMargin((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) - aSelectedItem.getUnitCostPrice());
                    } else {
                        ti.setUnitCostPrice(0);
                        ti.setUnitProfitMargin(0);
                    }

                    //check if itme+batchno already exists
                    int ItemFoundAtIndex = itemExists(aActiveTransItems, ti.getItemId(), ti.getBatchno());
                    if (ItemFoundAtIndex == -1) {
                        aActiveTransItems.add(0, ti);
                    } else {
                        ti.setItemQty(ti.getItemQty() + aActiveTransItems.get(ItemFoundAtIndex).getItemQty());
                        ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
                        ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                        ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                        aActiveTransItems.add(ItemFoundAtIndex, ti);
                        aActiveTransItems.remove(ItemFoundAtIndex + 1);
                    }

                    TransBean transB = new TransBean();
                    transB.clearAll(null, aActiveTransItems, NewTransItem, aSelectedItem, null, 1);

                    aStatusBean.setItemAddedStatus("ITEM ADDED");
                    aStatusBean.setItemNotAddedStatus("");
                    aStatusBean.setShowItemAddedStatus(1);
                    aStatusBean.setShowItemNotAddedStatus(0);

                    //update totals
                    new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
                }
            } catch (NullPointerException npe) {
                System.err.println("addTransItem:" + npe.getMessage());
            }
        }
    }

    public void addTransItem_Accomodation(Trans aTrans, StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem NewTransItem, Item aSelectedItem) {
        float IncludedVat;
        float ExcludedVat;
        float VatPercent;
        //note sales quote and sales order are in another function
        if (!"SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && aSelectedItem.getItemType().equals("SERVICE")) {
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("A SERVICE Item is not valid for this Transaction!");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(1);
        } else {
            try {
                //update vat perc to be used
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && ("COST-PRICE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType()) || "EXEMPT SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType()))) {
                    VatPercent = 0;
                } else {
                    VatPercent = CompanySetting.getVatPerc();
                }

                if (NewTransItem.getBatchno() == null) {
                    NewTransItem.setBatchno("");
                }

                //Update Override prices
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && NewTransItem.isOverridePrices()) {
                    NewTransItem.setUnitPrice(NewTransItem.getUnitPrice2());
                    NewTransItem.setVatRated(NewTransItem.getVatRated2());
                    NewTransItem.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount2());
                }

                //get item's stock details
                //get and check number of batches
                StockBean sb = new StockBean();
                Stock st = new Stock();
                st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), NewTransItem.getItemId(), NewTransItem.getBatchno());

                //unpacking - AutoUnPackModule
                //process items that might need to be unpacked
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) & "Yes".equals(CompanySetting.getIsAllowAutoUnpack())) {
                    float TotalQtyFromList = 0;
                    try {
                        TotalQtyFromList = this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno());
                    } catch (NullPointerException npe) {
                        TotalQtyFromList = 0;
                    }
                    float TotalQtyFromListAndNew = TotalQtyFromList + NewTransItem.getItemQty();

                    float SmallItemQtyNeeded = 0;
                    try {
                        SmallItemQtyNeeded = TotalQtyFromListAndNew - st.getCurrentqty();
                    } catch (NullPointerException npe) {
                        SmallItemQtyNeeded = TotalQtyFromListAndNew;
                    }
                    //Unpackaging if needed
                    if (st == null || st.getCurrentqty() == 0) {//this item and batch does not exist in this store OR its quatity is 0 (out of stock)
                        if (this.autoUnpackItem(NewTransItem, SmallItemQtyNeeded) == 1) {
                        } else {
                        }
                    } else if ((NewTransItem.getItemQty() + this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno())) > st.getCurrentqty()) {
                        //float TotalQtyFromList=this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno());
                        if (this.autoUnpackItem(NewTransItem, SmallItemQtyNeeded) == 1) {
                        } else {
                        }
                    }
                    //re-calculate stock for small item after unpackaing
                    st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), NewTransItem.getItemId(), NewTransItem.getBatchno());
                }

                //reset messages
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(0);
                int StockFail1 = 0, StockFail2 = 0, StockFail3 = 0, Fail4 = 0;

                if (aSelectedItem.getItemType().equals("PRODUCT")) {
                    if (st == null || st.getCurrentqty() == 0) {//this item and batch does not exist in this store OR its quatity is 0 (out of stock)
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("SELECTED ITEM and BATCH - DOES NOT EXIST or IS OUT OF STOCK !");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                        StockFail1 = 1;
                    }
                }
                if (aSelectedItem.getItemType().equals("PRODUCT")) {
                    if ((NewTransItem.getItemQty() + this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno())) > st.getCurrentqty()) {
                        //check if supplied qty + existing qty is more than total current stock qty
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("SELECTED ITEM and BATCH - INSUFFICIENT STOCK !");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                        StockFail2 = 1;
                    }
                }
                if (NewTransItem.getItemQty() <= 0) {
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("ENTER ITEM QUANTITY !");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(1);
                    StockFail3 = 1;
                }

                if (!aActiveTransItems.isEmpty()) {
                    int ItemFoundAtIndex2 = itemExists(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno());
                    if (ItemFoundAtIndex2 == -1) {
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("ONLY ONE ITEM CAN BE ADDED PER TRANSACTION FOR ACCOMODATION!");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                        Fail4 = 1;
                    }
                }

                if (StockFail1 == 0 && StockFail2 == 0 && StockFail3 == 0 && Fail4 == 0) {
                    TransItem ti = new TransItem();
                    ti.setCurrencyTypeId(aTrans.getCurrencyTypeId());
                    ti.setTransactionItemId(NewTransItem.getTransactionItemId());
                    ti.setTransactionId(NewTransItem.getTransactionId());
                    ti.setItemId(NewTransItem.getItemId());
                    ti.setBatchno(NewTransItem.getBatchno());
                    ti.setItemQty(NewTransItem.getItemQty());
                    ti.setUnitPrice(NewTransItem.getUnitPrice());
                    ti.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount());
                    ti.setAmount(NewTransItem.getAmount());
                    ti.setVatRated(NewTransItem.getVatRated());
                    ti.setRemarks(NewTransItem.getRemarks());

                    //for UNPACK
                    ti.setItemId2(NewTransItem.getItemId2());
                    ti.setItemQty2(NewTransItem.getItemQty2());
                    //Check if this is a vatable item
                    if ("STANDARD".equals(NewTransItem.getVatRated()) && ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()))) {
                        //this is a vatable item
                        ti.setVatPerc(VatPercent);
                        //Check if VAT is Inclusive or Excuksive
                        if ("Yes".equals(CompanySetting.getIsVatInclusive())) {
                            //VAT - Inclusive
                            if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                                ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                                IncludedVat = (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) - (100 * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) / (100 + VatPercent));
                                ti.setUnitVat(IncludedVat);
                                ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);

                            } else {
                                //do nothing; IncVat=IncVat
                                ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                                IncludedVat = NewTransItem.getUnitPrice() - (100 * NewTransItem.getUnitPrice() / (100 + VatPercent));
                                ti.setUnitVat(IncludedVat);
                                ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);
                            }
                        } else {
                            //VAT - Exclusive
                            ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                            if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                                ExcludedVat = (VatPercent / 100) * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount());
                            } else {
                                ExcludedVat = (VatPercent / 100) * NewTransItem.getUnitPrice();
                            }
                            ti.setUnitVat(ExcludedVat);
                            ti.setUnitPriceIncVat(NewTransItem.getUnitPrice() + ExcludedVat);
                        }

                    } else {
                        //this ISNT a vatable item
                        ti.setVatPerc(0);
                        ti.setUnitVat(0);
                        ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                        ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                    }
                    if (aSelectedItem.getItemType().equals("PRODUCT")) {
                        ti.setItemExpryDate(st.getItemExpDate());
                        ti.setItemMnfDate(st.getItemMnfDate());
                    } else {
                        ti.setItemExpryDate(null);
                        ti.setItemMnfDate(null);
                    }
                    ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                    ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());

                    //for profit margin
                    if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        ti.setUnitCostPrice(aSelectedItem.getUnitCostPrice());
                        ti.setUnitProfitMargin((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) - aSelectedItem.getUnitCostPrice());
                    } else {
                        ti.setUnitCostPrice(0);
                        ti.setUnitProfitMargin(0);
                    }

                    //check if itme+batchno already exists
                    int ItemFoundAtIndex = itemExists(aActiveTransItems, ti.getItemId(), ti.getBatchno());
                    if (ItemFoundAtIndex == -1) {
                        aActiveTransItems.add(0, ti);
                    } else {
                        ti.setItemQty(ti.getItemQty());
                        ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
                        ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                        ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                        aActiveTransItems.add(ItemFoundAtIndex, ti);
                        aActiveTransItems.remove(ItemFoundAtIndex + 1);
                    }

                    TransBean transB = new TransBean();
                    transB.clearAll(null, aActiveTransItems, NewTransItem, aSelectedItem, null, 1);

                    aStatusBean.setItemAddedStatus("ITEM ADDED");
                    aStatusBean.setItemNotAddedStatus("");
                    aStatusBean.setShowItemAddedStatus(1);
                    aStatusBean.setShowItemNotAddedStatus(0);

                    //update totals
                    new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
                }
            } catch (NullPointerException npe) {
                System.err.println("addTransItem:" + npe.getMessage());
            }
        }
    }

    public void addTransItemSaleQuotation(Trans aTrans, StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem NewTransItem, Item aSelectedItem) {
        float IncludedVat;
        float ExcludedVat;
        float VatPercent;
        try {
            //update vat perc to be used
            VatPercent = CompanySetting.getVatPerc();
            //set Batch no to 
            NewTransItem.setBatchno("");
            //Update Override prices
            if (NewTransItem.isOverridePrices()) {
                NewTransItem.setUnitPrice(NewTransItem.getUnitPrice2());
                NewTransItem.setVatRated(NewTransItem.getVatRated2());
                NewTransItem.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount2());
            }
            //reset messages
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(0);
            int StockFail1 = 0, StockFail2 = 0, StockFail3 = 0;
            if (NewTransItem.getItemQty() <= 0) {
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("ENTER ITEM QUANTITY !");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(1);
                StockFail3 = 1;
            }

            if (StockFail1 == 0 && StockFail2 == 0 && StockFail3 == 0) {
                TransItem ti = new TransItem();
                ti.setTransactionItemId(NewTransItem.getTransactionItemId());
                ti.setTransactionId(NewTransItem.getTransactionId());
                ti.setItemId(NewTransItem.getItemId());
                ti.setBatchno(NewTransItem.getBatchno());
                ti.setItemQty(NewTransItem.getItemQty());
                ti.setUnitPrice(NewTransItem.getUnitPrice());
                ti.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount());
                ti.setAmount(NewTransItem.getAmount());
                ti.setVatRated(NewTransItem.getVatRated());
                //for UNPACK
                //ti.setItemId2(NewTransItem.getItemId2());
                //ti.setItemQty2(NewTransItem.getItemQty2());

                //Check if this is a vatable item
                if ("STANDARD".equals(NewTransItem.getVatRated())) {
                    //this is a vatable item
                    ti.setVatPerc(VatPercent);
                    //Check if VAT is Inclusive or Excuksive
                    if ("Yes".equals(CompanySetting.getIsVatInclusive())) {
                        //VAT - Inclusive
                        if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                            ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                            IncludedVat = (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) - (100 * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) / (100 + VatPercent));
                            ti.setUnitVat(IncludedVat);
                            ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);

                        } else {
                            //do nothing; IncVat=IncVat
                            ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                            IncludedVat = NewTransItem.getUnitPrice() - (100 * NewTransItem.getUnitPrice() / (100 + VatPercent));
                            ti.setUnitVat(IncludedVat);
                            ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);
                        }
                    } else {
                        //VAT - Exclusive
                        ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                        if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                            ExcludedVat = (VatPercent / 100) * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount());
                        } else {
                            ExcludedVat = (VatPercent / 100) * NewTransItem.getUnitPrice();
                        }
                        ti.setUnitVat(ExcludedVat);
                        ti.setUnitPriceIncVat(NewTransItem.getUnitPrice() + ExcludedVat);
                    }

                } else {
                    //this ISNT a vatable item
                    ti.setVatPerc(0);
                    ti.setUnitVat(0);
                    ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                    ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                }

                //nullfy expiry and man dates
                ti.setItemExpryDate(null);
                ti.setItemMnfDate(null);
                //calculate Amt Inc/Exc Vat
                ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());

                //for profit margin set to 0
                ti.setUnitCostPrice(0);
                ti.setUnitProfitMargin(0);

                //check if itme+batchno already exists
                int ItemFoundAtIndex = itemExists(aActiveTransItems, ti.getItemId(), ti.getBatchno());
                if (ItemFoundAtIndex == -1) {
                    aActiveTransItems.add(0, ti);
                } else {
                    ti.setItemQty(ti.getItemQty() + aActiveTransItems.get(ItemFoundAtIndex).getItemQty());
                    ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
                    ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                    ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                    aActiveTransItems.add(ItemFoundAtIndex, ti);
                    aActiveTransItems.remove(ItemFoundAtIndex + 1);
                }

                TransBean transB = new TransBean();
                transB.clearAll(null, aActiveTransItems, NewTransItem, aSelectedItem, null, 1);

                aStatusBean.setItemAddedStatus("ITEM ADDED");
                aStatusBean.setItemNotAddedStatus("");
                aStatusBean.setShowItemAddedStatus(1);
                aStatusBean.setShowItemNotAddedStatus(0);

                //update totals
                new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
            }
        } catch (NullPointerException npe) {
            System.err.println("addTransItemSalesQuotation:" + npe.getMessage());
        }
    }

    public void addTransItemReservation(Trans aTrans, StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem NewTransItem, Item aSelectedItem) {
        float IncludedVat;
        float ExcludedVat;
        float VatPercent;
        try {
            //update vat perc to be used
            VatPercent = CompanySetting.getVatPerc();
            //set Batch no to 
            NewTransItem.setBatchno("");
            //Update Override prices
            if (NewTransItem.isOverridePrices()) {
                NewTransItem.setUnitPrice(NewTransItem.getUnitPrice2());
                NewTransItem.setVatRated(NewTransItem.getVatRated2());
                NewTransItem.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount2());
            }
            //reset messages
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(0);
            int StockFail1 = 0, StockFail2 = 0, StockFail3 = 0;
            if (NewTransItem.getItemQty() <= 0) {
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("ENTER ITEM QUANTITY !");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(1);
                StockFail3 = 1;
            }

            if (StockFail1 == 0 && StockFail2 == 0 && StockFail3 == 0) {
                TransItem ti = new TransItem();
                ti.setTransactionItemId(NewTransItem.getTransactionItemId());
                ti.setTransactionId(NewTransItem.getTransactionId());
                ti.setItemId(NewTransItem.getItemId());
                ti.setBatchno(NewTransItem.getBatchno());
                ti.setItemQty(NewTransItem.getItemQty());
                ti.setUnitPrice(NewTransItem.getUnitPrice());
                ti.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount());
                ti.setAmount(NewTransItem.getAmount());
                ti.setVatRated(NewTransItem.getVatRated());

                //for UNPACK
                //ti.setItemId2(NewTransItem.getItemId2());
                //ti.setItemQty2(NewTransItem.getItemQty2());
                //Check if this is a vatable item
                if ("STANDARD".equals(NewTransItem.getVatRated())) {
                    //this is a vatable item
                    ti.setVatPerc(VatPercent);
                    //Check if VAT is Inclusive or Excuksive
                    if ("Yes".equals(CompanySetting.getIsVatInclusive())) {
                        //VAT - Inclusive
                        if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                            ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                            IncludedVat = (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) - (100 * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) / (100 + VatPercent));
                            ti.setUnitVat(IncludedVat);
                            ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);

                        } else {
                            //do nothing; IncVat=IncVat
                            ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                            IncludedVat = NewTransItem.getUnitPrice() - (100 * NewTransItem.getUnitPrice() / (100 + VatPercent));
                            ti.setUnitVat(IncludedVat);
                            ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);
                        }
                    } else {
                        //VAT - Exclusive
                        ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                        if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                            ExcludedVat = (VatPercent / 100) * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount());
                        } else {
                            ExcludedVat = (VatPercent / 100) * NewTransItem.getUnitPrice();
                        }
                        ti.setUnitVat(ExcludedVat);
                        ti.setUnitPriceIncVat(NewTransItem.getUnitPrice() + ExcludedVat);
                    }

                } else {
                    //this ISNT a vatable item
                    ti.setVatPerc(0);
                    ti.setUnitVat(0);
                    ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                    ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                }

                //nullfy expiry and man dates
                ti.setItemExpryDate(null);
                ti.setItemMnfDate(null);
                //calculate Amt Inc/Exc Vat
                ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());

                //for profit margin set to 0
                ti.setUnitCostPrice(0);
                ti.setUnitProfitMargin(0);

                //check if itme+batchno already exists
                int ItemFoundAtIndex = itemExists(aActiveTransItems, ti.getItemId(), ti.getBatchno());
                if (ItemFoundAtIndex == -1) {
                    aActiveTransItems.add(0, ti);
                } else {
                    ti.setItemQty(ti.getItemQty() + aActiveTransItems.get(ItemFoundAtIndex).getItemQty());
                    ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
                    ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                    ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                    aActiveTransItems.add(ItemFoundAtIndex, ti);
                    aActiveTransItems.remove(ItemFoundAtIndex + 1);
                }

                TransBean transB = new TransBean();
                transB.clearAll(null, aActiveTransItems, NewTransItem, aSelectedItem, null, 1);

                aStatusBean.setItemAddedStatus("ITEM ADDED");
                aStatusBean.setItemNotAddedStatus("");
                aStatusBean.setShowItemAddedStatus(1);
                aStatusBean.setShowItemNotAddedStatus(0);

                //update totals
                new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
            }
        } catch (NullPointerException npe) {
            System.err.println("addTransItemSalesQuotation:" + npe.getMessage());
        }
    }

    public void addTransItemTransferRequest(Trans aTrans, StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem NewTransItem, Item aSelectedItem) {
        try {
            //reset messages
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(0);
            int StockFail1 = 0, StockFail2 = 0, StockFail3 = 0;
            if (NewTransItem.getItemQty() <= 0) {
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("ENTER ITEM QUANTITY !");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(1);
                StockFail1 = 1;
            }
            if (StockFail1 == 0 && StockFail2 == 0 && StockFail3 == 0) {
                TransItem ti = new TransItem();
                ti.setTransactionItemId(NewTransItem.getTransactionItemId());
                ti.setTransactionId(NewTransItem.getTransactionId());
                ti.setItemId(NewTransItem.getItemId());
                ti.setBatchno("");
                ti.setItemQty(NewTransItem.getItemQty());
                ti.setUnitPrice(0);
                ti.setUnitTradeDiscount(0);
                ti.setAmount(0);
                ti.setVatRated(NewTransItem.getVatRated());
                ti.setVatPerc(0);
                ti.setUnitVat(0);
                ti.setUnitPriceIncVat(0);
                ti.setUnitPriceExcVat(0);
                //nullfy expiry and man dates
                ti.setItemExpryDate(null);
                ti.setItemMnfDate(null);
                //calculate Amt Inc/Exc Vat
                ti.setAmountIncVat(0);
                ti.setAmountExcVat(0);

                //for profit margin set to 0
                ti.setUnitCostPrice(0);
                ti.setUnitProfitMargin(0);

                //check if itme+batchno already exists
                int ItemFoundAtIndex = itemExists(aActiveTransItems, ti.getItemId(), ti.getBatchno());
                if (ItemFoundAtIndex == -1) {
                    aActiveTransItems.add(0, ti);
                } else {
                    ti.setItemQty(ti.getItemQty() + aActiveTransItems.get(ItemFoundAtIndex).getItemQty());
                    ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
                    ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                    ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                    aActiveTransItems.add(ItemFoundAtIndex, ti);
                    aActiveTransItems.remove(ItemFoundAtIndex + 1);
                }
                TransBean transB = new TransBean();
                transB.clearAll(null, aActiveTransItems, NewTransItem, aSelectedItem, null, 1);
                aStatusBean.setItemAddedStatus("ITEM ADDED");
                aStatusBean.setItemNotAddedStatus("");
                aStatusBean.setShowItemAddedStatus(1);
                aStatusBean.setShowItemNotAddedStatus(0);

                //update totals
                //new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
            }
        } catch (NullPointerException npe) {
            System.err.println("addTransItemTransferRequest:" + npe.getMessage());
        }
    }

    public void editTransItem(int aTransTypeNameId, Trans aTrans, List<TransItem> aActiveTransItems, TransItem ti) {
        if (ti.getItemQty() < 0) {
            ti.setItemQty(0);
        }
        if (aTransTypeNameId == 2) {//SALE INVOICE
            ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 10) {//SALE QUOTATION
            ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 11) {//SALE ORDER
            ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 12) {//GOODS DELIVERY
            ti.setAmount(0);
            ti.setAmountIncVat(0);
            ti.setAmountExcVat(0);
        }

        if (aTransTypeNameId == 1) {//PURCHASE INVOICE
            ti.setAmount(ti.getItemQty() * (ti.getUnitPrice() + ti.getUnitVat() - ti.getUnitTradeDiscount()));
            ti.setAmountIncVat(ti.getAmount());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 8) {//PURCHASE ORDER
            ti.setAmount(ti.getItemQty() * (ti.getUnitPrice() + ti.getUnitVat() - ti.getUnitTradeDiscount()));
            ti.setAmountIncVat(ti.getAmount());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 9) {//GOODS RECEIVED
            ti.setAmount(0);
            ti.setAmountIncVat(0);
            ti.setAmountExcVat(0);
        }
        if (aTransTypeNameId == 3) {//DISPOSE
            aTrans.setCashDiscount(0);
            ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }

        //for profit margin
        if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            ti.setUnitCostPrice(ti.getUnitCostPrice());
            ti.setUnitProfitMargin((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) - ti.getUnitCostPrice());
        } else {
            ti.setUnitCostPrice(0);
            ti.setUnitProfitMargin(0);
        }

        //update totals
        new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
    }

    public void addTransItemBarCode(Trans aTrans, StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem NewTransItem, Item aSelectedItem) {
        float IncludedVat;
        float ExcludedVat;
        float VatPercent;
        try {

            //update vat perc to be used
            if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && "COST-PRICE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                VatPercent = 0;
            } else {
                VatPercent = CompanySetting.getVatPerc();
            }

            //Update Override prices
            if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && NewTransItem.isOverridePrices()) {
                NewTransItem.setUnitPrice(NewTransItem.getUnitPrice2());
                NewTransItem.setVatRated(NewTransItem.getVatRated2());
                NewTransItem.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount2());
            }

            //get item's stock details
            //get and check number of batches
            StockBean sb = new StockBean();
            Stock st = new Stock();
            st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), NewTransItem.getItemId(), NewTransItem.getBatchno());

            //reset messages
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(0);

            TransItem ti = new TransItem();
            ti.setTransactionItemId(NewTransItem.getTransactionItemId());
            ti.setTransactionId(NewTransItem.getTransactionId());
            ti.setItemId(NewTransItem.getItemId());
            ti.setBatchno(NewTransItem.getBatchno());
            ti.setItemQty(NewTransItem.getItemQty());
            ti.setUnitPrice(NewTransItem.getUnitPrice());
            ti.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount());
            ti.setAmount(NewTransItem.getAmount());
            ti.setVatRated(NewTransItem.getVatRated());
            //for UNPACK
            ti.setItemId2(NewTransItem.getItemId2());
            ti.setItemQty2(NewTransItem.getItemQty2());

            //Check if this is a vatable item
            if ("STANDARD".equals(NewTransItem.getVatRated()) && ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()))) {
                //this is a vatable item
                ti.setVatPerc(VatPercent);
                //Check if VAT is Inclusive or Excuksive
                if ("Yes".equals(CompanySetting.getIsVatInclusive())) {
                    //VAT - Inclusive
                    if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                        ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                        IncludedVat = (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) - (100 * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount()) / (100 + VatPercent));
                        ti.setUnitVat(IncludedVat);
                        ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);

                    } else {
                        //do nothing; IncVat=IncVat
                        ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                        IncludedVat = NewTransItem.getUnitPrice() - (100 * NewTransItem.getUnitPrice() / (100 + VatPercent));
                        ti.setUnitVat(IncludedVat);
                        ti.setUnitPriceExcVat(NewTransItem.getUnitPrice() - IncludedVat);
                    }
                } else {
                    //VAT - Exclusive
                    ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
                    if ("No".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                        ExcludedVat = (VatPercent / 100) * (NewTransItem.getUnitPrice() - NewTransItem.getUnitTradeDiscount());
                    } else {
                        ExcludedVat = (VatPercent / 100) * NewTransItem.getUnitPrice();
                    }
                    ti.setUnitVat(ExcludedVat);
                    ti.setUnitPriceIncVat(NewTransItem.getUnitPrice() + ExcludedVat);
                }

            } else {
                //this ISNT a vatable item
                ti.setVatPerc(0);
                ti.setUnitVat(0);
                ti.setUnitPriceIncVat(NewTransItem.getUnitPrice());
                ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
            }
            try {
                ti.setItemExpryDate(st.getItemExpDate());
                ti.setItemMnfDate(st.getItemMnfDate());
            } catch (NullPointerException npe) {
                ti.setItemExpryDate(null);
                ti.setItemMnfDate(null);
            }
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());

            //for profit margin
            if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                ti.setUnitCostPrice(aSelectedItem.getUnitCostPrice());
                ti.setUnitProfitMargin((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) - aSelectedItem.getUnitCostPrice());
            } else {
                ti.setUnitCostPrice(0);
                ti.setUnitProfitMargin(0);
            }

            //check if itme+batchno already exists
            int ItemFoundAtIndex = itemExists(aActiveTransItems, ti.getItemId(), ti.getBatchno());
            if (ItemFoundAtIndex == -1) {
                //this.ActiveTransItems.add(0,ti);
                aActiveTransItems.add(0, ti);
            } else {
                ti.setItemQty(ti.getItemQty() + aActiveTransItems.get(ItemFoundAtIndex).getItemQty());
                ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
                ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
                aActiveTransItems.add(ItemFoundAtIndex, ti);
                aActiveTransItems.remove(ItemFoundAtIndex + 1);
            }

            TransBean transB = new TransBean();
            transB.clearAll(null, aActiveTransItems, NewTransItem, aSelectedItem, null, 1);

            aStatusBean.setItemAddedStatus("ITEM ADDED");
            aStatusBean.setItemNotAddedStatus("");
            aStatusBean.setShowItemAddedStatus(1);
            aStatusBean.setShowItemNotAddedStatus(0);

            //update totals
            new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
        } catch (NullPointerException npe) {
            System.err.println("addTransItemBarCode:" + npe.getMessage());
        }
    }

    public void addTransItemUNPACK(StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem NewTransItem, Trans NewTrans, Item aItem) {
        if (aItem.getItemType().equals("SERVICE")) {
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("A SERVICE Item cannot be be UNPACKED... !");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(1);
        } else {
            //get item's stock details
            //get and check number of batches
            StockBean sb = new StockBean();
            Stock st = new Stock();
            st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), NewTransItem.getItemId(), NewTransItem.getBatchno());
            if (st == null || st.getCurrentqty() == 0) {//this item and batch does not exist in this store OR its quatity is 0 (out of stock)
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("SELECTED BIG ITEM and BATCH - DOES NOT EXIST or IS OUT OF STOCK !");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(1);
            } else if (NewTransItem.getItemQty() < 0) {
                //BACK
                //check if supplied qty + existing qty is more than total current stock qty
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("QUANTITY CANNOT BE NEGATIVE...");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(1);
            } else if ((NewTransItem.getItemQty() + this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno())) > st.getCurrentqty()) {
                //BACK
                //check if supplied qty + existing qty is more than total current stock qty
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("SELECTED BIG ITEM and BATCH - INSUFFICIENT STOCK !");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(1);
            } else if ("UNPACK".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && (NewTransItem.getItemId2() == 0 || NewTransItem.getItemQty2() == 0)) {
                //BACK
                //check if supplied qty + existing qty is more than total current stock qty
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("SELECTED ITEM/QUANTITY TO UNPACK TO IS INVALID !");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(1);
            } else {
                TransItem ti = new TransItem();
                ti.setTransactionItemId(NewTransItem.getTransactionItemId());
                ti.setTransactionId(NewTransItem.getTransactionId());
                ti.setItemId(NewTransItem.getItemId());
                ti.setBatchno(NewTransItem.getBatchno());
                ti.setItemQty(NewTransItem.getItemQty());
                ti.setUnitPrice(NewTransItem.getUnitPrice());
                ti.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount());
                ti.setAmount(NewTransItem.getAmount());
                ti.setVatRated(NewTransItem.getVatRated());
                //for UNPACK
                ti.setItemId2(NewTransItem.getItemId2());
                ti.setItemQty2(NewTransItem.getItemQty2());

                ti.setItemExpryDate(st.getItemExpDate());
                ti.setItemMnfDate(st.getItemMnfDate());

                //for profit margin
                ti.setUnitCostPrice(0);
                ti.setUnitProfitMargin(0);

                aActiveTransItems.add(0, ti);
                TransBean transB = new TransBean();
                transB.saveTrans(NewTrans, aActiveTransItems, null, null, null, null, null);
                transB.clearAll(NewTrans, null, NewTransItem, aItem, null, 1);

                aStatusBean.setItemAddedStatus("ITEM ADDED");
                aStatusBean.setItemNotAddedStatus("");
                aStatusBean.setShowItemAddedStatus(1);
                aStatusBean.setShowItemNotAddedStatus(0);
            }
        }
    }

    public int autoUnpackItem(TransItem aSmallTransItem, float aSmallItemQtyNeeded) {
        long aBigItemQtyToUnpack = 0;
        try {
            //System.out.println("-A-");
            //1. check if Small Item has big item
            ItemMap aItemMap;
            try {
                aItemMap = new ItemMapBean().getItemMapBySmallItemId(aSmallTransItem.getItemId());
                aBigItemQtyToUnpack = Math.round(Math.ceil(aSmallItemQtyNeeded / aItemMap.getFractionQty()));
                //System.out.println("ATY=" + aBigItemQtyToUnpack);
                //System.out.println("-B-");
            } catch (NullPointerException npe) {
                //System.out.println("-C-");
                aBigItemQtyToUnpack = 0;
                aItemMap = null;
            }

            //2. check if needed Qty can be unpacked from the Big Item
            if (aItemMap != null & aSmallItemQtyNeeded > 0 & aBigItemQtyToUnpack > 0) {
                //check if at-least 1 qty is available in stock for the BigItem
                Stock aStock;
                aStock = new StockBean().getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), aItemMap.getBigItemId(), aSmallTransItem.getBatchno());
                if (aStock != null && aBigItemQtyToUnpack <= aStock.getCurrentqty()) {
                    //System.out.println("-D-");
                    //now that Qty is available, unpack it
                    TransItem ti = new TransItem();
                    ti.setItemId(aItemMap.getBigItemId());
                    ti.setBatchno(aSmallTransItem.getBatchno());
                    ti.setItemQty(aBigItemQtyToUnpack);
                    //for UNPACK
                    ti.setItemId2(aSmallTransItem.getItemId());
                    ti.setItemQty2(aBigItemQtyToUnpack * aItemMap.getFractionQty());

                    ti.setItemExpryDate(aStock.getItemExpDate());
                    ti.setItemMnfDate(aStock.getItemMnfDate());

                    //for profit margin
                    ti.setUnitCostPrice(0);
                    ti.setUnitProfitMargin(0);

                    TransBean transB = new TransBean();
                    transB.saveTransAutoUnpack(ti);
                    return 1;
                } else {
                    //System.out.println("-E-");
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            System.err.println("autoUnpackItem:" + e.getMessage());
            return 0;
        }
    }

    public void addTransItemPURCHASE(Trans aTrans, StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem NewTransItem, Item aSelectedItem) {
        if (aSelectedItem.getItemType().equals("SERVICE")) {
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("A SERVICE Item cannot be SUPPLIED!");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(1);
        } else if (NewTransItem.getItemQty() < 0 || NewTransItem.getUnitPrice() < 0) {
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("QUANTITY and UNIT PRICE CANNOT BE IN NEGATIVES!");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(1);
        } else if (NewTransItem.getItemId() == 0) {
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("SELECT A VALID ITEM...!");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(1);
        } else {
            TransItem ti = new TransItem();
            ti.setTransactionItemId(NewTransItem.getTransactionItemId());
            ti.setTransactionId(NewTransItem.getTransactionId());
            ti.setItemId(NewTransItem.getItemId());
            ti.setBatchno(NewTransItem.getBatchno());
            ti.setItemQty(NewTransItem.getItemQty());
            ti.setUnitPrice(NewTransItem.getUnitPrice());
            ti.setUnitTradeDiscount(NewTransItem.getUnitTradeDiscount());
            ti.setAmount(NewTransItem.getAmount());
            ti.setVatRated(NewTransItem.getVatRated());

            ti.setVatPerc(CompanySetting.getVatPerc());
            ti.setUnitVat(NewTransItem.getUnitVat());
            ti.setUnitPriceExcVat(NewTransItem.getUnitPrice());
            ti.setUnitPriceIncVat(NewTransItem.getUnitPrice() + NewTransItem.getUnitVat());

            ti.setItemExpryDate(NewTransItem.getItemExpryDate());
            ti.setItemMnfDate(NewTransItem.getItemMnfDate());
            ti.setAmountIncVat(NewTransItem.getAmount());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());

            //check if itme+batchno already exists
            int ItemFoundAtIndex = itemExists(aActiveTransItems, ti.getItemId(), ti.getBatchno());
            if (ItemFoundAtIndex == -1) {
                aActiveTransItems.add(0, ti);
            } else {
                ti.setItemQty(ti.getItemQty() + aActiveTransItems.get(ItemFoundAtIndex).getItemQty());
                ti.setAmount(ti.getAmount() + aActiveTransItems.get(ItemFoundAtIndex).getAmount());
                ti.setAmountIncVat(ti.getAmountIncVat() + aActiveTransItems.get(ItemFoundAtIndex).getAmountIncVat());
                ti.setAmountExcVat(ti.getAmountExcVat() + aActiveTransItems.get(ItemFoundAtIndex).getAmountExcVat());
                aActiveTransItems.add(ItemFoundAtIndex, ti);
                aActiveTransItems.remove(ItemFoundAtIndex + 1);
            }

            //for profit margin
            ti.setUnitCostPrice(0);
            ti.setUnitProfitMargin(0);

            TransBean transB = new TransBean();
            transB.clearAll(null, aActiveTransItems, NewTransItem, aSelectedItem, null, 1);

            aStatusBean.setItemAddedStatus("ITEM ADDED");
            aStatusBean.setItemNotAddedStatus("");
            aStatusBean.setShowItemAddedStatus(1);
            aStatusBean.setShowItemNotAddedStatus(0);

            //update totals
            new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
        }
    }

    public int itemExists(List<TransItem> aActiveTransItems, Long ItemIdent, String BatchNumb) {
        List<TransItem> ati = aActiveTransItems;
        int ItemFoundAtIndex = -1;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        float SubT = 0;
        while (ListItemIndex < ListItemNo) {
            if (ati.get(ListItemIndex).getItemId() == ItemIdent && BatchNumb.equals(ati.get(ListItemIndex).getBatchno())) {
                ItemFoundAtIndex = ListItemIndex;
                break;
            } else {
                ItemFoundAtIndex = -1;
            }
            ListItemIndex = ListItemIndex + 1;
        }
        return ItemFoundAtIndex;
    }

    public void removeTransItem(Trans aTrans, List<TransItem> aActiveTransItems, TransItem ti) {
        aActiveTransItems.remove(ti);

        //update totals
        new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
    }

    public float getListTotalItemBatchQty(List<TransItem> aActiveTransItems, Long ItemI, String BatchN) {
        List<TransItem> ati = aActiveTransItems;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        float TQty = 0;
        while (ListItemIndex < ListItemNo) {
            if (ati.get(ListItemIndex).getItemId() == ItemI && BatchN.equals(ati.get(ListItemIndex).getBatchno())) {
                TQty = TQty + ati.get(ListItemIndex).getItemQty();
            }
            ListItemIndex = ListItemIndex + 1;
        }
        return TQty;
    }

    public String getAnyItemTotalQtyGreaterThanCurrentQty(List<TransItem> aActiveTransItems, int aStoreId, String aTransactionType) {
        List<TransItem> ati = aActiveTransItems;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        float TQty = 0;
        this.setItemString("");
        while (ListItemIndex < ListItemNo && (aTransactionType.equals("SALE INVOICE") || aTransactionType.equals("TRANSFER") || aTransactionType.equals("DISPOSE"))) {
            this.setItemString(ati.get(ListItemIndex).getDescription() + " " + ati.get(ListItemIndex).getBatchno());
            if (this.isItemTotalQtyGreaterThanCurrentQty(aStoreId, ati.get(ListItemIndex).getItemId(), ati.get(ListItemIndex).getBatchno(), ati.get(ListItemIndex).getItemQty())) {
                //break
                this.setItemString(new ItemBean().findItem(ati.get(ListItemIndex).getItemId()).getDescription() + " " + ati.get(ListItemIndex).getBatchno());
                break;
            } else {
                this.setItemString("");
            }
            ListItemIndex = ListItemIndex + 1;
        }
        return this.getItemString();
    }

    public boolean isItemTotalQtyGreaterThanCurrentQty(int aStoreId, Long ItemI, String BatchN, float aItemQty) {
        //check item type
        Item i = new ItemBean().getItem(ItemI);
        if (i.getItemType().equals("SERVICE")) {
            return false;
        } else {
            //get item's stock details
            //get and check number of batches
            StockBean sb = new StockBean();
            Stock st = new Stock();
            float aCurrentQty = 0;
            st = sb.getStock(aStoreId, ItemI, BatchN);
            if (st != null) {
                aCurrentQty = st.getCurrentqty();
            }

            if (aItemQty <= aCurrentQty) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void clearTransItem(TransItem tri) {
        if (tri != null) {
            tri.setTransactionItemId(0);
            tri.setTransactionId(0);
            tri.setItemId(0);
            tri.setItemId2(0);
            tri.setBatchno("");
            tri.setItemQty(0);
            tri.setItemQty2(0);
            tri.setFractionQty(0);
            tri.setUnitPrice(0);
            tri.setAmount(0);
            tri.setItemExpryDate(null);
            tri.setItemMnfDate(null);
            tri.setUnitVat(0);
            tri.setUnitTradeDiscount(0);
            tri.setItemCode("");
            tri.setUnitPriceIncVat(0);
            tri.setUnitPriceExcVat(0);
            tri.setAmountIncVat(0);
            tri.setAmountExcVat(0);
            tri.setStockEffect("");
            tri.setUnitPrice2(0);
            tri.setUnitTradeDiscount2(0);
            tri.setVatRated2("");
            tri.setOverridePrices(false);
            tri.setUnitCostPrice(0);
            tri.setUnitProfitMargin(0);
            tri.setEarnPerc(0);
            tri.setEarnAmount(0);
            tri.setRemarks("");
        }
    }

    public void clearTransItem2(TransItem tri) {
        if (tri != null) {
            tri.setTransactionItemId(0);
            tri.setTransactionId(0);
            tri.setItemId(0);
            tri.setItemId2(0);
            tri.setBatchno("");
            tri.setItemQty(0);
            tri.setItemQty2(0);
            tri.setFractionQty(0);
            tri.setUnitPrice(0);
            tri.setAmount(0);
            tri.setItemExpryDate(null);
            tri.setItemMnfDate(null);
            tri.setUnitVat(0);
            tri.setUnitTradeDiscount(0);
            tri.setItemCode("");
            tri.setUnitPriceIncVat(0);
            tri.setUnitPriceExcVat(0);
            tri.setAmountIncVat(0);
            tri.setAmountExcVat(0);
            tri.setStockEffect("");
            tri.setUnitPrice2(0);
            tri.setUnitTradeDiscount2(0);
            tri.setVatRated2("");
            tri.setOverridePrices(false);
            tri.setUnitCostPrice(0);
            tri.setUnitProfitMargin(0);
            tri.setEarnPerc(0);
            tri.setEarnAmount(0);
            tri.setRemarks("");
        }
    }

    /**
     * @return the ItemString
     */
    public String getItemString() {
        return ItemString;
    }

    /**
     * @param ItemString the ItemString to set
     */
    public void setItemString(String ItemString) {
        this.ItemString = ItemString;
    }

    /**
     * @return the ActiveTransItems
     */
    public List<TransItem> getActiveTransItems() {
        return ActiveTransItems;
    }

    /**
     * @param ActiveTransItems the ActiveTransItems to set
     */
    public void setActiveTransItems(List<TransItem> ActiveTransItems) {
        this.ActiveTransItems = ActiveTransItems;
    }

    /**
     * @param TransItems the TransItems to set
     */
    public void setTransItems(List<TransItem> TransItems) {
        this.TransItems = TransItems;
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
     * @return the SelectedTransItem
     */
    public TransItem getSelectedTransItem() {
        return SelectedTransItem;
    }

    /**
     * @param SelectedTransItem the SelectedTransItem to set
     */
    public void setSelectedTransItem(TransItem SelectedTransItem) {
        this.SelectedTransItem = SelectedTransItem;
    }

    /**
     * @return the SelectedTransactionItemId
     */
    public int getSelectedTransactionItemId() {
        return SelectedTransactionItemId;
    }

    /**
     * @param SelectedTransactionItemId the SelectedTransactionItemId to set
     */
    public void setSelectedTransactionItemId(int SelectedTransactionItemId) {
        this.SelectedTransactionItemId = SelectedTransactionItemId;
    }

    /**
     * @return the SearchTransItem
     */
    public String getSearchTransItem() {
        return SearchTransItem;
    }

    /**
     * @param SearchTransItem the SearchTransItem to set
     */
    public void setSearchTransItem(String SearchTransItem) {
        this.SearchTransItem = SearchTransItem;
    }

    public void updateModelTransItem(Trans aTrans, TransItem aTransItemToUpdate, StatusBean aStatusBean, List<TransItem> aActiveTransItems, Item i, int auto) {//auto=1 for itemCode, auto=0 is for desc/code    ,2 is for other
        aStatusBean.setItemAddedStatus("");
        aStatusBean.setItemNotAddedStatus("");
        aStatusBean.setShowItemAddedStatus(0);
        aStatusBean.setShowItemNotAddedStatus(0);

        StockBean sb = new StockBean();
        List<Stock> batches = new ArrayList<Stock>();
        DiscountPackageItem dpi = null;

        if (i == null) {
            aTransItemToUpdate.setItemId(0);
            aTransItemToUpdate.setUnitPrice(0);
            aTransItemToUpdate.setVatRated("");
            //aTransItemToUpdate.setItemQty(0);
            aTransItemToUpdate.setAmount(0);
            aTransItemToUpdate.setItemCode("");
            new ItemBean().clearItem(i);
            aTransItemToUpdate.setUnitPrice2(0);
            aTransItemToUpdate.setUnitTradeDiscount(0);
            aTransItemToUpdate.setUnitTradeDiscount2(0);
            aTransItemToUpdate.setVatRated2("");
        } else {
            aTransItemToUpdate.setItemId(i.getItemId());
            aTransItemToUpdate.setIsTradeDiscountVatLiable(CompanySetting.getIsTradeDiscountVatLiable());
            if (new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE ORDER") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE QUOTATION") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("RESERVATION")) {
                dpi = new DiscountPackageItemBean().getActiveDiscountPackageItem(new GeneralUserSetting().getCurrentStore().getStoreId(), i.getItemId(), 1);
            } else {
                dpi = null;
            }

            if ("EXEMPT SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aTransItemToUpdate.setUnitPrice(0);
                aTransItemToUpdate.setUnitTradeDiscount(0);
            } else if ("COST-PRICE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aTransItemToUpdate.setUnitPrice(i.getUnitCostPrice());
                aTransItemToUpdate.setUnitTradeDiscount(0);
            } else if ("WHOLE SALE QUOTATION".equals(new GeneralUserSetting().getCurrentSaleType()) || "WHOLE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aTransItemToUpdate.setUnitPrice(i.getUnitWholesalePrice());
                if (dpi != null) {
                    aTransItemToUpdate.setUnitTradeDiscount(dpi.getWholesaleDiscountAmt());
                }
            } else {
                if (new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE ORDER") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE QUOTATION") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("PURCHASE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("DISPOSE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("RESERVATION")) {
                    aTransItemToUpdate.setUnitPrice(i.getUnitRetailsalePrice());
                } else {
                    aTransItemToUpdate.setUnitPrice(0);
                }
                if (dpi != null) {
                    aTransItemToUpdate.setUnitTradeDiscount(dpi.getRetailsaleDiscountAmt());
                }
            }
            aTransItemToUpdate.setVatRated(i.getVatRated());
            aTransItemToUpdate.setItemCode(i.getItemCode());

            //??override/change UnitPrice  for all transactions
            //??override/change Trade.Discount for all transactions
            //??override/change Vat.Rated for all transactions
            //NOTE: all overide can be done when addition mode is notne automatic; see them below
            if (auto == 0) {
                aTransItemToUpdate.setUnitPrice2(aTransItemToUpdate.getUnitPrice());
                aTransItemToUpdate.setUnitTradeDiscount2(aTransItemToUpdate.getUnitTradeDiscount());
                aTransItemToUpdate.setVatRated2(aTransItemToUpdate.getVatRated());
                //just to help by autofilling
                //aTransItemToUpdate.setItemQty(1);
                aTransItemToUpdate.setAmount(aTransItemToUpdate.getItemQty() * aTransItemToUpdate.getUnitPrice());
            } else if (auto == 1) {//item entered through barcode
                batches = sb.getStocks(new GeneralUserSetting().getCurrentStore().getStoreId(), i.getItemId());
                if (batches.size() == 1) {
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(0);
                    //update model
                    //aTransItemToUpdate.setItemQty(1);
                    aTransItemToUpdate.setAmount(aTransItemToUpdate.getItemQty() * aTransItemToUpdate.getUnitPrice());
                    aTransItemToUpdate.setBatchno(batches.get(0).getBatchno());
                    //Add Item to the list
                    this.addTransItem(aTrans, aStatusBean, aActiveTransItems, aTransItemToUpdate, i);
                } else {//if batches are many OR item has zero current quantity
                    aTransItemToUpdate.setItemQty(0);
                    aTransItemToUpdate.setAmount(0);
                    if (batches.size() > 1) {
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("PLEASE SELECT BATCH No");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                    } else if (batches.size() <= 0) {
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("ITEM IS OUT OF STOCK");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                    }

                }
            }
        }
    }

    public void updateModelTransItem_Group_Checkin(Trans aTrans, TransItem aTransItemToUpdate, StatusBean aStatusBean, List<TransItem> aActiveTransItems, Item i, int auto, int numberOfPersons) {//auto=1 for itemCode, auto=0 is for desc/code    ,2 is for other
        aStatusBean.setItemAddedStatus("");
        aStatusBean.setItemNotAddedStatus("");
        aStatusBean.setShowItemAddedStatus(0);
        aStatusBean.setShowItemNotAddedStatus(0);

        StockBean sb = new StockBean();
        List<Stock> batches = new ArrayList<Stock>();
        DiscountPackageItem dpi = null;

        if (i == null) {
            aTransItemToUpdate.setItemId(0);
            aTransItemToUpdate.setUnitPrice(0);
            aTransItemToUpdate.setVatRated("");
            //aTransItemToUpdate.setItemQty(0);
            aTransItemToUpdate.setAmount(0);
            aTransItemToUpdate.setItemCode("");
            new ItemBean().clearItem(i);
            aTransItemToUpdate.setUnitPrice2(0);
            aTransItemToUpdate.setUnitTradeDiscount(0);
            aTransItemToUpdate.setUnitTradeDiscount2(0);
            aTransItemToUpdate.setVatRated2("");
        } else {
            aTransItemToUpdate.setItemId(i.getItemId());
            aTransItemToUpdate.setIsTradeDiscountVatLiable(CompanySetting.getIsTradeDiscountVatLiable());
            if (new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE ORDER") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE QUOTATION") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("RESERVATION")) {
                dpi = new DiscountPackageItemBean().getActiveDiscountPackageItem(new GeneralUserSetting().getCurrentStore().getStoreId(), i.getItemId(), 1);
            } else {
                dpi = null;
            }

            if ("EXEMPT SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aTransItemToUpdate.setUnitPrice(0);
                aTransItemToUpdate.setUnitTradeDiscount(0);
            } else if ("COST-PRICE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aTransItemToUpdate.setUnitPrice(i.getUnitCostPrice());
                aTransItemToUpdate.setUnitTradeDiscount(0);
            } else if ("WHOLE SALE QUOTATION".equals(new GeneralUserSetting().getCurrentSaleType()) || "WHOLE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aTransItemToUpdate.setUnitPrice(i.getUnitWholesalePrice());
                if (dpi != null) {
                    aTransItemToUpdate.setUnitTradeDiscount(dpi.getWholesaleDiscountAmt());
                }
            } else {
                if (new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE ORDER") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE QUOTATION") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("PURCHASE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("DISPOSE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("RESERVATION")) {
                    aTransItemToUpdate.setUnitPrice(i.getUnitRetailsalePrice());
                } else {
                    aTransItemToUpdate.setUnitPrice(0);
                }
                if (dpi != null) {
                    aTransItemToUpdate.setUnitTradeDiscount(dpi.getRetailsaleDiscountAmt());
                }
            }
            aTransItemToUpdate.setVatRated(i.getVatRated());
            aTransItemToUpdate.setItemCode(i.getItemCode());

            //??override/change UnitPrice  for all transactions
            //??override/change Trade.Discount for all transactions
            //??override/change Vat.Rated for all transactions
            //NOTE: all overide can be done when addition mode is notne automatic; see them below
            if (auto == 0) {
                aTransItemToUpdate.setUnitPrice2(aTransItemToUpdate.getUnitPrice());
                aTransItemToUpdate.setUnitTradeDiscount2(aTransItemToUpdate.getUnitTradeDiscount());
                aTransItemToUpdate.setVatRated2(aTransItemToUpdate.getVatRated());
                //just to help by autofilling
                //aTransItemToUpdate.setItemQty(1);
                aTransItemToUpdate.setAmount(aTransItemToUpdate.getItemQty() * aTransItemToUpdate.getUnitPrice());
            } else if (auto == 1) {//item entered through barcode
                batches = sb.getStocks(new GeneralUserSetting().getCurrentStore().getStoreId(), i.getItemId());
                if (batches.size() == 1) {
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(0);
                    //update model
                    //aTransItemToUpdate.setItemQty(1);
                    aTransItemToUpdate.setAmount(aTransItemToUpdate.getItemQty() * aTransItemToUpdate.getUnitPrice());
                    aTransItemToUpdate.setBatchno(batches.get(0).getBatchno());
                    //Add Item to the list
                    this.addTransItem(aTrans, aStatusBean, aActiveTransItems, aTransItemToUpdate, i);
                } else {//if batches are many OR item has zero current quantity
                    aTransItemToUpdate.setItemQty(0);
                    aTransItemToUpdate.setAmount(0);
                    if (batches.size() > 1) {
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("PLEASE SELECT BATCH No");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                    } else if (batches.size() <= 0) {
                        aStatusBean.setItemAddedStatus("");
                        aStatusBean.setItemNotAddedStatus("ITEM IS OUT OF STOCK");
                        aStatusBean.setShowItemAddedStatus(0);
                        aStatusBean.setShowItemNotAddedStatus(1);
                    }

                }
            }
        }
    }

    public void updateModelTransItemBarCode(Trans aTrans, TransItem aTransItemToUpdate, StatusBean aStatusBean, List<TransItem> aActiveTransItems, TransItem aSelectedTransItem, Item aSelectedItem) {//auto=1 for itemCode, auto=0 is for desc/code    ,2 is for other
        aStatusBean.setItemAddedStatus("");
        aStatusBean.setItemNotAddedStatus("");
        aStatusBean.setShowItemAddedStatus(0);
        aStatusBean.setShowItemNotAddedStatus(0);

        StockBean sb = new StockBean();
        List<Stock> batches = new ArrayList<Stock>();
        //TransItemBean tib = this;
        DiscountPackageItem dpi = null;

        try {
            aSelectedItem = new ItemBean().findItemByCodeActive(aSelectedTransItem.getItemCode());
        } catch (NullPointerException npe) {
            aSelectedItem = null;
        }

        if (aSelectedItem == null) {
            aStatusBean.setItemAddedStatus("");
            aStatusBean.setItemNotAddedStatus("ENTERED BARCODE NUMBER DOES NOT MATCH WITH ANY ITEM REGISTERED");
            aStatusBean.setShowItemAddedStatus(0);
            aStatusBean.setShowItemNotAddedStatus(1);
            new ItemBean().clearItem(aSelectedItem);
            this.clearTransItem2(aSelectedTransItem);
        } else if (aSelectedItem != null) {
            aSelectedTransItem.setItemId(aSelectedItem.getItemId());
            aSelectedTransItem.setItemQty(1);
            aSelectedTransItem.setIsTradeDiscountVatLiable(CompanySetting.getIsTradeDiscountVatLiable());
            if (new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE")) {
                dpi = new DiscountPackageItemBean().getActiveDiscountPackageItem(new GeneralUserSetting().getCurrentStore().getStoreId(), aSelectedItem.getItemId(), aSelectedTransItem.getItemQty());
            } else {
                dpi = null;
            }

            if ("EXEMPT SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aSelectedTransItem.setUnitPrice(0);
                aSelectedTransItem.setUnitTradeDiscount(0);
            } else if ("COST-PRICE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aSelectedTransItem.setUnitPrice(aSelectedItem.getUnitCostPrice());
                aSelectedTransItem.setUnitTradeDiscount(0);
            } else if ("WHOLE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                aSelectedTransItem.setUnitPrice(aSelectedItem.getUnitWholesalePrice());
                if (dpi != null) {
                    aSelectedTransItem.setUnitTradeDiscount(dpi.getWholesaleDiscountAmt());
                }
            } else {
                if (new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("PURCHASE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("DISPOSE")) {
                    aSelectedTransItem.setUnitPrice(aSelectedItem.getUnitRetailsalePrice());
                } else {
                    aSelectedTransItem.setUnitPrice(0);
                }
                if (dpi != null) {
                    aSelectedTransItem.setUnitTradeDiscount(dpi.getRetailsaleDiscountAmt());
                }
            }
            aSelectedTransItem.setVatRated(aSelectedItem.getVatRated());
            aSelectedTransItem.setItemCode(aSelectedItem.getItemCode());

            batches = sb.getStocks(new GeneralUserSetting().getCurrentStore().getStoreId(), aSelectedItem.getItemId());
            if (batches.size() == 1) {
                aStatusBean.setItemAddedStatus("");
                aStatusBean.setItemNotAddedStatus("");
                aStatusBean.setShowItemAddedStatus(0);
                aStatusBean.setShowItemNotAddedStatus(0);
                //update model
                aSelectedTransItem.setItemQty(1);
                aSelectedTransItem.setAmount(aSelectedTransItem.getItemQty() * aSelectedTransItem.getUnitPrice());
                aSelectedTransItem.setBatchno(batches.get(0).getBatchno());
                //Add Item to the list

                //get item's stock details
                Stock st = new Stock();
                st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), aSelectedTransItem.getItemId(), aSelectedTransItem.getBatchno());

                //unpacking - AutoUnPackModule
                //process items that might need to be unpacked
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) & "Yes".equals(CompanySetting.getIsAllowAutoUnpack())) {
                    TransItemBean autoTIB = this;
                    float TotalQtyFromList = 0;
                    try {
                        TotalQtyFromList = autoTIB.getListTotalItemBatchQty(aActiveTransItems, aSelectedTransItem.getItemId(), aSelectedTransItem.getBatchno());
                    } catch (NullPointerException npe) {
                        TotalQtyFromList = 0;
                    }
                    float TotalQtyFromListAndNew = TotalQtyFromList + aSelectedTransItem.getItemQty();

                    float SmallItemQtyNeeded = 0;
                    try {
                        SmallItemQtyNeeded = TotalQtyFromListAndNew - st.getCurrentqty();
                    } catch (NullPointerException npe) {
                        SmallItemQtyNeeded = TotalQtyFromListAndNew;
                    }
                    //Unpackaging if needed
                    if (st == null || st.getCurrentqty() == 0) {//this item and batch does not exist in this store OR its quatity is 0 (out of stock)
                        if (autoTIB.autoUnpackItem(aSelectedTransItem, SmallItemQtyNeeded) == 1) {
                        } else {
                        }
                    } else if ((aSelectedTransItem.getItemQty() + autoTIB.getListTotalItemBatchQty(aActiveTransItems, aSelectedTransItem.getItemId(), aSelectedTransItem.getBatchno())) > st.getCurrentqty()) {
                        //float TotalQtyFromList=this.getListTotalItemBatchQty(aActiveTransItems, NewTransItem.getItemId(), NewTransItem.getBatchno());
                        if (autoTIB.autoUnpackItem(aSelectedTransItem, SmallItemQtyNeeded) == 1) {
                        } else {
                        }
                    }
                    //re-calculate stock for small item after unpackaing
                    st = sb.getStock(new GeneralUserSetting().getCurrentStore().getStoreId(), aSelectedTransItem.getItemId(), aSelectedTransItem.getBatchno());
                }
                if (st == null || st.getCurrentqty() == 0) {//aSelectedTransItem item and batch does not exist in aSelectedTransItem store OR its quatity is 0 (out of stock)
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("(" + aSelectedItem.getDescription() + ") " + "SELECTED ITEM - DOES NOT EXIST or IS OUT OF STOCK !");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(1);
                    new ItemBean().clearItem(aSelectedItem);
                    this.clearTransItem2(aSelectedTransItem);
                } else if ((aSelectedTransItem.getItemQty() + this.getListTotalItemBatchQty(aActiveTransItems, aSelectedTransItem.getItemId(), aSelectedTransItem.getBatchno())) > st.getCurrentqty()) {
                    //BACK
                    //check if supplied qty + existing qty is more than total current stock qty
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("(" + aSelectedItem.getDescription() + ") " + "SELECTED ITEM and BATCH - INSUFFICIENT STOCK !");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(1);
                    new ItemBean().clearItem(aSelectedItem);
                    this.clearTransItem2(aSelectedTransItem);
                } else {
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(0);
                    this.addTransItemBarCode(aTrans, aStatusBean, aActiveTransItems, aSelectedTransItem, aSelectedItem);
                }
            } else {//if batches are many OR item has zero current quantity
                aSelectedTransItem.setItemQty(0);
                aSelectedTransItem.setAmount(0);
                if (batches.size() > 1) {
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("(" + aSelectedItem.getDescription() + ") " + "ITEM HAS BATCHES, ENTER ITEM MANUALLY AND SELECT BATCH No");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(1);
                    new ItemBean().clearItem(aSelectedItem);
                    this.clearTransItem2(aSelectedTransItem);
                } else if (batches.size() <= 0) {
                    aStatusBean.setItemAddedStatus("");
                    aStatusBean.setItemNotAddedStatus("(" + aSelectedItem.getDescription() + ") " + "ITEM IS OUT OF STOCK");
                    aStatusBean.setShowItemAddedStatus(0);
                    aStatusBean.setShowItemNotAddedStatus(1);
                    new ItemBean().clearItem(aSelectedItem);
                    this.clearTransItem2(aSelectedTransItem);
                }
            }
        }
    }

    public void calUnpackedQty(TransItem aTransItem) {
        try {
            if (aTransItem.getItemId() != 0 && aTransItem.getItemId2() != 0) {
                aTransItem.setItemQty2((new ItemMapBean().getItemMapByBigItemId(aTransItem.getItemId()).getFractionQty()) * aTransItem.getItemQty());
            } else {
                aTransItem.setItemQty2(0);
            }
        } catch (Exception e) {
            aTransItem.setItemQty2(0);
        }
    }

}
