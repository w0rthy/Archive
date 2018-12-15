package archive;

import sortedarraylist.IntValued;

public class HoleEntry extends IntValued {
    long pos;
    int len;
    
    public HoleEntry(long pos, int len){
        this.pos = pos;
        this.len = len;
    }
    
    @Override
    public int value(){
        return len;
    }
}
