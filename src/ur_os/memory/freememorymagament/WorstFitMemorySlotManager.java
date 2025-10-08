/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author super
 */
public class WorstFitMemorySlotManager extends FreeMemorySlotManager{
    
    public WorstFitMemorySlotManager(int memSize){
        super(memSize);
    }
    
    @Override
    public MemorySlot getSlot(int size) {
        if(list.isEmpty()){
            System.out.println("Error: No free memory available");
            return null; // no free memory available
        }
        
        MemorySlot bestSlot = null;

        //find the biggest slot that can fit size
        for(MemorySlot slot : list){
            if(slot.getSize() >= size){
                if(bestSlot == null || slot.getSize() > bestSlot.getSize()){
                    bestSlot = slot;
                }
            }
        }

        if(bestSlot == null){
            // No suitable slot found
            System.out.println("Error: No suitable slot found.");
            return null;

        }

        //Allocate from best slot
        MemorySlot allocated = bestSlot.assignMemory(size);

        //Remove slot if empty
        if(bestSlot.inNull()){
            list.remove(bestSlot);
        }

        return allocated;
    }
    
}
