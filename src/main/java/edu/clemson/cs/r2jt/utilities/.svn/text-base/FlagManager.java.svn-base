package edu.clemson.cs.r2jt.utilities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A <code>FlagManager</code> represents an actual configuration of flags, as
 * provided by the user.  It may be queried to find out what flags are set, or
 * what the values of parameters are.</p>
 */
public class FlagManager {

	private Map<Flag, FlagInfo> myFlags = new HashMap<Flag, FlagInfo>();
	
	private final String[] myRemainingArgs;
	
	/**
	 * <p>Creates a new <code>FlagManager</code> based on the provided user
	 * command-line arguments and the current state of 
	 * {@link FlagDependencies FlagDependencies}.</p>
	 * 
	 * @param args The command line arguments provided by the user.
	 * 
	 * @throws FlagDependencyException If the set flags are not acceptable for
	 *              some reason.
	 *              
	 * @throws IllegalStateException If <code>FlagDependencies</code> has not
	 *              yet been sealed with a call to 
	 *              {@link FlagDependencies#seal() seal()}.
	 */
	public FlagManager(String[] args) 
			throws FlagDependencyException {
		
		if (!FlagDependencies.isSealed()) {
			throw new IllegalStateException("FlagDependencies must be sealed " +
					"with a call to seal() before arguments can be processed.");
		}
		
		myRemainingArgs = processArguments(args);
	}
	
	/**
	 * <p>All arguments in the originally provided array of arguments that
	 * looked like flags but didn't match a known flag, up to the first argument
	 * that didn't look like a flag, concatenated with all arguments starting
	 * at the first argument that didn't look like a flag to the end.</p>
	 * 
	 * @return Those arguments this <code>FlagManager</code> didn't process.
	 */
	public String[] getRemainingArgs() {
		return myRemainingArgs;
	}
	
	/**
	 * <p>Returns <code>true</code> <strong>iff</strong> the provided flag is
	 * set.  Flags can be set either directly by the user, implicitly because
	 * they have default parameter values, or implicitly by another flag that
	 * was set and had an <em>implies</em> relationship with the provided flag.
	 * </p>
	 * 
	 * @param f The flag to check whether or not it is set.
	 * 
	 * @return <code>true</code> <strong>iff</strong> the flag is set.
	 */
	public boolean isFlagSet(Flag f) {
		return myFlags.containsKey(f);
	}
	
	Flag getFlagReason(Flag f) {
		return myFlags.get(f).reason;
	}
	
	/**
	 * <p>Returns the argument provided for a particular parameter name of the
	 * given flag.</p>
	 * 
	 * @param f The flag whose argument we would like.
	 * @param parameterName The parameter whose value we would like, as original
	 *                      provided to the <code>Flag</code> constructor.
	 *                      
	 * @return The value of the named parameter, either as provided explicitly
	 *         by the user or implicitly via the default.
	 *         
	 * @throws NullPointerException If the given flag is not set.
	 */
	public String getFlagArgument(Flag f, String parameterName) {
		return myFlags.get(f).arguments.get(parameterName);
	}
	
	private String[] processArguments(String[] args) 
			throws FlagDependencyException {
		
		List<String> unrecognized = new LinkedList<String>();
		
		int argIndex = 0;
		int argsLength = args.length;
		String curArg;
		
		try {
			while (argIndex < argsLength) {
				
				curArg = args[argIndex];
			
				try {
					//This will throw an IllegalArgumentException if curArg
					//doesn't look like a flag, and a NoSuchEntryException if it
					//looks like a flag but isn't one registered with the system
					Flag f = FlagDependencies.getFlagByInvocation(curArg);
					
					//Auxiliary flags may not be turned on via the command line
					if (f.getType() == Flag.Type.AUXILIARY) {
						noSuchFlag("-" + f.getName());
					}
					
					argIndex = f.process(this, args, argIndex + 1);
				}
				catch (NoSuchEntryException e) {
					argIndex = 
						updateUnrecognized(args, argIndex, unrecognized);
				}
			}
		}
		catch (IllegalArgumentException iaex) {
			//We reached the first non-flag argument, which better be the file 
			//name to compile.  Regardless, everything from here on out should
			//be passed back for further scrutiny
			for (int i = argIndex; i < argsLength; i++) {
				unrecognized.add(args[i]);
			}
		}
		
		finalizeAndCheck();
		
		return unrecognized.toArray(new String[0]);
	}
	
	private void noSuchFlag(String name) throws FlagDependencyException {
		throw new FlagDependencyException("No such flag: " + name + ".");
	}
	
	private void finalizeAndCheck() throws FlagDependencyException {
		setFlagsWithDefaults();
		
		setImpliedFlags();
		
		for (Flag f : myFlags.keySet()) {
			FlagDependencies.checkFlagDependencies(f, this);
		}
	}
	
	private void setImpliedFlags() {
		
		Map<Flag, FlagInfo> needToBeSet = new HashMap<Flag, FlagInfo>();
		
		Set<Flag> implications;
		do {
			needToBeSet.clear();
			
			for (Map.Entry<Flag, FlagInfo> setFlag : myFlags.entrySet()) {
				implications = FlagDependencies.getFlagImplications(
						setFlag.getKey());
				
				for (Flag implication : implications) {
					if (!myFlags.containsKey(implication)) {
						needToBeSet.put(implication,
								new FlagInfo(setFlag.getValue().reason));
					}
				}
			}
			
			myFlags.putAll(needToBeSet);
			
		} while (needToBeSet.size() > 0);
	}
	
	private void setFlagsWithDefaults() {
		Set<Flag> flagsWithDefaults = 
			FlagDependencies.getFlagsWithDefaultArguments();
		
		for (Flag f : flagsWithDefaults) {
			if (!myFlags.containsKey(f)) {
				turnOnFlag(f, f, f.getDefaultArguments());
			}
		}
	}
	
	void turnOnFlag(Flag f, Flag reason, Map<String, String> argumentMapping) {
		myFlags.put(f, new FlagInfo(reason, 
				new HashMap<String, String>(argumentMapping)));
	}
	
	private static int updateUnrecognized(String[] args, int startIndex, 
			List<String> unrecognized) {
		
		unrecognized.add(args[startIndex]);
		
		startIndex++;
		
		int argsLength = args.length;
		while (startIndex < argsLength && !args[startIndex].startsWith("-")) {
			unrecognized.add(args[startIndex]);
			startIndex++;
		}
		
		return startIndex;
	}
	
	private class FlagInfo {
		public final Map<String, String> arguments;
		public final Flag reason;
		
		public FlagInfo(Flag reason) {
			arguments = new HashMap<String, String>();
			this.reason = reason;
		}
		
		public FlagInfo(Flag reason, Map<String, String> arguments) {
			this.arguments = new HashMap<String, String>(arguments);
			this.reason = reason;
		}
	}
}
