import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static MMU mmu;

    public static void main(String[] args){

        Main.mmu = MMU.get();

        Main.runSim0();
        Main.mmu.printMemReport();
        Main.mmu.forceCompaction();
        Main.mmu.printMemReport();

        // mmu.clean();

        // Main.runSim1();
        // mmu.clean();

        // Main.runSim2();
        // mmu.clean();

    }

    private static void createProcess(int args[]) {

        int pid = args[0];

        // Create process
        Process process = new Process(pid);

        // Attach process to MMU
        Main.mmu.attachProcess(process);

        // Create segments and attach to process in MMU
        for(int i = 1; i < args.length; i ++){

            // A unique identifier for a segment is the tuple - (pid, sid, memSegSize)
            // ...

            int sid = i - 1;
            int memSegSize = args[i];

            // Check if segment size is a power of 2...

            // Allocate memory segment...
            Main.mmu.allocToSegment(pid, sid, memSegSize);
        }

    }


    // Simulation 0
    private static void runSim0 () {
        System.out.println("Running Simulation 0...");

        // size in bytes (should be a power of 2)
        int[][] to_alloc = {
                {1, 100, 200, 10},
                {1, 100, 200, 40},
                {2, 100, 200, 300},
                {4, 110, 130},
                {5, 74, 100},
        };

        for (int i = 0; i < to_alloc.length; i++) {
            createProcess(to_alloc[i]);
        }


    }

}
