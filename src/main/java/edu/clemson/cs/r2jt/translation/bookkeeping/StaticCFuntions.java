/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping;

/**
 *
 * @author
 * Mark
 * T
 */
public class StaticCFuntions {

    public static String getString(String moduleName) {
        if (moduleName.contains("Alt_Rev")) {
            return "int main(){\n"
                    + "     r_type_ptr SS = StackStackFac->Stack->init(StackStackFac->Stack);\n"
                    + "    r_type_ptr S1 = Stack_Fac.core->Stack->init(Stack_Fac.core->Stack);\n"
                    + "    r_type_ptr S2 = Stack_Fac.core->Stack->init(Stack_Fac.core->Stack);\n"
                    + "    StackStackFac->Push(S1, SS, StackStackFac);\n"
                    + "    StackStackFac->Push(S2, SS, StackStackFac);\n"
                    + "    StackStackFac->Pop(S1, SS, StackStackFac);\n"
                    + "    StackStackFac->Pop(S1, SS, StackStackFac); }";
        }
        else
            return "int main(){ }";
    }
}
