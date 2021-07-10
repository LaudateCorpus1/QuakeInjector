/*
Copyright 2009 Hauke Rehfeld


This file is part of QuakeInjector.

QuakeInjector is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

QuakeInjector is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with QuakeInjector.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.haukerehfeld.quakeinjector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import de.haukerehfeld.quakeinjector.gui.ErrorEvent;
import de.haukerehfeld.quakeinjector.gui.ErrorListener;
import de.haukerehfeld.quakeinjector.gui.JPathPanel;
import de.haukerehfeld.quakeinjector.gui.LookAndFeelDefaults;
import de.haukerehfeld.quakeinjector.gui.OkayCancelApplyPanel;

public class EngineConfigDialog extends JDialog {
	private final static String windowTitle = "Engine Configuration";

	private final ChangeListenerList listeners = new ChangeListenerList();

	private final JPanel configPanel;

	private final JPathPanel enginePath;
	private final JPathPanel engineExecutable;
	private final JTextField engineCommandline;

	private final JPathPanel downloadPath;


	private final JCheckBox rogue;
	private final JCheckBox hipnotic;

	private final WorkingDirOpts workingDirOpts;
	
	
	public EngineConfigDialog(final JFrame frame,
							  Configuration.EnginePath enginePathDefault,
							  Configuration.EngineExecutable engineExeDefault,
							  Configuration.WorkingDirAtExecutable workingDirAtExecutable,
	                          Configuration.DownloadPath downloadPathDefault,
	                          Configuration.EngineCommandLine cmdlineDefault,
	                          Configuration.RogueInstalled rogueInstalled,
	                          Configuration.HipnoticInstalled hipnoticInstalled) {
		super(frame, windowTitle, true);

		configPanel = new JPanel();
		configPanel.setBorder(LookAndFeelDefaults.PADDINGBORDER);
		configPanel.setLayout(new GridBagLayout());

		JLabel description = new JLabel("Configure engine specific settings");
		description.setLabelFor(this);
		description.setBorder(LookAndFeelDefaults.DIALOGDESCRIPTIONBORDER);
		configPanel.add(description, new GridBagConstraints());

		final JButton okay = new JButton("Okay");
		final JButton cancel = new JButton("Cancel");
		final JButton apply = new JButton("Apply");


		class LabelConstraints extends GridBagConstraints {{
				anchor = LINE_START;
				fill = NONE;
				gridx = 0;
				gridwidth = 1;
				gridheight = 1;
				weightx = 0;
				weighty = 0;
				
		}};
		class InputConstraints extends GridBagConstraints {{
				anchor = LINE_END;
				fill = HORIZONTAL;
				gridx = 1;
				gridwidth = 2;
				gridheight = 1;
				weightx = 1;
				weighty = 0;
		}};

		int row = 1;

		Border leftBorder = BorderFactory
		    .createEmptyBorder(0, 0, 0, LookAndFeelDefaults.FRAMEPADDING);

		{
			
			JLabel cmdlineLabel = new JLabel("Quake commandline options");
			cmdlineLabel.setBorder(leftBorder);

			this.engineCommandline = new JTextField(cmdlineDefault.get(), 40);

			final int row_ = row;
			configPanel.add(cmdlineLabel, new LabelConstraints() {{ gridy = row_; }});
			configPanel.add(engineCommandline, new InputConstraints() {{ gridy = row_; }});
		}

		++row;

		{
			//"Path to quake directory",
			JLabel enginePathLabel = new JLabel("Quake Directory");
			enginePathLabel.setBorder(leftBorder);
			
			enginePath = new JPathPanel(new JPathPanel.WritableDirectoryVerifier(),
			                            enginePathDefault.get(),
			                            javax.swing.JFileChooser.DIRECTORIES_ONLY);
			final int row_ = row;
			configPanel.add(enginePathLabel, new LabelConstraints() {{ gridy = row_; }});
			configPanel.add(enginePath, new InputConstraints() {{ gridy = row_; }});
		}
		++row;
		
		JLabel engineExeLabel = new JLabel("Quake Executable");
		engineExeLabel.setBorder(leftBorder);
		engineExecutable = new JPathPanel(
			new JPathPanel.Verifier() {
				public boolean verify(File exe) {
					return EngineStarter.isValidApplication(exe);
				}
				public String errorMessage(File f) {
					return EngineStarter.errorMessageForApplication(f);
				}
			},
			engineExeDefault.get(),
			enginePathDefault.get(),
			javax.swing.JFileChooser.FILES_ONLY);

		{
			final int row_ = row;
			configPanel.add(engineExeLabel, new LabelConstraints() {{ gridy = row_; }});
			configPanel.add(engineExecutable, new InputConstraints() {{ gridy = row_; }});
		}


		{
		}
		{
		}

		enginePath.verify();
		engineExecutable.verify();

		++row;

		{
			//"Path to quake directory",
			JLabel downloadLabel = new JLabel("Download Directory");
			downloadLabel.setBorder(leftBorder);
			downloadPath = new JPathPanel(new JPathPanel.WritableDirectoryVerifier(),
			                              downloadPathDefault.get(),
			                              javax.swing.JFileChooser.DIRECTORIES_ONLY);
			downloadPath.verify();

			final int row_ = row;
			configPanel.add(downloadLabel, new LabelConstraints() {{ gridy = row_; }});
			configPanel.add(downloadPath, new InputConstraints() {{ gridy = row_; }});
			
		}
		++row;


		{
			JLabel expansionsInstalled = new JLabel("Expansion packs installed");
			expansionsInstalled.setBorder(leftBorder);

			rogue = new JCheckBox("rogue");
			rogue.setMnemonic(KeyEvent.VK_R);
			rogue.setSelected(rogueInstalled.get());

			hipnotic = new JCheckBox("hipnotic");
			hipnotic.setMnemonic(KeyEvent.VK_H);
			hipnotic.setSelected(hipnoticInstalled.get());

			final int row_ = row;
			configPanel.add(expansionsInstalled, new LabelConstraints() {{ gridy = row_; }});
			configPanel.add(rogue, new InputConstraints() {{ gridy = row_; gridwidth = 1; }});
			configPanel.add(hipnotic, new InputConstraints() {{
				gridy = row_;
				gridx = 2;
				gridwidth = 1;
			}});
		}
		++row;

		workingDirOpts = new WorkingDirOpts(row, workingDirAtExecutable.get());

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(LookAndFeelDefaults.PADDINGBORDER);
		tabbedPane.addTab("Engine Specifics", null, configPanel, "Configure Engine Specifics");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		add(tabbedPane, BorderLayout.CENTER);
		

		class EnableOkay implements ChangeListener, DocumentListener {
			@Override
			public void changedUpdate(DocumentEvent e) {
				check();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				check();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				check();
			}

			@Override
			public void stateChanged(ChangeEvent e) {
				check();
			}
			
			private void check() {
				if (enginePath.verifies() && engineExecutable.verifies()) {
					okay.setEnabled(true);
					apply.setEnabled(true);
				}
			}
		};
		
		final EnableOkay enableOkay = new EnableOkay() ;
		

		engineCommandline.getDocument().addDocumentListener(enableOkay);
		
		enginePath.addErrorListener(new ErrorListener() {
				public void errorOccured(ErrorEvent e) {
					okay.setEnabled(false);
					apply.setEnabled(false);
				}
			});
		//change basepath of the exe when quakedir changes
		enginePath.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					engineExecutable.setBasePath(enginePath.getPath());
				}
			});
		//(un)set working dir opt visibility as appropriate
		enginePath.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					workingDirOpts.checkDisplay();
				}
			});
		enginePath.addChangeListener(enableOkay);

		engineExecutable.addErrorListener(new ErrorListener() {
				public void errorOccured(ErrorEvent e) {
					okay.setEnabled(false);
					apply.setEnabled(false);
				}
			});
		//(un)set working dir opt visibility as appropriate
		engineExecutable.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					workingDirOpts.checkDisplay();
				}
			});
		engineExecutable.addChangeListener(enableOkay);

		downloadPath.addChangeListener(enableOkay);

		rogue.addChangeListener(enableOkay);
		hipnotic.addChangeListener(enableOkay);

		
		

		ActionListener save = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					listeners.notifyChangeListeners(this);
					apply.setEnabled(false);
				}
			};
		

		okay.addActionListener(save);
		apply.addActionListener(save);

		ActionListener close = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();
				}
			};

		okay.addActionListener(close);
		cancel.addActionListener(close);

		{
			JPanel okayCancelPanel = new OkayCancelApplyPanel(okay, cancel, apply, true);
			add(okayCancelPanel, BorderLayout.PAGE_END);
		}
		
	}

	class WorkingDirOpts {
		private final JSeparator workingDirBoxSep;
		private final GridBagConstraints sepConstraints;
		private final JLabel workingDirTitle;
		private final GridBagConstraints titleConstraints;
		private final Box workingDirChoices;
		private final GridBagConstraints choicesConstraints;
		private final JRadioButton workAtExe;
		private boolean visible;
		public WorkingDirOpts(int configPanelRow, boolean workAtExeDefault) {
			// Separator
			workingDirBoxSep = new JSeparator(JSeparator.HORIZONTAL);
			sepConstraints = new GridBagConstraints();
			sepConstraints.gridx = 0;
			sepConstraints.gridy = configPanelRow;
			sepConstraints.gridwidth = GridBagConstraints.REMAINDER;
			sepConstraints.fill = GridBagConstraints.HORIZONTAL;
			sepConstraints.insets = new Insets(10, 0, 10, 0);
			sepConstraints.weightx = 1;
			// Explanatory text
			String workingDirBlurb =
				"<html><body><nobr>" +
				"<b>Notice:</b> your Quake Executable is not located in " +
				"your Quake Directory.<br/>Choose where the runtime " +
				"\"working directory\" for Quake should be located:" +
				"</nobr></body></html>";
			workingDirTitle = new JLabel(workingDirBlurb);
			titleConstraints = new GridBagConstraints();
			titleConstraints.gridx = 0;
			titleConstraints.gridy = configPanelRow + 1;
			titleConstraints.gridwidth = GridBagConstraints.REMAINDER;
			// Container for the choices
			workingDirChoices = new Box(BoxLayout.Y_AXIS);
			// Radio buttons for the choices
			JRadioButton workInBase = new JRadioButton("in Quake Directory");
			String baseBlurb =
				"Using the Quake Directory as the working directory may " +
				"fail if necessary libraries or other resources are " +
				"located with the engine.";
			workInBase.setToolTipText(baseBlurb);
			workInBase.setSelected(!workAtExeDefault);
			workAtExe = new JRadioButton("at Quake Executable");
			String exeBlurb =
				"Using the Quake Engine's location as the working directory " +
				"may require adding a -basedir argument to the command line.";
			workAtExe.setToolTipText(exeBlurb);
			workAtExe.setSelected(workAtExeDefault);
			ButtonGroup workingDirGroup = new ButtonGroup();
			workingDirGroup.add(workInBase);
			workingDirGroup.add(workAtExe);
			workingDirChoices.add(workInBase);
			workingDirChoices.add(workAtExe);
			choicesConstraints = new GridBagConstraints();
			choicesConstraints.gridx = 0;
			choicesConstraints.gridy = configPanelRow + 2;
			choicesConstraints.gridwidth = GridBagConstraints.REMAINDER;
			choicesConstraints.insets = new Insets(5, 0, 5, 0);
			// Start visible if appropriate
			visible = false;
			checkDisplay();
		}
		public void checkDisplay() {
			File exeDir = null;
			if (null != engineExecutable) {
				exeDir = engineExecutable.getPath().getParentFile();
			}
			boolean validPaths =
				null != exeDir &&
				null != enginePath &&
				!(exeDir.getPath().equals("")) &&
				!(enginePath.getPath().getPath().equals("")) &&
				!(engineExecutable.getPath().equals(enginePath.getPath()));
			boolean showOpt =
				validPaths && !(enginePath.getPath().equals(exeDir));
			if (showOpt) {
				if (!visible) {
					configPanel.add(workingDirBoxSep, sepConstraints);
					configPanel.add(workingDirTitle, titleConstraints);
					configPanel.add(workingDirChoices, choicesConstraints);
					verticalRepack();
					visible = true;
				}
			}
			else {
				if (visible) {
					configPanel.remove(workingDirBoxSep);
					configPanel.remove(workingDirTitle);
					configPanel.remove(workingDirChoices);
					verticalRepack();
					visible = false;
				}
			}
		}
		private void verticalRepack() {
			EngineConfigDialog dialog = EngineConfigDialog.this;
			Rectangle bounds = dialog.getBounds();
			dialog.setMinimumSize(new Dimension(bounds.width, 0));
			dialog.pack();
			dialog.setMinimumSize(null);
		}
		public boolean getWorkingDirAtExecutable() {
			return workAtExe.isSelected();
		}
	}

	public File getEnginePath() {
		return enginePath.getPath();
	}
	public File getEngineExecutable() {
		return engineExecutable.getPath();
	}
	public boolean getWorkingDirAtExecutable() {
		return workingDirOpts.getWorkingDirAtExecutable();
	}
	public String getCommandline() {
		return engineCommandline.getText();
	}

	/**
	 * get hipnoticInstalled
	 */
	public boolean getHipnoticInstalled() { return hipnotic.isSelected(); }

	/**
	 * get rogueInstalled
	 */
	public boolean getRogueInstalled() { return rogue.isSelected(); }

	public File getDownloadPath() {
		return downloadPath.getPath();
	}
	
	public void addChangeListener(ChangeListener l) {
		listeners.addChangeListener(l);
	}
}
