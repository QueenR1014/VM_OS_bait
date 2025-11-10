/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;
import java.util.HashMap;
/**
 *
 * @author user
 */
public class PVMM_LFU extends ProcessVirtualMemoryManager{


    public PVMM_LFU(){
        type = ProcessVirtualMemoryManagerType.LFU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {
        
        HashMap<Integer,Integer> freq = new HashMap<>();
        LinkedList<Integer> recent = new LinkedList<>();
        int size = memoryAccesses.size();


        //Create Frequency Map
        for(int i = size -1; i >= 0 ; i--){
            int page = memoryAccesses.get(i); 
            freq.put(page, freq.getOrDefault(page, 0) + 1);//add frecuencies
            
            // recently loaded pages (most recent first)
            if(recent.size() < loaded && !recent.contains(page)){
                recent.add(page);
            }
        }
         

        // Victim Selection
        int victim = -1;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < loaded; i++){
            int page = recent.get(i);
            int pageFreq = freq.get(page);
            if(pageFreq < min){
                victim = page;
                min = pageFreq;
            }
        }


        return victim;
    }
    
}
