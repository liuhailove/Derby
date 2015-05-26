package org.apache.derbyBuild;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**

A quick and dirty class for generating a properties file from the maint
property in DBMS.properties and release.properties. Useful for getting
the values of the third and fourth parts of the version number into Ant
as separate properties. It puts the third value into the output properties
as the property "interim", and the fourth value as "point".

Usage: java maintversion2props input_properties_file output_properties_file

**/
public class maintversion2props
{
	public static void main(String[] args) throws IOException
	{
		InputStream is=new FileInputStream(args[0]);
		Properties p=new Properties();
		p.load(is);
		String maint="";
		if(args[0].indexOf("DBMS")>0)
		{
			maint=p.getProperty("derby.version.maint");
		}
		else if(args[0].indexOf("release")>0)
		{
			maint=p.getProperty("maint");
		}
        Properties p2 = new Properties();
        p2.setProperty("interim", Integer.toString(Integer.parseInt(maint) / 1000000));
        p2.setProperty("point", Integer.toString(Integer.parseInt(maint) % 1000000));
        OutputStream os = new FileOutputStream(args[1]);
        p2.store(os, ""); 
		
	}
		
		

}
