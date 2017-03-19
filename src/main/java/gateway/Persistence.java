package gateway;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by dean on 3/18/17.
 * Static class to provide entity manager
 */
public class Persistence {
    private static EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(Persistence.class);

    private Persistence(){}

    public static EntityManager getEntityManager(){
        if(entityManager==null) {
            try {
                loadDatabaseProperties();
            }catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return entityManager;
    }
    /**
     * load database related properties
     * @throws IOException
     */
    static void loadDatabaseProperties() throws IOException{
        Properties props = new Properties();
        InputStream is = new FileInputStream("./application.properties");
        props.load(is);
        Properties hibernateProps;
        logger.debug("application.properties is "+props.toString());
        hibernateProps = new Properties();
        hibernateProps.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        hibernateProps.setProperty("hibernate.hbm2ddl.auto", "validate");
        hibernateProps.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        hibernateProps.setProperty("hibernate.connection.username", props.getProperty("spring.datasource.username"));
        hibernateProps.setProperty("hibernate.connection.password", props.getProperty("spring.datasource.password"));
        hibernateProps.setProperty("hibernate.connection.url", props.getProperty("spring.datasource.url"));
        EntityManagerFactory factory = javax.persistence.Persistence.createEntityManagerFactory("hello", props);
        entityManager = factory.createEntityManager();
    }
}
