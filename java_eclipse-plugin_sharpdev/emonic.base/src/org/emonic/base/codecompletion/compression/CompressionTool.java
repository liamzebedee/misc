/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2008 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 */
package org.emonic.base.codecompletion.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Compress/Decompress data
 * @author dertl
 *
 */
public class CompressionTool {

	
	/**
	 * compress an Object
	 * @param itrie
	 * @return
	 */
	public ByteArrayOutputStream compressObject(Object obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//time measurement
		//long start = new Date().getTime();

		try {
			GZIPOutputStream lzfos = new GZIPOutputStream(baos);//LZFOutputStream(baos); //GZIPOutputStream
			ObjectOutputStream oos = new ObjectOutputStream(lzfos); //lzfos
			oos.writeObject(obj);
		    oos.flush();
		    oos.close();
		    lzfos.close(); //?
		    baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//long runningTime = new Date().getTime() - start;
		//System.out.println("Time for compression: " + runningTime);
		
		return baos;
	}
	
	/*
	 * decompress an Object
	 */
	public Object decompressObject(ByteArrayOutputStream baos){
		Object obj = null;
	
		try {
			
			ByteArrayInputStream bain = new ByteArrayInputStream(baos.toByteArray());
			GZIPInputStream lzfin = new GZIPInputStream(bain);//LZFInputStream(bain); //GZIPInputStream
			ObjectInputStream oin = new ObjectInputStream(lzfin); //lzfin
			obj = oin.readObject();
			oin.close();
			lzfin.close(); //?
			bain.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


		return obj;
	}
	
	 /**
	  * object to bytearray
	  * @param obj
	  * @return
	  * @throws java.io.IOException
	  */
	  /*private byte[] getBytes(Object obj) throws java.io.IOException{
	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
	      ObjectOutputStream oos = new ObjectOutputStream(bos);
	      oos.writeObject(obj);
	      oos.flush();
	      oos.close();
	      bos.close();
	      byte [] data = bos.toByteArray();
	      return data;
	  }/*
	  
	  /**
	   * byteArray to object
	   * @param bytes
	   * @return
	   */
	  /*private Object getObject(byte[] bytes){
		  Object object = null;
		  
		  ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		  try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			object = ois.readObject();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return object;
	  }*/

}
