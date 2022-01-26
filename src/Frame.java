public class Frame {

    private char[] content;
    private boolean dirty;
    private boolean pinned;
    private int blockID;
    private int recordSize = 40;

    public Frame(){
        this.content = new char[4000];
        this.dirty = false;
        this.pinned = false;
        this.blockID = -1;
    }

    // Get given record num, which is the number of the record in the block
    public String getRecord(int recordNum){
        char[] record = new char[this.recordSize];
        for(int i = 0; i < this.recordSize; i++){
            record[i] = this.content[i + (this.recordSize * recordNum)];
        }
        return new String(record);
    }

    // Set recordnum with record, updates dirty flag
    public void setRecord(int recordNum, char[] record){
        for(int i = 0; i < this.recordSize; i++){
            content[i + (this.recordSize * recordNum)] = record[i];
        }
        this.dirty = true;
    }

    // get and set methods
    public boolean isDirty(){
        return this.dirty;
    }

    public boolean isPinned(){
        return this.pinned;
    }

    public int getBlockID(){
        return this.blockID;
    }

    public char[] getContent(){
        return this.content;
    }

    public void setBlockID(int blockID){
        this.blockID = blockID;
    }

    public void setContent(char[] content){
        this.content = content;
    }

    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }

    public void setPinned(boolean pinned){
        this.pinned = pinned;
    }




}
