package heros.debugui;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * This class is a singelton
 * 
 * @author Alexander Jandousek
 * 
 */
@SuppressWarnings("restriction")
public final class OptionTab extends AbstractLaunchConfigurationTab {


	private Button fLocalRadioButton;
	private Button fSharedRadioButton;
	private Text fSharedLocationText;

	private ModifyListener fBasicModifyListener = new ModifyListener() {

		@Override
		public void modifyText(ModifyEvent event) {
			updateLaunchConfigurationDialog();
		}

	};
	

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),IDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_COMMON_TAB);
		comp.setLayout(new GridLayout(2, true));
		comp.setFont(parent.getFont());
		createSharedConfigComponent(comp);
	}
	
	private void createSharedConfigComponent(Composite parent) {
		
		Group group = SWTFactory.createGroup(parent, "Server Settings", 3, 2,GridData.FILL_HORIZONTAL);
		Composite comp = SWTFactory.createComposite(group, parent.getFont(), 3,3, GridData.FILL_BOTH, 0, 0);

		// SetLocal
		fLocalRadioButton = createRadioButton(comp, "Local Server");
		fLocalRadioButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				setSharedEnabled(false);
				fSharedLocationText.setText(Option.LOCAL_ADDRESS);
				Option.getInstance().setAddress(Option.LOCAL_ADDRESS);
			}
		});
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		fLocalRadioButton.setLayoutData(gd);

		// notLocal
		fSharedRadioButton = createRadioButton(comp, "Server Address");
		fSharedRadioButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				setSharedEnabled(true);
				fSharedLocationText.setText(Option.EMPTY_STRING);
				Option.getInstance().setAddress(Option.EMPTY_STRING);
			}
		});

		// IP address
		fSharedLocationText = SWTFactory.createSingleText(comp, 1);
		fSharedLocationText.addModifyListener(fBasicModifyListener);
		fSharedLocationText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				Option.getInstance().setAddress(fSharedLocationText.getText());
			}
		});

		// Set Default Values
		fLocalRadioButton.setSelection(true);
		setSharedEnabled(false);
		fSharedLocationText.setText(Option.LOCAL_ADDRESS);
		Option.getInstance().setAddress(Option.LOCAL_ADDRESS);

	}

	private void setSharedEnabled(boolean enable) {
		fSharedLocationText.setEnabled(enable);
	}

	@Override
	public String getName() {
		return "Options";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy arg0) {
		// TODO Auto-generated method stub

	}


}
