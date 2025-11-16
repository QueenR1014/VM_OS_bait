/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author user
 */
public abstract class ProcessVirtualMemoryManager {
    
    ProcessVirtualMemoryManagerType type;
    
    public abstract int getVictim(LinkedList<Integer> memoryAccesses, ArrayList<Integer> validList);
    
    
    
}
