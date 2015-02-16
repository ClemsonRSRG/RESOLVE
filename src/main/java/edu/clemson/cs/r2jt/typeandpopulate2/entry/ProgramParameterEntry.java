package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.Token;

import java.util.Map;

public class ProgramParameterEntry extends SymbolTableEntry {

    public static enum ParameterMode {
        ALTERS {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { ALTERS, CLEARS };
            }
        },
        UPDATES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { UPDATES, CLEARS, RESTORES,
                        PRESERVES };
            }
        },
        REPLACES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { REPLACES, CLEARS };
            }
        },
        CLEARS {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { CLEARS };
            }
        },
        RESTORES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { RESTORES, PRESERVES };
            }
        },
        PRESERVES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { PRESERVES };
            }
        },
        EVALUATES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { EVALUATES };
            }
        },
        TYPE {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { TYPE };
            }
        };

        public boolean canBeImplementedWith(ParameterMode o) {
            return contains(getValidImplementationModes(), o);
        }

        private static boolean contains(Object[] os, Object o) {
            boolean result = false;

            int i = 0;
            int osLength = os.length;
            while (!result && i < osLength) {
                result = os[i].equals(o);
                i++;
            }

            return result;
        }

        public abstract ParameterMode[] getValidImplementationModes();
    }

    private final PTType myDeclaredType;
    private final ParameterMode myPassingMode;
    private final TypeGraph myTypeGraph;

    private final MathSymbolEntry myMathSymbolAlterEgo;
    private final ProgramVariableEntry myProgramVariableAlterEgo;

    public ProgramParameterEntry(TypeGraph g, String name,
                                 ResolveAST definingElement,
                                 ModuleIdentifier sourceModule, PTType type, ParameterMode mode) {
        super(name, definingElement, sourceModule);

        myTypeGraph = g;
        myDeclaredType = type;
        myPassingMode = mode;

        MTType typeValue = null;
        if (mode == ParameterMode.TYPE) {
            typeValue = new PTGeneric(type.getTypeGraph(), name).toMath();
        }

        //TODO: Probably need to recajigger this to correctly account for any
        //      generics in the defining context
        myMathSymbolAlterEgo =
                new MathSymbolEntry(type.getTypeGraph(), name,
                        Quantification.NONE, definingElement, type.toMath(),
                        typeValue, null, null, sourceModule);

        myProgramVariableAlterEgo =
                new ProgramVariableEntry(getName(), getDefiningElement(),
                        getSourceModuleIdentifier(), myDeclaredType);
    }

    @Override
    public ProgramParameterEntry toProgramParameterEntry(Token l) {
        return this;
    }

    @Override
    public MathSymbolEntry toMathSymbolEntry(Token l) {
        return myMathSymbolAlterEgo;
    }

    @Override
    public ProgramVariableEntry toProgramVariableEntry(Token l) {
        return myProgramVariableAlterEgo;
    }

    @Override
    public ProgramTypeEntry toProgramTypeEntry(Token l) {

        ProgramTypeEntry result;
        if (!myPassingMode.equals(ParameterMode.TYPE)) {
            //This will throw an appropriate error
            result = super.toProgramTypeEntry(l);
        }
        else {
            result =
                    new ProgramTypeEntry(myTypeGraph, getName(),
                            getDefiningElement(), getSourceModuleIdentifier(),
                            new MTNamed(myTypeGraph, getName()), new PTGeneric(
                            myTypeGraph, getName()));
        }

        return result;
    }

    public ParameterMode getParameterMode() {
        return myPassingMode;
    }

    public PTType getDeclaredType() {
        return myDeclaredType;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a program parameter";
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        return new ProgramParameterEntry(myTypeGraph, getName(),
                getDefiningElement(), getSourceModuleIdentifier(),
                myDeclaredType.instantiateGenerics(genericInstantiations,
                        instantiatingFacility), myPassingMode);
    }

    @Override
    public String toString() {
        return "" + myPassingMode + myDeclaredType;
    }
}
