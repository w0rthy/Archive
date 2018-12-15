package archive;

import java.io.File;
import java.util.ArrayList;

import static archive.IOTools.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

//The Master File For An Archive System
public class Archive {
    
    int HEADERINT = 0x0DED53C0;
    public boolean ERROR = false;
    
    public String filepath;
    RAF raf;
    public ArrayList<DataHeap> dataheaps;
    public ArrayList<FileEntry> entries;
    
    public boolean CHANGED = false; //Whether any changes have been made to archive (prevents writing if not)
    
    //0 = Constant dataheap, 1 = Place in smallest dataheap
    public int INSERTSTRATEGY = 1;
    public int INS0_CONSTHEAP = 0; //First dataheap
    
    public Archive(String file){
        filepath = file;
        
        dataheaps = new ArrayList<>();
        entries = new ArrayList<>();
        
        try{
            boolean fileexists = new File(file).exists();
            
            raf = new RAF(file); //Open the Stream for the file
            
            if(!fileexists){
                //Need to make a blank Archive
                archiveModified();
                return;
            }
            
            //Ensure the file is actually a master archive file
            int header = raf.readInt();
            if(header != HEADERINT){
                System.out.println("ERROR: NOT A VALID MASTER FILE OR OUTDATED");
                ERROR = true;
                return;
            }
            
            //Read in the dataheap information
            {
                int count = raf.readInt();
                while(count > 0){
                    //Read in dataheap information (String-name String-filename)
                    String name = raf.readString();
                    String path = raf.readString();
                    if(new File(path).exists())
                        dataheaps.add(new DataHeap(name,path));
                    else
                        System.out.println("WARNING: DATAHEAP '"+name+"' DECLARED IN MASTER FILE AT PATH '"+path+"', BUT DOES NOT EXIST");
                    count--;
                }
            }
            
            //Read in all file entries
            {
                int files = raf.readInt();
                while(files > 0){
                    //Read in file entry information (String-name int-heapNumber long-positionInHeap int-fileLen)
                    String name = raf.readString();
                    int heapnum = raf.readInt(); //Which DataHeap is this file in
                    long pos = raf.readLong(); //Where in the DataHeap is it
                    int filelength = raf.readInt(); //How long is this file
                    
                    int entryid = entries.size(); //FileEntryID for this file
                    
                    Map<String,String> properties = new TreeMap<>(); //Hold the property k,v pairs for this file
                    //Read the property pairs (if any)
                    int numprops = raf.readInt(); //Number of property pairs
                    while(numprops > 0){
                        String k = raf.readString(); //Read Key
                        String v = raf.readString(); //Read Value
                        properties.put(k, v); //Place in properties map
                        numprops--;
                    }
                    
                    entries.add(new FileEntry(this,name,heapnum,pos,filelength,entryid,properties));
                    
                    files--;
                }
            }
            
            //Read in all hole information (int heapnum, long pos, int len)
            {
                int numholes = raf.readInt();
                
                //Add all Hole Entries into a sorted array for quick searching
                while(numholes>0){
                    int heapnum = raf.readInt();
                    long pos = raf.readLong();
                    int len = raf.readInt();
                    if(!validHeap(heapnum))
                        continue;
                    dataheaps.get(heapnum).holes.add(new HoleEntry(pos, len)); //Place the entry into the appropriate heap
                    numholes--;
                }
            }
            
            //Done reading in everything
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Write the Master Archive File
    public void SAVE(){
        if(!CHANGED)
            return;
        
        try{
            if(raf==null){
                makedirs(filepath);
                raf = new RAF(filepath);
            }
            
            raf.seek(0);
            //Write the header integer (to verify it is a archive master file
            raf.writeInt(HEADERINT);
            
            //Write the dataheap information (String-filename)
            {
                raf.writeInt(dataheaps.size());
                for(int i = 0; i < dataheaps.size(); i++){
                    raf.writeString(dataheaps.get(i).name);
                    raf.writeString(dataheaps.get(i).filepath);
                }
            }
            
            //Write all file entries
            {
                raf.writeInt(entries.size()); //Write the number of files
                for(int i = 0; i < entries.size(); i++){
                    FileEntry fe = entries.get(i);
                    raf.writeString(fe.name); //Write filename
                    raf.writeInt(fe.heapnum); //Write DataHeap number
                    raf.writeLong(fe.pos); //Write position in DataHeap
                    raf.writeInt(fe.len); //Write size of file in DataHeap
                    
                    //Write the file properties k,v map
                    Set<Entry<String,String>> pairs = fe.properties.entrySet();
                    raf.writeInt(pairs.size()); //Number of property pairs
                    for(Entry<String,String> e : pairs){
                        raf.writeString(e.getKey()); //Write Key
                        raf.writeString(e.getValue()); //Write Value
                    }
                }
            }
            
            //Write all the hole information (int heapnum, long pos, int len)
            {
                //Count the number of holes
                int numholes = 0;
                for(DataHeap dh : dataheaps)
                    numholes+=dh.holes.size();
                raf.writeInt(numholes); //Write number of holes
                
                //Write information for each hole
                for(int i = 0; i < dataheaps.size(); i++){
                    for(HoleEntry he : dataheaps.get(i).holes){
                        raf.writeInt(i); //Write heapnum
                        raf.writeLong(he.pos); //Write pos
                        raf.writeInt(he.len); //Write len
                    }
                }
            }
            
            //Done writing everything
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Insert into specified dataheap index
    public FileEntry insertDataAsFile(int heap, byte[] data, String filename){
        HEAPINSERTSTATUS his = insertIntoHeap(heap, data);
        if(his.success){
            //Successfully added to heap, now make the file entry
            FileEntry fe = new FileEntry(this, filename, heap, his.pos, his.len, entries.size());
            entries.add(fe);
            
            archiveModified(); //Will now Write when Saving
            return fe;
        }
        return null;
    }
    
    //Insert into specified dataheap index using a stream
    public FileEntry insertDataAsFile(int heap, InputStream is, String filename){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096]; //Good Buffer Size
            while(true){
                int amt = is.read(buf);
                if(amt==-1)
                    break;
                baos.write(buf, 0, amt);
            }
            return insertDataAsFile(heap, baos.toByteArray(), filename);
        }catch(Exception e){e.printStackTrace();}
        
        return null;
    }
    
    //Places a file into specified dataheap index
    public FileEntry insertFile(String path, int heap){
        if(!validHeap(heap))
            return null;
        
        try{
            RAF r = new RAF(path);
            byte[] data = r.read((int)r.length());
            return insertDataAsFile(heap, data, path2name(path));
        }catch(Exception e){e.printStackTrace();}
        return null;
    }
    
    //Places a file into dataheap specified by its internal name
    //This function will not create a dataheap if not found
    public FileEntry insertFile(String path, String heap){
        int ind = findDataHeap(heap);
        if(ind==-1)
            return null;
        
        return insertFile(path,ind);
    }
    
    //Inserts a file into a dataheap using the current insert strategy
    public FileEntry insertFile(String path){
        return insertFile(path,doInsertStrategy());
    }
    
    //Places a file into dataheap specified by its internal name
    //This function will create DataHeap if not found using an automatic path
    public FileEntry insertFileC(String path, String heap){
        return insertFile(path,findOrCreateDataHeap(heap));
    }
    
    //Places a file into dataheap specified by its internal name
    //This function will create DataHeap if not found using given path
    public FileEntry insertFileC(String path, String heap, String heappath){
        return insertFile(path,findOrCreateDataHeap(heap,heappath));
    }

    //Attempt to insert data into dataheap # heap
    public HEAPINSERTSTATUS insertIntoHeap(int heap, byte[] data) {
        if(validHeap(heap)){
            DataHeap dh = dataheaps.get(heap);
            int hole = dh.holes.findInsLoc(data.length); //Look for a hole of the closest size
            if(hole != dh.holes.size()){ //A hole is available
                HoleEntry he = dh.holes.get(hole); //Get the hole
                HEAPINSERTSTATUS his = dh.insert(he.pos, data); //Try to insert
                if(!his.success)
                    return his; //Failed, just return it
                //Success, make the hole smaller
                int lendiff = he.len-data.length;
                he.pos = he.pos+he.len-lendiff;
                he.len = lendiff;
                if(he.len==0) //Check if hole is filled
                    dh.holes.remove(hole); //Hole is completely filled, delete it
                return his; //Done
            }
            else
                return dataheaps.get(heap).append(data); //No hole available
        }
        
        return new HEAPINSERTSTATUS(false);
    }
    
    //Extract a byte[] from dataheap # heap
    public byte[] extractFromHeap(int heap, long pos, int length){
        if(validHeap(heap)){
            byte[] data = new byte[length];
            return dataheaps.get(heap).read(pos, length);
        }
        
        return null;
    }
    
    //Extract a file from a dataheap and create it in the specified directory
    //For the path argument, if not blank, ensure a '/' is at the end
    public File extractAndCreate(FileEntry fe, String path){
        try{
            byte[] data = extractFromHeap(fe.heapnum, fe.pos, fe.len);
            if(data == null) //DataHeap read failed
                return null;
            
            makedirs(path+fe.name);
            RAF tmpraf = new RAF(path+fe.name);
            tmpraf.write(data);
        }catch(Exception e){e.printStackTrace();}
        return new File(fe.name);
    }
    
    //Extract a file from a dataheap and create it in the current directory
    public File extractAndCreate(FileEntry fe){
        return extractAndCreate(fe,"");
    }
    
    //Deletes a file from a dataheap. Accomplished by removing the file entry and marking the space as a hole.
    public boolean deleteFile(FileEntry fe){
        entries.remove(fe.entryid); //Remove file entry
        //Move all entries after this down a number to accomadate the shift
        for(int i = fe.entryid; i < entries.size(); i++)
            entries.get(i).entryid--;
        
        archiveModified(); //Mark archive as modified
        
        DataHeap dh = dataheaps.get(fe.heapnum);
        
        //Look for contiguous holes to merge with
        HoleEntry prev = null;
        HoleEntry next = null;
        for(HoleEntry h : dh.holes){
            if(fe.pos+fe.len == h.pos) //h beings where fe ended
                next = h; //Remember this HoleEntry
            if(h.pos+h.len == fe.pos) //fe began where h ends
                prev = h; //Remember this HoleEntry
        }
        if(prev!=null){
            prev.len = prev.len+fe.len; //Update the previous' length
            if(next!=null){
                prev.len = prev.len+next.len; //Update the previous' length again
                dh.holes.remove(next); //Remove the next hole, it has been merged with prev
            }
            if(prev.pos+prev.len == dh.length()){ //Check if prev goes to end of dataheap
                dh.shrink(prev.pos); //Shrink the dataheap
                dh.holes.remove(prev); //Remove prev as a hole
            }
        }else if(next!=null){
            next.pos = fe.pos; //Move next's starting position
            next.len = next.len+fe.len; //Update next's length
            if(next.pos+next.len == dh.length()){ //Check if next goes to end of dataheap
                dh.shrink(next.pos); //Shrink the dataheap
                dh.holes.remove(next); //Remove next as a hole
            }
        }else if(fe.pos+fe.len == dh.length()){ //Check if at end of file
            dh.shrink(fe.pos); //Shrink the dataheap
        }else { //Nothing to merge, make a new entry
            HoleEntry he = new HoleEntry(fe.pos, fe.len); //Make the HoleEntry
            dh.holes.add(he); //Insert into proper dataheap
        }
        
        return true;
    }
    
    //Finds a DataHeap by internal name
    public int findDataHeap(String name){
        for(DataHeap dh : dataheaps){
            if(name.equals(dh.name)){
                return dataheaps.indexOf(dh);
            }
        }
        return -1;
    }
    
    //Creates a new DataHeap (name is the internal name, path is the filename)
    public void createDataHeap(String name, String path){
        for(DataHeap dh : dataheaps){
            if(name.equals(dh.name)){
                System.out.println("TRIED TO CREATE DATAHEAP WITH NAME "+name+" BUT ALREADY EXISTED");
                return;
            }
        }
        if(new File(path).exists()){
            System.out.println("TRIED TO CREATE DATAHEAP WITH PATH "+path+" BUT A FILE ALREADY EXISTS THERE");
            return;
        }
        dataheaps.add(new DataHeap(name, path));
        
        archiveModified(); //Changes will be written on SAVE()
    }
    
    //Creates a new DataHeap with an automatically generated path
    public void createDataHeap(String name){
        createDataHeap(name, name+".dh");
    }
    
    //Close all streams without saving
    public void CLOSE(){
        for(DataHeap dh : dataheaps)
            dh.CLOSE();
        try{
            if(raf!=null)
                raf.close();
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Looks for specified DataHeap, and if not found, creates it
    public int findOrCreateDataHeap(String name, String path){
        int res = findDataHeap(name);
        if(res!=-1)
            return res;
        //DataHeap needs to be created
        createDataHeap(name, path);
        
        return findDataHeap(name);
    }
    
    //Looks for specified DataHeap, and if not found, creates it with a generic path
    public int findOrCreateDataHeap(String name){
        return findOrCreateDataHeap(name,name+".dh");
    }
    
    //Returns the name of the data heap specified by index (can be a bad index)
    public String getDataHeapName(int heapnum){
        if(heapnum < 0){ //Error heap number
            //Specific error names go here
            return "ERROR";
        }
        if(heapnum >= dataheaps.size()) //Invalid heap number
            return "INVALID";
        
        return dataheaps.get(heapnum).name;
    }
    
    public int doInsertStrategy(){
        if(INSERTSTRATEGY==0){ //Constant dataheap
            return INS0_CONSTHEAP;
        }else if(INSERTSTRATEGY==1){ //Smallest dataheap
            int heapnum = -1;
            long size = Long.MAX_VALUE;
            for(int i = 0; i < dataheaps.size(); i++){
                long tmp = dataheaps.get(i).length();
                if(tmp<size){
                    size = tmp;
                    heapnum = i;
                }
            }
            return heapnum;
        }
        
        return 0; //First dataheap if invalid setting
    }
    
    //Ensures a heap number corresponds to an actual heap
    public boolean validHeap(int heap) {
        return (heap >= 0 && heap < dataheaps.size() && dataheaps.get(heap) != null);
    }
    
    public void archiveModified(){
        CHANGED = true;
    }
}