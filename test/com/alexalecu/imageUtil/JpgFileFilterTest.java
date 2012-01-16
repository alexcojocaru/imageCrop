/*
 * Copyright (C) 2012 Alex Cojocaru
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alexalecu.imageUtil;

/**
 * @author Alex Cojocaru
 *
 */
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.alexalecu.util.FileUtil;
import com.alexalecu.util.FileUtilTest;

public class JpgFileFilterTest {

	@Test
	public void testAcceptDirectory() throws AssertionError, IOException {
		JpgFileFilter filter = new JpgFileFilter();
		
		File dir = FileUtilTest.createUniqueDirectory();
		try {
			Assert.assertTrue("The JPG file filter doesn't accept a directory", filter.accept(dir));
		}
		catch (AssertionError ex) {
			throw ex;
		}
		finally {
			FileUtil.deleteFileOrDirectory(dir);
		}
	}

	@Test
	public void testAcceptFile() {
		JpgFileFilter jpgFileFilter = new JpgFileFilter();
		
		File f = new File("test.jpg");
		Assert.assertTrue("The JPG file filter doesn't accept a jpg file", jpgFileFilter.accept(f));

		f = new File("test.jPG");
		Assert.assertTrue("The JPG file filter doesn't accept a jpg file", jpgFileFilter.accept(f));
		
		f = new File("test.jpeg");
		Assert.assertTrue("The JPG file filter doesn't accept a jpeg file", jpgFileFilter.accept(f));

		f = new File("test.jPeG");
		Assert.assertTrue("The JPG file filter doesn't accept a jpeg file", jpgFileFilter.accept(f));
	}

	@Test
	public void testReject() {
		JpgFileFilter jpgFileFilter = new JpgFileFilter();
		
		File f = new File("test.ext");
		Assert.assertFalse("The JPG file filter accepts a non-jpg file", jpgFileFilter.accept(f));
	}
}
