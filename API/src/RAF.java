package archive;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static archive.IOTools.*;

public class RAF extends RandomAccessFile{
    
    public RAF(String path) throws FileNotFoundException {
        super(path, "rw");
    }
    
    //Read len bytes and returns as a byte array
    public byte[] read(int len) throws IOException{
        byte[] data = new byte[len];
        read(data);
        return data;
    }
    
    //Read up to len bytes from pos
    public byte[] readOffset(long pos, int len) throws IOException{
        byte[] dat = new byte[len];
        seek(pos);
        read(dat);
        
        return dat;
    }
    
    //Write data at pos, overwriting existing data
    public void writeOffset(long pos, byte[] data) throws IOException{
        seek(pos);
        write(data);
    }
    
    //Write data at pos and push the existing data back
    public void insertOffset(long pos, byte[] data) throws IOException{
        byte[] finalseg = readOffset(pos,(int)(length()-pos));
        writeOffset(pos,data);
        write(finalseg);
    }
    
    //Append data to the end of the file
    public void append(byte[] data) throws IOException{
        seek(length());
        write(data);
    }
    
    //Write a string with its length prefixed as an int
    public void writeString(String s) throws IOException {
        writeInt(s.length());
        write(str2byte(s));
    }
    
    //Reads a string written by a RAF
    public String readString() throws IOException{
        int len = readInt();
        return byte2str(read(len));
    }
}
