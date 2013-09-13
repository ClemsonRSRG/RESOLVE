package edu.clemson.cs.r2jt.init;

import java.io.File;
import java.util.HashMap;

import edu.clemson.cs.r2jt.Main;
import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.ProcedureDec;
import edu.clemson.cs.r2jt.absyn.UsesItem;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.collections.Stack;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.MetaFile;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.proving2.ProverListener;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.scope.ModuleScope;
import edu.clemson.cs.r2jt.scope.OldSymbolTable;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.FlagDependencyException;
import edu.clemson.cs.r2jt.utilities.FlagManager;

/**
 * Encapsulates all the 'pieces' associated with the compilation
 * of user-provided file(s) -- makes explicit everything from the
 * symbol table, to the type graph to be used in the compilation
 * process.
 *
 * <p>Unlike the now-deprecated {@link Environment Environment}
 * which used lots of static variables that made instantiating
 * the compiler a mess under concurrency, this replacement class
 * is intended to serve the same purpose but be designed to be
 * instantiated.</p>
 *
 * <p>Features of {@link Environment Environment} should be moved
 * to this class so that {@link Environment Environment} can be
 * safely removed in favor of this class.</p>
 */
public class CompileEnvironment {

    /**
     * <p>The current configuration of environment flags,as defined
     * by the user.</p>
     */
    public final FlagManager flags;

    /**
     * <p>A small class that summarizes which stages of compilation
     * (translation, vc generation, proving, etc) were successfully
     * carried out and which weren't.</p>
     */
    private CompileReport myCompileReport = new CompileReport();

    /**
     * <p>A web-interface specific variable that </p>
     */
    private String myTargetSource = null;
    private String myTargetFileName = null;
    private String myCurrentTargetFileName = null;

    private ProverListener myListener = null;
    private TypeGraph myTypeGraph = null;
    private ErrorHandler err;

    // Variables brought in from the old Environment class.
    private Map<ModuleID, ModuleRecord> map = new Map<ModuleID, ModuleRecord>();

    private Map<File, ModuleID> fmap = new Map<File, ModuleID>();
    private List<File> unparsables = new List<File>();
    private Stack<ModuleID> stack = new Stack<ModuleID>();
    private File mainDir = null;
    private File targetFile = null;
    private List<String> javaFiles = new List<String>();
    private List<ModuleID> modules = new List<ModuleID>();
    private HashMap<String, MetaFile> myUserFileMap = null;

    // -----------------------------------------------------------
    // Compiler flags
    // -----------------------------------------------------------
    private boolean showBuild = false;
    private boolean showEnv = false;
    private boolean showTable = false;
    private boolean showBind = false;
    private boolean showImports = false;
    private boolean showIndirect = false;
    private boolean perf = false;
    private boolean isabelle = false; // left out the isabelle() method
    private boolean debugOff = false;

    private String outputFile = null;

    /**
     * <p>Array of the names of the Std Facilities to be automatically 
     * included by ImportScanner/Populator. Add additional Std_Facs to 
     * the array. Note that if the Fac is named <code>Std_XXX_Fac</code>, 
     * just add "XXX" to the array. This array should never be altered 
     * while running</p>.
     * 
     * Order matters. Files that match strings later in the array will
     * import ones earlier in the array.  So <code>Integer_Theory</code> 
     * will have <code>Boolean_Theory</code>, but <code>Boolean_Theory</code> 
     * will lack <code>Integer_Theory</code>.</p>
     * 
     * <p><strong>NOTE: This assumes that all files are formatted:</strong>
     * <code>Std_XXX_Fac</code>, <code>XXX_Template</code>, etc</p>.
     */
    private final String[] stdUses =
            { "Boolean", "Integer", "Character", "Char_Str" };

    /**
     * <p>Automatically generated Std_Fac dependency list for
     * ImportScanner/Populator.visitModuleDec()</p>.
     */
    private List<List<UsesItem>> stdUsesDepends;

    public List<ProcedureDec> encounteredProcedures;

    private ScopeRepository mySymbolTable = null;

    public CompileEnvironment(String[] args) throws FlagDependencyException {

        flags = new FlagManager(args);
    }

    public void setErrorHandler(ErrorHandler err) {
        this.err = err;
    }

    public ErrorHandler getErrorHandler() {
        return err;
    }

    public void setSymbolTable(ScopeRepository table) {
        if (table == null) {
            throw new IllegalArgumentException(
                    "Symbol table may not be set to null!");
        }

        if (mySymbolTable != null) {
            throw new IllegalStateException(
                    "Symbol table may only be set once!");
        }
        mySymbolTable = table;
    }

    public void setTypeGraph(TypeGraph graph) {
        myTypeGraph = graph;
    }

    public TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    public ScopeRepository getSymbolTable() {
        return mySymbolTable;
    }

    public void setCompileReport(CompileReport report) {
        myCompileReport = report;
    }

    public CompileReport getCompileReport() {
        return myCompileReport;
    }

    public void setTargetSource(String source) {
        myTargetSource = source;
    }

    public void setTargetFileName(String name) {
        myTargetFileName = name;
    }

    public void setCurrentTargetFileName(String name) {
        myCurrentTargetFileName = name;
    }

    public String getTargetSource() {
        return myTargetSource;
    }

    public String getTargetFileName() {
        return myTargetFileName;
    }

    public String getCurrentTargetFileName() {
        return myCurrentTargetFileName;
    }

    public String[] getRemainingArgs() {
        return flags.getRemainingArgs();
    }

    /**
     * <p>Sets a map of user files when used with the web interface.</p>
     */
    public void setUserFileMap(HashMap<String, MetaFile> userFileMap) {
        myUserFileMap = userFileMap;
    }

    public boolean isUserFile(String key) {
        if (myUserFileMap != null) {
            return myUserFileMap.containsKey(key);
        }
        return false;
    }

    public MetaFile getUserFileFromMap(String key) {
        return myUserFileMap.get(key);
    }

    public void setProverListener(ProverListener listener) {
        myListener = listener;
    }

    public ProverListener getProverListener() {
        return myListener;
    }

    /**
     * <p>Returns the array of Std_Fac names.</p>
     */
    public String[] getStdUses() {
        if (flags.isFlagSet(Main.FLAG_NO_STANDARD_IMPORT)) {
            return new String[0];
        }
        else {
            return stdUses;
        }
    }

    /**
     * <p>Returns the Lists of <code>List</code> of <code>UsesItem</code> 
     * which are the Std_Fac dependencies and creates an empty list of lists 
     * if it is empty.</p>
     */
    public List<List<UsesItem>> getStdUsesDepends() {
        if (stdUsesDepends == null) {
            stdUsesDepends = new List<List<UsesItem>>();
            for (int i = 0; i < stdUses.length; i++) {
                List<UsesItem> temp = new List<UsesItem>();
                stdUsesDepends.add(temp);
            }
        }
        return stdUsesDepends;
    }

    /**
     * <p>Sets the <code>stdUsesDepends</code> lists to the provided updated list.</p>
     * @param list The <code>List</code> of <code>List</code> of <code>UsesItem</code>
     *             which will be assigned to the global <code>stdUsesDepends</code>.
     */
    public void setStdUsesDepends(List<List<UsesItem>> list) {
        stdUsesDepends = list;
    }

    public List<OldSymbolTable> getSymbolTables() {
        List<OldSymbolTable> stList = new List<OldSymbolTable>();
        //Map<ModuleID, ModuleRecord> map = myOldEnvironment.getMap();
        for (java.util.Map.Entry<ModuleID, ModuleRecord> st : map.entrySet()) {
            stList.add(st.getValue().getSymbolTable());
        }
        return stList;
    }

    public void clearStopFlags() {
        showBuild = false;
        showEnv = false;
        showTable = false;
        showBind = false;
        perf = false;
    }

    /**
     * <p>Sets the main directory to the specified directory.</p>
     */
    public void setMainDir(File mainDir) {
        this.mainDir = mainDir;
    }

    /** Sets the target file to the specified file. */
    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    /** Returns the main directory. */
    public File getMainDir() {
        return mainDir;
    }

    /** Returns the target file. */
    public File getTargetFile() {
        return targetFile;
    }

    /** Name the output file. */
    public void setOutputFileName(String outputFile) {
        this.outputFile = outputFile;
    }

    /** Sets the main directory to the specified directory. */
    public String getOutputFilename() {
        return outputFile;
    }

    /**
     * Prints the pathname of the file relative to the default resolve
     * directory.
     */
    public String getResolveName(File file) {
        if (mainDir == null) {
            return this.toString();
        }
        else {
            File par = mainDir.getParentFile();
            String path = file.toString();
            String mask = par.toString();

            assert path.startsWith(mask) : "path does not start with mask: "
                    + path;
            return path.substring(mask.length() + 1);
        }
    }

    /**
     * Indicates that the module dec should be displayed.
     */
    public void setShowBuildFlag() {
        clearStopFlags();
        showBuild = true;
    }

    /**
     * Indicates that the compilation environment should be displayed.
     */
    public void setShowEnvFlag() {
        clearStopFlags();
        showEnv = true;
    }

    /**
     * Indicates that the symbol table should be displayed (before binding).
     */
    public void setShowTableFlag() {
        clearStopFlags();
        showTable = true;
    }

    /**
     * Indicates that the symbol table should be displayed after binding.
     */
    public void setShowBindFlag() {
        clearStopFlags();
        showBind = true;
    }

    /**
     * Indicates that imported module tables should be displayed if the main
     * symbol table is displayed.
     */
    public void setShowImportsFlag() {
        showImports = true;
    }

    /**
     * Indicates that debug output should be turned off to the maximum amount
     * this is possible.
     */
    public void setDebugOff() {
        debugOff = true;
    }

    /**
     * Indicates that indirect types should display the scopes they are bound
     * to.
     */
    public void setShowIndirectFlag() {
        showIndirect = true;
    }

    /**
     * For performance.
     */
    public void setPerformanceFlag() {
        perf = true;
    }

    /**
     * Returns true if the module dec will be displayed, false otherwise.
     */
    public boolean showBuild() {
        return showBuild;
    }

    /**
     * Returns true if the environment will be displayed, false otherwise.
     */
    public boolean showEnv() {
        return showEnv;
    }

    /**
     * Returns true if the symbol table (before binding) will be displayed,
     * false otherwise.
     */
    public boolean showTable() {
        return showTable;
    }

    /**
     * Returns true if the symbol table (after binding) will be displayed, false
     * otherwise.
     */
    public boolean showBind() {
        return showBind;
    }

    /**
     * Returns true if import tables should displayed when the main table is
     * displayed.
     */
    public boolean showImports() {
        return showImports;
    }

    /**
     * Returns true if indirect types should display the scopes they are bound
     * to.
     */
    public boolean showIndirect() {
        return showIndirect;
    }

    /**
     * For performance.
     */
    public boolean perf() {
        return perf;
    }

    /**
     * Returns true iff we should suppress debug output.
     */
    public boolean debugOff() {
        return debugOff;
    }

    /**
     * Returns true if the specified file is present in the compilation
     * environment but could not be successfully parsed.
     */
    /*public boolean containsUnparsable(File file) {
    	return myOldEnvironment.containsUnparsable(file);
    }*/

    /**
     * Returns true if the specified file is present in the compilation
     * environment, has an associated id and a valid module dec.
     */
    public boolean contains(File file) {
        return fmap.containsKey(file);
    }

    /**
     * Returns true if the specified module is present in the compilation
     * environment, has an associated file and a valid module dec.
     */
    public boolean contains(ModuleID id) {
        return map.containsKey(id);
    }

    /**
     * Returns true if the specified file has already been successfully
     * compiled.
     */
    public boolean compileCompleted(File file) {
        if (!fmap.containsKey(file)) {
            return false;
        }
        else {
            return map.get(fmap.get(file)).isComplete();
        }
    }

    /**
     * Returns true if compilation on the specified file has begun, has not
     * aborted, and has not completed.
     */
    public boolean compileIncomplete(File file) {
        if (!fmap.containsKey(file)) {
            return false;
        }
        else {
            return (!map.get(fmap.get(file)).isComplete() && !map.get(
                    fmap.get(file)).containsErrors());
        }
    }

    /**
     * Returns true if a compile had been attempted on the specified file and
     * was aborted due to errors.
     */
    public boolean compileAborted(File file) {
        if (unparsables.contains(file)) {
            return true;
        }
        if (!fmap.containsKey(file)) {
            return false;
        }
        else {
            return map.get(fmap.get(file)).containsErrors();
        }
    }

    /**
     * Returns the module id associated with the specified file.
     */
    public ModuleID getModuleID(File file) {
        return fmap.get(file);
    }

    /**
     * Returns the file associated with the specified module.
     */
    public File getFile(ModuleID id) {
        return map.get(id).getFile();
    }

    /**
     * Returns the module dec associated with the specified module.
     */
    public ModuleDec getModuleDec(ModuleID id) {
        return map.get(id).getModuleDec();
    }

    /**
     * Returns a list of visible theories for the specified module.
     */
    public List<ModuleID> getTheories(ModuleID id) {
        return map.get(id).getTheories();
    }

    /**
     * Returns the symbol table associated with the specified module.
     */
    public OldSymbolTable getSymbolTable(ModuleID id) {
        return map.get(id).getSymbolTable();
    }

    /**
     * Returns the map of symbol tables.
     */
    public Map<ModuleID, ModuleRecord> getMap() {
        return map;
    }

    /**
     * Returns the module scope associated with the specified module.
     */
    public ModuleScope getModuleScope(ModuleID id) {
        assert map.get(id).getSymbolTable() != null : "symbol table for id is null";
        return map.get(id).getSymbolTable().getModuleScope();
    }

    /**
     * Constructs a record containing the module id, the file, and the module
     * dec, and places it in the module environment. Also places the module into
     * a stack that indicates compilation has begun on this module but has not
     * completed.
     */
    public void constructRecord(ModuleID id, File file, ModuleDec dec) {
        ModuleRecord record = new ModuleRecord(id, file);
        record.setModuleDec(dec);
        assert !map.containsKey(id) : "map already contains key";
        assert !fmap.containsKey(file) : "fmap already contains file";
        map.put(id, record);
        fmap.put(file, id);
        stack.push(id);

        if (!debugOff) {
            err.message("Construct record: " + id.toString()); //DEBUG
        }
    }

    /**
     * Associates a list of visible theories with the specified module. This
     * method may only be called once during the life of a module. The visible
     * theories must be accessible to a module before population begins.
     */
    public void setTheories(ModuleID id, List<ModuleID> theories) {
        ModuleRecord record = map.get(id);
        record.setTheories(theories);
    }

    /**
     * Places the symbol table for an associated module into the environment and
     * pops the module from the compilation stack, indicating that compilation
     * has been completed for this module.
     */
    public void completeRecord(ModuleID id, OldSymbolTable table) {
        ModuleRecord record = map.get(id);
        record.setSymbolTable(table);
        ModuleID id2 = stack.pop();
        assert id == id2 : "id != id2";

        if (!debugOff) {
            err.message("Complete record: " + id.toString()); //DEBUG
        }
    }

    /**
     * Adds a file to the environment which failed to parse.
     */
    public void abortCompile(File file) {
        if (fmap.containsKey(file)) {
            abortCompile(fmap.get(file));
        }
        else {
            unparsables.add(file);
            err.message("Add unparsable: " + file.getName()); //DEBUG
        }
    }

    /**
     * Aborts compilation of a module which parsed without errors, and pops this
     * module from the compilation stack.
     */
    public void abortCompile(ModuleID id) {
        map.get(id).setErrorFlag();
        ModuleID id2 = stack.pop();
        assert id == id2 : "id != id2";
        err.message("Abort compile: " + id.toString()); //DEBUG
    }

    /**
     * Returns a string representation of the compilation environment.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("=============================="
                + "==============================\n");
        sb.append("Compilation environment for " + targetFile.getName() + "\n");
        sb.append("=============================="
                + "==============================\n");
        sb.append("Main directory: " + mainDir.getName() + "\n");
        sb.append("------------------------------"
                + "------------------------------\n");
        sb.append("Unparsable files: " + getResolveNames(unparsables) + "\n");
        sb.append("Compile stack: " + stack.toString() + "\n");
        Iterator<ModuleID> i = map.keyIterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleRecord record = map.get(id);
            //sb.append(getResolveName(record.getFile()) + " ");
            sb.append(id.toString());
            if (record.isComplete()) {
                sb.append(" is complete");
            }
            else {
                sb.append(" is incomplete");
            }
            if (record.containsErrors()) {
                sb.append(" due to errors");
            }
            sb.append(".\n");
            sb.append("    Theories: " + record.getTheories().toString());
            sb.append("\n");
        }
        sb.append("------------------------------"
                + "------------------------------\n");
        return sb.toString();
    }

    private String getResolveNames(List<File> files) {
        StringBuffer sb = new StringBuffer();
        sb.append("( ");
        Iterator<File> i = files.iterator();
        while (i.hasNext()) {
            File file = i.next();
            sb.append(getResolveName(file));
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" )");
        return sb.toString();
    }

    /**
     * Returns a string of the modules in the compile stack, beginning with the
     * the specified module and ending with the module at the top of the stack.
     * The modules have arrows between them to indicate dependencies. This
     * method is used when reporting a circular module dependency error.
     */
    public String printStackPath(ModuleID id) {
        StringBuffer sb = new StringBuffer();
        Stack<ModuleID> stack2 = new Stack<ModuleID>();
        boolean printID = false;
        ModuleID id2 = null;
        while (!stack.isEmpty()) {
            id2 = stack.pop();
            stack2.push(id2);
        }
        sb.append("(");
        while (!stack2.isEmpty()) {
            id2 = stack2.pop();
            if (id2 == id) {
                printID = true;
            }
            if (printID) {
                sb.append(id2.toString());
                if (!stack2.isEmpty()) {
                    sb.append(" -> ");
                }
            }
            stack.push(id2);
        }
        sb.append(")");
        return sb.toString();
    }

    public void addModule(ModuleID mod) {
        modules.addUnique(mod);
    }

    public void printModules() {
        Iterator<ModuleID> it = modules.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().getName().toString());
        }
    }
}
