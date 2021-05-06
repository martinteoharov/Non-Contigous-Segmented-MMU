import java.util.ArrayList;

public class Process {

    // pid is stored solely for "metadata", it doesn't actually come up in the memory management implementation
    private int pid; // process unique id

    private SegTable segTable;

    public Process(int pid) {
        this.pid = pid;
        this.segTable = new SegTable();
        // ...
    }

    public int getPID(){
        return this.pid;
    }

    // Process wants to allocate more memory
    public void allocateMemory(){

    }

    // Process wants to deallocate memory
    public void dealloateMemory(){

    }

    
}
