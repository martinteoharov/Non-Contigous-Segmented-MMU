public class Block {
    // simulates a block in memory

    // every block has an offset and a limit
    private static int offset;
    private static int limit;

    public Block(int offset, int limit){
        this.offset = offset;
        this.limit = limit;
    }
}
