//
// Java Web Browser
//
// haleyjd
// 11/25/02
//

package Browser;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class Main
{
   public static final String PROGRAM_NAME = "Java Browser";
   public static final String VERSION_NAME = "v0.5";
   
   private static final int MAINFRAME_WIDTH = 600;
   private static final int MAINFRAME_HEIGHT = 400;
   
   public static void main(String args[])
   {
      setInitialLookAndFeel();
      
      // open main window
      BrowserFrame mainframe = new BrowserFrame(PROGRAM_NAME);
      
      mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      mainframe.setSize(MAINFRAME_WIDTH, MAINFRAME_HEIGHT);
      mainframe.setVisible(true);
      mainframe.setBrowserLocation(BrowserFrame.mainBrowserURL);
   
   } // end method main 
   
   private static void setInitialLookAndFeel()
   {
      // TODO: Will want to get this value from the
      // configuration file rather than assuming cross-platform
      // at startup
      
      String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
      
      try 
      {
         UIManager.setLookAndFeel(lookAndFeel);
      }
      catch(ClassNotFoundException cnf)
      {
         System.err.println("Requested Look And Feel class not available.");
         System.err.println("Using default Look and Feel.");
      }
      catch(Exception e)
      {
         System.err.println("An error occured while initializing the " +
                            "Look and Feel.");
         System.err.println(e.getMessage());
      }
   } // end method setInitialLookAndFeel
      
} // end class Main
