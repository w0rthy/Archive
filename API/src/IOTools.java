package archive;

import java.io.File;

public abstract class IOTools {

    public static byte[] str2byte(String str){
        byte[] data = new byte[str.length()];
        for(int i = 0; i < data.length; i++){
            data[i] = (byte)(str.charAt(i));
        }
        return data;
    }
    
    public static String byte2str(byte[] data){
        StringBuilder sb = new StringBuilder(data.length);
        for(int i = 0; i < data.length; i++){
            sb.append((char)data[i]);
        }
        return sb.toString();
    }
    
    public static String path2name(String path){
        int ind = path.replace('\\', '/').lastIndexOf('/');
        if(ind<0)
            return path;
        return path.substring(ind+1);
    }
    
    public static String path2dirs(String path){
        int ind = path.replace('\\', '/').lastIndexOf('/');
        if(ind<0)
            return "";
        return path.substring(0,ind);
    }
    
    public static void makedirs(String path){
        String str = path2dirs(path);
        if(str.length()<=0) //Stop if there are no dirs to make
            return;
        
        new File(path2dirs(path)).mkdirs();
    }
}
