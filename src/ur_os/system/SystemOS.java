/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ur_os.system;

import ur_os.process.ProcessInstructionType;
import ur_os.memory.contiguous.SMM_Contiguous;
import ur_os.memory.Memory;
import ur_os.memory.MemoryManagerType;
import ur_os.process.Process;
import java.util.ArrayList;
import java.util.Random;
import ur_os.memory.MemoryInstruction;
import ur_os.memory.MemoryOperationType;
import ur_os.memory.freememorymagament.FreeMemorySlotManager;
import ur_os.process.EndInstruction;
import ur_os.process.IOInstruction;
import ur_os.process.Instruction;
import ur_os.virtualmemory.SwapMemory;

/**
 *
 * @author super
 */
public class SystemOS implements Runnable{
    
    SimulationType simType;
    private static int clock = 0;
    private static final int MAX_SIM_CYCLES = 100;
    private static final int MAX_SIM_PROC_CREATION_TIME = 50;
    private static final double PROB_PROC_CREATION = 0.1;
    public static final int MAX_PROC_SIZE = 1000;
    private static Random r = new Random(1235);
    private OS os;
    private CPU cpu;
    private IOQueue ioq;
    
    private Memory memory;
    private SwapMemory swap;
    
    
    public static final int SEED_SEGMENTS = 7401;
    public static final int SEED_PROCESS_SIZE = 9630;
    
    public static final int MEMORY_SIZE = 1_048_576; //1MB
    public static final int SWAP_MEMORY_SIZE = 1_073_741_824; //1 GB
    
    protected ArrayList<Process> processes;
    ArrayList<Integer> execution;

    public SystemOS(SimulationType simType) {
        memory = new Memory(MEMORY_SIZE);
        swap = new SwapMemory(MEMORY_SIZE);
        cpu = new CPU(memory,swap);
        ioq = new IOQueue();
        os = new OS(this, cpu, ioq);
        cpu.setOS(os);
        ioq.setOS(os);
        execution = new ArrayList();
        processes = new ArrayList();
        //initSimulationQueue();
        //initSimulationQueueSimple();
        //initSimulationQueueSimpler();
        
        // Use of VM Simulation: 
        //initVMSim();
        stableVMSim();

        showProcesses();
        this.simType = simType;
    }
    
    public int getTime(){
        return clock;
    }
    
    public ArrayList<Process> getProcessAtI(int i){
        ArrayList<Process> ps = new ArrayList();
        
        for (Process process : processes) {
            if(process.getTime_init() == i){
                ps.add(process);
            }
        }
        
        return ps;
    }


    // Virtual Memory Simulation Process Creation

    public void initVMSim(){
        // Create a single process with multiple memory instructions

        Process p = new Process(0,0);
        p.setSize(20);

        p.addCPUInstructions(5);


        Random random = new Random(1234);
        for(int i = 0; i < 10; i++){
            // Random creation of memory instruction components
            MemoryOperationType mo = random.nextBoolean() ? 
                MemoryOperationType.STORE : MemoryOperationType.LOAD;

            int logicalAddress = random.nextInt(800);
            byte content = (byte) random.nextInt(50);
            int duration = random.nextInt(5);


            MemoryInstruction temp = new MemoryInstruction(mo, logicalAddress, content, duration);
            p.addInstruction(temp);

        }


        //End Process Instruction
        p.addInstruction(new EndInstruction());

        processes.add(p);
    }

    public void stableVMSim(){
        processes.clear();
        clock = 0;
        Process p;
        Instruction temp;

        final int P0_ADDR = 100;
        final int P1_ADDR = 200;
        final int P2_ADDR = 300;
        final int P3_ADDR = 400;

        p = new Process(1, 0);
        p.setSize(512); // 8 pages long process size


        //Load three pages
        p.addInstruction(new MemoryInstruction(MemoryOperationType.LOAD, P0_ADDR, (byte) 4));
        p.addInstruction(new MemoryInstruction(MemoryOperationType.LOAD, P1_ADDR, (byte) 4));
        p.addInstruction(new MemoryInstruction(MemoryOperationType.LOAD, P2_ADDR, (byte) 4));
    
        p.addCPUInstructions(5); 
        
        // Load another page -> should make another page fault
        p.addInstruction(new MemoryInstruction(MemoryOperationType.LOAD, P3_ADDR, (byte) 4));
        temp = new IOInstruction(5); //Wait 5 cycles
        p.addInstruction(temp);

        p.addInstruction(new EndInstruction());
        processes.add(p);
    }

    public void initSimulationQueue(){
        double tp;
        Process p;
        for (int i = 0; i < MAX_SIM_PROC_CREATION_TIME; i++) {
            tp = r.nextDouble();
            if(PROB_PROC_CREATION >= tp){
                p = new Process();
                p.setTime_init(clock);
                processes.add(p);
            }
            clock++;
        }
        clock = 0;
    }
    
    public void initSimulationQueueSimple(){
        Process p;
        int cont = 0;
        for (int i = 0; i < MAX_SIM_PROC_CREATION_TIME; i++) {
            if(i % 4 == 0){
                p = new Process(cont++,-1,true);
                p.setTime_init(clock);
                processes.add(p);
            }
            clock++;
        }
        clock = 0;
    }
    
    public void initSimulationQueueSimpler() {

    processes.clear(); // Limpia la lista si ya hay procesos cargados

    Process p;
    Instruction temp;

    // === Proceso 0 ===
    p = new Process(0, 0); // ID=0, T-ini=0
    p.setSize(300);        // tamaño en memoria
    p.addCPUInstructions(6);  // CPU inicial (mitad del tiempo total 12)
    temp = new MemoryInstruction(
        MemoryOperationType.LOAD,
        100,                // dirección lógica de la tabla
        (byte) -1,          // valor no usado para LOAD
        4                   // duración (4 ciclos)
    );
    p.addInstruction(temp);
    p.addCPUInstructions(6);  // CPU final (mitad restante)
    temp = new EndInstruction();
    p.addInstruction(temp);
    processes.add(p);


    // === Proceso 1 ===
    p = new Process(1, 7); // ID=1, T-ini=7
    p.setSize(450);
    p.addCPUInstructions(14); // CPU inicial (mitad de 28)
    temp = new MemoryInstruction(
        MemoryOperationType.STORE,
        250,                // dirección lógica
        (byte) 42,          // valor a almacenar
        5                   // duración de la operación
    );
    p.addInstruction(temp);
    p.addCPUInstructions(14); // CPU final
    temp = new EndInstruction();
    p.addInstruction(temp);
    processes.add(p);


    // === Proceso 2 ===
    p = new Process(2, 12); // ID=2, T-ini=12
    p.setSize(250);
    p.addCPUInstructions(6); // CPU inicial (mitad de 13)
    temp = new MemoryInstruction(
        MemoryOperationType.LOAD,
        150,
        (byte) -1,
        4
    );
    p.addInstruction(temp);
    p.addCPUInstructions(7); // CPU final
    temp = new EndInstruction();
    p.addInstruction(temp);
    processes.add(p);


    // === Proceso 3 ===
    p = new Process(3, 18); // ID=3, T-ini=18
    p.setSize(150);
    p.addCPUInstructions(6); // CPU inicial (mitad de 12)
    temp = new MemoryInstruction(
        MemoryOperationType.STORE,
        10,
        (byte) 33,           // valor arbitrario
        4
    );
    p.addInstruction(temp);
    p.addCPUInstructions(6); // CPU final
    temp = new EndInstruction();
    p.addInstruction(temp);
    processes.add(p);


    clock = 0;
    }
    
    public void initSimulationQueueSimpler3(){
        
        
        Process p = new Process(0,0);
        p.setSize(200);
        Instruction temp;
        p.addCPUInstructions(5);
        temp = new IOInstruction(4);    
        p.addInstruction(temp);
        p.addCPUInstructions(3);
        processes.add(p);
        
        
        
        //Process 1
        p = new Process(1,5);
        p.setSize(500);
        p.addCPUInstructions(13);
        temp = new IOInstruction(5);    
        p.addInstruction(temp);
        p.addCPUInstructions(16);
        processes.add(p);
        
        
        //Process 2
        p = new Process(2,6);
        p.setSize(250);
        p.addCPUInstructions(7);
        temp = new IOInstruction(3);    
        p.addInstruction(temp);
        p.addCPUInstructions(5);
        processes.add(p);
        
        
        //Process 3
        p = new Process(3,24);
        p.setSize(800);
        p.addCPUInstructions(4);
        temp = new IOInstruction(3);    
        p.addInstruction(temp);
        p.addCPUInstructions(7);
        processes.add(p);
        
        
        
        //Process 4
        p = new Process(4,31);
        p.setSize(600);
        p.addCPUInstructions(7);
        temp = new IOInstruction(3);    
        p.addInstruction(temp);
        p.addCPUInstructions(7);
        processes.add(p);
        
        
        
        clock = 0;
    }
    
    
    
    public void initSimulationQueueSimpler2(){
        
        Process p = new Process(false);
        Instruction temp;
        p.addCPUInstructions(15);
        temp = new IOInstruction(12);    
        p.addInstruction(temp);
        p.addCPUInstructions(21);
        p.setTime_init(0);
        p.setPid(0);
        processes.add(p);
        
        
        p = new Process(false);
        p.addCPUInstructions(8);
        temp = new IOInstruction(4);    
        p.addInstruction(temp);
        p.addCPUInstructions(16);
        p.setTime_init(2);
        p.setPid(1);
        processes.add(p);
        
        p = new Process(false);
        p.addCPUInstructions(10);
        temp = new IOInstruction(15);    
        p.addInstruction(temp);
        p.addCPUInstructions(12);
        p.setTime_init(6);
        p.setPid(2);
        processes.add(p);
        
        p = new Process(false);
        p.addCPUInstructions(9);
        temp = new IOInstruction(6);    
        p.addInstruction(temp);
        p.addCPUInstructions(17);
        p.setTime_init(8);
        p.setPid(3);
        processes.add(p);
        
        clock = 0;
    }
    
    
    
    public boolean isSimulationFinished(){
        
        boolean finished = true;
        
        for (Process p : processes) {
            finished = finished && p.isFinished();
        }
        
        return finished;
    
    }

    public SimulationType getSimulationType() {
        return simType;
    }
    
    
    
    @Override
    public void run() {
        double tp;
        ArrayList<Process> ps;
        
        System.out.println("******SIMULATION START******");
        
        int i=0;
        Process temp_exec;
        int tempID;
        while(!isSimulationFinished() && i < MAX_SIM_CYCLES){//MAX_SIM_CYCLES is the maximum simulation time, to avoid infinite loops
            System.out.println("******Clock: "+i+"******");
            
            if(i == 8){
                i = i;
            }
            
            if(this.getSimulationType() == SimulationType.ALL || this.getSimulationType() == SimulationType.PROCESS_PLANNING){
                System.out.println(cpu);
                System.out.println(ioq);
            }
            
            //Crear procesos, si aplica en el ciclo actual
            ps = getProcessAtI(i);
            for (Process p : ps) {
                os.create_process(p);
                System.out.println("Process Created: "+p.getPid()+"\n"+p);
                
                showFreeMemory();
            } //If the scheduler is preemtive, this action will trigger the extraction from the CPU, is any process is there.
            
            //Actualizar el OS, quien va actualizar el Scheduler            

            os.update();
            //os.update() prepares the system for execution. It runs at the beginning of the cycle.
            
                        
            clock++;
            
            temp_exec = cpu.getProcess();
            if(temp_exec == null){
                tempID = -1;
            }else{
                tempID = temp_exec.getPid();
            }
            execution.add(tempID);
            
            //Actualizar la CPU
            cpu.update();
            
            
            ///Actualizar la IO
            ioq.update();
            
            //Las actualizaciones de CPU y IO pueden generar interrupciones que actualizan a cola de listos, cuando salen los procesos
            
            if(this.getSimulationType() == SimulationType.ALL || this.getSimulationType() == SimulationType.PROCESS_PLANNING){
                System.out.println("After the cycle: ");
                System.out.println(cpu);
                System.out.println(ioq);
            }
            i++;

        }
        System.out.println("******SIMULATION FINISHES******");
        //os.showProcesses();
        
        System.out.println("******Process Execution******");
        for (Integer num : execution) {
            System.out.print(num+" ");
        }
        System.out.println("");
        
        System.out.println("******Performance Indicators******");
        System.out.println("Total execution cycles: "+clock);
        System.out.println("CPU Utilization: "+this.calcCPUUtilization());
        System.out.println("Throughput: "+this.calcThroughput());
        System.out.println("Average Turnaround Time: "+this.calcTurnaroundTime());
        System.out.println("Average Waiting Time: "+this.calcAvgWaitingTime());
        System.out.println("Average Context Switches: "+this.calcAvgContextSwitches());
        System.out.println("Average Response Time: "+this.calcAvgResponseTime());
        System.out.println("");
        
        System.out.println("******Show Processes******");
        showProcesses();
        memory.showNotNullBytes();
        System.out.println("");

        System.out.println("******Final Free Memory******");
        showFreeMemory();
    }
    
    public void showFreeMemory(){
        if(OS.SMM == MemoryManagerType.PAGING){
            System.out.println("Free frame number: "+os.fmm.getSize());
        }else{
            System.out.println("Free Memory Slots ("+os.fmm.getSize()+"): ");
            FreeMemorySlotManager msm = (FreeMemorySlotManager)os.fmm;
            System.out.println(msm);
        }
    }
    
    public void showProcesses(){
        System.out.println("Process list:");
        StringBuilder sb = new StringBuilder();
        
        for (Process process : processes) {
            sb.append(process);
            sb.append("\n");
        }
        
        System.out.println(sb.toString());
    }
    
    
    public double calcCPUUtilization(){
        int cont=0;
        for (Integer num : execution) {
            if(num == -1)
                cont++;
        }
        
        return (execution.size()-cont)/(double)execution.size();
    }
    
    public double calcTurnaroundTime(){
        
        double tot = 0;
        
        for (Process p : processes) {
            tot = tot + (p.getTime_finished() - p.getTime_init());
        }
        
        
        return tot/processes.size();
    }
    
    public double calcThroughput(){
        return (double)processes.size()/execution.size();
    }
    
    public double calcAvgWaitingTime(){
        double tot = 0;
        
        for (Process p : processes) {
            tot = tot + ((p.getTime_finished() - p.getTime_init()) - p.getTotalExecutionTime());
        }
        
        return tot/processes.size();
    }
    
    public double calcAvgContextSwitches(){
        int cont = 1;
        int prev = execution.get(0);
        for (Integer i : execution) {
            if(prev != i){
                cont++;
                prev = i;
            }
        }
        
        return cont / (double)processes.size();
    }

    public double calcAvgResponseTime(){
        
        double tot = 0;
        int temp = 0;
        for (Process p : processes) {
            temp = execution.indexOf(p.getPid());//On which cycle did the process started execution
            tot = tot + (temp - p.getTime_init());//Difference between execution start and arrival
        }
        
        return tot/processes.size();
    }
    
    
}

