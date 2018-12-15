package archiveinterface;

import archive.*;

public class ArchColumn {
    
    String getName(){
        return "DEFAULT";
    }
    
    boolean getEditable(){
        return false;
    }
    
    String getData(FileEntry fe){
        return "";
    }
    
    void setData(FileEntry fe, String data){
        
    }

    //NAME COLUMN
    static ArchColumn name = new ArchColumn(){
        @Override
        String getName(){
            return "Name";
        }
        
        @Override
        boolean getEditable(){
            return true;
        }
        
        @Override
        String getData(FileEntry fe){
            return fe.name;
        }
        
        @Override
        void setData(FileEntry fe, String data){
            fe.setName(data);
        }
        
    };
    
    //DATAHEAP NUMBER COLUMN
    static ArchColumn heapnum = new ArchColumn(){
        @Override
        String getName(){
            return "DHeap#";
        }
        
        @Override
        String getData(FileEntry fe){
            return ""+fe.heapnum;
        }
    };
    
    //DATAHEAP NAME COLUMN
    static ArchColumn heapname = new ArchColumn(){
        @Override
        String getName(){
            return "DHeap";
        }
        
        @Override
        String getData(FileEntry fe){
            return fe.MASTER.getDataHeapName(fe.heapnum);
        }
    };
    
    //FILE POS COLUMN
    static ArchColumn heappos = new ArchColumn(){
        @Override
        String getName(){
            return "DHeap Pos";
        }
        
        @Override
        String getData(FileEntry fe){
            return ""+fe.pos;
        }
    };
    
    //FILE SIZE COLUMN
    static ArchColumn filesize = new ArchColumn(){
        @Override
        String getName(){
            return "Size";
        }
        
        @Override
        String getData(FileEntry fe){
            return ""+fe.len;
        }
    };
    
    //FILE NUMBER COLUMN
    static ArchColumn filenum = new ArchColumn(){
        @Override
        String getName(){
            return "File#";
        }
        
        @Override
        String getData(FileEntry fe){
            return ""+fe.entryid;
        }
    };
    
    //COLUMN FOR ANY PROPERTY
    static class PropColumn extends ArchColumn {
        String property;
        
        public PropColumn(String property){
            this.property = property;
        }
        
        @Override
        String getName(){
            return property;
        }
        
        @Override
        boolean getEditable(){
            return true; //All properties MUST be editable
        }
        
        @Override
        String getData(FileEntry fe){
            return fe.GetProp(property);
        }
        
        @Override
        void setData(FileEntry fe, String data){
            fe.SetProp(property, data);
        }
    }
    
    //Function to easily create a property column
    static ArchColumn genPropCol(String property){
        return new PropColumn(property);
    }
}
