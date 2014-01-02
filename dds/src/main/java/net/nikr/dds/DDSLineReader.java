/*
 * DDSLineReader.java - This file is part of Java DDS ImageIO Plugin
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
 * TODO Write File Description for DDSLineReader.java
 */

package net.nikr.dds;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import net.nikr.dds.DDSPixelFormat.Format;

public class DDSLineReader {
	
	private static final int BANK_RED = 0;
	private static final int BANK_GREEN = 1;
	private static final int BANK_BLUE = 2;
	private static final int BANK_ALPHA = 3;

	private static final int LINES_PER_READ = 4;
	private static final int COLORS_PER_READ = 4;
	
	private byte[][][] linesColor;
	private int lineNumber = 0;

	public DDSLineReader(){
		lineNumber = 0;
	}
	
	public byte[] readAll(ImageInputStream stream, Format format, int bitCount, int width, int height) throws IOException{
		byte[] bytes;
		switch (format){
			case UNCOMPRESSED:
				int byteCount = (int)(bitCount/8);
				bytes = new byte[(height*width*byteCount)];
				stream.readFully(bytes);
				break;
			case DXT1:
			case ATI1:
				bytes = new byte[8*Math.max(1, width/4)*Math.max(1, height/4)];
				stream.readFully(bytes);
				break;
			case DXT3:
			case DXT5:
			case ATI2:
				bytes = new byte[16*(int)Math.max(1, width/4)*(int)Math.max(1, height/4)];
				stream.readFully(bytes);
				break;
			case NOT_DDS:
				throw new IOException("Not a supported format!");
			default:
				throw new IOException(format.getName()+" is not a supported format!");
		}
		return bytes;
	}
	
	public void decodeLine(byte[] bytes, DDSHeader ddsHeader, byte [][] banks, int width, int y) throws IOException{
		switch (ddsHeader.getPixelFormat().getFormat()){
			case UNCOMPRESSED:
				decodeUncompressed(bytes, ddsHeader, banks, width, y);
				break;
			case DXT1:
			case DXT3:
			case DXT5:
				decodeDXT(bytes, ddsHeader, banks, width, y);
				break;
			case ATI1:
			case ATI2:
				decodeATI(bytes, ddsHeader, banks, width, y);
				break;
			case NOT_DDS:
				throw new IOException("Not a supported format!");
			default:
				throw new IOException(ddsHeader.getPixelFormat().getFormat().getName()+" is not a supported format!");
		}
	}
	
	private void decodeUncompressed(byte[] bytes, DDSHeader ddsHeader, byte [][] banks, int width, int y) throws IOException{
		long pixel, a, r, g, b;
		int byteCount = (int)(ddsHeader.getPixelFormat().getRgbBitCount()/8);
		int lineOffset = y * width * byteCount;
		for (int x = 0; x < width; x++) {
			pixel = 0;
			switch ((int)ddsHeader.getPixelFormat().getRgbBitCount()){
				case 8:
					pixel = (bytes[(y*width)+x] & 0xFFL);
					break;
				case 16:
					pixel = (bytes[(lineOffset+x*byteCount)+0] & 0xFFL) | ((bytes[(lineOffset+x*byteCount)+1] & 0xFFL) << 8);
					break;
				case 24:
					pixel = (bytes[(lineOffset+x*byteCount)+0] & 0xFFL) | ((bytes[(lineOffset+x*byteCount)+1] & 0xFFL) << 8) | ((bytes[(lineOffset+x*byteCount)+2] & 0xFFL) << 16);
					break;
				case 32:
					pixel = (bytes[(lineOffset+x*byteCount)+0] & 0xFFL) | ((bytes[(lineOffset+x*byteCount)+1] & 0xFFL) << 8) | ((bytes[(lineOffset+x*byteCount)+2] & 0xFFL) << 16) | ((bytes[(lineOffset+x*byteCount)+3] & 0xFFL) << 24);
					break;
			}
			DDSPixelFormat pf = ddsHeader.getPixelFormat();
			
			a = 255;
			r = 255;
			g = 255;
			b = 255;

			if ((pf.getRgbBitCount() == 32)
					&& (pf.getMaskRed() == 0x3ff00000l)
					&& (pf.getMaskGreen() == 0xffc00l)
					&& (pf.getMaskBlue() == 0x3ffl) && (pf.getMaskAlpha() == 0xc0000000l)){
				//RGB10A2
				r = (pixel >> pf.getShiftRed()) >> 2;
				g = (pixel >> pf.getShiftGreen()) >> 2;
				b = (pixel >> pf.getShiftBlue()) >> 2;
				if(pf.isAlphaPixels()){
					a = (pixel >> pf.getShiftAlpha() << (8 - pf.getBitsAlpha()) & pf.getMaskFixedAlpha()) * 255 / pf.getMaskFixedAlpha();
				}
			} else {
				if (pf.getMaskFixedAlpha() != 0){
					a = (pixel >> pf.getShiftAlpha() << (8 - pf.getBitsAlpha()) & pf.getMaskFixedAlpha()) * 255 / pf.getMaskFixedAlpha();
				}
				if (pf.getMaskFixedRed() != 0){
					r = (pixel >> pf.getShiftRed() << (8 - pf.getBitsRed()) & pf.getMaskFixedRed()) * 255 / pf.getMaskFixedRed();
				}
				if (pf.getMaskFixedGreen() != 0){
					g = (pixel >> pf.getShiftGreen() << (8 - pf.getBitsGreen()) & pf.getMaskFixedGreen()) * 255 / pf.getMaskFixedGreen();
				}
				if (pf.getMaskFixedBlue() != 0){
					b = (pixel >> pf.getShiftBlue() << (8 - pf.getBitsBlue()) & pf.getMaskFixedBlue()) * 255 / pf.getMaskFixedBlue();
				}
			}
			
			banks[BANK_RED][x] = (byte) r;
			banks[BANK_GREEN][x] = (byte) g;
			banks[BANK_BLUE][x] = (byte) b;
			banks[BANK_ALPHA][x] = (byte) a;
		}
	}
	
	private void decodeDXT(byte[] bytes, DDSHeader ddsHeader, byte [][] banks, int width, int y) throws IOException{
		if (lineNumber >= LINES_PER_READ) lineNumber = 0;
		if (lineNumber == 0){
			//System.out.println("Read line: "+y);
			linesColor = new byte[LINES_PER_READ][(int)Math.max(width, 8)][COLORS_PER_READ];
			
			if (ddsHeader.getPixelFormat().isDXT3()){
				for (int x = 0; x < (width); x = x + 4) {
					decodeDXT3AlphaBlock(bytes, 16, 0, x, (16 *  width / 4 * y / 4));
					decodeColorBlock(bytes, 16, 8, x, (16 *  width / 4 * y / 4), false);
				}
			} else if (ddsHeader.getPixelFormat().isDXT5()){
				for (int x = 0; x < (width); x = x + 4) {
					decodeAtiAndDxt5AlphaBlock(bytes, 16, 0, x, (16 *  width / 4 * y / 4), BANK_ALPHA);
					decodeColorBlock(bytes, 16, 8, x, (16 *  width / 4 * y / 4), false);
				}
			} else {
				for (int x = 0; x < width; x = x + 4) {
					decodeColorBlock(bytes, 8, 0, x, (8 *  width / 4 * y / 4),  true);
				}
			}
		}
		for (int x = 0; x < (width); x++) {
			banks[BANK_RED][x] = linesColor[lineNumber][x][BANK_RED];
			banks[BANK_GREEN][x] = linesColor[lineNumber][x][BANK_GREEN];
			banks[BANK_BLUE][x] = linesColor[lineNumber][x][BANK_BLUE];
			banks[BANK_ALPHA][x] = linesColor[lineNumber][x][BANK_ALPHA];
		}
		lineNumber++;
	}
	
	private void decodeATI(byte[] bytes, DDSHeader ddsHeader, byte[][] banks, int width, int y) throws IOException{
		if (lineNumber >= LINES_PER_READ) lineNumber = 0;
		if (lineNumber == 0){
			linesColor = new byte[LINES_PER_READ][(int)Math.max(width, 8)][COLORS_PER_READ];
			if (ddsHeader.getPixelFormat().isATI1()){
				for (int x = 0; x < (width); x = x + 4) {
					decodeAtiAndDxt5AlphaBlock(bytes,8 , 0, x, (8 *  width / 4 * y / 4), BANK_RED);
					for (int yi = 0; yi < 4 ; yi++){
						for (int xi = 0; xi < 4; xi++){
							linesColor[yi][x+xi][BANK_GREEN] = (byte) linesColor[yi][x+xi][BANK_RED];
							linesColor[yi][x+xi][BANK_BLUE] = (byte) linesColor[yi][x+xi][BANK_RED];
							linesColor[yi][x+xi][BANK_ALPHA] = (byte) 255;
						}
					}
				}
			}
			if (ddsHeader.getPixelFormat().isATI2()){
				for (int x = 0; x < (width); x = x + 4) {
					decodeAtiAndDxt5AlphaBlock(bytes, 16, 0, x, (16 *  width / 4 * y / 4), BANK_GREEN);
					decodeAtiAndDxt5AlphaBlock(bytes, 16, 8, x, (16 *  width / 4 * y / 4), BANK_RED);
					for (int yi = 0; yi < 4 ; yi++){
						for (int xi = 0; xi < 4; xi++){
							linesColor[yi][x+xi][BANK_BLUE] = (byte) 0;
							linesColor[yi][x+xi][BANK_ALPHA] = (byte) 255;
						}
					}
				}
			}
		}
		for (int x = 0; x < (width); x++) {
			banks[BANK_RED][x] = linesColor[lineNumber][x][BANK_RED];
			banks[BANK_GREEN][x] = linesColor[lineNumber][x][BANK_GREEN];
			banks[BANK_BLUE][x] = linesColor[lineNumber][x][BANK_BLUE];
			banks[BANK_ALPHA][x] = linesColor[lineNumber][x][BANK_ALPHA];
		}
		lineNumber++;
	}
	
	private void decodeColorBlock(byte[] bytes, int offset, int start, int x, int lineOffset, boolean dxt1) throws IOException{
		int c0lo, c0hi, c1lo, c1hi, bits0, bits1, bits2, bits3;
		//Read 8 bytes
		c0lo =  bytes[lineOffset+(x/4*offset)+(0+start)] & 0xff;
		c0hi =  bytes[lineOffset+(x/4*offset)+(1+start)] & 0xff;
		c1lo =  bytes[lineOffset+(x/4*offset)+(2+start)] & 0xff;
		c1hi =  bytes[lineOffset+(x/4*offset)+(3+start)] & 0xff;
		bits0 = bytes[lineOffset+(x/4*offset)+(4+start)] & 0xff;
		bits1 = bytes[lineOffset+(x/4*offset)+(5+start)] & 0xff;
		bits2 = bytes[lineOffset+(x/4*offset)+(6+start)] & 0xff;
		bits3 = bytes[lineOffset+(x/4*offset)+(7+start)] & 0xff;

		
		long bits = bits0 + 256 * (bits1 + 256 * (bits2 + 256 * bits3));
		int color0 = (c0lo + c0hi * 256);
		int color1 = (c1lo + c1hi * 256);
		
		int[][] colorsRGBA = new int[4][4];
		colorsRGBA[0] = unpackRBG565(color0);
		colorsRGBA[1] = unpackRBG565(color1);
		
		if (color0 > color1) {
			//RGB
			for (int i = 0; i < 3; i++){
				colorsRGBA[2][i] = (( 2 * colorsRGBA[0][i] + colorsRGBA[1][i]) / 3) ;
				colorsRGBA[3][i] = (( colorsRGBA[0][i] + 2 * colorsRGBA[1][i]) / 3);
			}
			//Transparency 
			colorsRGBA[2][3] = 255;
			colorsRGBA[3][3] = 255;
		} else { 
			//RGB
			for (int i = 0; i < 3; i++){
				colorsRGBA[2][i] = ((colorsRGBA[0][i] + colorsRGBA[1][i]) / 2);
				colorsRGBA[3][i] = 0; //Black or tranperant
			}
			//Transparency 
			colorsRGBA[2][3] = 255;
			if (dxt1){
				colorsRGBA[3][3] = 0;
			} else {
				colorsRGBA[3][3] = 255;
			}
		}
		int i = 0;
		for (int yi = 0; yi < 4 ; yi++){
			for (int xi = 0; xi < 4; xi++){
				byte code = (byte)((bits >> i*2) & 3);
				linesColor[yi][x+xi][BANK_RED] = (byte) colorsRGBA[code][0];
				linesColor[yi][x+xi][BANK_GREEN] = (byte) colorsRGBA[code][1];
				linesColor[yi][x+xi][BANK_BLUE] = (byte) colorsRGBA[code][2];
				if (dxt1){
					linesColor[yi][x+xi][BANK_ALPHA] = (byte) colorsRGBA[code][3];
				}
				i++;
			}
		}
	}
	private void decodeDXT3AlphaBlock(byte[] bytes, int offset, int start, int x, int lineOffset) throws IOException{
		int a0, a1, a2, a3, a4, a5, a6, a7;
		
		//Read 8 Bytes
		a0 = bytes[lineOffset+(x/4*offset)+(0+start)] & 0xff;
		a1 = bytes[lineOffset+(x/4*offset)+(1+start)] & 0xff;
		a2 = bytes[lineOffset+(x/4*offset)+(2+start)] & 0xff;
		a3 = bytes[lineOffset+(x/4*offset)+(3+start)] & 0xff;
		a4 = bytes[lineOffset+(x/4*offset)+(4+start)] & 0xff;
		a5 = bytes[lineOffset+(x/4*offset)+(5+start)] & 0xff;
		a6 = bytes[lineOffset+(x/4*offset)+(6+start)] & 0xff;
		a7 = bytes[lineOffset+(x/4*offset)+(7+start)] & 0xff;

		///Decompress the Alpha (in two longs, as the value is a 64bit UNSIGNED LONG)
		long alpha0 = a0 + 256 * (a1 + 256 * (a2 + 256 * (a3 + 256)));
		long alpha1 = a4 + 256 * (a5 + 256 * (a6 + 256 * (a7)));
		
		///Loop through each 4x4 block 
		for (int yi = 0; yi < 4 ; yi++){
			for (int xi = 0; xi < 4; xi++){
				//Calculate the bit posistion in the alpha0, alpha1 (long)
				byte code = (byte)(4*(4*yi+xi));
				int a;
				if (code >= 32){
					//Minus 32, as the long is only 32bit positive...
					code = (byte)(code - 32);
					//Extract the 4 bits
					a = (int)((( alpha1 >> code) & 0x0f) * 17);
				} else {
					//Extract the 4 bits
					a = (int)((( alpha0 >> code) & 0x0f) * 17);
				}
				//Add the values to the alpha map
				linesColor[yi][x+xi][BANK_ALPHA] = (byte) a;
			}
		}
	}
	
	private void decodeAtiAndDxt5AlphaBlock(byte[] bytes, int offset, int start, int x, int lineOffset, int bank) throws IOException{
		int color0, color1, bits0, bits1, bits2, bits3, bits4, bits5;
		int[] color = new int[8];
		int[] bits = new int[6];

		//Read 8 Bytes
		color0 = bytes[lineOffset+(x/4*offset)+(0+start)] & 0xff;
		color1 = bytes[lineOffset+(x/4*offset)+(1+start)] & 0xff;
		bits0 =  bytes[lineOffset+(x/4*offset)+(2+start)] & 0xff;
		bits1 =  bytes[lineOffset+(x/4*offset)+(3+start)] & 0xff;
		bits2 =  bytes[lineOffset+(x/4*offset)+(4+start)] & 0xff;
		bits3 =  bytes[lineOffset+(x/4*offset)+(5+start)] & 0xff;
		bits4 =  bytes[lineOffset+(x/4*offset)+(6+start)] & 0xff;
		bits5 =  bytes[lineOffset+(x/4*offset)+(7+start)] & 0xff;

		//64bit UNSIGNED LONG - broke up into 6 ints
		bits[0] = bits0 + 256 * (bits1 + 256);
		bits[1] = bits1 + 256 * (bits2 + 256);
		bits[2] = bits2 + 256 * (bits3 + 256);
		bits[3] = bits3 + 256 * (bits4 + 256);
		bits[4] = bits4 + 256 * (bits5);
		bits[5] = bits5;
		
		//FIXME is not calulated... should:  multiplying by 1/255.
		if (color0 > -128){
			color[0] = color0;
		} else {
			color[0] = - 1;
		}
		if (color1 > -128){
			color[1] = color1;
		} else {
			color[1] = - 1;
		}
		//alpha[0] = (int) (alpha0 * (1.0/255.0));
		//alpha[1] = (int) (alpha1 * (1.0/255.0));

		if (color0 > color1){
			color[2] = (6*color[0] + 1*color[1])/7;
            color[3] = (5*color[0] + 2*color[1])/7;
            color[4] = (4*color[0] + 3*color[1])/7;
            color[5] = (3*color[0] + 4*color[1])/7;
            color[6] = (2*color[0] + 5*color[1])/7;
            color[7] = (1*color[0] + 6*color[1])/7;
		} else {
			color[2] = (4*color[0] + 1*color[1])/5;
			color[3] = (3*color[0] + 2*color[1])/5;
			color[4] = (2*color[0] + 3*color[1])/5;
			color[5] = (1*color[0] + 4*color[1])/5;
			color[6] = 0;
			color[7] = 255;
		}
		int i, bit;
		byte code;
		for (int yi = 0; yi < 4 ; yi++){
			for (int xi = 0; xi < 4; xi++){
				//Calculate the bit posistion in the alpha0, alpha1 (long)
				
				i = (3*(4*yi+xi)); //64bit UNSIGNED LONG: bit position
				
				bit = (int) Math.floor(i / 8.0);  //where in the bits array to find the bit position
				i = i - (bit * 8); //offset from the 64bit position to the bits array

				//Extract 2bits, from the bits array
				code = (byte)((bits[bit] >> i) & 7);
				
				//Add the value to the alpha map
				linesColor[yi][x+xi][bank] = (byte) color[code];
			}
		}
	}
	
	private int[] unpackRBG565(int rbg565){
		int r = (rbg565 >> 11) & 0x1f;
		int g = (rbg565 >>  5) & 0x3f;
		int b = (rbg565      ) & 0x1f;

		int[] color = new int[4];
		color[0] = (char)((r << 3) | (r >> 2));
		color[1] = (char)((g << 2) | (g >> 4));
		color[2] = (char)((b << 3) | (b >> 2));
		color[3] = 255;
		
		return color;
	}
}
