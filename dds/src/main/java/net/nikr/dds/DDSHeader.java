/*
 * DDSHeader.java - This file is part of Java DDS ImageIO Plugin
 *
 * Copyright (C) 2011 Niklas Kyster Rasmussen <niklaskr@gmail.com>
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
 * TODO Write File Description for DDSHeader.java
 */

package net.nikr.dds;


public class DDSHeader {
	public static final int CAPS = 0x1;
	public static final int HEIGHT = 0x2;
	public static final int WIDTH = 0x4;
	public static final int PITCH = 0x8;
	public static final int PIXELFORMAT = 0x1000;
	public static final int MIPMAPCOUNT = 0x20000;
	public static final int LINEARSIZE = 0x80000;
	public static final int DEPTH = 0x800000;
	
	public static final int CAPS_COMPLEX = 0x8;
	public static final int CAPS_MIPMAP = 0x400000;
	public static final int CAPS_TEXTURE = 0x1000;
	
	public static final int CAPS2_CUBEMAP = 0x200;
	public static final int CAPS2_CUBEMAP_POSITIVEX = 0x400;
	public static final int CAPS2_CUBEMAP_NEGATIVEX = 0x800;
	public static final int CAPS2_CUBEMAP_POSITIVEY = 0x1000;
	public static final int CAPS2_CUBEMAP_NEGATIVEY = 0x2000;
	public static final int CAPS2_CUBEMAP_POSITIVEZ = 0x4000;
	public static final int CAPS2_CUBEMAP_NEGATIVEZ = 0x8000;
	public static final int CAPS2_VOLUME = 0x200000;
	
	private long size;
	private long flags;
	private long height;
	private long width;
	private long pitchOrLinearSize;
	private long depth;
	private long mipMapCount;
	private DDSPixelFormat ddsPixelFormat;
	private long caps;
    private long caps2;
    private long caps3;
    private long caps4;
	
	
	public DDSHeader(long size, long flags, long height, long width, long linearSize, long depth, long mipMapCount, DDSPixelFormat ddsPixelFormat, long caps, long caps2, long caps3, long caps4) {
		this.size = size;
		this.flags = flags;
		this.height = height;
		this.width = width;
		this.pitchOrLinearSize = linearSize;
		this.depth = depth;
		this.mipMapCount = mipMapCount;
		this.ddsPixelFormat = ddsPixelFormat;
		this.caps = caps;
		this.caps2 = caps2;
		this.caps3 = caps3;
		this.caps4 = caps4;
	}
	public long getSize() {
		return size;
	}
	public long getFlags() {
		return flags;
	}
	public long getHeight(int mipMap) {
		return Math.max(height >> mipMap, 1);
	}
	public long getWidth(int mipMap) {
		return Math.max(width >> mipMap, 1);
	}
	public long getPitchOrLinearSize() {
		return pitchOrLinearSize;
	}
	public long getDepth() {
		return depth;
	}
	public long getMipMapCount() {
		return mipMapCount;
	}
	public void printValues(){
		System.out.println("DDSHeader:");
		System.out.println("	size: "+size);
		System.out.print("	flags: "+flags);
		if ((flags & CAPS) != 0) System.out.print(" (CAPS)");
		if ((flags & HEIGHT) != 0) System.out.print(" (HEIGHT)");
		if ((flags & WIDTH) != 0) System.out.print(" (WIDTH)");
		if ((flags & PITCH) != 0) System.out.print(" (PITCH)");
		if ((flags & PIXELFORMAT) != 0) System.out.print(" (PIXELFORMAT)");
		if ((flags & MIPMAPCOUNT) != 0) System.out.print(" (MIPMAPCOUNT)");
		if ((flags & LINEARSIZE) != 0) System.out.print(" (LINEARSIZE)");
		if ((flags & DEPTH) != 0) System.out.print(" (DEPTH)");
		System.out.print("\n");
		System.out.println("	height: "+height);
		System.out.println("	width: "+width);
		System.out.println("	linearSize: "+pitchOrLinearSize);
		System.out.println("	depth: "+depth);
		System.out.println("	mipMapCount: "+mipMapCount);
		ddsPixelFormat.printValues(1);
		System.out.print("	caps: "+caps);
		if ((caps & CAPS_COMPLEX) != 0) System.out.print(" (CAPS_COMPLEX)");
		if ((caps & CAPS_MIPMAP) != 0) System.out.print(" (CAPS_MIPMAP)");
		if ((caps & CAPS_TEXTURE) != 0) System.out.print(" (CAPS_TEXTURE)");
		System.out.print("\n");
		System.out.print("	caps2: "+caps2);
		if ((caps & CAPS2_CUBEMAP) != 0) System.out.print(" (CAPS2_CUBEMAP)");
		if ((caps & CAPS2_CUBEMAP_POSITIVEX) != 0) System.out.print(" (CAPS2_CUBEMAP_POSITIVEX)");
		if ((caps & CAPS2_CUBEMAP_NEGATIVEX) != 0) System.out.print(" (CAPS2_CUBEMAP_NEGATIVEX)");
		if ((caps & CAPS2_CUBEMAP_POSITIVEY) != 0) System.out.print(" (CAPS2_CUBEMAP_POSITIVEY)");
		if ((caps & CAPS2_CUBEMAP_NEGATIVEY) != 0) System.out.print(" (CAPS2_CUBEMAP_NEGATIVEY)");
		if ((caps & CAPS2_CUBEMAP_POSITIVEZ) != 0) System.out.print(" (CAPS2_CUBEMAP_POSITIVEZ)");
		if ((caps & CAPS2_CUBEMAP_NEGATIVEZ) != 0) System.out.print(" (CAPS2_CUBEMAP_NEGATIVEZ)");
		if ((caps & CAPS2_VOLUME) != 0) System.out.print(" (CAPS2_VOLUME)");
		System.out.print("\n");
		System.out.println("	caps3: "+caps3);
		System.out.println("	caps4: "+caps4);
	}
	public DDSPixelFormat getPixelFormat() {
		return ddsPixelFormat;
	}
}
