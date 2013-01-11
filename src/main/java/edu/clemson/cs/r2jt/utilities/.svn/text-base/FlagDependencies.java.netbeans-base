package edu.clemson.cs.r2jt.utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * <p><code>FlagDependencies</code> represents a static container for 
 * information about the dependencies between <code>Flag</code>s.  It can be
 * <em>sealed</em> to prevent further changes from being made.</p>
 */
public class FlagDependencies {
	
	private static final Map<String, Flag> myFlagNames = 
		new HashMap<String, Flag>();
	
	private static final Map<String, List<Flag>> myFlagSections =
		new HashMap<String, List<Flag>>();
	
	private static final Map<Flag, FlagInfo> myFlags = 
		new HashMap<Flag, FlagInfo>();
	
	private static final Set<Flag> myFlagsWithDefaultArguments = 
		new HashSet<Flag>();
	
	/**
	 * <p>Set to true once this class has been sealed.  Once sealed, no new 
	 * flags or dependencies may be created.</p>
	 */
	private static boolean mySealedFlag = false;
	
	/**
	 * <p>Declaring the default constructor private stops anyone from 
	 * instantiating this class, as it is intended to be used only statically.
	 * </p>
	 */
	private FlagDependencies() { }
	
	/**
	 * <p>Seals the dependencies.  Further attempts to create 
	 * <code>Flag</code>s or dependencies between them will throw an 
	 * <code>IllegalStateException</code>.</p>
	 */
	public static void seal() {
		mySealedFlag = true;
	}
	
	/**
	 * <p>Returns whether or not <code>seal()</code> has already been called.
	 * </p>
	 * 
	 * @return <code>true</code> <strong>iff</strong> <code>seal()</code> has
	 *         already been called.
	 */
	public static boolean isSealed() {
		return mySealedFlag;
	}
	
	static void introduceFlag(Flag f) {

		if (mySealedFlag) {
			throw new IllegalStateException("Cannot create a new flag after " + 
					"dependency set-up time.  Are you sure you declared your " +
					"flag static and included a call to your setUpFlags() " +
					"method in edu.clemson.cs.r2jt.setUpFlagDependencies()?");
		}
		
		if (myFlags.containsKey(f)) {
			throw new DuplicateEntryException();
		}
		
		myFlags.put(f, new FlagInfo());
		myFlagNames.put(f.getName().toLowerCase(), f);
		
		if (f.getDefaultArguments() != null) {
			myFlagsWithDefaultArguments.add(f);
		}
		
		List<Flag> sectionList = myFlagSections.get(f.getSection());
		if (sectionList == null) {
			sectionList = new LinkedList<Flag>();
			myFlagSections.put(f.getSection(), sectionList);
		}
		
		sectionList.add(f);
	}
	
	/**
	 * <p>Returns the <code>Flag</code> corresponding to a particular 
	 * invocation, that is: a hyphen plus the name of the flag, as it would
	 * appear on the command line.</p>
	 * 
	 * @param name The invocation, which must take the form of 
	 *             <code>"-" + someFlagName</code>.
	 *             
	 * @return The corresponding <code>Flag</code>
	 * 
	 * @throws NoSuchEntryException If no flag with the given name exists.
	 * 
	 * @throws IllegalArgumentException If <code>name</code> doesn't look like
	 *              an invocation (i.e., doesn't start with a hyphen).
	 */
	public static Flag getFlagByInvocation(String name) 
			throws NoSuchEntryException {
		
		if (name.charAt(0) != '-') {
			throw new IllegalArgumentException(
					"All flags must begin with a hyphen.");
		}
		
		name = name.substring(1).toLowerCase();
		
		if (!myFlagNames.containsKey(name)) {
			throw new NoSuchEntryException(name);
		}
		
		return myFlagNames.get(name);
	}

	static Set<Flag> getFlagImplications(Flag f) {
		FlagInfo info = getFlagInfo(f);
		
		return new HashSet<Flag>(info.implies);
	}
	
	static void setFlagImplications(Flag f, FlagManager m) {
		FlagInfo info = getFlagInfo(f);
		
		for (Flag implication : info.implies) {
			m.turnOnFlag(implication, m.getFlagReason(f),
					new HashMap<String, String>());
		}
	}
	
	static void checkFlagDependencies(Flag f, FlagManager m) 
			throws FlagDependencyException {
		
		FlagInfo info = getFlagInfo(f);
		
		for (Set<Flag> oneOfThese : info.requires) {
			checkThatAtLeastOneIsOn(oneOfThese, f, m);
		}
		
		for (Flag excluded : info.excludes) {
			if (m.isFlagSet(excluded)) {
				throw new FlagDependencyException("The -" + m.getFlagReason(f) + 
						" flag and -" + m.getFlagReason(excluded) + 
						" flag may not be on simultaneously.");
			}
		}
	}
	
	private static void checkThatAtLeastOneIsOn(
				Set<Flag> s, Flag f, FlagManager m) 
			throws FlagDependencyException {
		
		boolean found = false;
		
		Iterator<Flag> flags = s.iterator();
		while (!found && flags.hasNext()) {
			found = m.isFlagSet(flags.next());
		}
		
		if (!found) {
			String flagList = "";
			for (Flag requirement : s) {
				if (!flagList.equals("")) {
					flagList += ", ";
				}
				
				flagList += "-" + requirement;
			}
			
			if (s.size() == 1) {
				throw new FlagDependencyException("The -" + m.getFlagReason(f) + 
						" flag requires that the " + flagList + " flag be " +
						"set.");
			} else {
				throw new FlagDependencyException("The -" + m.getFlagReason(f) + 
						" flag requires that at least one of the following " +
						" flags be set: " + flagList);
			}
		}
	}
	
	/**
	 * <p>Returns a <code>Set</code> containing all <code>Flag</code>s that
	 * have default arguments provided (and should therefore be set 
	 * automatically).</p>
	 * 
	 * @return A <code>Set</code> of all <code>Flag</code>s with default 
	 *         arguments.
	 */
	public static Set<Flag> getFlagsWithDefaultArguments() {
		return copySet(myFlagsWithDefaultArguments);
	}
	
	/**
	 * <p>Creates a <em>requires</em> relationship between two 
	 * <code>Flag</code>s.  If the user specifies the <code>ifOn</code> flag,
	 * but not the <code>require</code> flag, an error will be generated.</p>
	 * 
	 * @param ifOn    The flag that requires the other.
	 * @param require The flag that must be on if <code>ifOn</code> is on.
	 */
	public static void addRequires(Flag ifOn, Flag require) {
		checkSealedState();
		
		Set<Flag> s = new HashSet<Flag>();
		s.add(require);
		
		addRequires(ifOn, s);
	}
	
	/**
	 * <p>Creates a <em>requires at least one of</em> relationship between
	 * a flag and a set of others.</p>
	 * 
	 * @param ifOn       The flag that requires at least one of the others.
	 * @param requireOne The flags at least one of which must be on it 
	 *                   <code>ifOn</code> is on.
	 */
	public static void addRequires(Flag ifOn, Flag[] requireOne) {
		checkSealedState();
		
		Set<Flag> s = new HashSet<Flag>();
		
		for (Flag f : requireOne) {
			s.add(f);
		}
		
		addRequires(ifOn, s);
	}
	
	/**
	 * <p>Creates a <em>requires at least one of</em> relationship between
	 * a flag and a set of others.</p>
	 * 
	 * @param ifOn       The flag that requires at least one of the others.
	 * @param requireOne The flags at least one of which must be on it 
	 *                   <code>ifOn</code> is on.
	 */
	public static void addRequires(Flag ifOn, Set<Flag> requireOne) {
		checkSealedState();
		
		//Make sure all of the required Flags have been introduced
		for (Flag f : requireOne) {
			getFlagInfo(f);
		}
		
		FlagInfo info = getFlagInfo(ifOn);
		info.requires.add(copySet(requireOne));
	}
	
	/**
	 * <code>Creates an <em>implies</em> relationship between two flags.  If
	 * the user turns on the <code>ifOn</code> flag, the <code>impliesOn</code>
	 * flag will be automatically turned on too.</code>
	 * 
	 * @param ifOn      The flag that implies the other.
	 * @param impliesOn The flag that should be turned on if <code>ifOn</code>
	 *                  is on.
	 *                  
	 * @throws IllegalArgumentException If <code>impliesOn</code> takes 
	 *              parameters.  Flags with parameters may not be implied.
	 */
	public static void addImplies(Flag ifOn, Flag impliesOn) {
		checkSealedState();
		
		//We can't imply something that takes parameters!
		if (impliesOn.getParameterCount() != 0) {
			throw new IllegalArgumentException("A flag that takes parameters " +
					"cannot be implied.  " + impliesOn + " takes " + 
					impliesOn.getParameterCount() + " parameters.");
		}
		
		//Make sure the implied flag has been introduced
		getFlagInfo(impliesOn);
		
		FlagInfo info = getFlagInfo(ifOn);
		info.implies.add(impliesOn);
	}
	
	/**
	 * <p>Creates an <em>excludes</em> relationship between two flags.  If the
	 * user specifies both <code>ifOn</code> <em>and</em> <code>exclude</code>,
	 * an error will be generated.</p>
	 * 
	 * @param ifOn    The flag that excludes the other.
	 * @param exclude The flag that must not be on if <code>ifOn</code> is on.
	 */
	public static void addExcludes(Flag ifOn, Flag exclude) {
		checkSealedState();
		
		FlagInfo info = getFlagInfo(ifOn);
		info.excludes.add(exclude);
	}
	
	private static void checkFlagHasBeenIntroduced(Flag f) {
		if (!myFlags.containsKey(f)) {
			throw new NoSuchEntryException(f);
		}
	}
	
	private static FlagInfo getFlagInfo(Flag f) {
		checkFlagHasBeenIntroduced(f);
		
		return myFlags.get(f);
	}
	
	private static <T> Set<T> copySet(Set<T> s) {
		Set<T> copy = new HashSet<T>(s);
		
		return copy;
	}
	
	private static void checkSealedState() {
		if (mySealedFlag) {
			throw new IllegalStateException("Cannot create dependencies " +
					"after dependency set-up time.  Are you sure you " +
					"included a call to your setUpFlags() method in " +
					"edu.clemson.cs.r2jt.setUpFlagDependencies()?");
		}
	}
	
	/**
	 * <p>Returns a help listing for all the flags.</p>
	 * 
	 * @param extended Whether or not the listing should represent an extended 
	 *                 listing.
	 *                 
	 * @return The listing.
	 */
	public static String getListingString(boolean extended) {
		String retval = "";
		
		List<Flag> flags;
		for (Map.Entry<String, List<Flag>> e : myFlagSections.entrySet()) {
			retval += "\n  " + e.getKey() + "\r";
			
			flags = e.getValue();
			for (Flag f : flags) {
				if (f.shouldDisplay(extended)) {
					retval += wordWrap(
							"    -" + formatInto(f.getName(), 13) + "  " + 
								f.getDescription() + "\n", 
							80, 20);
				}
			}
		}
		
		return retval;
	}
	
	private static String formatInto(String s, int space) {
		String retval = s;
		
		while (retval.length() < space) {
			retval += " ";
		}
		
		return retval;
	}
	
	private static String wordWrap(String s, int rightMargin, int indentSize) {
		String retval = "";
		
		String indent = formatInto("", indentSize);
		
		StringTokenizer tokenizer = new StringTokenizer(s, " ", true);
		int lineLength = 0;
		String curToken;
		while (tokenizer.hasMoreTokens()) {
			curToken = tokenizer.nextToken();
			
			if (lineLength + curToken.length() > rightMargin) {
				retval += "\n" + indent;
				lineLength = 0;
			}
			
			retval += curToken;
			lineLength += curToken.length();
		}
		
		return retval;
	}
	
	private static class FlagInfo {
		/**
		 * <p>A set of <strong>requires</strong> relationships.  The semantic of
		 * this map is as follows: if this flag is on then for each Set of 
		 * Flags in <code>requires</code>, at least one Flag in that Set is
		 * on.</p>
		 * 
		 * <p>That is, this is a set of "at least one on" sets.</p>
		 */
		public final Set<Set<Flag>> requires = new HashSet<Set<Flag>>();
		
		public final Set<Flag> implies = new HashSet<Flag>();
		
		public final Set<Flag> excludes = new HashSet<Flag>();
	}
}
