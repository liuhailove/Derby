package org.apache.derbyBuild;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class MessageVetter
{
    /**
     * <p>
     * Check all the message translations in the specified directories for
     * common problems. Assume that all properties files in the directories
     * are message translations.
     * </p>
     *
     * <p>
     * If a problem is found, an error will be raised.
     * </p>
     *
     * @param args names of the directories to check
     */
	
    /**
     * A regular expression that matches a single-quote character that is
     * neither preceeded nor followed by another single-quote character. Used
     * by {@link #checkSingleQuotes(java.lang.String, java.lang.String)} to
     * verify that messages contain two single-quotes in order to produce a
     * single apostrophe (dictated by {@code java.text.MessageFormat}).
     */
	private static final Pattern LONE_QUOTE_PATTERN =
			Pattern.compile("^'[^']|[^']'[^']|[^']'$");
	
    /**
     * A regular expression that matches a single-quote character that have
     * no adjacent single-quote or curly brace character. Used by
     * {@link #checkSingleQuotes(java.lang.String, java.lang.String)} to
     * verify that all single-quotes are either correctly formatted apostrophes
     * or used for quoting curly braces, as required by
     * {@code java.text.MessageFormat}.
     */
    private static final Pattern LONE_QUOTE_ALLOWED_PATTERN =
            Pattern.compile("^'[^'{}]|[^'{}]'[^'{}]|[^'{}]'$");
    /**
     * A set of message identifiers in whose messages single-quotes may legally
     * appear with no adjacent single-quote character. This will be messages
     * where the single-quotes are needed to quote curly braces that should
     * appear literally in the message text.
     */
    private static final Set<String> LONE_QUOTE_ALLOWED=new HashSet<String>();
	
	
	
}
