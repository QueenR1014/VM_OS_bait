/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.paging;

/**
 *
 * @author super
 */
public class PageTableEntry implements Comparable{
    
    MemoryFrame frameId;
    boolean valid;
    boolean dirty;

    public PageTableEntry(int frameId) {
        this(frameId, false);
    }
    
    public PageTableEntry(int frameId, boolean valid) {
        this.frameId = new MemoryFrame(frameId);
        this.valid = valid;
        dirty = false;
    }
    
    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    
    
    public int getFrameId(){
        return frameId.getFrameID();
    }
    
    public void setFrameId(int frame){
        frameId.setFrameID(frame);
    }
    
    public boolean isDirty(){
        return dirty;
    }
    
    @Override
    public String toString(){
        return "Frame: "+frameId+" Valid: "+valid+" Dirty: "+dirty;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof PageTableEntry){
            PageTableEntry p = (PageTableEntry)o;
            return this.getFrameId() - p.getFrameId();
        }
        return -999;
    }
    
    
}
