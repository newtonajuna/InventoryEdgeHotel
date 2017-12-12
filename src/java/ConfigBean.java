/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class ConfigBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private String ActionMessage;
    private Config SelectedConfig;
    
    public void initSelectedConfig(){
        this.setSelectedConfig(this.getConfigFile());
    }
    
    public void updateConfigFile(Config aConfig) {
        String workingDir = System.getProperty("user.dir");
        String workingDir2=this.getClass().getClassLoader().getResource("ConfigFile.properties").getPath();
        try {
            FileInputStream in = new FileInputStream(workingDir2);
            Properties props = new Properties();
            props.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream(workingDir2);
            props.setProperty("branch_host", aConfig.getBranch_host());
            props.setProperty("branch_database", aConfig.getBranch_database());
            props.setProperty("branch_user", aConfig.getBranch_user());
            props.setProperty("branch_password", aConfig.getBranch_password());
            props.setProperty("inter_branch_host", aConfig.getInter_branch_host());
            props.setProperty("inter_branch_database", aConfig.getInter_branch_database());
            props.setProperty("inter_branch_user", aConfig.getInter_branch_user());
            props.setProperty("inter_branch_password", aConfig.getInter_branch_password());
            props.setProperty("license_key", aConfig.getLicense_key());
            props.store(out, null);
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(ConfigBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Config getConfigFile() {
        ResourceBundle properties = ResourceBundle.getBundle("ConfigFile");
        Config NewConfig = new Config();

        NewConfig.setBranch_host(properties.getString("branch_host"));
        NewConfig.setBranch_database(properties.getString("branch_database"));
        NewConfig.setBranch_user(properties.getString("branch_user"));
        NewConfig.setBranch_password(properties.getString("branch_password"));

        NewConfig.setInter_branch_host(properties.getString("inter_branch_host"));
        NewConfig.setInter_branch_database(properties.getString("inter_branch_database"));
        NewConfig.setInter_branch_user(properties.getString("inter_branch_user"));
        NewConfig.setInter_branch_password(properties.getString("inter_branch_password"));

        NewConfig.setLicense_key(properties.getString("license_key"));

        return NewConfig;
    }
    
    public void getConfigFileToConfig(Config NewConfig) {
        ResourceBundle properties = ResourceBundle.getBundle("ConfigFile");

        NewConfig.setBranch_host(properties.getString("branch_host"));
        NewConfig.setBranch_database(properties.getString("branch_database"));
        NewConfig.setBranch_user(properties.getString("branch_user"));
        NewConfig.setBranch_password(properties.getString("branch_password"));

        NewConfig.setInter_branch_host(properties.getString("inter_branch_host"));
        NewConfig.setInter_branch_database(properties.getString("inter_branch_database"));
        NewConfig.setInter_branch_user(properties.getString("inter_branch_user"));
        NewConfig.setInter_branch_password(properties.getString("inter_branch_password"));

        NewConfig.setLicense_key(properties.getString("license_key"));
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
     * @return the SelectedConfig
     */
    public Config getSelectedConfig() {
        return SelectedConfig;
    }

    /**
     * @param SelectedConfig the SelectedConfig to set
     */
    public void setSelectedConfig(Config SelectedConfig) {
        this.SelectedConfig = SelectedConfig;
    }

}
