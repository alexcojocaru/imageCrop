package com.alexalecu.imageUtil;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import com.alexalecu.imageCrop.exception.InvalidOperationException;

public class AutoSelectTask2 extends SwingWorker<Object[], AutoSelectStatus> {

	// disable the disk-based cache to speed up the image processing
	static {
		ImageIO.setUseCache(false);
	}
	
	private AutoSelectStatus autoSelectStatus; // the current task status
	private Object[] result; // the task execution result

	private AutoSelectTaskConfig config;

	/**
	 * @return the current task status
	 */
	public AutoSelectStatus getAutoSelectStatus()
	{
		return autoSelectStatus;
	}

	/**
	 * set the current task status and trigger a property change event
	 * @param autoSelectStatus
	 */
	private void setAutoSelectStatus(AutoSelectStatus autoSelectStatus)
	{
		AutoSelectStatus old = this.autoSelectStatus;
		this.autoSelectStatus = autoSelectStatus;
		getPropertyChangeSupport().firePropertyChange("autoSelectStatus", old, autoSelectStatus);
	}

	/**
	 * @return the task execution result
	 */
	public Object[] getResult()
	{
		return result;
	}

	/**
	 * set the task execution result and trigger a property change event
	 * @param result
	 */
	private void setResult(Object[] result)
	{
		Object[] oldResult = this.result;
		this.result = result;
		getPropertyChangeSupport().firePropertyChange("result", oldResult, result);
	}
	
	/**
	 * Set the task configuration; if the task is already running, the setter throws an exception
	 * @param config
	 * @throws InvalidOperationException
	 */
	public void setConfig(AutoSelectTaskConfig config) throws InvalidOperationException {
		if (getState() != StateValue.PENDING && getState() != StateValue.DONE)
			throw new InvalidOperationException("Cannot change the configuration" +
					" while the auto selecting task is in progress");
		
		this.config = config;
	}


	/**
	 * compute the rectangle which is the optimized solution for cropping the source BufferedImage;
	 * make sure you set the execution parameters before executing the task
	 * @return an array containing two Objects; the first one is the resulting Rectangle,
	 * while the 2nd object is an ArrayList containing the polygon edges
	 */
	@Override
	protected Object[] doInBackground() {
		return null;
	}

	@Override
	protected void process(List<AutoSelectStatus> statusList) {
		setAutoSelectStatus(statusList.get(statusList.size() - 1));
	}

	@Override
	public void done() {
		try {
			setResult(get());
		}
		catch (InterruptedException e) {
			setResult(new Object[] {null, null});
		}
		catch (ExecutionException e) {
			setResult(new Object[] {null, null});
		}
		catch (CancellationException e) {
			setResult(new Object[] {null, null});
		}
	}
}
