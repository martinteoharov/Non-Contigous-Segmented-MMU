import java.util.ArrayList;

public class FrameTable {

    private static ArrayList<Block> frames;

    public FrameTable(){
        this.frames = new ArrayList<Block>();
    }

    public int allocateMemory (int pid, int limit) {

        int bSize = frames.size();

        // Get the last element of our list
        Block lastBlock;
        if(frames.size() > 0) {
            lastBlock = frames.get(frames.size() - 1);
        } else {
           lastBlock = new Block(0, 0, 0);
        }


        int base = lastBlock.getBase() + lastBlock.getLimit();
        Block block = new Block(pid, base, limit);
        frames.add(block);

        int eSize = frames.size();

        if(eSize > bSize) {
            return base;
        }

        return -1;
    }

    // TODO: compact memory
    public void compactMemory (){

    }
}


/**
 * Currently blocks know the PID of the process they belong to, so that necessary
 * changes can be applied to the virtual representation, in the case of compaction
 */

class Block {
    // simulates a block in memory

    // every block has an offset and a limit
    private int base;
    private int limit;
    private int pid;

    public Block(int pid, int base, int limit){
        this.pid = pid;
        this.base = base;
        this.limit = limit;
    }

    public int getPID(){
        return this.pid;
    }

    public int getBase(){
        return this.base;
    }

    public int getLimit(){
        return this.limit;
    }
}
