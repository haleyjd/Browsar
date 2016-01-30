//
// Java Web Browser
//
// haleyjd
// 11/25/02
//

package Browser;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.IOException;
import java.util.Stack;

public class BrowserFrame extends JFrame implements HyperlinkListener
{
   protected BrowserToolBar toolBar;
   protected JEditorPane browserPane;
   
   protected String location;
   protected Stack  locationStack; // stack of visited locations
   protected Stack  forwardStack;  // stack of sites for forward button
   
   public final static String mainBrowserURL =
            "file:" + System.getProperty("user.dir").replace('\\', '/') + 
            "/data/Browser.html";
   
   // need to sub-class to add custom variables, encapsulate controls
   protected class BrowserToolBar extends JToolBar implements ActionListener
   {            
      // add one of these for anything added to the toolbar
      private int homeIndex;
      private int backIndex;
      private int forwardIndex;
      private int comboBoxIndex;
      
      private static final String homeCmd = "home";
      private static final String backCmd = "back";
      private static final String forwardCmd = "forward";
      private static final String comboCmd = "combo";
      
      private BrowserFrame parentBrowserFrame;
      
      public BrowserToolBar(BrowserFrame pc)
      {
         super();
         addMyControls();
         parentBrowserFrame = pc;
      }
      
      // external accessors for manipulating the controls
      public Component getHomeButton()
      {
         return getComponentAtIndex(homeIndex);
      }
      
      public Component getBackButton()
      {
         return getComponentAtIndex(backIndex);
      }
      
      public Component getForwardButton()
      {
         return getComponentAtIndex(forwardIndex);
      }      
      
      public Component getComboBox()
      {
         return getComponentAtIndex(comboBoxIndex);
      }
      
      // implemented for ActionListener event handling
      public void actionPerformed(ActionEvent e)
      {
         String actionCmd = e.getActionCommand();
         Stack locStack = parentBrowserFrame.locationStack;
         Stack fwdStack = parentBrowserFrame.forwardStack;
         
         if(actionCmd.equals(homeCmd)) // event from home button
         {
            fwdStack.removeAllElements();
            parentBrowserFrame.setBrowserLocation(mainBrowserURL);
         }
         else if(actionCmd.equals(backCmd)) // event from back button
         {
            if(!locStack.isEmpty())
            {
               String myLocale = (String)(locStack.pop());
               
               // push current location on forward stack
               fwdStack.push(location);
               getForwardButton().setEnabled(true);
               
               // do *not* cache the last location in the stack
               parentBrowserFrame.setBrowserLocation(myLocale, false);
            }           
         }
         else if(actionCmd.equals(forwardCmd)) // event from forward button
         {
            if(!fwdStack.isEmpty())
            {
               // remove location from forward stack
               String newLoc = (String)(fwdStack.pop());
               
               // DO add the current location to the back stack
               parentBrowserFrame.setBrowserLocation(newLoc);
            }
         }
         else if(actionCmd.equals(comboCmd)) // event from URL combo box!
         {
            if(e.getSource() instanceof JComboBox) // just to be sure
            {
               JComboBox thisBox = (JComboBox)e.getSource();
               
               String newLoc = thisBox.getSelectedItem().toString();
               if(newLoc != null && !newLoc.equals("")) // ignore empty selections
               {
                  if(thisBox.getSelectedIndex() == -1)
                  {
                     thisBox.insertItemAt(newLoc, 0);
                  }
                  fwdStack.removeAllElements();
                  parentBrowserFrame.setBrowserLocation(newLoc);
               }
            }
         }
                  
         // disable the back button if we find the location stack is empty
         if(locStack.isEmpty())
         {
            getBackButton().setEnabled(false);
         }
         
         // disable forward button if forward stack is empty
         if(fwdStack.isEmpty())
         {
            getForwardButton().setEnabled(false);
         }
      }
      
      protected void addMyControls()
      {
         // add browser-style control buttons
         JButton home = new JButton(new ImageIcon("data/Home24.gif"));
         JButton back = new JButton(new ImageIcon("data/Back24.gif"));
         JButton fwd  = new JButton(new ImageIcon("data/Forward24.gif"));
         
         home.setToolTipText("Home");
         home.addActionListener(this);
         home.setActionCommand(homeCmd);
         
         back.setToolTipText("Back");
         back.addActionListener(this);
         back.setActionCommand(backCmd);
         back.setEnabled(false); // initially disabled
         
         fwd.setToolTipText("Forward");
         fwd.addActionListener(this);
         fwd.setActionCommand(forwardCmd);
         fwd.setEnabled(false); // initially disabled
                  
         add(home);
         add(back);
         add(fwd);
         add(new JToolBar.Separator());
         
         // set built-in index variables
         homeIndex = getComponentIndex(home);
         backIndex = getComponentIndex(back);
         forwardIndex = getComponentIndex(fwd);
         
         JComboBox comboBox = new JComboBox();
         comboBox.setEditable(true);
         comboBox.addActionListener(this);
         comboBox.setActionCommand(comboCmd);
         comboBox.setMaximumRowCount(3); // don't let it get too long
         comboBox.insertItemAt(mainBrowserURL, 0); // don't start it out empty
         
         add(comboBox);
         
         comboBoxIndex = getComponentIndex(comboBox);
      }      
   } // end inner class HelpToolBar
   
   public BrowserFrame(String title)
   {
      super(title);
      
      // set window icon
      setIconImage((new ImageIcon("data/browse.gif")).getImage());
      
      initToolbar();
      
      initContent();
      
      locationStack = new Stack();
      forwardStack  = new Stack();
   }

   public void setBrowserLocation(String locale)
   {
      // push old location if one exists
      if(location != null)
      {
         locationStack.push(location);
         toolBar.getBackButton().setEnabled(true);
      }
              
      location = locale;
      try
      {
         browserPane.setPage(location);
      }
      catch(IOException ioex)
      {
         String message = "Error loading from location:\n" + ioex.getMessage();
         JOptionPane.showMessageDialog(this, message, "Browser Error",
                                       JOptionPane.ERROR_MESSAGE);
         return;
      }      
   }

   public void setBrowserLocation(String locale, boolean cacheLocation)
   {
      // push old location if one exists AND its specified behavior
      if(cacheLocation && location != null)
      {
         locationStack.push(location);
         toolBar.getBackButton().setEnabled(true);
      }
                    
      location = locale;
      try
      {
         browserPane.setPage(location);
      }
      catch(IOException ioex)
      {
         String message = "Error loading from location:\n" + ioex.getMessage();
         JOptionPane.showMessageDialog(this, message, "Browser Error",
                                       JOptionPane.ERROR_MESSAGE);
         return;
      }
   }
   
   // implemented for HyperlinkListener
   public void hyperlinkUpdate(HyperlinkEvent e)
   {
      if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
      {
         JEditorPane ep = (JEditorPane)(e.getSource());
         
         // handle frame events properly
         if(e instanceof HTMLFrameHyperlinkEvent)
         {
            HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent)e;
            HTMLDocument doc = (HTMLDocument)(ep.getDocument());
            doc.processHTMLFrameHyperlinkEvent(evt);
         }
         else // handle normal links
         {
            try
            {
               URL currentLoc = new URL(location);
               URL newLoc = new URL(currentLoc, e.getDescription());
               
               setBrowserLocation(newLoc.toString());
            }
            catch(MalformedURLException malUrl)
            {
               JOptionPane.showMessageDialog(this, "Malformed URL", "Browser Error",
                                             JOptionPane.ERROR_MESSAGE);
               return;
            }
         }
      }
   }

   protected void initToolbar()
   {
      // toolbar for home, history buttons, et al.
      toolBar = new BrowserToolBar(this);
      toolBar.setFloatable(false);
      
      getContentPane().add(toolBar, BorderLayout.NORTH);
   }

   protected void initContent()
   {
      browserPane = new JEditorPane();
      browserPane.setEditable(false);
      browserPane.setSize(1,1);
      browserPane.addHyperlinkListener(this); // this frame is the listener
      
      getContentPane().add(new JScrollPane(browserPane), BorderLayout.CENTER);
   }
   
}

