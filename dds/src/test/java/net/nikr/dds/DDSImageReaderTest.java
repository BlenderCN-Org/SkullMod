/*
 * DDSImageReaderTest.java - This file is part of Java DDS ImageIO Plugin
 *
 * Copyright (C) 2011 Niklas Kyster Rasmussen
 * 
 * COPYRIGHT NOTICE:
 * Java DDS ImageIO Plugin is based on code from the DDS GIMP plugin.
 * Copyright (C) 2004-2010 Shawn Kirst <skirst@insightbb.com>,
 * Copyright (C) 2003 Arne Reuter <homepage@arnereuter.de>
 *
 * Java DDS ImageIO Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Java DDS ImageIO Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Java DDS ImageIO Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * FILE DESCRIPTION:
 * TODO Write File Description for DDSImageReaderTest.java
 */

package net.nikr.dds;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class DDSImageReaderTest {
	
	private final TestParams[] params = {
		new TestParams("acids.dds", 8, 128, 128),			//DXT1 (With 8 mipMaps)
		new TestParams("ATI1N.dds", 1, 256, 256),			//ATI1N - OK?
		new TestParams("ATI2N.dds", 1, 256, 256),			//ATI2N - OK?
		new TestParams("ATI2N_alt.dds", 1, 256, 256),		//ATI2N - OK?
		new TestParams("dxt1.dds", 1, 256, 256),			//DXT1 - OK
		new TestParams("dxt3.dds", 1, 256, 256),			//DXT3 - OK
		new TestParams("dxt5.dds", 1, 256, 256),			//DXT5 - OK
		new TestParams("dxt5_ati2n.dds", 1, 256, 256),		//DXT5 - OK
		new TestParams("dxt5_rbxg.dds", 1, 256, 256),		//DXT5 - OK
		new TestParams("dxt5_rgxb.dds", 1, 256, 256),		//DXT5 - OK
		new TestParams("dxt5_rxbg.dds", 1, 256, 256),		//DXT5 - OK
		new TestParams("dxt5_xgbr.dds", 1, 256, 256),		//DXT5 - OK
		new TestParams("dxt5_xgxr.dds", 1, 256, 256),		//DXT5 - OK
		new TestParams("dxt5_xrbg.dds", 1, 256, 256),		///DXT5 - OK
		new TestParams("test_image.dds", 1, 256, 256),		//A8R8G8B8 - OK
		new TestParams("test_image-bc1c.dds", 9, 256, 256),	//DXT1 (no Alpha) - OK
		new TestParams("test_image-bc1a.dds", 9, 256, 256),	//DXT1 (1bit Alpha) - OK
		new TestParams("test_image-bc2.dds", 9, 256, 256),	//DXT3 - OK
		new TestParams("test_image-bc3.dds", 9, 256, 256),	//DXT5 - OK
		new TestParams("test_image-bc4.dds", 1, 256, 256),	//ATI1N - OK?
		new TestParams("test_image-bc4.dds", 1, 256, 256),	//ATI2N - OK?
		
		new TestParams("gimp" + File.separator + "a8.dds", 9, 256, 256),				//NONE A8 - OK
		new TestParams("gimp" + File.separator + "abgr8.dds", 9, 256, 256),				//NONE ABGR8 - OK
		new TestParams("gimp" + File.separator + "aexp.dds", 9, 256, 256),				//??? - OK
		new TestParams("gimp" + File.separator + "alpha_exponent_dxt5.dds", 9, 256, 256),//bc3/dxt5 (Alpha Exponent)
		
		new TestParams("gimp" + File.separator + "bc1.dds", 9, 256, 256),				//bc1/dxt1 - OK
		new TestParams("gimp" + File.separator + "bc2.dds", 9, 256, 256),				//bc2/dxt3 - OK
		new TestParams("gimp" + File.separator + "bc3.dds", 9, 256, 256),				//bc3/dxt5 - OK
		new TestParams("gimp" + File.separator + "bc3n.dds", 9, 256, 256),				//bc3n/dxt5 - OK
		new TestParams("gimp" + File.separator + "bc4.dds", 9, 256, 256),				//bc4/ati1 - OK
		new TestParams("gimp" + File.separator + "bc5.dds", 9, 256, 256),				//bc5/ati2 - OK
		
		//FIXME can not be writen by GIMP
		//new TestParams("gimp" + File.separator + "bgr8.dds", 9, 256, 256),				//NONE BGR8
		
		new TestParams("gimp" + File.separator + "l8.dds", 9, 256, 256),				//NONE L8 - OK
		new TestParams("gimp" + File.separator + "l8a8.dds", 9, 256, 256),				//NONE L8A8 - OK
		
		new TestParams("gimp" + File.separator + "r3g3b2.dds", 9, 256, 256),			//NONE R3G3B2 - OK
		new TestParams("gimp" + File.separator + "r5g6b5.dds", 9, 256, 256),			//NONE R5G6B5 - OK
		new TestParams("gimp" + File.separator + "rgb5a1.dds", 9, 256, 256),			//NONE RGB5A1 - FAIL
		new TestParams("gimp" + File.separator + "rgb8.dds", 9, 256, 256),				//NONE RGB8 - FAIL
		new TestParams("gimp" + File.separator + "rgb10a2.dds", 9, 256, 256),			//NONE RGB10A2 - FAIL
		new TestParams("gimp" + File.separator + "rgba4.dds", 9, 256, 256),				//NONE RGBA4 - OK
		new TestParams("gimp" + File.separator + "rgba8.dds", 9, 256, 256),				//NONE RGBA8 - OK
		
		new TestParams("gimp" + File.separator + "ycocg.dds", 9, 256, 256),				//NONE YCoCg
		new TestParams("gimp" + File.separator + "ycocg_dxt5.dds", 9, 256, 256),		//DXT5 (YCoCg)
		new TestParams("gimp" + File.separator + "ycocg_scaled_dxt5.dds", 9, 256, 256),	//DXT5 (YCoCg scaled)
	};
    
	
	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}


	/**
	 * Test of setInput method, of class DDSImageReader.
	 */
	@Test
	public void testSetInput() {
		System.out.println("setInput");
		for (int i = 0; i < params.length; i++){
			Object input = getInput(i);
			boolean seekForwardOnly = false;
			boolean ignoreMetadata = false;
			DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
			instance.setInput(input, seekForwardOnly, ignoreMetadata);
			assertEquals(input, instance.getInput());
		}
	}

	/**
	 * Test of getNumImages method, of class DDSImageReader.
	 */
	@Test
	public void testGetNumImages() throws Exception {
		System.out.println("getNumImages");
		for (int i = 0; i < params.length; i++){
			boolean allowSearch = false;
			DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
			instance.setInput( getInput(i) );
			int result = instance.getNumImages(allowSearch);
			assertEquals(params[i].getMipMaps(), result);
		}
	}

	/**
	 * Test of getWidth method, of class DDSImageReader.
	 */
	@Test
	public void testGetWidth() throws Exception {
		System.out.println("getWidth");
		for (int i = 0; i < params.length; i++){
			int width = params[i].getWidth();
			for (int imageIndex = 0; imageIndex < params[i].getMipMaps(); imageIndex++){
				DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
				instance.setInput( getInput(i) );
				int result = instance.getWidth(imageIndex);
				assertEquals(result, width);
				width = Math.max(width / 2, 1);
			}
		}
	}

	/**
	 * Test of getHeight method, of class DDSImageReader.
	 */
	@Test
	public void testGetHeight() throws Exception {
		System.out.println("getHeight");
		for (int i = 0; i < params.length; i++){
			int height = params[i].getHeight();
			for (int imageIndex = 0; imageIndex < params[i].getMipMaps(); imageIndex++){
				DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
				instance.setInput( getInput(i) );
				int result = instance.getHeight(imageIndex);
				assertEquals(result, height);
				height = height / 2;
			}
		}
	}

	/**
	 * Test of getImageTypes method, of class DDSImageReader.
	 */
	@Test
	public void testGetImageTypes() throws Exception {
		System.out.println("getImageTypes");
		for (int i = 0; i < params.length; i++){
			for (int imageIndex = 0; imageIndex < params[i].getMipMaps(); imageIndex++){
				DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
				instance.setInput( getInput(i) );
				ImageTypeSpecifier expResult = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_4BYTE_ABGR);
				ImageTypeSpecifier result = instance.getImageTypes(imageIndex).next();
				assertEquals(expResult, result);
			}
		}
	}

	/**
	 * Test of getStreamMetadata method, of class DDSImageReader.
	 */
	@Test
	public void testGetStreamMetadata() {
		System.out.println("getStreamMetadata");
		for (int i = 0; i < params.length; i++){
			for (int imageIndex = 0; imageIndex < params[i].getMipMaps(); imageIndex++){
				DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
				instance.setInput( getInput(i) );
				IIOMetadata result = instance.getStreamMetadata();
				assertNull(result); //TODO no meta data returned...
			}
		}
	}

	/**
	 * Test of getImageMetadata method, of class DDSImageReader.
	 */
	@Test
	public void testGetImageMetadata() throws IOException {
		System.out.println("getImageMetadata");
		for (int i = 0; i < params.length; i++){
			for (int imageIndex = 0; imageIndex < params[i].getMipMaps(); imageIndex++){
				DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
				instance.setInput( getInput(i) );
				IIOMetadata result = instance.getImageMetadata(imageIndex);
				assertNull(result); //TODO no meta data returned...
			}
		}
	}

	/**
	 * Test of read method, of class DDSImageReader.
	 */
	@Test
	public void testRead() throws IOException {
		System.out.println("read");
		for (int i = 0; i < params.length; i++){
			System.out.println(params[i].getName());
			for (int imageIndex = 0; imageIndex < params[i].getMipMaps(); imageIndex++){
				DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
				instance.setInput( getInput(i) );
				BufferedImage result;
				try {
					result = instance.read(imageIndex);
					assertNotNull(result);
				} catch (Exception ex) {
					//fail("Failed to read imageIndex "+imageIndex+" from "+params[i].getName());
					throw new IIOException("Failed to read imageIndex "+imageIndex+" from "+params[i].getName(), ex);
				}
				
			}
		}
	}
	
	/**
	 * Test of ImageIO
	 */
	@Test
	public void testImageIO() throws Exception {
		System.out.println("ImageIO");
		for (TestParams param : params){
			try {
				System.out.println(param.getName());
				ImageIO.read(new File(param.getFilename()));
			} catch (IOException ex) {
				fail("testImageIO -> Fail for file: "+param.getName()+" -> "+ex.getMessage());
			}
		}
	}

	@Test
	public void testThreads() throws Exception {
		System.out.println("Threads");
		List<ThreadTester> threads = new ArrayList<ThreadTester>();
		for (int i = 0; i < 8; i++) {
			ThreadTester thread = new ThreadTester();
			threads.add(thread);
			thread.start();
		}
		for (ThreadTester thread : threads) {
			thread.join();
			assertFalse("Thread test failed", thread.isFailed());
		}	
	}

	private Object getInput(int i){
		Object input = null;
		File file = new File(params[i].getFilename());
		try {
			input = new FileImageInputStream(file);
		} catch (FileNotFoundException ex) {
			fail("getInput -> File "+file.getName()+" not found -> "+ex.getMessage());
		} catch (IOException ex) {
			fail("getInput -> IO Failed for "+file.getName()+" -> "+ex.getMessage());
		}
		return input;
	}
	
	private BufferedImage getImage(int i){
		BufferedImage image = null;
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(new File(params[i].getFilename()));
			Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix ("dds");
			ImageReader reader = (ImageReader) iter.next();
			reader.setInput (iis, true);
			ImageReadParam irp = reader.getDefaultReadParam ();
			//image = reader.read (0, irp);
			image = reader.read (0);
			// Cleanup.
			reader.dispose ();
		} catch (IOException ex) {
			
		}
		
		/*
		try {
			image = ImageIO.read(new File(testdir+testfile));
		} catch (IOException ex) {
			
		}
		 */
		return image;
	}

	private class ThreadTester extends Thread{

		private boolean failed = false;;

		@Override
		public void run() {
			for (TestParams param : params){
				try {
					System.out.println(param.getName());
					ImageIO.read(new File(param.getFilename()));
				} catch (Exception ex) {
					failed = true;
				}
			}
		}

		public boolean isFailed() {
			return failed;
		}
	}
	
	private class TestParams{
		private final String dir = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "net" + File.separator + "nikr" + File.separator + "dds" + File.separator;
		private String filename;
		private int mipMaps;
		private int width;
		private int height;

		public TestParams(String filename, int mipMaps, int width, int height) {
			this.filename = filename;
			this.mipMaps = mipMaps;
			this.width = width;
			this.height = height;
		}

		public String getName(){
			return filename;
		}
		
		public String getFilename() {
			return dir+filename;
		}

		public int getHeight() {
			return height;
		}

		public int getMipMaps() {
			return mipMaps;
		}

		public int getWidth() {
			return width;
		}
	}
}
