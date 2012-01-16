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
package com.alexalecu.imageCrop.controller;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.alexalecu.imageCrop.ImageCropConfig;
import com.alexalecu.imageCrop.ImageCropController;
import com.alexalecu.imageCrop.ImageCropState;
import com.alexalecu.imageCrop.event.AutoSelectRectangleEvent;
import com.alexalecu.imageCrop.event.EventBus;
import com.alexalecu.imageCrop.exception.InvalidOperationException;
import com.alexalecu.imageCrop.gui.ImageCropGUI;
import com.alexalecu.imageCrop.util.ImageCropUtil;
import com.alexalecu.imageUtil.AutoSelectStatus;
import com.alexalecu.imageUtil.AutoSelectTask;
import com.alexalecu.imageUtil.GeomEdge;
import com.google.common.eventbus.Subscribe;

/**
 * @author Alex Cojocaru
 *
 */
public class AutoSelectionController {
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private ImageCropController controller;
	private ImageCropGUI gui;
	private AutoSelectTask autoSelectTask;
	
	
	public AutoSelectionController(ImageCropController controller, ImageCropGUI gui) {
		this.controller = controller;
		this.gui = gui;
		
		EventBus.register(this);
	}

	/**
	 * auto adjust the selection rectangle to mark the optimum image that can be cropped
	 */
	@Subscribe
	public void autoSelect(AutoSelectRectangleEvent event) {
		ImageCropConfig imageCropConfig = controller.getImageConfig();

		if (!imageCropConfig.isSelection()) {
			gui.showInfoDialog("First draw a selection inside the image !");
			return;
		}
		
		if (imageCropConfig.getState() == ImageCropState.StateAutoSelecting) {
			gui.setAutoSelectStatus(AutoSelectStatus.Cancelled);
			autoSelectTask.cancel(true);
		}
		else {
			autoSelectTask = new AutoSelectTask();
			// connect the task property change events to the current object actions
			autoSelectTask.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					if (arg0.getPropertyName().equals("autoSelectStatus")) {
						gui.setAutoSelectStatus((AutoSelectStatus)arg0.getNewValue());
					}
					else if (arg0.getPropertyName().equals("result")) {
						autoSelectDone( (Object[])arg0.getNewValue() );
					}
				}
			});
			
			try {
				autoSelectTask.setImage(controller.getImage());
				autoSelectTask.setSelectionRect(imageCropConfig.getSelectionRect());
				autoSelectTask.setBgColor(imageCropConfig.getBgColor());
				autoSelectTask.setBgTolerance(imageCropConfig.getBgTolerance());
				autoSelectTask.setSelectMethod(imageCropConfig.getSelectMethod());
			}
			catch (InvalidOperationException e) {
				gui.showErrorDialog("Cannot initialize the auto selecting job!");
				return;
			}
			
			imageCropConfig.setState(ImageCropState.StateAutoSelecting);
			gui.setState(imageCropConfig.getState());

			// and let it roll
			autoSelectTask.execute();
		}
	}
	
	/**
	 * called when the auto selection process is done
	 * @param rectProps a two element array containing the selection rectangle properties; first
	 * element is the rectangle bounding the polygon, the second is the list of polygon edges
	 */
	private void autoSelectDone(Object[] rectProps) {
		boolean isCancelled = autoSelectTask.isCancelled();
		autoSelectTask = null; // rest the auto select task, as the SwingWorker is not re-usable
		
		Rectangle polygonRect = (Rectangle)rectProps[0];
		@SuppressWarnings("unchecked")
		ArrayList<GeomEdge> edgeList = (ArrayList<GeomEdge>)rectProps[1];

		ImageCropConfig imageCropConfig = controller.getImageConfig();
		
		if (isCancelled) { // operation was cancelled, reset state to previous
			imageCropConfig.setState(ImageCropState.StateSelectionAutoSelected);
			gui.setAutoSelectStatus(AutoSelectStatus.Cancelled);
			gui.setState(imageCropConfig.getState());
			return;
		}

		gui.setAutoSelectStatus(AutoSelectStatus.Finished);
		
		logger.debug("Auto select method: " + imageCropConfig.getSelectMethod());
		logger.debug("Auto select result (x, y, w, h): " +
				(polygonRect == null ? "null" : polygonRect.x + ", " + polygonRect.y + ", " +
						polygonRect.width + ", " + polygonRect.height));

		// reject the result if it is not valid
		if (!ImageCropUtil.validateSelectionRectangle(controller.getImage(), polygonRect)) {
			imageCropConfig.setState(ImageCropState.StateSelectionDone);
			gui.setState(imageCropConfig.getState());
			gui.showErrorDialog("An error has occured !\nCheck the selection, background color" +
					" and tolerance and try again.");
			logger.error("An error has occured: invalid selection rectangle");
			return;
		}

		imageCropConfig.setSelectionRect(polygonRect);

		// update the GUI properties
		gui.setSelectionRect(imageCropConfig.getSelectionRect(), false);
		gui.setSelectionEdgeList(edgeList, true);

		// and finally set the state to 'selection'
		if (imageCropConfig.getState() != ImageCropState.StateSelectionAutoSelected) {
			imageCropConfig.setState(ImageCropState.StateSelectionAutoSelected);
			gui.setState(imageCropConfig.getState());
		}
		
		controller.triggerWizard(false); // switch to the next state if the wizard is on
	}

}
