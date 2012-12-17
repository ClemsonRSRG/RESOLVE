package edu.clemson.cs.r2jt.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A <code>Flag</code> represents a command-line flag for the RESOLVE
 * compiler.  Each flag has a name, which indicates what the flag will be
 * called from the command line (for instance a <code>Flag</code> named "myflag"
 * will be turned on by adding "-myflag" as an option from the command line.)
 * </p>
 * 
 * <p>Each flag also has a <em>section</em> and a <em>description</em>, which
 * are used to display information about flags to the user.  All flags with the
 * same section string will be grouped together under that heading.  Each flag's
 * description will be displayed to explain what it does.</p>
 * 
 * <p>Some flags take parameters.  An example would be a flag that lets the
 * user specify the output directory.  Hypothetically let's name that flag
 * "outdir."  We expect that after giving this flag, the user will provide a
 * directory, like this: "-outdir /home/user/bin/".  Flags may be specified to
 * take any number of parameters including zero.  Each parameter is given a 
 * <code>String</code> name and its value can be retrieved with a call to 
 * {@link FlagManager#getFlagArgument(Flag, String) getFlagArgument()} from an 
 * instance of <code>FlagManager</code>.  Parameters may optionally be provided
 * default values.  If default values are provided, the flag is considered to be
 * always set, even if the user doesn't turn it on directly.  However, if the
 * user chooses to turn on the flag from the command line, his provided values
 * will be used for the parameters, otherwise the defaults will be used.</p>
 * 
 * <p>If the help screen displayed all available flags, there would be very many
 * indeed.  Some flags shouldn't be shown to the user, either because they are
 * intended for developers, or because they are intended for advanced users.
 * <code>Flag</code>s may be hidden on the basic help page by making them 
 * <em>hidden</em>.  Hidden flags will only be displayed when the user requests
 * the extended help page.  A flag may be hidden by using one of the
 * constructors that takes a <code>Flag.Type</code> and giving it the type
 * <code>HIDDEN</code>.</p>
 * 
 * <p>Occasionally, it is useful to have certain flags that only the compiler
 * can set.  For instance, several flags might put the compiler into a certain
 * "mode" and we'd like to check if that mode is on without checking each of
 * those flags individually.  For this purpose, <em>auxiliary</em> flags can be
 * created.  An auxiliary flag is created in the same way as a hidden one, 
 * except the <code>AUXILIARY</code> type is specified.  Auxiliary flags do not
 * appear in either the help or extended help pages and users cannot turn them
 * on.  They can only be turned on if they are implied by another flag.</p>
 */
public class Flag {
	
	/**
	 * <p>The type of the <code>Flag</code>.</p>
	 */
	public enum Type { 
		
		/**
		 * <p>Normal flags are displayed on the main help page and can be set by
		 * the user.</p>
		 */
		NORMAL {
			protected boolean shouldDisplay(boolean extended) {
				return true;
			}
		},
		
		/**
		 * <p>Hidden flags are displayed only on the extended help page and can 
		 * be set by the user.  Use this mode for flags intended for developers
		 * or advanced users.</p>
		 */
		HIDDEN {
			protected boolean shouldDisplay(boolean extended) {
				return extended;
			}
		},
		
		/**
		 * <p>Auxiliary flags are not displayed on any help page and cannot 
		 * be set by the user.  Auxiliary flags can only be set by being implied
		 * by another flag that the user <em>can</em> set.</p>
		 */
		AUXILIARY {
			protected boolean shouldDisplay(boolean extended) {
				return false;
			}
		};
		
		protected abstract boolean shouldDisplay(boolean extended);
	};
	
	/**
	 * <p>The text used to invoke this flag from the command line.  So, for
	 * example "-prove" is the invocation for the prove flag.</p>
	 */
	public final String invocation;
	
	private final String mySection;
	private final String myName;
	private final String myDescription;
	private final String[] myParameterNames;
	private final Map<String, String> myDefaultArgumentMapping;
	private final Type myType;
	
	/**
	 * <p>Creates a new zero-parameter <code>Flag</code> with the given section,
	 * name, description, and type.</p>
	 * 
	 * @param section     The section where this flag should appear in the help 
	 *                    page.
	 * @param name        The name the user should use to invoke the flag.
	 * @param description The description displayed to the user on the help 
	 *                    page.
	 * @param t           The type of this flag.
	 * 
	 * @throws NullPointerException If any argument is <code>null</code>.
	 * 
	 * @throws IllegalStateException If <code>FlagDependencies.seal()</code> has
	 *                               already been called.
	 *                               
	 * @throws DuplicateEntryException If a flag with this name already exists.
	 */
	public Flag(String section, String name, String description, Type t) {
		this(section, name, description, new String[0], null, false, t);
	}
	
	/**
	 * <p>Creates a new <code>Flag</code> with the given section, name, 
	 * description, parameter names, and type.</p>
	 * 
	 * @param section     The section where this flag should appear in the help 
	 *                    page.
	 * @param name        The name the user should use to invoke the flag.
	 * @param description The description displayed to the user on the help 
	 *                    page.
	 * @param parameterNames The names of the parameters this flag should take,
	 *                    used to retrieve the supplied values later.  This flag
	 *                    will take a number of parameters equal to 
	 *                    <code>parameterNames.length()</code>.
	 * @param t           The type of this flag.
	 * 
	 * @throws NullPointerException If any argument is <code>null</code>.
	 * 
	 * @throws IllegalStateException If <code>FlagDependencies.seal()</code> has
	 *                               already been called.
	 *                               
	 * @throws DuplicateEntryException If a flag with this name already exists.
	 */
	public Flag(String section, String name, String description, 
			String[] parameterNames, Type t) {
		this(section, name, description, parameterNames, null, false, t);
	}
	
	/**
	 * <p>Creates a new <code>Flag</code> with the given section, name, 
	 * description, parameter names, default arguments, and type.</p>
	 * 
	 * @param section     The section where this flag should appear in the help 
	 *                    page.
	 * @param name        The name the user should use to invoke the flag.
	 * @param description The description displayed to the user on the help 
	 *                    page.
	 * @param parameterNames The names of the parameters this flag should take,
	 *                    used to retrieve the supplied values later.  This flag
	 *                    will take a number of parameters equal to 
	 *                    <code>parameterNames.length()</code>.
	 * @param defaultArguments A list of default values for the parameters.  The
	 *                    length of this array must be exactly the length of
	 *                    <code>parameterNames</code>.  A flag with default
	 *                    arguments is always set.
	 * @param t           The type of this flag.
	 * 
	 * @throws IllegalArgumentException If <code>parameterNames.length() !=
	 *              defaultArguments.length()</code>.
	 * 
	 * @throws NullPointerException If any argument is <code>null</code>.
	 * 
	 * @throws IllegalStateException If <code>FlagDependencies.seal()</code> has
	 *                               already been called.
	 *                               
	 * @throws DuplicateEntryException If a flag with this name already exists.
	 */
	public Flag(String section, String name, String description, 
			String[] parameterNames, String[] defaultArguments, Type t) {
		
		this(section, name, description, parameterNames, defaultArguments, true,
				t);
	}
	
	/**
	 * <p>Creates a new <code>Flag</code> with the given section, name, 
	 * description, parameter names, and default arguments.  It's type is
	 * assumed to be <code>NORMAL</code>.</p>
	 * 
	 * @param section     The section where this flag should appear in the help 
	 *                    page.
	 * @param name        The name the user should use to invoke the flag.
	 * @param description The description displayed to the user on the help 
	 *                    page.
	 * @param parameterNames The names of the parameters this flag should take,
	 *                    used to retrieve the supplied values later.  This flag
	 *                    will take a number of parameters equal to 
	 *                    <code>parameterNames.length()</code>.
	 * @param defaultArguments A list of default values for the parameters.  The
	 *                    length of this array must be exactly the length of
	 *                    <code>parameterNames</code>.  A flag with default
	 *                    arguments is always set.
	 * @param t           The type of this flag.
	 * 
	 * @throws IllegalArgumentException If <code>parameterNames.length() !=
	 *              defaultArguments.length()</code>.
	 * 
	 * @throws NullPointerException If any argument is <code>null</code>.
	 * 
	 * @throws IllegalStateException If <code>FlagDependencies.seal()</code> has
	 *                               already been called.
	 * 
	 * @throws DuplicateEntryException If a flag with this name already exists.
	 */
	public Flag(String section, String name, String description, 
			String[] parameterNames, String[] defaultArguments) {
		
		this(section, name, description, parameterNames, defaultArguments, true,
				Type.NORMAL);
	}
	
	/**
	 * <p>Creates a new <code>Flag</code> with the given section, name, 
	 * description, and parameter names.  It's type is assumed to be 
	 * <code>NORMAL</code>.</p>
	 * 
	 * @param section     The section where this flag should appear in the help 
	 *                    page.
	 * @param name        The name the user should use to invoke the flag.
	 * @param description The description displayed to the user on the help 
	 *                    page.
	 * @param parameterNames The names of the parameters this flag should take,
	 *                    used to retrieve the supplied values later.  This flag
	 *                    will take a number of parameters equal to 
	 *                    <code>parameterNames.length()</code>.
	 * @param t           The type of this flag.
	 * 
	 * @throws NullPointerException If any argument is <code>null</code>.
	 * 
	 * @throws IllegalStateException If <code>FlagDependencies.seal()</code> has
	 *                               already been called.
	 * 
	 * @throws DuplicateEntryException If a flag with this name already exists.
	 */
	public Flag(String section, String name, String description,
			String[] parameterNames) {
		
		this(section, name, description, parameterNames, null, false, 
				Type.NORMAL);
	}
	
	/**
	 * <p>Creates a new zero-parameter <code>Flag</code> with the given section,
	 * name, and description.  It's type is assumed to be <code>NORMAL</code>.
	 * </p>
	 * 
	 * @param section     The section where this flag should appear in the help 
	 *                    page.
	 * @param name        The name the user should use to invoke the flag.
	 * @param description The description displayed to the user on the help 
	 *                    page.
	 *                    
	 * @throws NullPointerException If any argument is <code>null</code>.
	 * 
	 * @throws IllegalStateException If <code>FlagDependencies.seal()</code> has
	 *                               already been called.
	 *                               
	 * @throws DuplicateEntryException If a flag with this name already exists.
	 */
	public Flag(String section, String name, String description) {
		this(section, name, description, new String[0], null, false, 
				Type.NORMAL);
	}
	
	private Flag(String section, String name, String description, 
			String[] parameterNames, String[] defaultArguments, 
			boolean useDefaults, Type type) {
		
		if (useDefaults) {
			if (parameterNames.length != defaultArguments.length) {
				throw new IllegalArgumentException("parameterNames and " +
						"defaultArguments must be of the same length.");
			}
			
			myDefaultArgumentMapping = new HashMap<String, String>();
			for (int i = 0; i < parameterNames.length; i++) {
				myDefaultArgumentMapping.put(
						parameterNames[i], defaultArguments[i]);
			}
		}
		else {
			myDefaultArgumentMapping = null;
		}
		
		invocation = "-" + name;
		
		myName = name;
		myParameterNames = Arrays.copyOf(parameterNames, parameterNames.length);
		mySection = section;
		myDescription = description;

		FlagDependencies.introduceFlag(this);
		
		myType = type;
	}
	
	/**
	 * <p>Returns the type of this <code>Flag</code>.</p>
	 * 
	 * @return The <code>Flag.Type</code> of this <code>Flag</code>.
	 */
	public Type getType() {
		return myType;
	}
	
	/**
	 * <p>Returns <code>true</code> <strong>iff</strong> this <code>Flag</code>
	 * should appear in a help listing.</p>
	 * 
	 * @param extended Indicates whether the help listing is intended to be an
	 *                 "extended" listing or not.
	 *                 
	 * @return <code>true</code> <strong>iff</strong> this <code>Flag</code>
	 * should appear in a help listing of the specified type.
	 */
	public boolean shouldDisplay(boolean extended) {
		return myType.shouldDisplay(extended);
	}
	
	/**
	 * <p>Returns the name of the section in which this <code>Flag</code> should
	 * appear.</p>
	 * 
	 * @return The section name.
	 */
	public String getSection() {
		return mySection;
	}
	
	/**
	 * <p>Returns the description that should be provided for this
	 * <code>Flag</code> in a help listing.</p>
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return myDescription;
	}
	
	public int getParameterCount() {
		return myParameterNames.length;
	}
	
	/**
	 * <p>Returns a mapping from parameter names to default values.  This map
	 * is a copy and modifying it will not modify the parameters or defaults of
	 * this flag.</p>
	 * 
	 * @return The default mapping.
	 */
	public Map<String, String> getDefaultArguments() {
		Map<String, String> retval = myDefaultArgumentMapping;
		
		if (retval != null) {
			retval = new HashMap<String, String>(myDefaultArgumentMapping);
		}
		
		return retval;
	}
	
	/**
	 * <p>The name of this <code>Flag</code>, by which the user should invoke it
	 * (without the preceding hyphen).</p>
	 * 
	 * @return The name.
	 */
	public String getName() {
		return myName;
	}
	
	int process(FlagManager m, String[] args, int startIndex) 
			throws FlagDependencyException {
		Map<String, String> argumentMapping = new HashMap<String, String>();
				
		int argsLength = args.length;
		
		for (String argument : myParameterNames) {
			if (args[startIndex].startsWith("-") || startIndex > argsLength) {
				throw new FlagDependencyException("The " + myName + " flag " +
						"requires " + myParameterNames.length + " arguments.");
			}
			
			argumentMapping.put(argument, args[startIndex]);
			
			startIndex++;
		}
		
		m.turnOnFlag(this, this, argumentMapping);
		
		return startIndex;
	}

	public String toString() {
		return myName;
	}
	
	/**
	 * <p>The hash code of a <code>Flag</code> reflects only its name so that
	 * <code>Flag</code>s with the same name will have equal hash codes.</p>
	 */
	public int hashCode() {
		return myName.hashCode();
	}
}
