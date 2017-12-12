import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class DBConnection implements Serializable {

    private static final long serialVersionUID = 1L;

    private static String MySQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String MySQL_DB_HOST_IP_OR_NAME = "";
    private static String MySQL_DB_NAME = "";
    private static String MySQL_DB_URL = "";
    private static String MySQL_USER = "";
    private static String MySQL_PASSWORD = "";
    private static Connection MySQL_Conn;

    private static String INTER_BRANCH_MySQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String INTER_BRANCH_MySQL_DB_HOST_IP_OR_NAME = "";
    private static String INTER_BRANCH_MySQL_DB_NAME = "";
    private static String INTER_BRANCH_MySQL_DB_URL = "";
    private static String INTER_BRANCH_MySQL_USER = "";
    private static String INTER_BRANCH_MySQL_PASSWORD = "";
    private static Connection INTER_BRANCH_MySQL_Conn;

    private static String PASSWORDS_ARE_ENCRYPTED;
    private static String BRANCH_PASSWORD_CODE;
    private static String INTER_BRANCH_PASSWORD_CODE;

    public static void readConnectionConfigurations(String aConfigFile) throws FileNotFoundException {
        ResourceBundle properties = ResourceBundle.getBundle(aConfigFile);

        DBConnection.PASSWORDS_ARE_ENCRYPTED = properties.getString("passwords_are_encrypted");

        DBConnection.MySQL_DB_HOST_IP_OR_NAME = properties.getString("branch_host");
        DBConnection.MySQL_DB_NAME = properties.getString("branch_database");
        DBConnection.MySQL_USER = properties.getString("branch_user");
        if (DBConnection.PASSWORDS_ARE_ENCRYPTED.equals("Yes")) {
            DBConnection.BRANCH_PASSWORD_CODE = properties.getString("branch_password");
            DBConnection.MySQL_PASSWORD = Security.Decrypt(DBConnection.BRANCH_PASSWORD_CODE);
        } else {
            DBConnection.BRANCH_PASSWORD_CODE = Security.Encrypt(properties.getString("branch_password"));
            DBConnection.MySQL_PASSWORD = properties.getString("branch_password");
        }

        DBConnection.INTER_BRANCH_MySQL_DB_HOST_IP_OR_NAME = properties.getString("inter_branch_host");
        DBConnection.INTER_BRANCH_MySQL_DB_NAME = properties.getString("inter_branch_database");
        DBConnection.INTER_BRANCH_MySQL_USER = properties.getString("inter_branch_user");
        if (DBConnection.PASSWORDS_ARE_ENCRYPTED.equals("Yes")) {
            DBConnection.INTER_BRANCH_PASSWORD_CODE = properties.getString("inter_branch_password");
            DBConnection.INTER_BRANCH_MySQL_PASSWORD = Security.Decrypt(DBConnection.INTER_BRANCH_PASSWORD_CODE);
        } else {
            DBConnection.INTER_BRANCH_PASSWORD_CODE = Security.Encrypt(properties.getString("inter_branch_password"));
            DBConnection.INTER_BRANCH_MySQL_PASSWORD = properties.getString("inter_branch_password");
        }

        //refresh co stgs
        CompanySetting.RefreshStaticCompanySettings();
    }

    public static void readConnectionConfigurationsClient(String aConfigFile) throws FileNotFoundException {
        FileInputStream in = new FileInputStream(aConfigFile);
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        DBConnection.PASSWORDS_ARE_ENCRYPTED = properties.getProperty("passwords_are_encrypted", "passwords_are_encrypted");

        DBConnection.MySQL_DB_HOST_IP_OR_NAME = properties.getProperty("branch_host", "branch_host");
        DBConnection.MySQL_DB_NAME = properties.getProperty("branch_database", "branch_database");
        DBConnection.MySQL_USER = properties.getProperty("branch_user", "branch_user");
        DBConnection.MySQL_PASSWORD = properties.getProperty("branch_password", "branch_password");
        if (DBConnection.PASSWORDS_ARE_ENCRYPTED.equals("Yes")) {
            DBConnection.BRANCH_PASSWORD_CODE = properties.getProperty("branch_password", "branch_password");
            DBConnection.MySQL_PASSWORD = Security.Decrypt(DBConnection.BRANCH_PASSWORD_CODE);
        } else {
            DBConnection.BRANCH_PASSWORD_CODE = Security.Encrypt(properties.getProperty("branch_password", "branch_password"));
            DBConnection.MySQL_PASSWORD = properties.getProperty("branch_password", "branch_password");
        }

        DBConnection.INTER_BRANCH_MySQL_DB_HOST_IP_OR_NAME = properties.getProperty("inter_branch_host", "inter_branch_host");
        DBConnection.INTER_BRANCH_MySQL_DB_NAME = properties.getProperty("inter_branch_database", "inter_branch_database");
        DBConnection.INTER_BRANCH_MySQL_USER = properties.getProperty("inter_branch_user", "inter_branch_user");
        DBConnection.INTER_BRANCH_MySQL_PASSWORD = properties.getProperty("inter_branch_password", "inter_branch_password");
        if (DBConnection.PASSWORDS_ARE_ENCRYPTED.equals("Yes")) {
            DBConnection.INTER_BRANCH_PASSWORD_CODE = properties.getProperty("inter_branch_password", "inter_branch_password");
            DBConnection.INTER_BRANCH_MySQL_PASSWORD = Security.Decrypt(DBConnection.INTER_BRANCH_PASSWORD_CODE);
        } else {
            DBConnection.INTER_BRANCH_PASSWORD_CODE = Security.Encrypt(properties.getProperty("inter_branch_password", "inter_branch_password"));
            DBConnection.INTER_BRANCH_MySQL_PASSWORD = properties.getProperty("inter_branch_password", "inter_branch_password");
        }

        //refresh co stgs
        CompanySetting.RefreshStaticCompanySettings();
    }

    public static void readConnectionConfigurationsOld() {
        try (
                BufferedReader br = new BufferedReader(new FileReader("C:\\configuration.txt"))) {
            String sCurrentLine;
            int lineno = 0;
            while ((sCurrentLine = br.readLine()) != null) {
                lineno = lineno + 1;
                //System.out.println(lineno + ":" + sCurrentLine);
                switch (lineno) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        DBConnection.MySQL_DB_HOST_IP_OR_NAME = sCurrentLine;
                        break;
                    case 4:
                        DBConnection.MySQL_DB_NAME = sCurrentLine;
                        break;
                    case 5:
                        DBConnection.MySQL_USER = sCurrentLine;
                        break;
                    case 6:
                        DBConnection.MySQL_PASSWORD = sCurrentLine;
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        DBConnection.INTER_BRANCH_MySQL_DB_HOST_IP_OR_NAME = sCurrentLine;
                        break;
                    case 11:
                        DBConnection.INTER_BRANCH_MySQL_DB_NAME = sCurrentLine;
                        break;
                    case 12:
                        DBConnection.INTER_BRANCH_MySQL_USER = sCurrentLine;
                        break;
                    case 13:
                        DBConnection.INTER_BRANCH_MySQL_PASSWORD = sCurrentLine;
                        break;
                    case 14://;
                        break;
                    case 15://;
                        break;
                    case 16://;
                        break;
                    case 17:
                        CompanySetting.setLICENSE_KEY(Security.Decrypt(sCurrentLine));
                        break;
                    default:
                        break;
                }
                //refresh co stgs
                CompanySetting.RefreshStaticCompanySettings();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Connection getMySQLConnection() {
        try {
            Class.forName(MySQL_JDBC_DRIVER);
            DBConnection.MySQL_DB_URL = "jdbc:mysql://" + DBConnection.MySQL_DB_HOST_IP_OR_NAME + "/" + DBConnection.MySQL_DB_NAME;
            MySQL_Conn = DriverManager.getConnection(MySQL_DB_URL, MySQL_USER, MySQL_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
        }
        return MySQL_Conn;
    }

    public static Connection getINTER_BRANCH_MySQLConnection() {
        try {
            Class.forName(INTER_BRANCH_MySQL_JDBC_DRIVER);
            DBConnection.INTER_BRANCH_MySQL_DB_URL = "jdbc:mysql://" + DBConnection.INTER_BRANCH_MySQL_DB_HOST_IP_OR_NAME + "/" + DBConnection.INTER_BRANCH_MySQL_DB_NAME;
            INTER_BRANCH_MySQL_Conn = DriverManager.getConnection(INTER_BRANCH_MySQL_DB_URL, INTER_BRANCH_MySQL_USER, INTER_BRANCH_MySQL_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
        }
        return INTER_BRANCH_MySQL_Conn;
    }

    /**
     * @return the PASSWORDS_ARE_ENCRYPTED
     */
    public static String getPASSWORDS_ARE_ENCRYPTED() {
        return PASSWORDS_ARE_ENCRYPTED;
    }

    /**
     * @param aPASSWORDS_ARE_ENCRYPTED the PASSWORDS_ARE_ENCRYPTED to set
     */
    public static void setPASSWORDS_ARE_ENCRYPTED(String aPASSWORDS_ARE_ENCRYPTED) {
        PASSWORDS_ARE_ENCRYPTED = aPASSWORDS_ARE_ENCRYPTED;
    }

    /**
     * @return the BRANCH_PASSWORD_CODE
     */
    public static String getBRANCH_PASSWORD_CODE() {
        return BRANCH_PASSWORD_CODE;
    }

    /**
     * @param aBRANCH_PASSWORD_CODE the BRANCH_PASSWORD_CODE to set
     */
    public static void setBRANCH_PASSWORD_CODE(String aBRANCH_PASSWORD_CODE) {
        BRANCH_PASSWORD_CODE = aBRANCH_PASSWORD_CODE;
    }

    /**
     * @return the INTER_BRANCH_PASSWORD_CODE
     */
    public static String getINTER_BRANCH_PASSWORD_CODE() {
        return INTER_BRANCH_PASSWORD_CODE;
    }

    /**
     * @param aINTER_BRANCH_PASSWORD_CODE the INTER_BRANCH_PASSWORD_CODE to set
     */
    public static void setINTER_BRANCH_PASSWORD_CODE(String aINTER_BRANCH_PASSWORD_CODE) {
        INTER_BRANCH_PASSWORD_CODE = aINTER_BRANCH_PASSWORD_CODE;
    }

    public String isMySQLConnectionAvailable() {
        try {
            Class.forName(MySQL_JDBC_DRIVER);
            DBConnection.MySQL_DB_URL = "jdbc:mysql://" + DBConnection.MySQL_DB_HOST_IP_OR_NAME + "/" + DBConnection.MySQL_DB_NAME;
            DriverManager.getConnection(MySQL_DB_URL, MySQL_USER, MySQL_PASSWORD);
            return "ON";
        } catch (ClassNotFoundException | SQLException e) {
            return "OFF";
        }
    }

    public String isINTER_BRANCH_MySQLConnectionAvailable() {
        try {
            Class.forName(INTER_BRANCH_MySQL_JDBC_DRIVER);
            DBConnection.INTER_BRANCH_MySQL_DB_URL = "jdbc:mysql://" + DBConnection.INTER_BRANCH_MySQL_DB_HOST_IP_OR_NAME + "/" + DBConnection.INTER_BRANCH_MySQL_DB_NAME;
            DriverManager.getConnection(INTER_BRANCH_MySQL_DB_URL, INTER_BRANCH_MySQL_USER, INTER_BRANCH_MySQL_PASSWORD);
            return "ON";
        } catch (ClassNotFoundException | SQLException e) {
            return "OFF";
        }
    }

}
