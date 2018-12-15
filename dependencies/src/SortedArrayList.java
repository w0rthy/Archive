package sortedarraylist;

import java.util.ArrayList;

public class SortedArrayList<T extends IntValued> extends ArrayList<T>{

    //Binary Insert
    @Override
    public boolean add(T a) {
        int pos = findInsLoc(a);
        add(pos, a);
        return true;
    }
    
    //Binary Search, finds where something should go
    public int findInsLoc(int a){
        if(size()<=0)
            return 0;
        
        int l = 0;
        int h = size();
        int m = (l+h)/2;
        int mprv = -1;
        
        while(m!=mprv){
            T tmp = get(m);
            if(a == tmp.value())
                return m; //Found identical value
            //Did not find it
            if(a > tmp.value()){
                l = m;
            }else{
                h = m;
            }
            mprv = m;
            m = (l+h)/2;
        }
        //Determine if it should go before or after element
        if(a > get(m).value())
            return m+1;
        return m;
    }
    
    public int findInsLoc(T a){
        return findInsLoc(a.value());
    }
    
    //Binary Search
    public int findLoc(int a){
        if(size()<=0)
            return -1;
        
        int l = 0;
        int h = size();
        int m = (l+h)/2;
        int mprv = -1;
        
        while(m!=mprv){
            T tmp = get(m);
            if(a == tmp.value())
                return m; //Found it
            //Did not find it
            if(a > tmp.value()){
                l = m;
            }else{
                h = m;
            }
            mprv = m;
            m = (l+h)/2;
        }
        return -1;
    }
    
    public int findLoc(T a){
        return findLoc(a.value());
    }
    
    public T find(int a){
        int pos = findLoc(a);
        return pos!=-1?get(pos):null;
    }
    
    public T find(T a){
        return find(a.value());
    }

    public boolean delete(int a){
        int pos = findLoc(a);
        return (pos!=-1?remove(pos):null) != null;
    }
    
    public boolean delete(T a){
        return delete(a.value());
    }
}
