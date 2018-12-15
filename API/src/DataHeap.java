package archive;

import static archive.IOTools.*;
import sortedarraylist.SortedArrayList;

public class DataHeap {
    public String name;
    public String filepath;
    public SortedArrayList<HoleEntry> holes;
    RAF raf;

    public DataHeap(String name, String file) {
        try{
            this.name = name;
            holes = new SortedArrayList<>();
            filepath = file;
            makedirs(file);
            raf = new RAF(file);
        }catch(Exception e){e.printStackTrace();}
    }

    HEAPINSERTSTATUS insert(long pos, byte [] data){
        try{
            int len = data.length;
            raf.writeOffset(pos, data);
            return new HEAPINSERTSTATUS(true, pos, len);
        }catch(Exception e){e.printStackTrace();}
        
        return new HEAPINSERTSTATUS(false);
    }
    
    HEAPINSERTSTATUS append(byte[] data) {
        try{
            long pos = raf.length();
            int len = data.length;
            raf.append(data);
            return new HEAPINSERTSTATUS(true, pos, len);
        }catch(Exception e){e.printStackTrace();}
        
        return new HEAPINSERTSTATUS(false);
    }
    
    byte[] read(long pos, int length){
        try{
            return raf.readOffset(pos, length);
        }catch(Exception e){e.printStackTrace();}
        
        return null;
    }
    
    //Get length of the dataheap file
    long length(){
        try{
            return raf.length();
        }catch(Exception e){e.printStackTrace();}
        
        return -1;
    }
    
    //Shrinks the dataheap to the specified size
    boolean shrink(long len){
        try{
            raf.setLength(len);
            return true;
        }catch(Exception e){e.printStackTrace();}
        
        return false;
    }
    
    void CLOSE(){
        try{
            if(raf!=null)
                raf.close();
        }catch(Exception e){e.printStackTrace();}
    }
}