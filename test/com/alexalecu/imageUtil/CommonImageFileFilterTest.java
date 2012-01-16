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

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.alexalecu.util.FileUtil;
import com.alexalecu.util.FileUtilTest;

/**
 * @author Alex Cojocaru
 *
 */
public class CommonImageFileFilterTest {
	
	@Test
	public void testAcceptDirectory() throws AssertionError, IOException {
		CommonImageFileFilter filter = new CommonImageFileFilter();
		
		File dir = FileUtilTest.createUniqueDirectory();
		try {
			Assert.assertTrue(
					"The common image file filter doesn't accept a directory", filter.accept(dir));
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
		CommonImageFileFilter filter = new CommonImageFileFilter();
		
		File f = new File("test.jpg");
		Assert.assertTrue("The image file filter doesn't accept a jpg file", filter.accept(f));

		f = new File("test.jPG");
		Assert.assertTrue("The image file filter doesn't accept a jpg file", filter.accept(f));
		
		f = new File("test.bmp");
		Assert.assertTrue("The image file filter doesn't accept a jpeg file", filter.accept(f));

		f = new File("test.PNG");
		Assert.assertTrue("The image file filter doesn't accept a jpeg file", filter.accept(f));
	}

	@Test
	public void testReject() {
		CommonImageFileFilter filter = new CommonImageFileFilter();
		
		File f = new File("test.ext");
		Assert.assertFalse(
				"The common image file filter accepts a non-image file", filter.accept(f));
	}

}
