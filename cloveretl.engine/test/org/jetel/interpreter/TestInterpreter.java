/*
*    jETeL/Clover.ETL - Java based ETL application framework.
*    Copyright (C) 2002-2004  David Pavlis <david_pavlis@hotmail.com>
*    
*    This library is free software; you can redistribute it and/or
*    modify it under the terms of the GNU Lesser General Public
*    License as published by the Free Software Foundation; either
*    version 2.1 of the License, or (at your option) any later version.
*    
*    This library is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
*    Lesser General Public License for more details.
*    
*    You should have received a copy of the GNU Lesser General Public
*    License along with this library; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.jetel.interpreter;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetel.data.DataRecord;
import org.jetel.data.SetVal;
import org.jetel.data.lookup.LookupTable;
import org.jetel.data.lookup.LookupTableFactory;
import org.jetel.data.parser.Parser;
import org.jetel.data.primitive.CloverLong;
import org.jetel.data.primitive.DecimalFactory;
import org.jetel.data.sequence.Sequence;
import org.jetel.data.sequence.SequenceFactory;
import org.jetel.graph.TransformationGraph;
import org.jetel.graph.runtime.EngineInitializer;
import org.jetel.interpreter.ASTnode.CLVFStart;
import org.jetel.interpreter.ASTnode.CLVFStartExpression;
import org.jetel.interpreter.data.TLValue;
import org.jetel.interpreter.data.TLVariable;
import org.jetel.metadata.DataFieldMetadata;
import org.jetel.metadata.DataRecordMetadata;
import org.jetel.util.string.StringUtils;
/**
 * @author dpavlis
 * @since  10.8.2004
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestInterpreter extends TestCase {
	
	DataRecordMetadata metadata,metadata1,metaOut,metaOut1;
	DataRecord record,record1,out,out1;
    TransformationGraph graph;
    LookupTable lkp;
	
	protected void setUp() {
	    EngineInitializer.initEngine(null, null);
	    
        graph=new TransformationGraph();
        
		metadata=new DataRecordMetadata("in",DataRecordMetadata.DELIMITED_RECORD);
		
		metadata.addField(new DataFieldMetadata("Name",DataFieldMetadata.STRING_FIELD, ";"));
		metadata.addField(new DataFieldMetadata("Age",DataFieldMetadata.NUMERIC_FIELD, "|"));
		metadata.addField(new DataFieldMetadata("City",DataFieldMetadata.STRING_FIELD, "\n"));
		metadata.addField(new DataFieldMetadata("Born",DataFieldMetadata.DATE_FIELD, "\n"));
		metadata.addField(new DataFieldMetadata("Value",DataFieldMetadata.INTEGER_FIELD, "\n"));
		
		metadata1=new DataRecordMetadata("in1",DataRecordMetadata.DELIMITED_RECORD);
		
		metadata1.addField(new DataFieldMetadata("Name",DataFieldMetadata.STRING_FIELD, ";"));
		metadata1.addField(new DataFieldMetadata("Age",DataFieldMetadata.NUMERIC_FIELD, "|"));
		metadata1.addField(new DataFieldMetadata("City",DataFieldMetadata.STRING_FIELD, "\n"));
		metadata1.addField(new DataFieldMetadata("Born",DataFieldMetadata.DATE_FIELD, "\n"));
		metadata1.addField(new DataFieldMetadata("Value",DataFieldMetadata.INTEGER_FIELD, "\n"));
		
		metaOut=new DataRecordMetadata("out",DataRecordMetadata.DELIMITED_RECORD);
		
		metaOut.addField(new DataFieldMetadata("Name",DataFieldMetadata.STRING_FIELD, ";"));
		metaOut.addField(new DataFieldMetadata("Age",DataFieldMetadata.NUMERIC_FIELD, "|"));
		metaOut.addField(new DataFieldMetadata("City",DataFieldMetadata.STRING_FIELD, "\n"));
		metaOut.addField(new DataFieldMetadata("Born",DataFieldMetadata.DATE_FIELD, "\n"));
		metaOut.addField(new DataFieldMetadata("Value",DataFieldMetadata.INTEGER_FIELD, "\n"));
				
		metaOut1=new DataRecordMetadata("out1",DataRecordMetadata.DELIMITED_RECORD);
		
		metaOut1.addField(new DataFieldMetadata("Name",DataFieldMetadata.STRING_FIELD, ";"));
		metaOut1.addField(new DataFieldMetadata("Age",DataFieldMetadata.NUMERIC_FIELD, "|"));
		metaOut1.addField(new DataFieldMetadata("City",DataFieldMetadata.STRING_FIELD, "\n"));
		metaOut1.addField(new DataFieldMetadata("Born",DataFieldMetadata.DATE_FIELD, "\n"));
		metaOut1.addField(new DataFieldMetadata("Value",DataFieldMetadata.INTEGER_FIELD, "\n"));

		record = new DataRecord(metadata);
		record.init();
		record1 = new DataRecord(metadata1);
		record1.init();
		out = new DataRecord(metaOut);
		out.init();
		out1 = new DataRecord(metaOut1);
		out1.init();
		
		SetVal.setString(record,0,"  HELLO ");
		SetVal.setString(record1,0,"  My name ");
		SetVal.setInt(record,1,135);
		SetVal.setDouble(record1,1,13.5);
		SetVal.setString(record,2,"Some silly longer string.");
		SetVal.setString(record1,2,"Prague");
		SetVal.setValue(record1,3,Calendar.getInstance().getTime());
		record.getField("Born").setNull(true);
		SetVal.setInt(record,4,-999);
		record1.getField("Value").setNull(true);
        
        Sequence seq = SequenceFactory.createSequence(graph, "PRIMITIVE_SEQUENCE", 
        		new Object[]{"test",graph,"test"}, new Class[]{String.class,TransformationGraph.class,String.class});
        graph.addSequence(seq);
        
//        LookupTable lkp=new SimpleLookupTable("LKP", metadata, new String[] {"Name"}, null);
        lkp = LookupTableFactory.createLookupTable(graph, "simpleLookup", 
        		new Object[]{"LKP" , metadata ,new String[] {"Name"} , null}, new Class[]{String.class, 
        		DataRecordMetadata.class, String[].class, Parser.class});
        try {
        lkp.init();
        graph.addLookupTable(lkp);
        }catch(Exception ex) {
            throw new RuntimeException(ex);
        }
        lkp.put("one",record);
        record.getField("Name").setValue("xxxx");
        lkp.put("two", record);
  
//        RecordKey key = new RecordKey(new int[]{0}, metadata);
//        key.init();
//        lkp.setLookupKey(key);
//        DataRecord keyRecord = new DataRecord(metadata);
//        keyRecord.init();
//        keyRecord.getField(0).setValue("one");
        lkp.setLookupKey("nesmysl");
	}
	
	protected void tearDown() {
		metadata= null;
		record=null;
		out=null;
	}
	
	public void testA_lexer(){
		//new ByteArrayInputStream("\"([^\\\\|]*\\\\|){3}\"".getBytes())
		/*System.out.print("enter token string:");
		JavaCharStream cs=new JavaCharStream(System.in);
		TransformLangParserTokenManager ltm;
		ltm=new TransformLangParserTokenManager(cs);
		Token t = ltm.getNextToken();
		System.out.println(t.image);*/
		//assertEquals(t.kind,TransformLangParserConstants.STRING_LITERAL);
		
		// test expression
		System.out.print("enter exp string: ");
		
		   String strin="$Name~=\"([^\\\\|]*\\\\|){3}\"";
		   System.out.println(strin);
		   TransformLangParser parser = new TransformLangParser(metadata,strin);
		    
		   CLVFStartExpression parseTree=null;
		   try{
			   parseTree = parser.StartExpression();
		   }catch(ParseException ex){
			   ex.printStackTrace();
		   }
		   System.out.println("Initializing parse tree..");
		      parseTree.init();
		   System.out.println("Parse tree:");
		      parseTree.dump("");

		      
		      
		      /*
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      */
		      System.out.println("Finished interpreting.");
		
		
	}
	
	public void test_int(){
		System.out.println("int test:");
		String expStr = "int i; i=0; print_err(i); \n"+
						"int j; j=-1; print_err(j);\n"+
						"int minInt; minInt="+Integer.MIN_VALUE+"; print_err(minInt, true);\n"+
						"int maxInt; maxInt="+Integer.MAX_VALUE+"; print_err(maxInt, true);\n"+
						"int field; field=$Value; print_err(field);";

		try {
		      print_code(expStr);

		      TransformLangParser parser = new TransformLangParser(record.getMetadata(), expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(0,executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getInt());
		      assertEquals(-1,executor.getGlobalVariable(parser.getGlobalVariableSlot("j")).getTLValue().getNumeric().getInt());
		      assertEquals(Integer.MIN_VALUE,executor.getGlobalVariable(parser.getGlobalVariableSlot("minInt")).getTLValue().getNumeric().getInt());
		      assertEquals(Integer.MAX_VALUE,executor.getGlobalVariable(parser.getGlobalVariableSlot("maxInt")).getTLValue().getNumeric().getInt());
		      assertEquals(((Integer)record.getField("Value").getValue()).intValue(),executor.getGlobalVariable(parser.getGlobalVariableSlot("field")).getTLValue().getNumeric().getInt());
	    } catch (ParseException e) {
	    	System.err.println(e.getMessage());
	    	e.printStackTrace();
	    	throw new RuntimeException("Parse exception",e);
	    }
		      
	}
	
	public void test_long(){
		System.out.println("\nlong test:");
		String expStr = "long i; i=0; print_err(i); \n"+
						"long j; j=-1; print_err(j);\n"+
						"long minLong; minLong="+(Long.MIN_VALUE+1)+"; print_err(minLong);\n"+
						"long maxLong; maxLong="+(Long.MAX_VALUE)+"; print_err(maxLong);\n"+
						"long field; field=$Value; print_err(field);\n"+
						"long wrong;wrong="+Long.MAX_VALUE+"; print_err(wrong);\n";
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(0,executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getLong());
		      assertEquals(-1,executor.getGlobalVariable(parser.getGlobalVariableSlot("j")).getTLValue().getNumeric().getLong());
		      assertEquals(Long.MIN_VALUE+1,executor.getGlobalVariable(parser.getGlobalVariableSlot("minLong")).getTLValue().getNumeric().getLong());
		      assertEquals(Long.MAX_VALUE,executor.getGlobalVariable(parser.getGlobalVariableSlot("maxLong")).getTLValue().getNumeric().getLong());
		      assertEquals(((Integer)record.getField("Value").getValue()).longValue(),executor.getGlobalVariable(parser.getGlobalVariableSlot("field")).getTLValue().getNumeric().getLong());
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
	}

	public void test_decimal(){
		System.out.println("\ndecimal test:");
		String expStr = "decimal i; i=0; print_err(i); \n"+
						"decimal j; j=-1.0; print_err(j);\n"+
						"decimal(18,3) minLong; minLong=999999.999d; print_err(minLong);\n"+
						"decimal maxLong; maxLong=0000000.0000000; print_err(maxLong);\n"+
						"decimal fieldValue; fieldValue=$Value; print_err(fieldValue);\n"+
						"decimal fieldAge; fieldAge=$Age; print_err(fieldAge);\n"+
						"decimal(400,350) minDouble; minDouble="+Double.MIN_VALUE+"d; print_err(minDouble);\n" +
						"decimal def;print_err(def);\n" +
						"print_err('the end');\n";
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      assertEquals(DecimalFactory.getDecimal(0),executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric());
		      assertEquals(DecimalFactory.getDecimal(-1),executor.getGlobalVariable(parser.getGlobalVariableSlot("j")).getTLValue().getNumeric());
		      assertEquals(DecimalFactory.getDecimal(999999.999),executor.getGlobalVariable(parser.getGlobalVariableSlot("minLong")).getTLValue().getNumeric());
		      assertEquals(DecimalFactory.getDecimal(0),executor.getGlobalVariable(parser.getGlobalVariableSlot("maxLong")).getTLValue().getNumeric());
		      assertEquals(((Integer)record.getField("Value").getValue()).intValue(),executor.getGlobalVariable(parser.getGlobalVariableSlot("fieldValue")).getTLValue().getNumeric().getInt());
		      assertEquals((Double)record.getField("Age").getValue(),executor.getGlobalVariable(parser.getGlobalVariableSlot("fieldAge")).getTLValue().getNumeric().getDouble());
		      assertEquals(new Double(Double.MIN_VALUE),executor.getGlobalVariable(parser.getGlobalVariableSlot("minDouble")).getTLValue().getNumeric().getDouble());
		      assertTrue(executor.getGlobalVariable(parser.getGlobalVariableSlot("def")).getTLValue().getNumeric().isNull());

		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }
		      
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
	}

	public void test_number(){
		System.out.println("\nnumber test:");
		String expStr = "number i; i=0; print_err(i); \n"+
						"number j; j=-1.0; print_err(j);\n"+
						"number minLong; minLong=999999.99911; print_err(minLong);  \n"+
						"number fieldValue; fieldValue=$Value; print_err(fieldValue);\n"+
						"number fieldAge; fieldAge=$Age; print_err(fieldAge);\n"+
						"number minDouble; minDouble="+Double.MIN_VALUE+"; print_err(minDouble);\n" +
						"number def;print_err(def);\n";
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(), expStr);
			  CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(new Double(0),executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getDouble());
		      assertEquals(new Double(-1),executor.getGlobalVariable(parser.getGlobalVariableSlot("j")).getTLValue().getNumeric().getDouble());
		      assertEquals(new Double(999999.99911),executor.getGlobalVariable(parser.getGlobalVariableSlot("minLong")).getTLValue().getNumeric().getDouble());
		      assertEquals(new Double(((Integer)record.getField("Value").getValue())),executor.getGlobalVariable(parser.getGlobalVariableSlot("fieldValue")).getTLValue().getNumeric().getDouble());
		      assertEquals(new Double((Double)record.getField("Age").getValue()),executor.getGlobalVariable(parser.getGlobalVariableSlot("fieldAge")).getTLValue().getNumeric().getDouble());
		      assertEquals(new Double(Double.MIN_VALUE),executor.getGlobalVariable(parser.getGlobalVariableSlot("minDouble")).getTLValue().getNumeric().getDouble());
		      assertEquals(new Double(0),executor.getGlobalVariable(parser.getGlobalVariableSlot("def")).getTLValue().getNumeric().getDouble());
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
	}

	public void test_string(){
		System.out.println("\nstring test:");
		int lenght=1000;
        StringBuilder tmp = new StringBuilder(lenght);
		for (int i=0;i<lenght;i++){
			tmp.append(i%10);
		}
		String expStr = "string i; i=\"0\"; print_err(i); \n"+
						"string hello; hello='hello\\nworld'; print_err(hello);\n"+
						"string fieldName; fieldName=$Name; print_err(fieldName);\n"+
						"string fieldCity; fieldCity=$City; print_err(fieldCity);\n"+
						"string longString; longString=\""+tmp+"\"; print_err(longString);\n"+
						"string specialChars; specialChars='a\u0101\u0102A'; print_err(specialChars);\n" +
						"string empty=\"\";print_err(empty+specialChars);\n" +
						"print_err(\"\"+specialChars);\n" +
						"print_err(concat('', specialChars));\n";
	      print_code(expStr);
		
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),
			  		new ByteArrayInputStream(expStr.getBytes("UTF-8")));
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals("0",executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().toString());
		      assertEquals("hello\nworld",executor.getGlobalVariable(parser.getGlobalVariableSlot("hello")).getTLValue().toString());
		      assertEquals(record.getField("Name").getValue().toString(),executor.getGlobalVariable(parser.getGlobalVariableSlot("fieldName")).getTLValue().toString());
		      assertEquals(record.getField("City").getValue().toString(),executor.getGlobalVariable(parser.getGlobalVariableSlot("fieldCity")).getTLValue().toString());
		      assertEquals(tmp.toString(),executor.getGlobalVariable(parser.getGlobalVariableSlot("longString")).getTLValue().toString());
		      assertEquals("a\u0101\u0102A",executor.getGlobalVariable(parser.getGlobalVariableSlot("specialChars")).getTLValue().toString());
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    } catch (UnsupportedEncodingException ex){
		        ex.printStackTrace();
            }
	}

	public void test_date(){
		System.out.println("\ndate test:");
		String expStr = "date d3; d3=2006-08-01; print_err(d3);\n"+
						"date d2; d2=2006-08-02 15:15:00 ; print_err(d2);\n"+
						"date d1; d1=2006-1-1 1:2:3; print_err(d1);\n"+
						"date born; born=$0.Born; print_err(born);\n" +
						"date dnull = null; print_err(dnull);\n";
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
		
	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

  		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(new GregorianCalendar(2006,7,01).getTime(),executor.getGlobalVariable(parser.getGlobalVariableSlot("d3")).getTLValue().getDate());
		      assertEquals(new GregorianCalendar(2006,7,02,15,15).getTime(),executor.getGlobalVariable(parser.getGlobalVariableSlot("d2")).getTLValue().getDate());
		      assertEquals(new GregorianCalendar(2006,0,01,01,02,03).getTime(),executor.getGlobalVariable(parser.getGlobalVariableSlot("d1")).getTLValue().getDate());
		      assertEquals((Date)record.getField("Born").getValue(),executor.getGlobalVariable(parser.getGlobalVariableSlot("born")).getTLValue().getDate());


		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
	}

	public void test_boolean(){
		System.out.println("\nboolean test:");
		String expStr = "boolean b1; b1=true; print_err(b1);\n"+
						"boolean b2; b2=false ; print_err(b2);\n"+
						"boolean b4; print_err(b4);";
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);
		
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("b1")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(false,executor.getGlobalVariable(parser.getGlobalVariableSlot("b2")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(false,executor.getGlobalVariable(parser.getGlobalVariableSlot("b4")).getTLValue()==TLValue.TRUE_VAL);
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
	}

	public void test_variables(){
		System.out.println("\nvariable test:");
		String expStr = "boolean b1; boolean b2; b1=true; print_err(b1);\n"+
						"b2=false ; print_err(b2);\n"+
						"string b4; b4=\"hello\"; print_err(b4);\n"+
						"b2 = true; print_err(b2);\n" +
						"int in;\n" +
						"if (b2) {in=2;print_err('in');}\n"+
						"print_err(b2);\n" +
						"b4=null; print_err(b4);\n"+
						"b4='hi'; print_err(b4);";
	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();
		      
		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("b1")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("b2")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("hi",executor.getGlobalVariable(parser.getGlobalVariableSlot("b4")).getTLValue().toString());
//		      assertEquals(2,executor.getGlobalVariable(parser.getGlobalVariableSlot("in")).getValue().getNumeric().getInt());
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
	}

	public void test_plus(){
		System.out.println("\nplus test:");
		String expStr = "int i; i=10;\n"+
						"int j; j=100;\n" +
						"int iplusj;iplusj=i+j; print_err(\"plus int:\"+iplusj);\n" +
						"long l;l="+Integer.MAX_VALUE/10+"l;print_err(l);\n" +
						"long m;m="+(Integer.MAX_VALUE)+"l;print_err(m);\n" +
						"long lplusm;lplusm=l+m;print_err(\"plus long:\"+lplusm);\n" +
						"number n; n=0;print_err(n);\n" +
						"number m1; m1=0.001;print_err(m1);\n" +
						"number nplusm1; nplusm1=n+m1;print_err(\"plus number:\"+nplusm1);\n" +
						"number nplusj;nplusj=n+j;print_err(\"number plus int:\"+nplusj);\n"+
						"decimal d; d=0.1;print_err(d);\n" +
						"decimal(10,4) d1; d1=0.0001;print_err(d1);\n" +
						"decimal(10,4) dplusd1; dplusd1=d+d1;print_err(\"plus decimal:\"+dplusd1);\n" +
						"decimal dplusj;dplusj=d+j;print_err(\"decimal plus int:\"+dplusj);\n" +
						"decimal(10,4) dplusn;dplusn=d+m1;print_err(\"decimal plus number:\"+dplusn);\n" +
						"dplusn=dplusn+10;\n" +
						"string s; s=\"hello\"; print_err(s);\n" +
						"string s1;s1=\" world\";print_err(s1);\n " +
						"string spluss1;spluss1=s+s1;print_err(\"adding strings:\"+spluss1);\n" +
						"string splusm1;splusm1=s+m1;print_err(\"string plus decimal:\"+splusm1);\n" +
						"date mydate; mydate=2004-01-30 15:00:30;print_err(mydate);\n" +
						"date dateplus;dateplus=mydate+i;print_err(dateplus);\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals("iplusj",110,(executor.getGlobalVariable(parser.getGlobalVariableSlot("iplusj")).getTLValue().getNumeric().getInt()));
		      assertEquals("lplusm",(long)Integer.MAX_VALUE+(long)Integer.MAX_VALUE/10,executor.getGlobalVariable(parser.getGlobalVariableSlot("lplusm")).getTLValue().getNumeric().getLong());
		      assertEquals("nplusm1",new Double(0.001),executor.getGlobalVariable(parser.getGlobalVariableSlot("nplusm1")).getTLValue().getNumeric().getDouble());
		      assertEquals("nplusj",new Double(100),executor.getGlobalVariable(parser.getGlobalVariableSlot("nplusj")).getTLValue().getNumeric().getDouble());
		      assertEquals("dplusd1",new Double(0.1000),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusd1")).getTLValue().getNumeric().getDouble());
		      assertEquals("dplusj",new Double(100.1),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusj")).getTLValue().getNumeric().getDouble());
		      assertEquals("dplusn",new Double(10.1),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusn")).getTLValue().getNumeric().getDouble());
		      assertEquals("spluss1","hello world",executor.getGlobalVariable(parser.getGlobalVariableSlot("spluss1")).getTLValue().toString());
		      assertEquals("splusm1","hello0.0010",executor.getGlobalVariable(parser.getGlobalVariableSlot("splusm1")).getTLValue().toString());
		      assertEquals("dateplus",new GregorianCalendar(2004,01,9,15,00,30).getTime(),executor.getGlobalVariable(parser.getGlobalVariableSlot("dateplus")).getTLValue().getDate());

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_minus(){
		System.out.println("\nminus test:");
		String expStr = "int i; i=10;\n"+
						"int j; j=100;\n" +
						"int iplusj;iplusj=i-j; print_err(\"minus int:\"+iplusj);\n" +
						"long l;l="+((long)Integer.MAX_VALUE+10)+";print_err(l);\n" +
						"long m;m=1;print_err(m);\n" +
						"long lplusm;lplusm=l-m;print_err(\"minus long:\"+lplusm);\n" +
						"number n; n=0;print_err(n);\n" +
						"number m1; m1=0.001;print_err(m1);\n" +
						"number nplusm1; nplusm1=n-m1;print_err(\"minus number:\"+nplusm1);\n" +
						"number nplusj;nplusj=n-j;print_err(\"number minus int:\"+nplusj);\n"+
						"decimal d; d=0.1;print_err(d);\n" +
						"decimal(10,4) d1; d1=0.0001d;print_err(d1);\n" +
						"decimal(10,4) dplusd1; dplusd1=d-d1;print_err(\"minus decimal:\"+dplusd1);\n" +
						"decimal dplusj;dplusj=d-j;print_err(\"decimal minus int:\"+dplusj);\n" +
						"decimal(10,4) dplusn;dplusn=d-m1;print_err(\"decimal minus number:\"+dplusn);\n" +
						"number d1minusm1;d1minusm1=d1-m1;print_err('decimal minus number = number:'+d1minusm1);\n" +
						"date mydate; mydate=2004-01-30 15:00:30;print_err(mydate);\n" +
						"date dateplus;dateplus=mydate-i;print_err(dateplus);\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      assertEquals("iplusj",-90,(executor.getGlobalVariable(parser.getGlobalVariableSlot("iplusj")).getTLValue().getNumeric().getInt()));
		      assertEquals("lplusm",(long)Integer.MAX_VALUE+9,executor.getGlobalVariable(parser.getGlobalVariableSlot("lplusm")).getTLValue().getNumeric().getLong());
		      assertEquals("nplusm1",new Double(-0.001),executor.getGlobalVariable(parser.getGlobalVariableSlot("nplusm1")).getTLValue().getNumeric().getDouble());
		      assertEquals("nplusj",new Double(-100),executor.getGlobalVariable(parser.getGlobalVariableSlot("nplusj")).getTLValue().getNumeric().getDouble());
//		      Decimal tmp = DecimalFactory.getDecimal(0.1);
//		      tmp.sub(DecimalFactory.getDecimal(0.0001,10,4));
//		      assertEquals("dplusd1",tmp, executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusd1")).getValue().getNumeric());
		      assertEquals("dplusd1",DecimalFactory.getDecimal(0.09), executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusd1")).getTLValue().getNumeric());
		      assertEquals("dplusj",new Double(-99.9),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusj")).getTLValue().getNumeric().getDouble());
		      assertEquals("dplusn",new Double(0.0900),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusn")).getTLValue().getNumeric().getDouble());
		      assertEquals("d1minusm1",new Double(-0.0009),executor.getGlobalVariable(parser.getGlobalVariableSlot("d1minusm1")).getTLValue().getNumeric().getDouble());
		      assertEquals("dateplus",new GregorianCalendar(2004,0,20,15,00,30).getTime(),executor.getGlobalVariable(parser.getGlobalVariableSlot("dateplus")).getTLValue().getDate());

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_multiply(){
		System.out.println("\nmultiply test:");
		String expStr = "int i; i=10;\n"+
						"int j; j=100;\n" +
						"int iplusj;iplusj=i*j; print_err(\"multiply int:\"+iplusj);\n" +
						"long l;l="+((long)Integer.MAX_VALUE+10)+";print_err(l);\n" +
						"long m;m=1;print_err(m);\n" +
						"long lplusm;lplusm=l*m;print_err(\"multiply long:\"+lplusm);\n" +
						"number n; n=0.1;print_err(n);\n" +
						"number m1; m1=-0.01;print_err(m1);\n" +
						"number nplusm1; nplusm1=n*m1;print_err(\"multiply number:\"+nplusm1);\n" +
						"number m1plusj;m1plusj=m1*j;print_err(\"number multiply int:\"+m1plusj);\n"+
						"decimal(8,4) d; d=0.1; print_err(d);\n" +
						"decimal(10,4) d1; d1=10.01d;print_err(d1);\n" +
						"decimal(10,4) dplusd1; dplusd1=d*d1;print_err(\"multiply decimal:\"+dplusd1);\n" +
						"decimal(10,4) dplusj;dplusj=d*j;print_err(\"decimal multiply int:\"+dplusj);\n"+
						"decimal(10,4) dplusn;dplusn=d*n;print_err(\"decimal multiply number:\"+dplusn);\n";
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals("i*j",1000,(executor.getGlobalVariable(parser.getGlobalVariableSlot("iplusj")).getTLValue().getNumeric().getInt()));
		      assertEquals("l*m",(long)Integer.MAX_VALUE+10,executor.getGlobalVariable(parser.getGlobalVariableSlot("lplusm")).getTLValue().getNumeric().getLong());
		      assertEquals("n*m1",new Double(-0.001),executor.getGlobalVariable(parser.getGlobalVariableSlot("nplusm1")).getTLValue().getNumeric().getDouble());
		      assertEquals("m1*j",new Double(-1),executor.getGlobalVariable(parser.getGlobalVariableSlot("m1plusj")).getTLValue().getNumeric().getDouble());
		      assertEquals("d*d1",DecimalFactory.getDecimal(1.001,10,4),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusd1")).getTLValue().getNumeric());
		      assertEquals("d*j",DecimalFactory.getDecimal(10,10,4),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusj")).getTLValue().getNumeric());
		      assertEquals("d*n",DecimalFactory.getDecimal(0.01, 10, 4),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusn")).getTLValue().getNumeric());

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_division(){
		System.out.println("\ndivision test:");
		String expStr = "int i; i=10;\n"+
						"int j; j=100;\n" +
						"int iplusj;iplusj=i/j; print_err(\"div int:\"+iplusj);\n" +
						"int jdivi;jdivi=j/i; print_err(\"div int:\"+jdivi);\n" +
						"long l;l="+((long)Integer.MAX_VALUE+10)+";print_err(l);\n" +
						"long m;m=1;print_err(m);\n" +
						"long lplusm;lplusm=l/m;print_err(\"div long:\"+lplusm);\n" +
						"number n; n=0;print_err(n);\n" +
						"number m1; m1=0.01;print_err(m1);\n" +
						"number n1; n1=10;print_err(n1);\n" +
						"number nplusm1; nplusm1=n/m1;print_err(\"0/0.01:\"+nplusm1);\n" +
						"number m1divn; m1divn=m1/n;print_err(\"deleni nulou:\"+m1divn);\n" +
						"number m1divn1; m1divn1=m1/n1;print_err(\"deleni numbers:\"+m1divn1);\n" +
						"number m1plusj;m1plusj=j/n1;print_err(\"number division int:\"+m1plusj);\n"+
						"decimal d; d=0.1;print_err(d);\n" +
						"decimal(10,4) d1; d1=0.01;print_err(d1);\n" +
						"decimal(10,4)  dplusd1; dplusd1=d/d1;print_err(\"div decimal:\"+dplusd1);\n" +
						"decimal(10,4)  dplusj;dplusj=d/j;print_err(\"decimal div int:\"+dplusj);\n"+
						"decimal(10,4)  dplusn;dplusn=n1/d;print_err(\"decimal div number:\"+dplusn);\n";
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

  		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals("i/j",0,(executor.getGlobalVariable(parser.getGlobalVariableSlot("iplusj")).getTLValue().getNumeric().getInt()));
		      assertEquals("j/i",10,(executor.getGlobalVariable(parser.getGlobalVariableSlot("jdivi")).getTLValue().getNumeric().getInt()));
		      assertEquals("l/m",(long)Integer.MAX_VALUE+10,executor.getGlobalVariable(parser.getGlobalVariableSlot("lplusm")).getTLValue().getNumeric().getLong());
		      assertEquals("n/m1",new Double(0),executor.getGlobalVariable(parser.getGlobalVariableSlot("nplusm1")).getTLValue().getNumeric().getDouble());
		      assertEquals("m1/n",new Double(Double.POSITIVE_INFINITY),executor.getGlobalVariable(parser.getGlobalVariableSlot("m1divn")).getTLValue().getNumeric().getDouble());
		      assertEquals("m1/n1",new Double(0.001),executor.getGlobalVariable(parser.getGlobalVariableSlot("m1divn1")).getTLValue().getNumeric().getDouble());
		      assertEquals("j/n1",new Double(10),executor.getGlobalVariable(parser.getGlobalVariableSlot("m1plusj")).getTLValue().getNumeric().getDouble());
		      assertEquals("d/d1",DecimalFactory.getDecimal(0.1/0.01),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusd1")).getTLValue().getNumeric());
		      assertEquals("d/j",DecimalFactory.getDecimal(0.0000),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusj")).getTLValue().getNumeric());
		      assertEquals("n1/d",DecimalFactory.getDecimal(100.0000),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusn")).getTLValue().getNumeric());

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_modulus(){
		System.out.println("\nmodulus test:");
		String expStr = "int i; i=10;\n"+
						"int j; j=103;\n" +
						"int iplusj;iplusj=j%i; print_err(\"mod int:\"+iplusj);\n" +
						"long l;l="+((long)Integer.MAX_VALUE+10)+";print_err(l);\n" +
						"long m;m=2;print_err(m);\n" +
						"long lplusm;lplusm=l%m;print_err(\"mod long:\"+lplusm);\n" +
						"number n; n=10.2;print_err(n);\n" +
						"number m1; m1=2;print_err(m1);\n" +
						"number nplusm1; nplusm1=n%m1;print_err(\"mod number:\"+nplusm1);\n" +
						"number m1plusj;m1plusj=n%i;print_err(\"number mod int:\"+m1plusj);\n"+
						"decimal d; d=10.1;print_err(d);\n" +
						"decimal(10,4) d1; d1=10;print_err(d1);\n" +
						"decimal dplusd1; dplusd1=d%d1;print_err(\"mod decimal:\"+dplusd1);\n" +
						"decimal(10,4) dplusj;dplusj=d1%j;print_err(\"decimal mod int:\"+dplusj);\n"+
						"decimal dplusn;dplusn=d%m1;print_err(\"decimal mod number:\"+dplusn);\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(3,(executor.getGlobalVariable(parser.getGlobalVariableSlot("iplusj")).getTLValue().getNumeric().getInt()));
		      assertEquals(((long)Integer.MAX_VALUE+10)%2,executor.getGlobalVariable(parser.getGlobalVariableSlot("lplusm")).getTLValue().getNumeric().getLong());
		      assertEquals(new Double(10.2%2),executor.getGlobalVariable(parser.getGlobalVariableSlot("nplusm1")).getTLValue().getNumeric().getDouble());
		      assertEquals(new Double(10.2%10),executor.getGlobalVariable(parser.getGlobalVariableSlot("m1plusj")).getTLValue().getNumeric().getDouble());
		      assertEquals(DecimalFactory.getDecimal(0.1),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusd1")).getTLValue().getNumeric());
		      assertEquals(DecimalFactory.getDecimal(10),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusj")).getTLValue().getNumeric());
		      assertEquals(DecimalFactory.getDecimal(0.1),executor.getGlobalVariable(parser.getGlobalVariableSlot("dplusn")).getTLValue().getNumeric());

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_increment_decrement(){
		System.out.println("\nincrement-decrement test:");
		String expStr = "int i; i=10;print_err(++i);\n" +
						"i--;" +
						"print_err(--i);\n"+
						"long j;j="+(Long.MAX_VALUE-10)+"l;print_err(++j);\n" +
						"print_err(--j);\n"+
						"decimal d;d=2;d++;\n" +
						"print_err(--d);\n;" +
						"number n;n=3.5;print_err(++n);\n" +
						"n--;\n" +
						"{print_err(++n);}\n" +
						"print_err(++n);\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }
		      
		      assertEquals(9,(executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getInt()));
		      assertEquals(new CloverLong(Long.MAX_VALUE-10).
		    		  getLong(),executor.getGlobalVariable(parser.getGlobalVariableSlot("j")).getTLValue().getNumeric().getLong());
		      assertEquals(DecimalFactory.getDecimal(2),executor.getGlobalVariable(parser.getGlobalVariableSlot("d")).getTLValue().getNumeric());
		      assertEquals(new Double(5.5),executor.getGlobalVariable(parser.getGlobalVariableSlot("n")).getTLValue().getNumeric().getDouble());

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_equal(){
		System.out.println("\nequal test:");
		String expStr = "int i; i=10;print_err(\"i=\"+i);\n" +
						"int j;j=9;print_err(\"j=\"+j);\n" +
						"boolean eq1; eq1=(i==j+1);print_err(\"eq1=\"+eq1);\n" +
//						"boolean eq1;eq1=(i.eq.(j+1));print_err(\"eq1=\"+eq1);\n" +
						"long l;l=10;print_err(\"l=\"+l);\n" +
						"boolean eq2;eq2=(l==j);print_err(\"eq2=\"+eq2);\n" +
						"eq2=(l.eq.i);print_err(\"eq2=\");print_err(eq2);\n" +
						"decimal d;d=10;print_err(\"d=\"+d);\n" +
						"boolean eq3;eq3=d==i;print_err(\"eq3=\"+eq3);\n" +
						"number n;n=10;print_err(\"n=\"+n);\n" +
						"boolean eq4;eq4=n.eq.l;print_err(\"eq4=\"+eq4);\n" +
						"boolean eq5;eq5=n==d;print_err(\"eq5=\"+eq5);\n" +
						"string s;s='hello';print_err(\"s=\"+s);\n" +
						"string s1;s1=\"hello \";print_err(\"s1=\"+s1);\n" +
						"boolean eq6;eq6=s.eq.s1;print_err(\"eq6=\"+eq6);\n" +
						"boolean eq7;eq7=s==trim(s1);print_err(\"eq7=\"+eq7);\n" +
						"date mydate;mydate=2006-01-01;print_err(\"mydate=\"+mydate);\n" +
						"date anothermydate;print_err(\"anothermydate=\"+anothermydate);\n" +
						"boolean eq8;eq8=mydate.eq.anothermydate;print_err(\"eq8=\"+eq8);\n" +
						"anothermydate=2006-1-1 0:0:0;print_err(\"anothermydate=\"+anothermydate);\n" +
						"boolean eq9;eq9=mydate==anothermydate;print_err(\"eq9=\"+eq9);\n" +
						"boolean eq10;eq10=eq9.eq.eq8;print_err(\"eq10=\"+eq10);\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq1")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq2")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq3")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq4")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq5")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(false,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq6")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq7")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(false,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq8")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq9")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(false,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq10")).getTLValue()==TLValue.TRUE_VAL);

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_non_equal(){
		System.out.println("\nNon equal test:");
		String expStr = "int i; i=10;print_err(\"i=\"+i);\n" +
						"int j;j=9;print_err(\"j=\"+j);\n" +
						"boolean eq1; eq1=(i!=j);print_err(\"eq1=\");print_err(eq1);\n" +
						"long l;l=10;print_err(\"l=\"+l);\n" +
						"boolean eq2;eq2=(l<>j);print_err(\"eq2=\");print_err(eq2);\n" +
						"decimal d;d=10;print_err(\"d=\"+d);\n" +
						"boolean eq3;eq3=d.ne.i;print_err(\"eq3=\");print_err(eq3);\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq1")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq2")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals(false,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq3")).getTLValue()==TLValue.TRUE_VAL);

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_greater_less(){
		System.out.println("\nGreater and less test:");
		String expStr = "int i; i=10;print_err(\"i=\"+i);\n" +
						"int j;j=9;print_err(\"j=\"+j);\n" +
						"boolean eq1; eq1=(i>j);print_err(\"eq1=\"+eq1);\n" +
						"long l;l=10;print_err(\"l=\"+l);\n" +
						"boolean eq2;eq2=(l>=j);print_err(\"eq2=\"+eq2);\n" +
						"decimal d;d=10;print_err(\"d=\"+d);\n" +
						"boolean eq3;eq3=d=>i;print_err(\"eq3=\"+eq3);\n" +
						"number n;n=10;print_err(\"n=\"+n);\n" +
						"boolean eq4;eq4=n.gt.l;print_err(\"eq4=\"+eq4);\n" +
						"boolean eq5;eq5=n.ge.d;print_err(\"eq5=\"+eq5);\n" +
						"string s;s='hello';print_err(\"s=\"+s);\n" +
						"string s1;s1=\"hello\";print_err(\"s1=\"+s1);\n" +
						"boolean eq6;eq6=s<s1;print_err(\"eq6=\"+eq6);\n" +
						"date mydate;mydate=2006-01-01;print_err(\"mydate=\"+mydate);\n" +
						"date anothermydate;print_err(\"anothermydate=\"+anothermydate);\n" +
						"boolean eq7;eq7=mydate.lt.anothermydate;print_err(\"eq7=\"+eq7);\n" +
						"anothermydate=2006-1-1 0:0:0;print_err(\"anothermydate=\"+anothermydate);\n" +
						"boolean eq8;eq8=mydate<=anothermydate;print_err(\"eq8=\"+eq8);\n" ;

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals("eq1",true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq1")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("eq2",true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq2")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("eq3",true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq3")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("eq4",false,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq4")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("eq5",true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq5")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("eq6",false,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq6")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("eq7",true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq7")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("eq8",true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq8")).getTLValue()==TLValue.TRUE_VAL);

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_regex(){
		System.out.println("\nRegex test:");
		String expStr = "string s;s='Hej';print_err(s);\n" +
						"boolean eq2;eq2=(s~=\"[A-Za-z]{3}\");\n" +
						"print_err(\"eq2=\"+eq2);\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }
		     

		      
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("eq2")).getTLValue()==TLValue.TRUE_VAL);

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_1_expression() {
		String expStr="$Age>=135 or 200>$Age and not $Age<=0 and 1==999999999999999 or $Name==\"HELLO\"";
		
	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStartExpression parseTree = parser.StartExpression();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      assertEquals(true, executor.getResult()==TLValue.TRUE_VAL);
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
		}
	
	public void test_2_expression() {
		String expStr="datediff(nvl($Born,2005-2-1),2005-1-1,month)";
	      print_code(expStr);
		try {
            TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStartExpression parseTree = parser.StartExpression();
              
 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
      
              assertEquals(1, executor.getResult().getNumeric().getInt());
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
		
	}
		
	public void test_3_expression() {
//		String expStr="not (trim($Name) .ne. \"HELLO\") || replace($Name,\".\" ,\"a\")=='aaaaaaa'";
		String expStr="print_err(trim($Name)); print_err(replace('xyyxyyxzz',\"x\" ,\"ab\")); print_err('aaaaaaa'); print_err(split('abcdef','c'));";
	      print_code(expStr);
		try {
			System.out.println("in Test3expression");
            TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));           
            
            CLVFStart parseTree = parser.Start();
//            CLVFStartExpression parseTree = parser.StartExpression();

            parseTree.dump("ccc");
              
              for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
            	  System.err.println(it.next());
              }
              
              
 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
		      
//              assertEquals(false, executor.getResult()==TLValue.TRUE_VAL);
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
		
	}
	
	public void test_if(){
		System.out.println("\nIf statement test:");
		String expStr = "int i; i=10;print_err(\"i=\"+i);\n" +
						"int j;j=9;print_err(\"j=\"+j);\n" +
						"long l;" +
						"if (i>j) l=1; else l=0;\n" +
						"print_err(l);\n" +
						"decimal d;" +
						"if (i.gt.j and l.eq.1) {d=0;print_err('d rovne 0');}\n" +
						"else d=0.1;\n" +
						"number n;\n" +
						"if (d==0.1) n=0;\n" +
						"if (d==0.1 || l<=1) n=0;\n" +
						"else {n=-1;print_err('n rovne -1');}\n" +
						"date date1; date1=2006-01-01;print_err(date1);\n" +
						"date date2; date2=2006-02-01;print_err(date2);\n" +
						"boolean result;result=false;\n" +
						"boolean compareDates;compareDates=date1<=date2;print_err(compareDates);\n" +
						"if (date1<=date2) \n" +
						"{  print_err('before if (i<j)');\n" +
						"	if (i<j) print_err('date1<today and i<j'); else print_err('date1<date2 only');\n" +
						"	result=true;}\n" +
						"result=false;" +
						"if (i<j) result=true;\n" +
						"else if (not result) result=true;\n" +
						"else print_err('last else');\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }
		     
		      assertEquals(1,executor.getGlobalVariable(parser.getGlobalVariableSlot("l")).getTLValue().getNumeric().getLong());
		      assertEquals(DecimalFactory.getDecimal(0),executor.getGlobalVariable(parser.getGlobalVariableSlot("d")).getTLValue().getNumeric());
		      assertEquals(new Double(0),executor.getGlobalVariable(parser.getGlobalVariableSlot("n")).getTLValue().getNumeric().getDouble());
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("result")).getTLValue()==TLValue.TRUE_VAL);

		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
                
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_switch(){
		System.out.println("\nSwitch test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"int n;n=date2num(born,month);print_err(n);\n" +
						"string mont;\n" +
						"decimal april;april=4;\n" +
						"switch (n) {\n" +
						"	case 0.0:mont='january';\n" +
						"	case 1.0:mont='february';\n" +
						"	case 2.0:mont='march';\n" +
						"	case 3:mont='april';\n" +
						"	case april:mont='may';\n" +
						"	case 5.0:mont='june';\n" +
						"	case 6.0:mont='july';\n" +
						"	case 7.0:mont='august';\n" +
						"	case 3:print_err('4th month');\n" +
						"	case 8.0:mont='september';\n" +
						"	case 9.0:mont='october';\n" +
						"	case 10.0:mont='november';\n" +
						"	case 11.0:mont='december';\n" +
						"	default: mont='unknown';};\n"+
						"print_err('month:'+mont);\n" +
						"boolean ok;ok=(n.ge.0)and(n.lt.12);\n" +
						"switch (ok) {\n" +
						"	case true:print_err('OK');\n" +
						"	case false:print_err('WRONG');};\n" +
						"switch (born) {\n" +
						"	case 2006-01-01:{mont='January';print_err('january');}\n" +
						"	case 1973-04-23:{mont='April';print_err('april');}}\n" +
						"//	default:print_err('other')};\n"+
						"switch (born<1996-08-01) {\n" +
						"	case true:{print_err('older then ten');}\n" +
						"	default:print_err('younger then ten');};\n";
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(3,executor.getGlobalVariable(parser.getGlobalVariableSlot("n")).getTLValue().getNumeric().getInt());
		      assertEquals("April",executor.getGlobalVariable(parser.getGlobalVariableSlot("mont")).getTLValue().toString());
		      assertEquals(true,executor.getGlobalVariable(parser.getGlobalVariableSlot("ok")).getTLValue()==TLValue.TRUE_VAL);
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}
	
	public void test_while(){
		System.out.println("\nWhile test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"date now;now=today();\n" +
						"int yer;yer=0;\n" +
						"while (born<now) {\n" +
						"	born=dateadd(born,1,year);\n " +
						"	while (yer<5) yer=yer+1;\n" +
						"	yer=yer+1;}\n" +
						"print_err('years:'+yer);\n";
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      GregorianCalendar b;
		      b= new GregorianCalendar();
		      b.setTime(((Date)record.getField("Born").getValue()));
		      assertEquals(new GregorianCalendar().get(Calendar.YEAR) - b.get(Calendar.YEAR) + 6, 
		    		  (executor.getGlobalVariable(parser.getGlobalVariableSlot("yer")).getTLValue().getNumeric().getInt()));
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_do_while(){
		System.out.println("\nDo-while test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"date now;now=today();\n" +
						"int yer;yer=0;\n" +
						"do {\n" +
						"	born=dateadd(born,1,year);\n " +
						"	print_err('years:'+yer);\n" +
						"	print_err(born);\n" +
						"	do yer=yer+1; while (yer<5);\n" +
						"	print_err('years:'+yer);\n" +
						"	print_err(born);\n" +
						"	yer=yer+1;}\n" +
						"while (born<now)\n" +
						"print_err('years on the end:'+yer);\n";
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      GregorianCalendar b;
		      b= new GregorianCalendar();
		      b.setTime(((Date)record.getField("Born").getValue()));
		      assertEquals(2 * (new GregorianCalendar().get(Calendar.YEAR) - b.get(Calendar.YEAR)) + 6,
		    		  (executor.getGlobalVariable(parser.getGlobalVariableSlot("yer")).getTLValue().getNumeric().getInt()));
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_for(){
		System.out.println("\nFor test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"date now;now=today();\n" +
						"int yer;yer=0;\n" +
						"for (born;born<now;born=dateadd(born,1,year)) yer++;\n" +
						"print_err('years on the end:'+yer);\n" +
						"boolean b;\n" +
						"for (born;!b;++yer) \n" +
						"	if (yer==100) b=true;\n" +
						"print_err(born);\n" +
						"print_err('years on the end:'+yer);\n" +
						"print_err('born:'+born);\n"+
						"int i;\n" +
						"for (i=0;i.le.10;++i) ;\n" +
						"print_err('on the end i='+i);\n";
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
              int iVarSlot=parser.getGlobalVariableSlot("i");
              int yerVarSlot=parser.getGlobalVariableSlot("yer");
		      TLVariable[] result = executor.stack.globalVarSlot;
		      assertEquals(101,result[yerVarSlot].getTLValue().getNumeric().getInt());
		      assertEquals(11,result[iVarSlot].getTLValue().getNumeric().getInt());
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_for2(){
		System.out.println("\nFor test:");
		String expStr ="int i;int yer;yer=0;\n" +
						"for (i=0;i.le.10;++i) ;\n" +
						"print_err('on the end i='+i);\n" +
						"int j=1;long l=123456789012345678L;\n" +
						"for (j=5;j<i;++j){\n" +
						"	l=l-i;}";
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
              int iVarSlot=parser.getGlobalVariableSlot("i");
              int jVarSlot=parser.getGlobalVariableSlot("j");
		      TLVariable[] result = executor.stack.globalVarSlot;
		      assertEquals(11,result[jVarSlot].getTLValue().getNumeric().getInt());
		      assertEquals(11,result[iVarSlot].getTLValue().getNumeric().getInt());
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_break(){
		System.out.println("\nBreak test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"date now;now=today();\n" +
						"int yer;yer=0;\n" +
						"int i;\n" +
						"while (born<now) {\n" +
						"	yer++;\n" +
						"	born=dateadd(born,1,year);\n" +
						"	for (i=0;i<20;++i) \n" +
						"		if (i==10) break;\n" +
						"}\n" +
						"print_err('years on the end:'+yer);\n"+
						"print_err('i after while:'+i);\n" ;
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(new GregorianCalendar().get(Calendar.YEAR) - born.get(Calendar.YEAR) + 1,
		    		  (executor.getGlobalVariable(parser.getGlobalVariableSlot("yer")).getTLValue().getNumeric().getInt()));
		      assertEquals(10,(executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getInt()));
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_break2(){
		System.out.println("\nBreak test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"date now; now=today(); print_err(now);\n" +
						"int yer;yer=0;\n" +
						"int i;\n" +
						"while (yer<date2num(now,year)) {\n" +
						"	yer++;\n" +
						"	for (i=0;i<20;++i) \n" +
						"		if (i==10) break;\n" +
						"}\n" +
						"print_err('years on the end:'+yer);\n"+
						"print_err('i after while:'+i);\n" ;
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals(2007,(executor.getGlobalVariable(parser.getGlobalVariableSlot("yer")).getTLValue().getNumeric().getInt()));
		      assertEquals(10,(executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getInt()));
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_continue(){
		System.out.println("\nContinue test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"date now;now=today();\n" +
						"int yer;yer=0;\n" +
						"int i;\n" +
						"for (i=0;i<10;i=i+1) {\n" +
						"	print_err('i='+i);\n" +
						"	if (i>5) continue;\n" +
						"	print_err('After if');" +
						"}\n" +
						"print_err('new loop starting');\n" +
						"print_err('born '+born+' now '+now);\n"+
						"while (born<now) {\n" +
						"	print_err('i='+i);i=0;\n" +
						"	print_err(yer);\n" +
						"	yer=yer+1;\n" +
						"	born=dateadd(born,1,year);\n" +
						"	if (yer>30) continue\n" +
						"	for (i=0;i<20;++i) \n" +
						"		if (i==10) break;\n" +
						"}\n" +
						"print_err('years on the end:'+yer);\n"+
						"print_err('i after while:'+i);\n" ;
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      
		      parseTree.dump("");
		      
		      assertEquals(35,(executor.getGlobalVariable(parser.getGlobalVariableSlot("yer")).getTLValue().getNumeric().getInt()));
		      assertEquals(0,(executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getInt()));
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_continue2(){
		System.out.println("\nContinue test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"date now;now=today();\n" +
						"int yer;yer=0;\n" +
						"int i;\n" +
						"for (i=0;i<10;i=i+1) {\n" +
						"	print_err('i='+i);\n" +
						"	if (i>5) continue;\n" +
						"	print_err('After if');" +
						"}\n" +
						"print_err('i after f:'+i);\n" ;
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      
		      parseTree.dump("");
		      
		      assertEquals(10,(executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getInt()));
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_return(){
		System.out.println("\nReturn test:");
		String expStr = "date born; born=$Born;print_err(born);\n" +
						"function year_before(now) {\n" +
						"	return dateadd(now,-1,year);" +
						"}\n" +
						"function age(born){\n" +
						"	date now;int yer;\n" +
						"	now=today();yer=0;\n" +
						"	for (born;born<now;born=dateadd(born,1,year)) yer++;\n" +
						"	if (yer>0) return yer else return -1;" +
						"}\n" +
						"print_err('years born'+age(born));\n" +
						"print_err(\"year before:\"+year_before(born));\n" +
						" while (true) {print_err('pred return');" +
						"return;\n" +
						"print_err('po return');}\n" +
						"print_err('za blokem');\n";
		GregorianCalendar born = new GregorianCalendar(1973,03,23);
		record.getField("Born").setValue(born.getTime());
	      print_code(expStr);

		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
              
            for(Iterator iter=parser.getParseExceptions().iterator();iter.hasNext();){
                System.err.println(iter.next());
            }
              
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      //TODO
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

	public void test_list_map(){
        System.out.println("\nList/Map test:");
        String expStr = "list seznam; list seznam2; list fields;\n"+
        				 "map mapa; mapa['f1']=10; mapa['f2']='hello'; map mapa2; mapa2[]=mapa; map mapa3; \n"+
        				 "int i; for(i=0;i<20;i++) { seznam[]=i; if (i==10) seznam2[]=seznam;  }\n"+
        				 "seznam[1]=999; seznam2[3]='hello'; \n"+
        				 "fields=split('a,b,c,d,e,f,g,h',','); fields[]=null;"+
        				 "int length=length(seznam); print_err('length: '+length);\n print_err(seznam);\n print_err(seznam2); print_err(fields);\n"+
        				 "list novy; novy[]=mapa; print_err('novy1:'+novy); mapa2['f2']='xxx'; novy[]=mapa2; mapa['f1']=99; novy[]=mapa; \n" +
        				 "print_err('novy='+novy); print_err(novy[1]); \n" +
        				 "print_err('novy[3]:'+novy[3]); mapa3=novy[3]; print_err(mapa2['f2']); print_err(mapa3); \n" +
        				 "fields=seznam2; print_err(fields);\n" +
        				 "print_err(join(':del:',seznam,mapa,novy[1]));\n";
        print_code(expStr);

       Log logger = LogFactory.getLog(this.getClass());
       TransformLangParser parser=null;
        
        try {
              parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStart parseTree = parser.Start();

              System.out.println("Initializing parse tree..");
              parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
              System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.setRuntimeLogger(logger);
              executor.setGraph(graph);
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
              
              assertEquals("lengh",20,executor.getGlobalVariable(parser.getGlobalVariableSlot("length")).getTLValue().getNumeric().getInt());
              
              
        } catch (ParseException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
             
                Iterator it=parser.getParseExceptions().iterator();
                while(it.hasNext()){
                	System.err.println(((Throwable)it.next()).getMessage());
                }
                throw new RuntimeException("Parse exception",e);
        }
    }
    
	
	
	public void test_buildInFunctions(){
		System.out.println("\nBuild-in functions test:");
		String expStr = "string s;s='hello world';\n" +
						"number lenght;lenght=5.5;\n" +
						"string subs;subs=substring(s,1,lenght);\n" +
						"print_err('original string:'+s );\n" +
						"print_err('substring:'+subs );\n" +
						"string upper;upper=uppercase(subs);\n" +
						"print_err('to upper case:'+upper );\n"+
						"string lower;lower=lowercase(subs+'hI   ');\n" +
						"print_err('to lower case:'+lower );\n"+
						"string t;t=trim('\t  im  '+lower);\n" +
						"print_err('after trim:'+t );\n" +
						"breakpoint();\n" +
						"//print_stack();\n"+
						"decimal l;l=length(upper);\n" +
						"print_err('length of '+upper+':'+l );\n"+
						"string c;c=concat(lower,upper,2,',today is ',today());\n" +
						"print_err('concatenation \"'+lower+'\"+\"'+upper+'\"+2+\",today is \"+today():'+c );\n"+
						"date datum; date born;born=nvl($Born,today()-400);\n" +
						"datum=dateadd(born,100,millisec);\n" +
						"print_err(datum );\n"+
						"long ddiff;date otherdate;otherdate=today();\n" +
						"ddiff=datediff(born,otherdate,year);\n" +
						"print_err('date diffrence:'+ddiff );\n" +
						"print_err('born: '+born+' otherdate: '+otherdate);\n" +
						"boolean isn;isn=isnull(ddiff);\n" +
						"print_err(isn );\n" +
						"number s1;s1=nvl(l+1,1);\n" +
						"print_err(s1 );\n" +
						"string rep;rep=replace(c,'[lL]','t');\n" +
						"print_err(rep );\n" +
						"decimal(10,5) stn;stn=str2num('2.5125e-1',decimal);\n" +
						"print_err(stn );\n" +
						"int i = str2num('1234');\n" +
						"string nts;nts=num2str(10,4);\n" +
						"print_err(nts );\n" +
						"date newdate;newdate=2001-12-20 16:30:04;\n" +
						"decimal dtn;dtn=date2num(newdate,month);\n" +
						"print_err(dtn );\n" +
						"int ii;ii=iif(newdate<2000-01-01,20,21);\n" +
						"print_err('ii:'+ii);\n" +
						"print_stack();\n" +
						"date ndate;ndate=2002-12-24;\n" +
						"string dts;dts=date2str(ndate,'yy.MM.dd');\n" +
						"print_err('date to string:'+dts);\n" +
						"print_err(str2date(dts,'yy.MM.dd'));\n" +
						"string lef=left(dts,5);\n" +
						"string righ=right(dts,5);\n" +
						"print_err('s=word, soundex='+soundex('word'));\n" +
						"print_err('s=world, soundex='+soundex('world'));\n" +
						"int j;for (j=0;j<length(s);j++){print_err(char_at(s,j));};\n" ;

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }


 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals("subs","ello ",executor.getGlobalVariable(parser.getGlobalVariableSlot("subs")).getTLValue().toString());
		      assertEquals("upper","ELLO ",executor.getGlobalVariable(parser.getGlobalVariableSlot("upper")).getTLValue().toString());
		      assertEquals("lower","ello hi   ",executor.getGlobalVariable(parser.getGlobalVariableSlot("lower")).getTLValue().toString());
		      assertEquals("t(=trim)","im  ello hi",executor.getGlobalVariable(parser.getGlobalVariableSlot("t")).getTLValue().toString());
		      assertEquals("l(=length)",5,executor.getGlobalVariable(parser.getGlobalVariableSlot("l")).getTLValue().getNumeric().getInt());
		      assertEquals("c(=concat)","ello hi   ELLO 2,today is "+new Date(),executor.getGlobalVariable(parser.getGlobalVariableSlot("c")).getTLValue().toString());
//		      assertEquals("datum",record.getField("Born").getValue(),executor.getGlobalVariable(parser.getGlobalVariableSlot("datum")).getValue().getDate());
		      assertEquals("ddiff",-1,executor.getGlobalVariable(parser.getGlobalVariableSlot("ddiff")).getTLValue().getNumeric().getLong());
		      assertEquals("isn",false,executor.getGlobalVariable(parser.getGlobalVariableSlot("isn")).getTLValue()==TLValue.TRUE_VAL);
		      assertEquals("s1",new Double(6),executor.getGlobalVariable(parser.getGlobalVariableSlot("s1")).getTLValue().getNumeric().getDouble());
		      assertEquals("rep",("etto hi   EttO 2,today is "+new Date()).replaceAll("[lL]", "t"),executor.getGlobalVariable(parser.getGlobalVariableSlot("rep")).getTLValue().toString());
		      assertEquals("stn",0.25125,executor.getGlobalVariable(parser.getGlobalVariableSlot("stn")).getTLValue().getNumeric().getDouble());
		      assertEquals("i",1234,executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getInt());
		      assertEquals("nts","22",executor.getGlobalVariable(parser.getGlobalVariableSlot("nts")).getTLValue().toString());
		      assertEquals("dtn",11.0,executor.getGlobalVariable(parser.getGlobalVariableSlot("dtn")).getTLValue().getNumeric().getDouble());
		      assertEquals("ii",21,executor.getGlobalVariable(parser.getGlobalVariableSlot("ii")).getTLValue().getNumeric().getInt());
		      assertEquals("dts","02.12.24",executor.getGlobalVariable(parser.getGlobalVariableSlot("dts")).getTLValue().toString());
		      assertEquals("lef","02.12",executor.getGlobalVariable(parser.getGlobalVariableSlot("lef")).getTLValue().toString());
		      assertEquals("righ","12.24",executor.getGlobalVariable(parser.getGlobalVariableSlot("righ")).getTLValue().toString());
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

    public void test_functions2(){
        System.out.println("\nFunctions test:");
        String expStr = "string test='test';\n" +
        		"boolean isBlank=is_blank(test);\n" +
        		"boolean isBlank1=is_blank('');\n" +
        		"test=null; boolean isBlank2=is_blank(test);\n" +
        		"boolean isAscii1=is_ascii('test');\n" +
        		"boolean isAscii2=is_ascii('aęř');\n" +
        		"boolean isNumber=is_number('t1');\n" +
        		"boolean isNumber1=is_number('1g');\n" +
        		"boolean isNumber2=is_number('1'); print_err(str2num('1'));\n" +
        		"boolean isNumber3=is_number('-382.334'); print_err(str2num('-382.334',decimal));\n" +
        		"boolean isNumber4=is_number('+332e2');\n" +
        		"boolean isNumber5=is_number('8982.8992e-2');print_err(str2num('8982.8992e-2',double));\n" +
        		"boolean isNumber6=is_number('-7888873.2E3');print_err(str2num('-7888873.2E3',number));\n" +
        		"boolean isInteger=is_integer('h3');\n" +
        		"boolean isInteger1=is_integer('78gd');\n" +
        		"boolean isInteger2=is_integer('8982.8992');\n" +
        		"boolean isInteger3=is_integer('-766542378');print_err(str2num('-766542378'));\n" +
        		"boolean isLong=is_long('7864232568822234');\n" +
        		"boolean isDate5=is_date('20Jul2000','ddMMMyyyy');print_err(str2date('20Jul2000','ddMMMyyyy'));\n" +
        		"boolean isDate6=is_date('20July    2000','ddMMMMMMMMyyyy');print_err(str2date('20July    2000','ddMMMyyyy'));\n" +
        		"boolean isDate3=is_date('4:42','HH:mm');print_err(str2date('4:42','HH:mm'));\n" +
        		"boolean isDate=is_date('20.11.2007','dd.MM.yyyy');print_err(str2date('20.11.2007','dd.MM.yyyy'));\n" +
        		"boolean isDate1=is_date('20.11.2007','dd-MM-yyyy');\n" +
        		"boolean isDate2=is_date('24:00 20.11.2007','HH:mm dd.MM.yyyy');print_err(str2date('24:00 20.11.2007','HH:mm dd.MM.yyyy'));\n" +
        		"boolean isDate4=is_date('test 20.11.2007','hhmm dd.MM.yyyy');\n" +
        		"boolean isDate7=is_date('','HH:mm dd.MM.yyyy');print_err(str2date('','HH:mm dd.MM.yyyy'));\n" +
        		"boolean isDate8=is_date('                ','HH:mm dd.MM.yyyy');\n";
        print_code(expStr);

       Log logger = LogFactory.getLog(this.getClass());
       
        
        try {
              TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStart parseTree = parser.Start();

              System.out.println("Initializing parse tree..");
              parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
              System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.setRuntimeLogger(logger);
              executor.setGraph(graph);
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
              
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isBlank")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isBlank1")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isBlank2")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isAscii1")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isAscii2")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isNumber")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isNumber1")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isNumber2")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isNumber3")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isNumber4")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isNumber5")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isNumber6")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isInteger")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isInteger1")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isInteger2")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isInteger3")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isLong")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate1")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate2")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate3")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate4")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate5")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate6")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(true,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate7")).getTLValue()==TLValue.TRUE_VAL));
		      assertEquals(false,(executor.getGlobalVariable(parser.getGlobalVariableSlot("isDate8")).getTLValue()==TLValue.TRUE_VAL));

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Parse exception",e);
        }
              
    }

    public void test_functions3(){
        System.out.println("\nFunctions test:");
        String expStr = "string test=remove_diacritic('teścik');\n" +
        				"string test1=remove_diacritic('žabička');\n" +
        				"string r1=remove_blank_space('" + 
        				StringUtils.specCharToString(" a	b\nc\rd   e \u000Cf\r\n") + 
        				"');\n" +
        				"string an1 = get_alphanumeric_chars('" +
           				StringUtils.specCharToString(" a	1b\nc\rd \b  e \u000C2f\r\n") + 
        				"');\n" +
           				"string an2 = get_alphanumeric_chars('" +
           				StringUtils.specCharToString(" a	1b\nc\rd \b  e \u000C2f\r\n") + 
        				"',true,true);\n" +
           				"string an3 = get_alphanumeric_chars('" +
           				StringUtils.specCharToString(" a	1b\nc\rd \b  e \u000C2f\r\n") + 
        				"',true,false);\n" +
           				"string an4 = get_alphanumeric_chars('" +
           				StringUtils.specCharToString(" a	1b\nc\rd \b  e \u000C2f\r\n") + 
        				"',false,true);\n" +
        				"string t=translate('hello','leo','pii');\n" +
        				"string t1=translate('hello','leo','pi');\n" +
        				"string t2=translate('hello','leo','piims');\n" +
        				"string t3=translate('hello','leo',null); print_err(t3);\n" +
        				"string t4=translate('my language needs the letter e', 'egms', 'X');\n" +
        				"string input='hello world';\n" +
        				"int index=index_of(input,'l');\n" +
        				"int index1=index_of(input,'l',5);\n" +
        				"int index2=index_of(input,'hello');\n" +
        				"int index3=index_of(input,'hello',1);\n" +
        				"int index4=index_of(input,'world',1);\n";
        print_code(expStr);

       Log logger = LogFactory.getLog(this.getClass());
       
        
        try {
              TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStart parseTree = parser.Start();

              System.out.println("Initializing parse tree..");
              parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
              System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.setRuntimeLogger(logger);
              executor.setGraph(graph);
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
              
		  //    assertEquals("tescik",(executor.getGlobalVariable(parser.getGlobalVariableSlot("test")).getTLValue().toString()));
		  //    assertEquals("zabicka",(executor.getGlobalVariable(parser.getGlobalVariableSlot("test1")).getTLValue().toString()));
		      assertEquals("abcdef",(executor.getGlobalVariable(parser.getGlobalVariableSlot("r1")).getTLValue().toString()));
		      assertEquals("a1bcde2f",(executor.getGlobalVariable(parser.getGlobalVariableSlot("an1")).getTLValue().toString()));
		      assertEquals("a1bcde2f",(executor.getGlobalVariable(parser.getGlobalVariableSlot("an2")).getTLValue().toString()));
		      assertEquals("abcdef",(executor.getGlobalVariable(parser.getGlobalVariableSlot("an3")).getTLValue().toString()));
		      assertEquals("12",(executor.getGlobalVariable(parser.getGlobalVariableSlot("an4")).getTLValue().toString()));
		      assertEquals("hippi",(executor.getGlobalVariable(parser.getGlobalVariableSlot("t")).getTLValue().toString()));
		      assertEquals("hipp",(executor.getGlobalVariable(parser.getGlobalVariableSlot("t1")).getTLValue().toString()));
		      assertEquals("hippi",(executor.getGlobalVariable(parser.getGlobalVariableSlot("t2")).getTLValue().toString()));
		    //  assertTrue((executor.getGlobalVariable(parser.getGlobalVariableSlot("t3")).isNULL()));
		      assertEquals("y lanuaX nXXd thX lXttXr X",(executor.getGlobalVariable(parser.getGlobalVariableSlot("t4")).getTLValue().toString()));
		      assertEquals(2,(executor.getGlobalVariable(parser.getGlobalVariableSlot("index")).getTLValue().getNumeric().getInt()));
		      assertEquals(9,(executor.getGlobalVariable(parser.getGlobalVariableSlot("index1")).getTLValue().getNumeric().getInt()));
		      assertEquals(0,(executor.getGlobalVariable(parser.getGlobalVariableSlot("index2")).getTLValue().getNumeric().getInt()));
		      assertEquals(-1,(executor.getGlobalVariable(parser.getGlobalVariableSlot("index3")).getTLValue().getNumeric().getInt()));
		      assertEquals(6,(executor.getGlobalVariable(parser.getGlobalVariableSlot("index4")).getTLValue().getNumeric().getInt()));

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Parse exception",e);
        }
              
    }

    public void test_math_functions(){
		System.out.println("\nMath functions test:");
		String expStr = "number original;original=pi();\n" +
						"print_err('pi='+original);\n" +
						"number ee=e();\n" +
						"number result;result=sqrt(original);\n" +
						"print_err('sqrt='+result);\n" +
						"int i;i=9;\n" +
						"number p9;p9=sqrt(i);\n" +
						"number ln;ln=log(p9);\n" +
						"print_err('sqrt(-1)='+sqrt(-1));\n" +
						"decimal d;d=0;"+
						"print_err('log(0)='+log(d));\n" +
						"number l10;l10=log10(p9);\n" +
						"number ex;ex =exp(l10);\n" +
						"number po;po=pow(p9,1.2);\n" +
						"number p;p=pow(-10,-0.3);\n" +
						"print_err('power(-10,-0.3)='+p);\n" +
						"int r;r=round(-po);\n" +
						"print_err('round of '+(-po)+'='+r);"+
						"int t;t=trunc(-po);\n" +
						"print_err('truncation of '+(-po)+'='+t);\n" +
						"date date1;date1=2004-01-02 17:13:20;\n" +
						"date tdate1; tdate1=trunc(date1);\n" +
						"print_err('truncation of '+date1+'='+tdate1);\n" +
						"print_err('Random number: '+random());\n";

	      print_code(expStr);
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");

		      if (parser.getParseExceptions().size()>0){
		    	  //report error
		    	  for(Iterator it=parser.getParseExceptions().iterator();it.hasNext();){
			    	  System.out.println(it.next());
			      }
		    	  throw new RuntimeException("Parse exception");
		      }

		      
		      assertEquals("pi",new Double(Math.PI),executor.getGlobalVariable(parser.getGlobalVariableSlot("original")).getTLValue().getNumeric().getDouble());
		      assertEquals("e",new Double(Math.E),executor.getGlobalVariable(parser.getGlobalVariableSlot("ee")).getTLValue().getNumeric().getDouble());
		      assertEquals("sqrt",new Double(Math.sqrt(Math.PI)),executor.getGlobalVariable(parser.getGlobalVariableSlot("result")).getTLValue().getNumeric().getDouble());
		      assertEquals("sqrt(9)",new Double(3),executor.getGlobalVariable(parser.getGlobalVariableSlot("p9")).getTLValue().getNumeric().getDouble());
		      assertEquals("ln",new Double(Math.log(3)),executor.getGlobalVariable(parser.getGlobalVariableSlot("ln")).getTLValue().getNumeric().getDouble());
		      assertEquals("log10",new Double(Math.log10(3)),executor.getGlobalVariable(parser.getGlobalVariableSlot("l10")).getTLValue().getNumeric().getDouble());
		      assertEquals("exp",new Double(Math.exp(Math.log10(3))),executor.getGlobalVariable(parser.getGlobalVariableSlot("ex")).getTLValue().getNumeric().getDouble());
		      assertEquals("power",new Double(Math.pow(3,1.2)),executor.getGlobalVariable(parser.getGlobalVariableSlot("po")).getTLValue().getNumeric().getDouble());
		      assertEquals("power--",new Double(Math.pow(-10,-0.3)),executor.getGlobalVariable(parser.getGlobalVariableSlot("p")).getTLValue().getNumeric().getDouble());
		      assertEquals("round",Integer.parseInt("-4"),executor.getGlobalVariable(parser.getGlobalVariableSlot("r")).getTLValue().getNumeric().getInt());
		      assertEquals("truncation",Integer.parseInt("-3"),executor.getGlobalVariable(parser.getGlobalVariableSlot("t")).getTLValue().getNumeric().getInt());
		      assertEquals("date truncation",new GregorianCalendar(2004,00,02).getTime(),executor.getGlobalVariable(parser.getGlobalVariableSlot("tdate1")).getTLValue().getDate());
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}

//	public void test_global_parameters(){
//		System.out.println("\nGlobal parameters test:");
//		String expStr = "string original;original=${G1};\n" +
//						"int num; num=str2num(original); \n"+
//						"print_err(original);\n"+
//						"print_err(num);\n";
//
//	      print_code(expStr);
//		try {
//			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),
//			  		new ByteArrayInputStream(expStr.getBytes()));
//		      CLVFStart parseTree = parser.Start();
//
// 		      System.out.println("Initializing parse tree..");
//		      parseTree.init();
//		      System.out.println("Parse tree:");
//		      parseTree.dump("");
//		      
//		      System.out.println("Interpreting parse tree..");
//		      TransformLangExecutor executor=new TransformLangExecutor();
//		      executor.setInputRecords(new DataRecord[] {record});
//		      Properties globalParameters = new Properties();
//		      globalParameters.setProperty("G1","10");
//		      executor.setGlobalParameters(globalParameters);
//		      executor.visit(parseTree,null);
//		      System.out.println("Finished interpreting.");
//		      
//		      assertEquals("num",10,executor.getGlobalVariable(parser.getGlobalVariableSlot("num")).getValue().getNumeric().getInt());
//		      
//		} catch (ParseException e) {
//		    	System.err.println(e.getMessage());
//		    	e.printStackTrace();
//		    	throw new RuntimeException("Parse exception",e);
//	    }
//	}
//
	public void test_mapping(){
		System.out.println("\nMapping test:");
		String expStr = "print_err($1.City); "+
						"function test(){\n" +
						"	string result;\n" +
						"	print_err('function');\n" +
						"	result='result';\n" +
						"	//return result;\n" +
						"	$Name:=result;\n" +
						"	$0.Age:=$Age;\n" +
						"	$out.City:=concat(\"My City \",$City);\n" +
						"	$Born:=$1.Born;\n" +
						"	$0.Value:=nvl(0,$in1.Value);\n" +
						"	}\n" +
						"test();\n" +
						"print_err('Age='+ $0.Age);\n "+
						"if (isnull($0.Age)) {print_err('!!!! Age is null!!!');}\n" +
						"print_err($1.City); "+
						"//print_err($out.City); " +
						"print_err($1.City); "+
						"$1.Name:=test();\n" +
						"$out1.Age:=$Age;\n" +
						"$1.City:=$1.City;\n" +
						"$out1.Value:=$in.Value;\n";

	      print_code(expStr);
		try {
		      DataRecordMetadata[] recordMetadata=new DataRecordMetadata[] {metadata,metadata1};
		      DataRecordMetadata[] outMetadata=new DataRecordMetadata[] {metaOut,metaOut1};
			  TransformLangParser parser = new TransformLangParser(recordMetadata,
			  		outMetadata,new ByteArrayInputStream(expStr.getBytes()),"UTF-8");
		      CLVFStart parseTree = parser.Start();

		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record,record1});
		      executor.setOutputRecords(new DataRecord[]{out,out1});
		      SetVal.setString(record1,2,"Prague");
		      record.getField("Age").setNull(true);
		      
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		      assertEquals("result",out.getField("Name").getValue().toString());
		      assertEquals(record.getField("Age").getValue(),out.getField("Age").getValue());
		      assertEquals("My City "+record.getField("City").getValue().toString(), out.getField("City").getValue().toString());
		      assertEquals(record1.getField("Born").getValue(), out.getField("Born").getValue());
		      assertEquals(0,out.getField("Value").getValue());
		      assertEquals("",out1.getField("Name").getValue().toString());
		      assertEquals(record.getField("Age").getValue(), out1.getField("Age").getValue());
		      assertEquals(record1.getField("City").getValue().toString(), out1.getField("City").getValue().toString());
		      assertNull(out1.getField("Born").getValue());
		      assertEquals(record.getField("Value").getValue(), out1.getField("Value").getValue());
		      
		} catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
	    }
	}
    
    
    public void test_logger(){
        System.out.println("\nLogger test:");
        String expStr = "/*raise_error(\"my testing error\") ;*/ " +
        				"print_log(fatal,10 * 15);";
        print_code(expStr);

       Log logger = LogFactory.getLog(this.getClass());
        
        try {
              TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStart parseTree = parser.Start();

              System.out.println("Initializing parse tree..");
              parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
              System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.setRuntimeLogger(logger);
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
              
        } catch (ParseException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Parse exception",e);
        }
    }

    public void test_sequence(){
        System.out.println("\nSequence test:");
        String expStr = "print_err(sequence(test).next);\n"+
                        "print_err(sequence(test).next);\n"+
                        "int i; for(i=0;i<10;++i) print_err(sequence(test).next);\n"+
                        "i=sequence(test).current; print_err(i,true); i=sequence(test).reset; \n" +
                        "print_err('i after reset='+i);\n" +
                        "int current;string next;"+
                        "for(i=0;i<50;++i) { current=sequence(test).current;\n" +
                        "print_err('current='+current);\n" +
                        "next=sequence(test).next;\n" +
                        " print_err('next='+next); }\n";
        print_code(expStr);

       Log logger = LogFactory.getLog(this.getClass());
        
        try {
              TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStart parseTree = parser.Start();

              System.out.println("Initializing parse tree..");
              parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
             System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.setRuntimeLogger(logger);
              executor.setGraph(graph);
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
              
        } catch (ParseException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Parse exception",e);
        }
    }

    public void test_lookup(){
        System.out.println("\nLookup test:");
        String expStr = "string key='one';\n"+
                        "string val=lookup(LKP,key).Name;\n"+
                        "print_err(lookup(LKP,'  HELLO ').Age); \n"+
                        "print_err(lookup_found(LKP));\n"+
                        "print_err(lookup_next(LKP).Age);\n"+
                        "print_err(lookup(LKP,'two').Name); \n"+
                        "/*print_err(lookup(LKP,'two').Name);\n"+
                        "print_err(lookup(LKP,'xxx').Name);\n*/";
        print_code(expStr);

       Log logger = LogFactory.getLog(this.getClass());
       
        
        try {
              TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStart parseTree = parser.Start();

              System.out.println("Initializing parse tree..");
              parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
              System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.setRuntimeLogger(logger);
              executor.setGraph(graph);
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
              
        } catch (ParseException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Parse exception",e);
        }
    }
    
    
    public void test_function(){
        System.out.println("\nFunction test:");
        String expStr = "function myFunction(idx){\n" +
        		"if (idx==1) print_err('idx equals 1'); else print_err('idx does not equal 1');}\n" +
        		"myFunction(1);\n" +
        		"myFunction1(1);\n";
        print_code(expStr);

       Log logger = LogFactory.getLog(this.getClass());
       
        
        try {
              TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStart parseTree = parser.Start();

              System.out.println("Initializing parse tree..");
              parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
              System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.setRuntimeLogger(logger);
              executor.setGraph(graph);
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
              
//              CLVFFunctionDeclaration function = (CLVFFunctionDeclaration)parser.getFunctions().get("myFunction");
//              executor.executeFunction(function, new TLValue[]{new TLValue(TLValueType.INTEGER,new CloverInteger(1))});
//              executor.executeFunction(function, new TLValue[]{new TLValue(TLValueType.INTEGER,new CloverInteger(10))});
    
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Parse exception",e);
        }
              
    }

    public void test_import(){
        System.out.println("\nImport test:");
        String expStr = "import 'data/tlExample.ctl';";
        print_code(expStr);

       Log logger = LogFactory.getLog(this.getClass());
       
        
        try {
              TransformLangParser parser = new TransformLangParser(record.getMetadata(),
                    new ByteArrayInputStream(expStr.getBytes()));
              CLVFStart parseTree = parser.Start();

              System.out.println("Initializing parse tree..");
              parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
              System.out.println("Interpreting parse tree..");
              TransformLangExecutor executor=new TransformLangExecutor();
              executor.setInputRecords(new DataRecord[] {record});
              executor.setRuntimeLogger(logger);
              executor.setGraph(graph);
              executor.visit(parseTree,null);
              System.out.println("Finished interpreting.");
              
		      assertEquals(10,(executor.getGlobalVariable(parser.getGlobalVariableSlot("i")).getTLValue().getNumeric().getInt()));

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Parse exception",e);
        }
              
    }

    public void test_eval(){
		System.out.println("\neval test:");
		String expStr = "string str='print_err(\"eval test OK\");';\n eval(str); print_err(eval_exp('\"ahoj\"'));\n";
		
		try {
			  TransformLangParser parser = new TransformLangParser(record.getMetadata(),expStr);
		      CLVFStart parseTree = parser.Start();

		      print_code(expStr);
		      
 		      System.out.println("Initializing parse tree..");
		      parseTree.init();
		      System.out.println("Parse tree:");
		      parseTree.dump("");
		      
		      System.out.println("Interpreting parse tree..");
		      TransformLangExecutor executor=new TransformLangExecutor();
		      executor.setInputRecords(new DataRecord[] {record});
		      executor.setParser(parser);
		      executor.visit(parseTree,null);
		      System.out.println("Finished interpreting.");
		      
		    } catch (ParseException e) {
		    	System.err.println(e.getMessage());
		    	e.printStackTrace();
		    	throw new RuntimeException("Parse exception",e);
		    }
	}

    
    
    
    public void print_code(String text){
        String[] lines=text.split("\n");
        System.out.println("\t:         1         2         3         4         5         ");
        System.out.println("\t:12345678901234567890123456789012345678901234567890123456789");
        for(int i=0;i<lines.length;i++){
            System.out.println((i+1)+"\t:"+lines[i]);
        }
    }
}
