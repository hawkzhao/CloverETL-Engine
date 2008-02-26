/*
 *  jETeL/Clover - Java based ETL application framework.
 *  Copyright (C) 2002-05  David Pavlis, Wes Maciorowski
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.jetel.data.tape;

import java.io.IOException;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.jetel.data.DataRecord;
import org.jetel.graph.runtime.EngineInitializer;
import org.jetel.metadata.DataFieldMetadata;
import org.jetel.metadata.DataRecordMetadata;

/**
 * @author dpavlis
 *
 */
public class DataRecordTapeTest  extends TestCase {

    private DataRecord testRecordA,testRecordB;

	

protected void setUp() { 
	
    DataRecordMetadata metadata=new DataRecordMetadata("test_record");
    
    metadata.addField(new DataFieldMetadata("Field1",DataFieldMetadata.STRING_FIELD,(short)10));
    metadata.addField(new DataFieldMetadata("Field2",DataFieldMetadata.STRING_FIELD,(short)15));
    metadata.addField(new DataFieldMetadata("Field3",DataFieldMetadata.INTEGER_FIELD,(short)10));
    
    testRecordA=new DataRecord(metadata);
    testRecordB=new DataRecord(metadata);
    testRecordA.init();
    testRecordB.init();
    testRecordA.getField(0).setValue("First");
    testRecordA.getField(1).setValue("Second field");
    testRecordA.getField(2).setValue(new Integer(-1234567));
    testRecordB.copyFieldsByPosition(testRecordA);
    
	EngineInitializer.initEngine(null, null, null);
}



protected void tearDown() {
	testRecordA=testRecordB = null;
}


/**
 *  Test for @link org.jetel.data.StringDataField.StringDataField(DataFieldMetadata _metadata)
 *
 */
public void test_1_DataTape() {
	ByteBuffer buffer= ByteBuffer.allocateDirect(2048);
    
    DataRecordTape tape=new DataRecordTape("tapeTest.tmp", true, false);
    // first chunk of data
    try{
        tape.open();
        tape.addDataChunk();
    }catch(IOException ex){
        throw new RuntimeException(ex);
    }
    
    for(int i=0;i<100;i++){
        testRecordA.serialize(buffer);
        buffer.flip();
        try{
        	tape.put(buffer);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        buffer.clear();
    }
    //second chunk of data
        tape.addDataChunk();
    
    for(int i=0;i<9999;i++){
        testRecordA.serialize(buffer);
        buffer.flip();
        try{
        tape.put(buffer);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        buffer.clear();
    }
    //  third chunk of data
        tape.addDataChunk();
    
    for(int i=0;i<1;i++){
        testRecordA.serialize(buffer);
        buffer.flip();
        try{
        tape.put(buffer);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        buffer.clear();
    }
    
    System.out.println(tape);
    
    tape.rewind();
    System.out.println("Tape rewinded");
    
    boolean result=false;
    int counter=0;
    do{
        buffer.clear();
        try{
        result=tape.get(buffer);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        if (result) counter++;
        assertTrue(testRecordA.equals(testRecordB));    
        }while(result!=false);
    
    assertEquals(counter,100);

    // set last data chunk and read from it
    tape.setDataChunk(2);
    counter=0;
    do{
        buffer.clear();
        try{
        result=tape.get(buffer);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        if (result) counter++;
        assertTrue(testRecordA.equals(testRecordB));    
        }while(result!=false);
    
    assertEquals(counter,1);
    
    // set last but one data chunk and read from it
    tape.setDataChunk(1);
    counter=0;
    do{
        buffer.clear();
        try{
        result=tape.get(buffer);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        if (result) counter++;
        assertTrue(testRecordA.equals(testRecordB));    
        }while(result!=false);
    
    assertEquals(counter,9999);
    
    try{
        tape.close();
    }catch(IOException ex){
        //throw new RuntimeException(ex);
    }
    
	
	System.out.println("Finished");
	
}

}
