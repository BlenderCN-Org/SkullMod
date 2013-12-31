/*
 * DDSPixelFormat.java - This file is part of Java DDS ImageIO Plugin
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
 * TODO Write File Description for DDSPixelFormat.java
 */

package net.nikr.dds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DDSPixelFormat {
	//Flags
	public static final int ALPHAPIXELS = 0x1; //0x00000001;
	public static final int ALPHA = 0x2; //0x00000001;
	public static final int FOURCC = 0x4;
	public static final int RGB = 0x40;
	public static final int YUV = 0x200;
	public static final int LUMINANCE = 0x20000;
	
	//Format
	public enum Format {
		NOT_DDS ("NOT DDS FORMAT"),
		UNCOMPRESSED("UNCOMPRESSED"),
		DXT1	("DXT1"),
		DXT2	("DXT2"),
		DXT3	("DXT3"),
		DXT4	("DXT4"),
		DXT5	("DXT5"),
		BC4U	("BC4U"),
		BC4S	("BC4S"),
		ATI1	("ATI1"),
		ATI2	("ATI2"),
		BC5S	("BC5S"),
		RGBG	("RGBG"),
		GRGB	("GRGB"),
		UYVY	("UYVY"),
		YUY2	("YUY2"),
		DX10	("DX10"),
		;

		private String name;
		private int fourCC;

		private Format(String name) {
			this.name = name;
			this.fourCC = fourCC(name);
		}

		public int getFourCC() {
			return fourCC;
		}

		public String getName() {
			return name;
		}
		
		private void setName(String name){
			this.name = name;
		}
		
		private int fourCC(String cc){
			int result = 0;
			for ( int i = cc.length()-1; i >= 0; i--) {
				result = ( result << 8 ) + (int) cc.charAt( i );
			}
			return result;
		}
	}
	
	private int size;
    private long flags;
    private long fourCC;
    private long rgbBitCount;
    private long rMask;
	private long gMask;
	private long bMask;
    private long aMask;
    private long rMaskFixed;
	private long gMaskFixed;
	private long bMaskFixed;
    private long aMaskFixed;
    private int rShift;
	private int gShift;
	private int bShift;
    private int aShift;
    private int rBits;
	private int gBits;
	private int bBits;
    private int aBits;
	private Format format;
	/**
	 * Creates a new instance of DDSPixelFormat
	 */
	public DDSPixelFormat(int size, long flags, long fourCC, long rgbBitCount, long rMask, long gMask, long bMask, long aMask) {
		this.size = size;
		this.flags = flags;
		this.fourCC = fourCC;
		this.rgbBitCount = rgbBitCount;
		this.rMask = rMask;
		this.gMask = gMask;
		this.bMask = bMask;
		this.aMask = aMask;
		
		this.rMask = rMask;
		this.gMask = gMask;
		this.bMask = bMask;
		this.aMask = aMask;
		
		this.rShift = shift(rMask);
		this.gShift = shift(gMask);
		this.bShift = shift(bMask);
		this.aShift = shift(aMask);
		
		this.rBits = bits(rMask);
		this.gBits = bits(gMask);
		this.bBits = bits(bMask);
		this.aBits = bits(aMask);
		
		this.rMaskFixed = rMask >> rShift << (8 - rBits);
		this.gMaskFixed = gMask >> gShift << (8 - gBits);
		this.bMaskFixed = bMask >> bShift << (8 - bBits);
		this.aMaskFixed = aMask >> aShift << (8 - aBits);
		
		format = calcFormat();
	}

	public int getSize() {
		return size;
	}
	public long getFlags() {
		return flags;
	}
	public long getFourCC() {
		return fourCC;
	}
	public long getRgbBitCount() {
		return rgbBitCount;
	}
	public long getMaskRed() {
		return rMask;
	}
	public long getMaskGreen() {
		return gMask;
	}
	public long getMaskBlue() {
		return bMask;
	}
	public long getMaskAlpha() {
		return aMask;
	}
	public long getMaskFixedRed() {
		return rMaskFixed;
	}
	public long getMaskFixedGreen() {
		return gMaskFixed;
	}
	public long getMaskFixedBlue() {
		return bMaskFixed;
	}
	public long getMaskFixedAlpha() {
		return aMaskFixed;
	}
	public int getShiftRed() {
		return rShift;
	}
	public int getShiftGreen() {
		return gShift;
	}
	public int getShiftBlue() {
		return bShift;
	}
	public int getShiftAlpha() {
		return aShift;
	}
	public int getBitsRed() {
		return rBits;
	}
	public int getBitsGreen() {
		return gBits;
	}
	public int getBitsBlue() {
		return bBits;
	}
	public int getBitsAlpha() {
		return aBits;
	}
	public Format getFormat(){
		return format;
	}
	public boolean isCompressed(){
		return ((flags & FOURCC) != 0);
	}
	public boolean isDXT1(){
		return (fourCC == Format.DXT1.getFourCC());
	}
	public boolean isDXT2(){
		return (fourCC == Format.DXT2.getFourCC());
	}
	public boolean isDXT3(){
		return (fourCC == Format.DXT3.getFourCC());
	}
	public boolean isDXT4(){
		return (fourCC == Format.DXT4.getFourCC());
	}
	public boolean isDXT5(){
		return (fourCC == Format.DXT5.getFourCC());
	}
	public boolean isATI1(){
		return (fourCC == Format.ATI1.getFourCC());
	}
	public boolean isATI2(){
		return (fourCC == Format.ATI2.getFourCC());
	}
	
	public boolean isAlphaPixels(){
		return ((flags & ALPHAPIXELS) != 0);
	}
	public boolean isAlpha(){
		return ((flags & ALPHA) != 0);
	}
	public boolean isFourCC(){
		return ((flags & FOURCC) != 0);
	}
	public boolean isRGB(){
		return ((flags & RGB) != 0);
	}
	public boolean isYUV(){
		return ((flags & YUV) != 0);
	}
	public boolean isLuminance(){
		return ((flags & LUMINANCE) != 0);
	}	
		
	public void printValues(){
		printValues(0);
	}
	
	public void printValues(int nSpace){
		String sSpace = "";
		for (int i = 0; i < nSpace; i++){
			sSpace = sSpace + "	";
		}
		System.out.println(sSpace+"PixelFormat: ");
		
		System.out.println(sSpace+"	size: "+size);
		System.out.print(sSpace+"	flags: "+flags);
		if ((flags & ALPHAPIXELS) != 0) System.out.print(" (ALPHAPIXELS)");
		if ((flags & ALPHA) != 0) System.out.print(" (ALPHA)");
		if ((flags & FOURCC) != 0) System.out.print(" (FOURCC)");
		if ((flags & RGB) != 0) System.out.print(" (RGB)");
		if ((flags & YUV) != 0) System.out.print(" (YUV)");
		if ((flags & LUMINANCE) != 0) System.out.print(" (LUMINANCE)");
		System.out.print("\n");
		System.out.println(sSpace+"	fourCC: "+fourCC+" ("+getFormat().getName()+")");
		System.out.println(sSpace+"	rgbBitCount: "+rgbBitCount);
		System.out.println(sSpace+"	rMask: "+Long.toHexString(rMask)+" int("+rMask+") fixed("+Long.toHexString(rMaskFixed)+") shift("+rShift+") bits("+rBits+")");
		System.out.println(sSpace+"	gMask: "+Long.toHexString(gMask)+" int("+gMask+") fixed("+Long.toHexString(gMaskFixed)+") shift("+gShift+") bits("+gBits+")");
		System.out.println(sSpace+"	bMask: "+Long.toHexString(bMask)+" int("+bMask+") fixed("+Long.toHexString(bMaskFixed)+") shift("+bShift+") bits("+bBits+")");
		System.out.println(sSpace+"	aMask: "+Long.toHexString(aMask)+" int("+aMask+") fixed("+Long.toHexString(aMaskFixed)+") shift("+aShift+") bits("+aBits+")");
		System.out.println(sSpace+"	Format: "+getFormat().getName());
	}
	
	private char shift(long mask){
		char i = 0;
		if(mask <= 0) return 0;
		while(((mask >> i) & 1) <= 0){
			++i;
		}
		return i;
	}
	
	private char bits(long mask){
		char i = 0;

		while(mask > 0) {
			if((mask & 1) != 0) ++i;
			mask >>= 1;
		}
		return i;
	}
	
	private Format calcFormat(){
		if (!isCompressed()){
			List<FormatItem> list = new ArrayList<FormatItem>();
			if (isLuminance()){
				if (aMask != 0) list.add(new FormatItem("A", aMask, aBits));
				if (rMask != 0) list.add(new FormatItem("L", rMask, rBits));
			} else {
				if (aMask != 0) list.add(new FormatItem("A", aMask, aBits));
				if (rMask != 0) list.add(new FormatItem("R", rMask, rBits));
				if (gMask != 0) list.add(new FormatItem("G", gMask, gBits));
				if (bMask != 0) list.add(new FormatItem("B", bMask, bBits));
			}
			Collections.sort(list);
			String s = "";
			for (FormatItem item : list){
				s = s + item.toString();
			}
			Format.UNCOMPRESSED.setName(rgbBitCount+"bit-"+s);
			return Format.UNCOMPRESSED;
		} else {
			if (isDXT1()) return Format.DXT1;
			if (isDXT2()) return Format.DXT2;
			if (isDXT3()) return Format.DXT3;
			if (isDXT4()) return Format.DXT4;
			if (isDXT5()) return Format.DXT5;
			if (isATI1()) return Format.ATI1;
			if (isATI2()) return Format.ATI2;
			if (fourCC == Format.BC4U.getFourCC()) return Format.BC4U;
			if (fourCC == Format.BC4S.getFourCC()) return Format.BC4S;
			if (fourCC == Format.BC5S.getFourCC()) return Format.BC5S;
			if (fourCC == Format.RGBG.getFourCC()) return Format.RGBG;
			if (fourCC == Format.GRGB.getFourCC()) return Format.GRGB;
			if (fourCC == Format.UYVY.getFourCC()) return Format.UYVY;
			if (fourCC == Format.YUY2.getFourCC()) return Format.YUY2;
			if (fourCC == Format.DX10.getFourCC()) return Format.DX10;
		}
		return Format.NOT_DDS;
	}
	
	private static class FormatItem implements Comparable<FormatItem>{

		private String name;
		private Long mask;
		private long bits;

		
		public FormatItem(String name, long mask, long bits) {
			this.name = name;
			this.mask = mask;
			this.bits = bits;
		}
		
		@Override
		public String toString(){
			return name+bits;
		}
		
		@Override
		public int compareTo(FormatItem o) {
			return o.mask.compareTo(mask);
		}
		
	}
}
