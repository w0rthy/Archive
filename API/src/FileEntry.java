package archive;

import java.util.Map;
import java.util.TreeMap;

public class FileEntry {
    public Archive MASTER; //Archive this FileEntry resides in
    
    public String name;
    public int heapnum;
    public long pos;
    public int len;
    public int entryid;
    
    Map<String,String> properties;

    FileEntry(Archive MASTER, String name){
        this(MASTER,name,0,0,0,0);
    }
    
    FileEntry(Archive MASTER, String name, int heapnum, long pos, int len, int entryid) {
        this(MASTER,name,heapnum,pos,len,entryid,new TreeMap<String,String>());
    }
    
    FileEntry(Archive MASTER, String name, int heapnum, long pos, int len, int entryid,Map<String,String> properties) {
        this.MASTER = MASTER;
        this.name = name;
        this.heapnum = heapnum;
        this.pos = pos;
        this.len = len;
        this.entryid = entryid;
        this.properties = properties;
    }
    
    //Sets or updates value of a property
    public void SetProp(String key, String val){
        properties.put(key, val);
        MASTER.archiveModified(); //Alerts the Master Archive of modifications
    }
    
    //Gets value of a property
    public String GetProp(String key){
        String val = properties.get(key);
        return (val==null?"":val);
    }
    
    //Removes a property
    public void DelProp(String key){
        properties.remove(key);
        MASTER.archiveModified(); //Alerts the Master Archive of modifications
    }
    
    //Updates the filename of this FileEntry
    public void setName(String name){
        this.name = name;
        
        MASTER.archiveModified(); //Alerts the Master Archive of modifications
    }
    
}