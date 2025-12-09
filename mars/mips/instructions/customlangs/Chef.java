package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.*;
import mars.mips.instructions.*;

import java.util.Scanner;




public class Chef extends CustomAssembly{


    String[] foodGroups = new String[5];


    @Override
    public String getName(){
        return "Chef Assembly";
    }


    @Override
    public String getDescription(){
        return "Simulate cooking like a professional chef";
    }


    @Override
    protected void populate(){
        instructionList.add(
                new BasicInstruction("season $t0,$t1,12",
                        "Assign imm value to register: set $t0 to ($t1 plus signed 16-bit immediate)",
                        BasicInstructionFormat.I_FORMAT,
                        "111111 sssss fffff tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int placeholder = RegisterFile.getValue(operands[1]);
                                int value = operands[2] << 16 >> 16;
                                int result = placeholder + value;
                                if ((placeholder >= 0 && value >= 0 && result < 0)
                                        || (placeholder < 0 && value < 0 && result >= 0))
                                {
                                    throw new ProcessingException(statement,
                                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                RegisterFile.updateRegister(operands[0], result);


                            }
                        }));


        instructionList.add(
                new BasicInstruction("mix $t0,$t1,$t2",
                        "Add two registers: add $t1 and $t2, storing result in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int add1 = RegisterFile.getValue(operands[1]);
                                int add2 = RegisterFile.getValue(operands[2]);


                                int result = add1 + add2;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));


        instructionList.add(
                new BasicInstruction("chop $t0,$t1,$t2",
                        "Divide with two registers: divide $t1 by $t2 and store result in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 011010",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int result = RegisterFile.getValue(operands[0]);
                                int div1 = RegisterFile.getValue(operands[1]);
                                int div2 = RegisterFile.getValue(operands[2]);


                                result = div1 / div2;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));




        instructionList.add(
                new BasicInstruction("spill $t0,$t1,$t2",
                        "Subtract with two registers: subtract $t1 by $t2 and store result in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100010",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int sub1 = RegisterFile.getValue(operands[1]);
                                int sub2 = RegisterFile.getValue(operands[2]);


                                int result = sub1 - sub2;


                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));


        instructionList.add(
                new BasicInstruction("bake $t0,$t1,$t2",
                        "Multiply two registers: multiply $t1 and $t2 together, and store result in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 011000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();


                                int mult1 = RegisterFile.getValue(operands[1]);
                                int mult2 = RegisterFile.getValue(operands[2]);


                                int result = mult1 * mult2;


                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("taste $t0,$t1,label",
                        "Branch if equal: Branch to statement at label's address if $t0 and $t1 are equal",
                        BasicInstructionFormat.I_FORMAT,
                        "000100 sssss fffff tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();


                                if (RegisterFile.getValue(operands[0])
                                        == RegisterFile.getValue(operands[1]))
                                {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));


        instructionList.add(
                new BasicInstruction("serve",
                        "syscall: Call function in kernel",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 001100",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int code = RegisterFile.getValue(2);     //$v0
                                int arg = RegisterFile.getValue(4);         //$a0


                                if (code == 1) {
                                    System.out.println("Serving " + arg + "dishes");
                                }
                                else if (code == 4) {
                                    System.out.println("Serving dish: " + arg);
                                }
                            }
                        }));


        instructionList.add(
                new BasicInstruction("to_fridge $t0,12($t1)",
                        "Store word: ($t1 + offset) in memory = $t0",
                        BasicInstructionFormat.I_FORMAT,
                        "111111 sssss fffff tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();


                                int valueToStore = RegisterFile.getValue(operands[0]);
                                int base = RegisterFile.getValue(operands[1]);
                                int offset = (operands[2] << 16) >> 16;
                                int address = base + offset;


                                try {
                                    Globals.memory.setWord(address, valueToStore);
                                }
                                catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));


        instructionList.add(
                new BasicInstruction("from_fridge $t0,12($t1)",
                        "Load word: $t0 = ($t1 + offset) in memory",
                        BasicInstructionFormat.I_FORMAT,
                        "100011 sssss fffff tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();


                                int base = RegisterFile.getValue(operands[1]);
                                int offset = (operands[2] << 16) >> 16;
                                int address = base + offset;


                                try {
                                    int value = Globals.memory.getWord(address);
                                    RegisterFile.updateRegister(operands[0], value);
                                }
                                catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));


        instructionList.add(
                new BasicInstruction("plate $t0,$t1",
                        "move: $t0 = value stored in $t1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 100001",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();


                                int valueToMove = RegisterFile.getValue(operands[1]);
                                RegisterFile.updateRegister(operands[0], valueToMove);
                            }
                        }));


        instructionList.add(
                new BasicInstruction("protein",
                        "Choose your protein for the meal",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 111011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Scanner scnr = new Scanner(System.in);
                                System.out.print("Enter the protein that you would like to add to your dish: ");
                                String protein = scnr.nextLine();
                                foodGroups[0] = protein;
                            }
                        }));


        instructionList.add(
                new BasicInstruction("grains",
                        "Choose your grains for the meal",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 101011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Scanner scnr = new Scanner(System.in);
                                System.out.print("Enter the type of grains that you would like to add to your dish: ");
                                String grains = scnr.nextLine();
                                foodGroups[1] = grains;
                            }
                        }));


        instructionList.add(
                new BasicInstruction("vegetable",
                        "Choose your vegetable for the meal",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 011001",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Scanner scnr = new Scanner(System.in);
                                System.out.print("Enter the vegetable that you would like to add to your dish: ");
                                String vegetable = scnr.nextLine();
                                foodGroups[2] = vegetable;
                            }
                        }));


        instructionList.add(
                new BasicInstruction("fruit",
                        "Choose your fruit for the meal",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 101000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Scanner scnr = new Scanner(System.in);
                                System.out.print("Enter the fruit that you would like to add to your dish: ");
                                String fruit = scnr.nextLine();
                                foodGroups[3] = fruit;
                            }
                        }));


        instructionList.add(
                new BasicInstruction("dairy",
                        "Choose your dairy for the meal",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 110011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Scanner scnr = new Scanner(System.in);
                                System.out.print("Enter the dairy that you would like to add to your dish: ");
                                String dairy = scnr.nextLine();
                                foodGroups[4] = dairy;
                            }
                        }));


        instructionList.add(
                new BasicInstruction("present",
                        "Present your dish to your guests",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 111001",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                System.out.println("Tonight I have prepared for you all a world-class, gourmet, " +
                                        "masterpiece of a dish that will surely transform your night. " +
                                        "This dish incorporates " + foodGroups[0] + ", with " + foodGroups[1]
                                        + ", " + foodGroups[2] + ", and " + foodGroups[4] + " for added " +
                                        "flavor, as well as " + foodGroups[3] + " as garnish, all being of " +
                                        "the highest quality. Enjoy!");
                            }
                        }));


        instructionList.add(
                new BasicInstruction("salt $t0",
                        "Multiply register value by two: multiply $t0 by two, and store result in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 001000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();


                                int result = RegisterFile.getValue(operands[0]);
                                result *= 2;


                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));


        instructionList.add(
                new BasicInstruction("pepper $t0",
                        "Divide register value by two: divide $t0 by two, and store result in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 001111",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();


                                int result = RegisterFile.getValue(operands[0]);
                                result /= 2;


                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));


        instructionList.add(
                new BasicInstruction("blend $t0,$t1,$t2",
                        "Calculate average of the values of two registers: add $t1 and $t2, divide by two, and store result in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "101111 sssss ttttt fffff 00000 000000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();


                                int firstVal = RegisterFile.getValue(operands[1]);
                                int secondVal = RegisterFile.getValue(operands[2]);
                                int result = (firstVal + secondVal) / 2;


                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));


        instructionList.add(
                new BasicInstruction("throw_out $t0",
                        "Set register value to 0: $t0 = 0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 101101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                RegisterFile.updateRegister(operands[0], 0);
                            }
                        }));
    }
}
