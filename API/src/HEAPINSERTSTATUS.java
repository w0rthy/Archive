package archive;

public class HEAPINSERTSTATUS {
        boolean success;
        long pos;
        int len;
        
        public HEAPINSERTSTATUS(boolean success) {
            this.success = success;
        }
        
        public HEAPINSERTSTATUS(boolean success, long pos, int len){
            this.success = success;
            this.pos = pos;
            this.len = len;
        }
}