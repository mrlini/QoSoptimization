package org.utcluj.bpel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * This class is used to create {@link BPELProcess} instances.
 * 
 * @author Florin Pop
 *
 */

public class BPELProcessFactory {
	
	/**
	 * 
	 * Creates a {@link BPELProcess} instance from a BPEL file.
	 * 
	 * @param path the path of the BPEL file.
	 * @return the generated BPEL process instance.
	 * 
	 * @throws FileNotFoundException if the BPEL file doesn't exist.
	 */
	public static BPELProcess fromFile(String path) throws FileNotFoundException {
		
				
		return (BPELProcess) xstream().fromXML(new FileInputStream(path));	
	}
	
	/**
	 * Writes a {@link BPELProcess} instance to a BPEL file.
	 * 
	 * @param process the process to write.
	 * @param path the file path.
	 * 
	 * @throws FileNotFoundException if the destination file can't be created.
	 */
	public static void toFile(BPELProcess process, String path) throws FileNotFoundException {
	
		toFile(process, path, xstream());
	}
	
	/**
	 * Writes a {@link BPELProcess} instance to a BPEL file.
	 * 
	 * @param process the process to write.
	 * @param path the file path.
	 * @param xStream the streamer object.
	 * 
	 * @throws FileNotFoundException if the destination file can't be created.
	 */
	public static void toFile(BPELProcess process, String path, XStream xStream) throws FileNotFoundException {
		
		xStream.toXML(process, new FileOutputStream(path));
	}
	
	/**
	 * Creates a streamer object used to read and write BPEL processes.
	 * 
	 * @return the streamer object.
	 */
	public static XStream xstream() {
	
		XStream xs = new XStream();
		xs.alias("bpel:process", BPELProcess.class);
		xs.alias("bpel:sequence", BPELSequence.class);
		xs.alias("bpel:flow", BPELFlow.class);
		xs.alias("bpel:invoke", BPELInvoke.class);
		xs.alias("bpel:if", BPELIf.class);
		xs.alias("bpel:elseif", BPELElseIf.class);
		xs.alias("bpel:else", BPELElse.class);
		xs.alias("bpel:while", BPELWhile.class);
		
		xs.useAttributeFor(BPELActivity.class, "name");
		xs.useAttributeFor(BPELCondition.class, "probability");
		xs.useAttributeFor(BPELCycle.class, "repeat");
		
		xs.useAttributeFor(BPELProcess.class, "targetNamespace");
		xs.useAttributeFor(BPELProcess.class, "xmlNS");
		xs.aliasAttribute("xmlns:bpel", "xmlNS");
		
		xs.omitField(BPELProcess.class, "bpel:import");
		xs.omitField(BPELProcess.class, "bpel:partnerLinks");
		xs.omitField(BPELProcess.class, "bpel:variables");
		xs.omitField(BPELProcess.class, "bpel:receive");
		xs.omitField(BPELSequence.class, "bpel:receive");
		
		xs.omitField(BPELActivity.class, "parent");
		xs.omitField(BPELActivity.class, "services");
		xs.omitField(BPELInvoke.class, "type");
		
		xs.addImplicitCollection(BPELActivity.class, "activities");
		
		return xs;
	}
}
