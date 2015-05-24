package org.apache.derbyBuild;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

public class propertyconfig
{
	public static final String header =
			"######## This is a generated file, do not edit.\n" +
			"#\n# This file is generated as by propertyConfig\n" +
			"#\n";

	public static final String footer =
		"\n######## This is a generated file, do not edit.\n";
	
	public static void main(String[] args) throws IOException
	{
		if(args.length!=3)
		{
			printUsageAndExit();
		}
		File masterfile=new File(args[0]);
		File outputfile=new File(args[2]);
		if(!masterfile.exists())
		{
			printUsageAndExit();
		}
		
		// OK, got the input cleared up, now do the processing
		Properties masterProp=new Properties();
		FileInputStream is=new FileInputStream(masterfile);
		try
		{
			masterProp.load(is);
		} 
		finally
		{
			if(is!=null)
				is.close();
		}
		process(masterProp,args[1],outputfile);
	}
	private static void printUsageAndExit()
	{
		StringBuffer buf = new StringBuffer(400);

		buf.append("Usage propertyConfig <masterFile> <config> <outputFile>\n")
			.append("masterFile must be a pre-existing properties file ")
			.append("containing all the modules properites\n")
			.append("config must be a configuration defined in ")
			.append("org.apache.derby.modules.properties.\n")
			.append("outputFile must not be a pre-existing properties file.\n\n")
			.append("propertyConfig will generate the outputFile based on")
			.append("the masterfile and the configuration specified.")
			.append("\n\nE.g., java propertyConfig dbms.properties cloudsync dbms.cloudsync.properties\n");
		

		System.out.println(buf.toString());
		System.exit(1);
	}
	/**
	 * For each module with a particular tag in derby.module.<tag>, see if
	 * there is any configuration restriction.  If there is no
	 * cloudscape.config.<tag> property, then this module should be present in
	 * all configurations.  If there is a cloudscape.config.<tag>, then this
	 * module should only be present in the configurations listed.
	 *
	 * <br>If this module should be present or this configuration, then gather
	 * up all the properties belong to this module and send it to the output
	 * file.
	 *
	 */
	private static void process(Properties moduleList,String config,File outputfile)throws IOException
	{
		Properties outputProp=new Properties();
		// copy this code from
		// org.apache.derby.impl.services.monitor.BaseMonitor 
		//
		for(Enumeration e= moduleList.propertyNames();e.hasMoreElements();)
		{
			String key=(String)e.nextElement();
			if(key.startsWith("derby.module."))
			{
				String tag=key.substring("derby.module.".length());
				
				// Check to see if it has any configuration requirements 
				String configKey="cloudscape.config.".concat(tag);
				String configProp=moduleList.getProperty(configKey);
				
				boolean match=false;
				if(configProp!=null)
				{
					StringTokenizer st=new StringTokenizer(configProp,",");
					while(st.hasMoreElements())
					{
						String s=st.nextToken().trim();
						// if config spec says all, it should not have other
						// configurations
						if(s.equalsIgnoreCase("all")&&
								!configProp.trim().equals("all"))
						{
							System.out.println("illegal config specification "+key);
							System.exit(3);
						}
						
						// if config spec says none, it should not have other
						// configurations
						if(s.equalsIgnoreCase("none")&&
								!configProp.trim().equals("none"))
						{
							System.out.println("illegal config specification "+key);
							System.exit(4);
						}
						
						if(s.equalsIgnoreCase(config)||
								s.equalsIgnoreCase("all"))
						{
							match=true;
							break;
						}
						
					}
					
				}
				else
				{
					// no config property, this module goes to all configs
					System.out.println("Need config specification for "+key);
					System.exit(2);
				}
				if(match)
				{
					// gather up all relavant properties and put it in
					// outputProp
					
					// derby.module.<tag>
					outputProp.put(key, moduleList.getProperty(key));
					
					// don't output cloudscape.config.<tag>
					// that line only has meaning to this program

					// derby.env.classes.<tag>
					String envKey="derby.env.classes.".concat(tag);
					if(moduleList.getProperty(envKey)!=null)
					{
						outputProp.put(envKey, moduleList.getProperty(envKey));
					}
					// derby.env.jdk.<tag>
					// 
					// some of the configs only support one java env.  Some modules
					// have alternate implementation for running on java1 and
					// java2 platforms.  If we get rid of, say, the java2
					// implementation, then the monitor won't load the java1
					// implementation if that module specifies that it should
					// only be loaded in a java1 environment.  The result is
					// that some critical modules will be missing and the
					// database won't boot.
					//
					// the convention is, for modules that have both java1 and
					// java2 implementation, they must named the module as
					// derby.env.jdk.<name>J1 or
					// derby.env.jdk.<name>J2
					// in other words, the <tag> must end with J1 or J2.
					//
					// If a config only use one of the two implementation, then
					// this program will not put the one env.jdk line to the
					// output properties.  As a result, this one implementation
					// will be loaded when run in any environment.
					//
					// This does not apply to any module that only has one
					// implementation that runs on a specific jdk environment.
					//
					//derby.env.jdk.<tag>
					envKey="derby.env.jdk.".concat(tag);
					if(moduleList.getProperty(envKey)!=null)
					{
						// by default keep the jdk env specification with the
						// config
						boolean saveEnvKey=true;
						
						// figure out if this is a tag of the form <name>J1 or
						// <name>J2.
						if(tag.endsWith("J1")||tag.endsWith("J2"))
						{
							// ok, this is a module with alternate
							// implementation for java 1 and java 2.  If this
							// config ditches one of them, then do not output
							// the env line
							int length=tag.length()-2;
							String alternateTag=tag.substring(0,length);
							
							if(tag.endsWith("J1"))
								alternateTag+="J2";
							else
								alternateTag+="J1";
							
							// see if 
							// 1) this module has an alternate impl for the 
							// other jdk and 
							// 2) this config is not going to pick it up.
							//
							
							String alternateImplKey="derby.module."+alternateTag;
							String alternateJDKEnv="derby.env.jdk."+alternateTag;
							String alternateImplConfigKey="cloudscape.config."+alternateTag;
							
							// if any of of these are not present, then we
							// don't have a problem because either there is no
							// alternate implementation, or the alternate
							// implementation is not based on jdk, or the
							// alternate jdk based implemenation will also be
							// present in this configuration
							
							if((moduleList.getProperty(alternateImplKey)!=null)&&
								(moduleList.getProperty(alternateJDKEnv)!=null)&&
								(moduleList.getProperty(alternateImplConfigKey) != null))
							{
								// there is an alternate impl that is jdk based
								// and it has a config tag.  Let's see if it is
								// part of this config.
								
								String alternateConfigProp=moduleList.getProperty(alternateImplConfigKey);
								
								// we know that there are
								// derby.module.<tag>J2 and
								// derby.module.<tag>J1 and
								// derby.env.jdk.<tag>J2 and
								// derby.env.jdk.<tag>J1 and
								// cloudscape.config.<tag>J2 and 
								// cloudscape.config.<tag>J1
								StringTokenizer st2=new StringTokenizer(alternateConfigProp,",");
								boolean ok=false;
								
								while(st2.hasMoreElements())
								{
									String s=st2.nextToken().trim();
									
									if(s.equalsIgnoreCase(config)||
									   s.equalsIgnoreCase("all"))
									{
										ok=true;
										break;
									}
								}
								// the alternate module impl is not part of
								// this config, do not save the jdk env key
								if(!ok)
									saveEnvKey=false;
							}
									
									
						}
						if(saveEnvKey)
							outputProp.put(envKey,moduleList.getProperty(envKey));
					}
					// NOTE, if other types of properties are added to
					// modules.properties, be sure to add it here too.
				}
			}
		}
		FileOutputStream os=new FileOutputStream(outputfile);
		try
		{
			outputProp.store(os,
							 header.concat(" #	confi is ").concat(config).concat(footer));
		}
		finally
		{
			if(os!=null)
				os.close();
		}
	}
}
