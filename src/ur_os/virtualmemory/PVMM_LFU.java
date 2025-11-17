/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;
/**
 *
 * @author user
 */
public class PVMM_LFU extends ProcessVirtualMemoryManager{


    public PVMM_LFU(){
        type = ProcessVirtualMemoryManagerType.LFU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, ArrayList<Integer> validList){
        
        if (memoryAccesses == null || memoryAccesses.isEmpty() || validList.size() <= 0) {
            System.out.println(memoryAccesses.toString());
            return -1;
        }
        System.out.println("FAULTING ACCESSES: " + memoryAccesses.toString());
        HashMap<Integer,Integer> freq = new HashMap<>();

        //Create Frequency Map
        for(int page : memoryAccesses){
            freq.put(page, freq.getOrDefault(page, 0) + 1);//add frecuencies
        }
         

        // Victim Selection
        int victim = -1;
        int min = Integer.MAX_VALUE;
        for(int page: validList){
            int pageFreq = freq.getOrDefault(page,0);
            if(pageFreq < min){
                victim = page;
                min = pageFreq;
            }
        }


        return victim;
    }
    
}
