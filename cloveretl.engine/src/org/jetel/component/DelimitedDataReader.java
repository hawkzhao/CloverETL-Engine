/*
*    jETeL/Clover - Java based ETL application framework.
*    Copyright (C) 2002  David Pavlis
*
*    This program is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.jetel.component;
import java.io.*;
import org.w3c.dom.NamedNodeMap;
import org.jetel.graph.*;
import org.jetel.data.DataRecord;
import org.jetel.data.DelimitedDataParser;
import org.jetel.exception.BadDataFormatExceptionHandler;
import org.jetel.exception.BadDataFormatExceptionHandlerFactory;
import org.jetel.exception.ComponentNotReadyException;


/**
 *  <h3>Delimited Data Reader Component</h3>
 *
 * <!-- Parses specified input data file and broadcasts the records to all connected out ports -->
 *
 * <table border="1">
 *  <th>Component:</th>
 * <tr><td><h4><i>Name:</i></h4></td>
 * <td>DelimitedDataReader</td></tr>
 * <tr><td><h4><i>Category:</i></h4></td>
 * <td></td></tr>
 * <tr><td><h4><i>Description:</i></h4></td>
 * <td>Parses specified input data file and broadcasts the records to all connected out ports.</td></tr>
 * <tr><td><h4><i>Inputs:</i></h4></td>
 * <td></td></tr>
 * <tr><td><h4><i>Outputs:</i></h4></td>
 * <td>At least one output port defined/connected.</td></tr>
 * <tr><td><h4><i>Comment:</i></h4></td>
 * <td></td></tr>
 * </table>
 *  <br>  
 *  <table border="1">
 *  <th>XML attributes:</th>
 *  <tr><td><b>type</b></td><td>"DELIMITED_DATA_READER"</td></tr>
 *  <tr><td><b>id</b></td><td>component identification</td>
 *  <tr><td><b>fileURL</b></td><td>path to the input file</td>
 *  <tr><td><b>DataPolicy</b></td><td>specifies how to handle misformatted or incorrect data.  'Strict' aborts processing, 'Controlled' logs the entire record while processing continues, and 'Lenient' attempts to set incorrect data to default values while processing continues.</td>
 *  </tr>
 *  </table>  
 *
 *  <h4>Example:</h4> 
 *  <pre>&lt;Node type="DELIMITED_DATA_READER" id="InputFile" fileURL="/tmp/mydata.dat" /&gt;</pre>
 * @author     dpavlis
 * @since    April 4, 2002
 * @see		org.jetel.data.DelimitedDataParser
 */
public class DelimitedDataReader extends Node {

	public static final String COMPONENT_TYPE="DELIMITED_DATA_READER";
	
	private static final int OUTPUT_PORT=0;
	private String fileURL;
	
	private DelimitedDataParser parser;
	
	public DelimitedDataReader(String id,String fileURL){
		super(id);
		this.fileURL=fileURL;
		parser=new DelimitedDataParser();
	}
	
	/**
	 *  Gets the Type attribute of the SimpleCopy object
	 *
	 * @return    The Type value
	 * @since     April 4, 2002
	 */
	public String getType() {
		return COMPONENT_TYPE;
	}


	/**
	 *  Main processing method for the SimpleCopy object
	 *
	 * @since    April 4, 2002
	 */
	public void run() {
		// we need to create data record - take the metadata from first output port
		DataRecord record=new DataRecord(getOutputPort(OUTPUT_PORT).getMetadata());
		record.init();
		
		try{
			// till it reaches end of data or it is stopped from outside
			while((record=parser.getNext(record))!=null){
				//broadcast the record to all connected Edges
				writeRecordBroadcast(record);
			}
		}
		catch(IOException ex){
			resultMsg=ex.getMessage();
			resultCode=Node.RESULT_ERROR;
			closeAllOutputPorts();
			return;
		}catch(Exception ex){
			resultMsg=ex.getMessage();
			resultCode=Node.RESULT_FATAL_ERROR;
			return;
		}
		// we are done, close all connected output ports to indicate end of stream
		broadcastEOF();
		parser.close();
		if (runIt) resultMsg="OK"; else resultMsg="STOPPED";
		resultCode=Node.RESULT_OK;
	}	


	/**
	 *  Description of the Method
	 *
	 * @since    April 4, 2002
	 */
	public void init() throws ComponentNotReadyException {
		// test that we have at least one output port
		if (outPorts.size()<1){
			throw new ComponentNotReadyException(getID()+": atleast one output port has to be defined!");
		}
		// try to open file & initialize data parser
		try{
			parser.open(new FileInputStream(fileURL), getOutputPort(OUTPUT_PORT).getMetadata());
		}
		catch(FileNotFoundException ex){
			throw new ComponentNotReadyException(getID()+"IOError: "+ex.getMessage());
		}
		
	}
	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 * @since     May 21, 2002
	 */
	public org.w3c.dom.Node toXML() {
		// TODO
		return null;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  nodeXML  Description of Parameter
	 * @return          Description of the Returned Value
	 * @since           May 21, 2002
	 */
	public static Node fromXML(org.w3c.dom.Node nodeXML) {
		NamedNodeMap attribs=nodeXML.getAttributes();
		DelimitedDataReader aDelimitedDataReader = null;
		
		if (attribs!=null){
			String id=attribs.getNamedItem("id").getNodeValue();
			String fileURL=attribs.getNamedItem("fileURL").getNodeValue();
			String aDataPolicy = attribs.getNamedItem("DataPolicy").getNodeValue();
			if ((id!=null) && (fileURL!=null)){
				aDelimitedDataReader = new DelimitedDataReader(id,fileURL);

				if(aDataPolicy != null) {
					aDelimitedDataReader.addBDFHandler(BadDataFormatExceptionHandlerFactory.getHandler(aDataPolicy));
				}
			}
		}
		return aDelimitedDataReader;
	}
	
	/**
	 * Adds BadDataFormatExceptionHandler to behave according to DataPolicy.
	 * @param handler
	 */
	private void addBDFHandler(BadDataFormatExceptionHandler handler) {
		parser.addBDFHandler(handler);
	}
}

