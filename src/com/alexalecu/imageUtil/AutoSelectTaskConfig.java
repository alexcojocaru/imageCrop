package com.alexalecu.imageUtil;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class AutoSelectTaskConfig {
	private BufferedImage image;
	private Rectangle selectionRect;
	private Color bgColor;
	private int bgTolerance;
	private ImageSelectMethod selectMethod;
	
	private AutoSelectTaskConfig() {
	}
	
	public BufferedImage getImage() {
		return image;
	}
	public Rectangle getSelectionRect() {
		return selectionRect;
	}
	public Color getBgColor() {
		return bgColor;
	}
	public int getBgTolerance() {
		return bgTolerance;
	}
	public ImageSelectMethod getSelectMethod() {
		return selectMethod;
	}

	public static class AutoSelectTaskConfigBuilder {
		private BufferedImage image;
		private Rectangle selectionRect;
		private Color bgColor;
		private int bgTolerance;
		private ImageSelectMethod selectMethod;
		
		public AutoSelectTaskConfig build() {
			AutoSelectTaskConfig taskConfig = new AutoSelectTaskConfig();
			
			taskConfig.image = image;
			taskConfig.selectionRect = selectionRect;
			taskConfig.bgColor = bgColor;
			taskConfig.bgTolerance = bgTolerance;
			taskConfig.selectMethod = selectMethod;
			
			return taskConfig;
		}
		
		public AutoSelectTaskConfigBuilder image(BufferedImage image) {
			this.image = image;
			return this;
		}
		public AutoSelectTaskConfigBuilder selectionRect(Rectangle selectionRect) {
			this.selectionRect = selectionRect;
			return this;
		}
		public AutoSelectTaskConfigBuilder bgColor(Color bgColor) {
			this.bgColor = bgColor;
			return this;
		}
		public AutoSelectTaskConfigBuilder bgTolerance(int bgTolerance) {
			this.bgTolerance = bgTolerance;
			return this;
		}
		public AutoSelectTaskConfigBuilder selectMethod(ImageSelectMethod selectMethod) {
			this.selectMethod = selectMethod;
			return this;
		}
	}

}
