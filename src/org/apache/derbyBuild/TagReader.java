package org.apache.derbyBuild;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;

/**
 * <p>
 * This little machine is constructed from an xml/html document/element and is used to parse
 * content inside that element. This machine advances through the element,
 * letting the operator look for substrings. This machine was created to read
 * elements in JIRA's html reports, which are not well-formed xml documents.
 * </p>
 *
 * <p>
 * To operate the TagReader, keep positioning on substrings, following some
 * pattern which allows you to navigate to the content you need.
 * </p>
 */
public class TagReader
{
    /////////////////////////////////////////////////////////////////////////
    //
    //  CONSTANTS
    //
    /////////////////////////////////////////////////////////////////////////
	
	private static final int NOT_FOUND = -1;
	
    /////////////////////////////////////////////////////////////////////////
    //
    //  STATE
    //
    /////////////////////////////////////////////////////////////////////////
	
	private String _content;
	private int _cursor;
	
    /////////////////////////////////////////////////////////////////////////
    //
    //  CONSTRUCTORS
    //
    /////////////////////////////////////////////////////////////////////////
	
	/** Wrap a TagReader around a piece of content */
	public TagReader(String content)
	{
		if( content==null)
		{
			content="";
		}
		_content=content;
		
		init();
	}
	
	/** Wrap a TagReader around the content siphoned out of a stream */
	public TagReader(InputStream is) throws IOException
	{
		StringWriter buffer=new StringWriter();
		
		while(true)
		{
			int nextChar=is.read();
			if(nextChar<0)
				break;
			buffer.write(nextChar);
		}
		
		_content=buffer.toString();
		
		is.close();
		
		init();
	}
	
	 /** Initialization common to all constructors */
	private void init()
	{
		reset();
	}
	
    /////////////////////////////////////////////////////////////////////////
    //
    //  PUBLIC BEHAVIOR
    //
    /////////////////////////////////////////////////////////////////////////
	
    /**
     * <p>
     * Resets the reader to the beginning of the content.
     * </p>
     */
	public void reset()
	{
		_cursor=0;
	}
	
    /**
     * <p>
     * Starting at the current position, search for a substring in the content. If the substring is found, positions
     * the reader AFTER the substring and returns that new cursor position. If the
     * substring is not found, does not advance the cursor, but returns -1.
     * </p>
     * @throws ParseException 
     */
	public int position(String tag,boolean failIfNotFound) throws ParseException
	{
		int retval=NOT_FOUND;
		if(_cursor< _content.length())
		{
			retval=_content.indexOf(tag,_cursor);
			if(retval<0)
			{
				retval=NOT_FOUND;
			}
			else
			{
				retval=_cursor+tag.length();
				_cursor=retval;
			}
			
		}
		if(failIfNotFound&&(retval==NOT_FOUND))
		{
			throw new ParseException("Could not found substring '"+tag+"'",_cursor);
		}
		return retval;
	}
	
    /**
     * <p>
     * Starting at the current position, search for a substring in the content. If the
     * substring is found, return everything from the cursor up to the start of the substring
     * and position the reader AFTER the substring. If the substring is not found, return null
     * and do not alter the cursor.
     * </p>
     * @throws ParseException 
     */
	public String getUpTill(String tag,boolean failIfNotFound) throws ParseException
	{
		int oldCursor=_cursor;
		int endIdx=position(tag,failIfNotFound);
		if(endIdx<0)
		{
			return null;
		}
		return _content.substring(oldCursor,endIdx-tag.length());
	}
	
    /////////////////////////////////////////////////////////////////////////
    //
    //  MINIONS
    //
    /////////////////////////////////////////////////////////////////////////

}
