/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;

/**
 *
 * @author user
 */
public class PVMM_LRU extends ProcessVirtualMemoryManager{

    public PVMM_LRU(){
        type = ProcessVirtualMemoryManagerType.LRU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {
        
        //ToDo
        LinkedList<Integer> recent = new LinkedList<>();
        int size  = memoryAccesses.size() -1 ;


        while(size >= 0 && recent.size() < loaded){

            int access = memoryAccesses.get(size);

            if(!recent.contains(access)){
                //add to recent accesses if not in list
                recent.addFirst(access);

            }else{
                //move to front if accessed again
                int indx = recent.indexOf(access);
                recent.remove(indx);
                recent.addFirst(access);
            }

            size--;
        }
        
        //return the least recently used page
        return recent.getLast();
    }
    
}
