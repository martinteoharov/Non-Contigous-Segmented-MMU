import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static MMU mmu;

    public static void main(String[] args){

        Main.mmu = MMU.get();

        Main.runSim0();
        Main.mmu.printMemReportSimple();
        Main.mmu.forceCompaction();
        Main.mmu.printMemReportSimple();

        Main.mmu.clean();

        Main.runSim1();
        Main.mmu.printMemReport();
        Main.mmu.forceCompaction();
        Main.mmu.printMemReport();

        Main.mmu.clean();

        Main.runSim2();
        Main.mmu.printMemReport();
        Main.mmu.forceCompaction();
        Main.mmu.printMemReport();


    }

    private static void createProcess(int args[]) {

        int pid = args[0];

        // Create process
        Process process = new Process(pid);

        // Attach process to MMU
        try {
            Main.mmu.attachProcess(process);
        } catch (IllegalArgumentException e) {
            System.err.println("WARNING: " + e.getMessage());
        }

        int sid = 0;

        // Create segments and attach to process in MMU
        for(int i = 1; i < args.length; i ++, sid ++){

            // A unique identifier for a segment is the tuple - (pid, sid, memSegSize)
            // ...
            int memSegSize = args[i];

            // TODO: Check if segment size is a power of 2...

            // Allocate memory segment...
            try {
                int result = Main.mmu.allocToSegment(pid, sid, memSegSize);
                if(result == 0) {
                    sid --;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();     
            }
        }

    }


    // Simulation 0
    private static void runSim0 () {
        System.out.println("Running Memory Test 0...");

        // size in bytes (should be a power of 2)
        int[][] to_alloc = {
                {1, 100, 200, 10},
                {1, 100, -200, 40},
                {2, 100, 200, 300},
                {4, 110, 130},
                {5, 74, 100},
        };

        for (int i = 0; i < to_alloc.length; i++) {
            createProcess(to_alloc[i]);
        }
    }

    // Simulation 1
    private static void runSim1 () {
        System.out.println("Running Memory Test 1...");

        // size in bytes (should be a power of 2)
        int[][] to_alloc = {
                {1, 100, 200, 10},
                {1, -40, -80, 40},
                {2, 100, 10, 300},
                {4, 110, 130},
                {5, 74, 100},
        };

        for (int i = 0; i < to_alloc.length; i++) {
            createProcess(to_alloc[i]);
        }
    }

    // Simulation 2
    private static void runSim2 () {
        System.out.println("Running Memory Test 2...");

        // size in bytes (should be a power of 2)
        int[][] to_alloc = {
                {1, 100, 200, 10},
                {1, -100, -200, 40},
                {2, 100, 10, 300},
                {4, 110, 130},
                {5, 74, 100},
        };

        for (int i = 0; i < to_alloc.length; i++) {
            createProcess(to_alloc[i]);
        }
    }



}
