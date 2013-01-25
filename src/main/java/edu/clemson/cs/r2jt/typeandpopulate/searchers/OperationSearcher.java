package edu.clemson.cs.r2jt.typeandpopulate.searchers;

import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

public class OperationSearcher implements TableSearcher<OperationEntry> {

    private final String myQueryName;
    private final Location myQueryLocation;
    private final List<PTType> myActualArgumentTypes;

    public OperationSearcher(PosSymbol name, List<PTType> argumentTypes) {
        myQueryName = name.getName();
        myQueryLocation = name.getLocation();
        myActualArgumentTypes = new LinkedList<PTType>(argumentTypes);
    }

    @Override
    public boolean addMatches(SymbolTable entries,
            List<OperationEntry> matches, SearchContext l)
            throws DuplicateSymbolException {

        if (entries.containsKey(myQueryName)) {
            try {
                OperationEntry operation =
                        entries.get(myQueryName).toOperationEntry(
                                myQueryLocation);

                if (argumentsMatch(operation.getParameters())) {
                    //We have a match at this point
                    if (!matches.isEmpty()) {
                        throw new DuplicateSymbolException();
                    }

                    matches.add(operation);
                }
            }
            catch (SourceErrorException see) {
                //No problem, just don't include it in the result
            }
        }

        return false;
    }

    private boolean argumentsMatch(
            ImmutableList<ProgramParameterEntry> formalParameters) {

        boolean result =
                (formalParameters.size() == myActualArgumentTypes.size());

        if (result) {
            Iterator<ProgramParameterEntry> formalParametersIter =
                    formalParameters.iterator();
            Iterator<PTType> actualArgumentTypeIter =
                    myActualArgumentTypes.iterator();

            PTType actualArgumentType, formalParameterType;
            while (result && formalParametersIter.hasNext()) {
                actualArgumentType = actualArgumentTypeIter.next();
                formalParameterType =
                        formalParametersIter.next().getDeclaredType();

                result = actualArgumentType.acceptableFor(formalParameterType);
            }
        }

        return result;
    }
}
