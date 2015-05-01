/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pos;

import com.ustasoft.component.DBConnection;
import com.ustasoft.component.rhs.ini;
import java.awt.Color;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;



/**
 *
 * @author cak-ust
 */
public class SIA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        try{
//            AbstractApplicationContext appContext =
//                new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
//            DataSource dataSource = (DataSource) appContext.getBean("dataSource");
            //dataSource=(DataSource) appContext.getBean("dataSource");
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            BorderUIResource borderUIResource= new BorderUIResource(BorderFactory.createLineBorder(Color.yellow, 2));
            
            UIManager.put("Table.focusCellHighlightBorder", borderUIResource);
            LoginUser f1=new LoginUser();
            //f1.setConn(dataSource.getConnection());
            f1.setConn(new DBConnection(get1(), get2(), f1).getCon());
            f1.setVisible(true);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(SIA.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            Logger.getLogger(SIA.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(SIA.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedLookAndFeelException ex) {
//            Logger.getLogger(SIA.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception be){
            System.out.println(be.getMessage());
        }
    }
    
    private static String get1(){
        return ini.u;
    }
    
    private static String get2(){
        return ini.p;
    }
}
