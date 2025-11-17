/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;
import java.util.ArrayList;
/**
 *
 * @author user
 */
public class PVMM_LRU extends ProcessVirtualMemoryManager{

    public PVMM_LRU(){
        type = ProcessVirtualMemoryManagerType.LRU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, ArrayList<Integer> validList) {
        if (memoryAccesses == null || memoryAccesses.isEmpty() || validList.size() <= 0) return -1;
        
        int victim = validList.getLast(); //default to last page
        int vIndex = Integer.MAX_VALUE;

        for(int page:validList){
            int i = memoryAccesses.lastIndexOf(page); //Get last appeareance of page in memory accesses
            if(vIndex > i){
                vIndex = i; //replace index if its further away in the past
                victim = page;
            }

        }
        return victim;

    }
    
}
