package org.apache.derbyBuild;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
* A convenience wrapper around an XML Document Element. Provides some utility
* methods for common operations on Element trees.
*/
public class ElementFacade
{
	private Element root;
	
    /**
     * Construct a new ElementFacade from an Element.
     * @param r - the Element to wrap
     */
	public ElementFacade(Element r)
	{
		root=r;
	}
	
    /**
     * Construct a new ElementFacade from a Document (extract the top Element)
     * @param d document to get Element from
     * @throws java.lang.Exception
     */
	public ElementFacade(Document d) throws Exception
	{
		this(d.getDocumentElement());
	}
	
    /**
     * Lookup the Element subtree that starts with the specified tag. If more
     * than one, or no such tags exist an IllegalArgumentException is thrown.
     * @param tag to look up in wrapped tree
     * @return Element subtree rooted at the specified tag
     * @throws java.lang.Exception
     */
	public Element getElementByTagName(String tag)throws Exception
	{
		NodeList matchingTags=root.getElementsByTagName(tag);
		final int length=matchingTags.getLength();
		if (length != 1) 
		{
            throw new IllegalArgumentException("Tag `" + tag + "' occurs " +
                    length + " times in Document.");
        }
		return (Element)matchingTags.item(0);
	}

    /**
     * Lookup the text (as String) identified by the specified tag. If more
     * than one, or no such tags exist an IllegalArgumentException is thrown.
     * @param tag to look up in wrapped tree
     * @return text corresponding to the specified tag
     * @throws java.lang.Exception
     */
	public String getTextTagName(String tag) throws Exception
	{
		return getElementByTagName(tag).getFirstChild().getNodeName();
	}
    /**
     * Produce a list of the texts specified by the
     * instances of tag in the wrapped tree. An empty list is retured if
     * there are no instances of tag in the tree.
     * @param tag to look up in wrapped tree
     * @return list of texts corresponding to the specified tag
     * @throws java.lang.Exception
     */
	public List<String>getTextListByName(String tag) throws Exception
	{
		NodeList matchingTags=root.getElementsByTagName(tag);
		final int length=matchingTags.getLength();
		ArrayList<String> tagValues=new ArrayList<String>();
		for(int i=0;i<length;i++)
		{
			tagValues.add(matchingTags.item(i).getFirstChild().getNodeValue());
		}
		return tagValues;
	}
	public static void main(String[] args)
	{
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder db=factory.newDocumentBuilder();
			Document document=db.parse(new File("arapbase_config.xml"));
			ElementFacade ef=new ElementFacade(document);
			ArrayList<String> tagValues=(ArrayList<String>) ef.getTextListByName("book");
			System.out.println(ef.getTextTagName("book"));
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
