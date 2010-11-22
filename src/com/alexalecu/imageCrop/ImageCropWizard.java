/*
 * Copyright (C) 2010 Alex Cojocaru
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

package com.alexalecu.imageCrop;

public class ImageCropWizard {

	private boolean wizardMode;
	private ImageCropGUI gui;
	
	/**
	 * @param gui the app GUI
	 */
	public ImageCropWizard(ImageCropGUI gui) {
		this.gui = gui;
	}
	
	/**
	 * @return true if we're in wizard mode
	 */
	public boolean isWizardMode() {
		return wizardMode;
	}

	/**
	 * set the wizard mode
	 * @param wizardMode
	 */
	public void setWizardMode(boolean wizardMode) {
		this.wizardMode = wizardMode;
	}

	/**
	 * trigger the wizard for the corresponding image state passed as parameter, only if the wizard
	 * mode is true
	 * @param state the current image state
	 * @return true if the next step has been triggered, false if not
	 */
	public boolean triggerWizard(ImageParams.ImageState state) {
		if (!wizardMode) // do nothing if we're not in wizard mode
			return false;

		switch (state) {
			case StateInit:
				gui.loadImage();
				break;
			case StateBackgroundColor:
				gui.showInfoDialog(
						"<html>Select the background color and tolerance.<br>" +
						"Use the background red, green and blue controls on the right<br>" +
						"or click the <b>Select background color</b> button " +
						"and pick the background color from the image.<br><br>" +
						"When done, click the <b>Resume wizard</b> button on the right.</html>");
				break;
			case StateSelection:
				gui.showInfoDialog(
						"<html>Mark the image you want to select by drawing a " +
						"selection rectangle inside it.<br>" +
						"Choose the <b>Select method</b> using the control on the right<br>" +
						"and click the <b>Auto select picture</b> button.</html>");
				break;
			case StateSelectionAutoSelected:
				gui.showInfoDialog(
						"<html>Fine tune the selection if needed.<br>" +
						"Use the controls on the right to change its position and size or<br>" +
						"drag / resize the selection using the mouse in the image panel." +
						"When done, click the <b>Resume wizard</b> button on the right.</html>");
				break;
			case StateSelectionDone:
				gui.showInfoDialog(
						"<html>Crop the selection and start working with it.<br>" +
						"You can save it later.</html>");
				break;
		}
		return true;
	}

}
