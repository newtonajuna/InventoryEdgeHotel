/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String branch_host;
    private String branch_database;
    private String branch_user;
    private String branch_password;
    private String inter_branch_host;
    private String inter_branch_database;
    private String inter_branch_user;
    private String inter_branch_password;
    private String license_key;
    private String location;

    /**
     * Creates a new instance of Config
     */
    public Config() {
    }

    /**
     * @return the branch_host
     */
    public String getBranch_host() {
        return branch_host;
    }

    /**
     * @param branch_host the branch_host to set
     */
    public void setBranch_host(String branch_host) {
        this.branch_host = branch_host;
    }

    /**
     * @return the branch_database
     */
    public String getBranch_database() {
        return branch_database;
    }

    /**
     * @param branch_database the branch_database to set
     */
    public void setBranch_database(String branch_database) {
        this.branch_database = branch_database;
    }

    /**
     * @return the branch_user
     */
    public String getBranch_user() {
        return branch_user;
    }

    /**
     * @param branch_user the branch_user to set
     */
    public void setBranch_user(String branch_user) {
        this.branch_user = branch_user;
    }

    /**
     * @return the branch_password
     */
    public String getBranch_password() {
        return branch_password;
    }

    /**
     * @param branch_password the branch_password to set
     */
    public void setBranch_password(String branch_password) {
        this.branch_password = branch_password;
    }

    /**
     * @return the inter_branch_host
     */
    public String getInter_branch_host() {
        return inter_branch_host;
    }

    /**
     * @param inter_branch_host the inter_branch_host to set
     */
    public void setInter_branch_host(String inter_branch_host) {
        this.inter_branch_host = inter_branch_host;
    }

    /**
     * @return the inter_branch_database
     */
    public String getInter_branch_database() {
        return inter_branch_database;
    }

    /**
     * @param inter_branch_database the inter_branch_database to set
     */
    public void setInter_branch_database(String inter_branch_database) {
        this.inter_branch_database = inter_branch_database;
    }

    /**
     * @return the inter_branch_user
     */
    public String getInter_branch_user() {
        return inter_branch_user;
    }

    /**
     * @param inter_branch_user the inter_branch_user to set
     */
    public void setInter_branch_user(String inter_branch_user) {
        this.inter_branch_user = inter_branch_user;
    }

    /**
     * @return the inter_branch_password
     */
    public String getInter_branch_password() {
        return inter_branch_password;
    }

    /**
     * @param inter_branch_password the inter_branch_password to set
     */
    public void setInter_branch_password(String inter_branch_password) {
        this.inter_branch_password = inter_branch_password;
    }

    /**
     * @return the license_key
     */
    public String getLicense_key() {
        return license_key;
    }

    /**
     * @param license_key the license_key to set
     */
    public void setLicense_key(String license_key) {
        this.license_key = license_key;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

}
