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
package com.alexalecu.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.alexalecu.event.EventBus;

/**
 * @author Alex Cojocaru
 *
 */
public class NotificationButton extends JButton {
	private static final long serialVersionUID = 1L;

	private NotificationButton() {
	}
	
	public static class Builder {
		private String text;
		private String tooltip;
		private Object event;
		
		public NotificationButton build() {
			NotificationButton button = new NotificationButton();
			
			button.setText(text);
			if (tooltip != null)
				button.setToolTipText(tooltip);

			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EventBus.post(event);
				}
			});
			
			return button;
		}
		
		public Builder text(String text) {
			this.text = text;
			return this;
		}

		public Builder tooltip(String tooltip) {
			this.tooltip = tooltip;
			return this;
		}
		
		public Builder event(Object event) {
			this.event = event;
			return this;
		}
	}
}
