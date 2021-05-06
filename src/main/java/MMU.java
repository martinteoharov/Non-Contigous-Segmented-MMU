// this class acts as the memory management unit

/*
    All changes to a processes segment size/count etc. go
    through here. The MMU creates its data and passes a
    request to the specific process to change its SegTable
    data.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class MMU {

    // There won't be a need for more than one MMU at a time, so we keep this class static
    private static MMU instance;

    // All of the processes the OS knows of
    private static ArrayList<Process> processes;
    private static HashMap<Integer, Process> hm_processes;

    // OS Uses Frame Table to keep track of available frames
    private static FrameTable frameTable;

    private MMU(){
        MMU.frameTable = new FrameTable();
        MMU.processes = new ArrayList<Process>();
    }

    public static MMU get() {
        if(MMU.instance == null){
            MMU.instance = new MMU();
        }

        return MMU.instance;
    }

    /* --- Attach Process --- */
    // insert process into ArrayList
    public void attachProcess (Process process) {
        MMU.processes.add(process);
    }
    // TODO: use a HashTable with pid as key instead of arraylist.
    public void attachProcess (int pid, Process process) {
        MMU.processes.set(pid, process);
    }

    /* --- Segment Manager --- */
    // Responsible for creating new segments for a process
    public void createSegment(int pid, int memSegID, int memSegSize){
        Process process = this.hm_processes.get(pid);

        // TODO: internal call to process to create an entry for its SegTable
        // ...

    }

    /* --- Segment Size Manager --- */
    // Manages existing segments' sizes
    public void resizeSegment(int pid, int memSegID, int target_memSegSize){
        Process process = this.hm_processes.get(pid);

        // TODO: set target_memSegSize to segment in segment table with memSegID as id
        // ...
    }



}
