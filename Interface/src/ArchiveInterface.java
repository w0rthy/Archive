package archiveinterface;

import archive.*;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class ArchiveInterface {
    
    public static void main(String[] args) {
        Archive arch;
        if(args.length<=0){
            String wd = System.getProperty("user.dir"); //Get current directory
            JFileChooser jfc = new JFileChooser(wd);

            jfc.setMultiSelectionEnabled(false);
            jfc.showSaveDialog(null); //Ask for file
            File f = jfc.getSelectedFile(); //Get the chosen file
            if(f==null)
                return;

            arch = new Archive(f.getAbsolutePath()); //Create the Archive object
        }else{
            arch = new Archive(args[0]);
        }
        
        if(arch.ERROR){
            //Failed to open Archive, warn and then exit
            JOptionPane.showMessageDialog(null, "Failed to open Archive", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ArchiveFrame af = new ArchiveFrame(arch);
        
        while(af.isVisible()){
            try{Thread.sleep(100);}catch(Exception e){}
        }
        
        arch.SAVE();
        arch.CLOSE();
    }
    
}
