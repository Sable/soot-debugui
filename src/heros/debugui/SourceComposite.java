package heros.debugui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SourceComposite extends Composite {
	private List activeSources;
	private List inactiveSources;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SourceComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		setLayoutData(gd);
		
		Button btnAdd = new Button(this, SWT.NONE);
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				
				
				String[] selections = inactiveSources.getSelection();
				
				for(String selection : selections){
					
					
					if(activeSources.indexOf(selection) == -1){
						activeSources.add(selection);
					}
				}
				
				Option.getInstance().setSources(activeSources.getItems());
				
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		btnAdd.setText("Add");
		
		Button btnRemove = new Button(this, SWT.NONE);
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				int[] selections = activeSources.getSelectionIndices();
				activeSources.remove(selections);
				
			}
		});
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		btnRemove.setText("Remove");
		
		
		
		inactiveSources = new List(this, SWT.BORDER | SWT.H_SCROLL | SWT.MULTI);
		inactiveSources.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		activeSources = new List(this, SWT.BORDER | SWT.H_SCROLL | SWT.MULTI);
		activeSources.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		

		fillInactiveList();

	}

	private void fillInactiveList() {
		//TODO implement
		inactiveSources.add("Hello");
		inactiveSources.add("blu");
		inactiveSources.add("Helsdado");
		inactiveSources.add("Hellosadasdas");
		inactiveSources.add("Hellqqqo");
		inactiveSources.add("Hello");
		inactiveSources.add("blu");
		inactiveSources.add("Helsdado");
		inactiveSources.add("Hellosadasdas");
		inactiveSources.add("Hellqqqo");
		inactiveSources.add("Hello");
		inactiveSources.add("blu");
		inactiveSources.add("Helsdado");
		inactiveSources.add("Hellosadasdas");
		inactiveSources.add("Hellqqqo");
		inactiveSources.add("Hello");
		inactiveSources.add("blu");
		inactiveSources.add("Helsdado");
		inactiveSources.add("Hellosadasdas");
		inactiveSources.add("Hellqqqo");
		inactiveSources.add("Hello");
		inactiveSources.add("blu");
		inactiveSources.add("Helsdado");
		inactiveSources.add("Hellosadasdas");
		inactiveSources.add("Hellqqqo");
		inactiveSources.add("Hello");
		inactiveSources.add("blu");
		inactiveSources.add("Helsdado");
		inactiveSources.add("Hellosadasdas");
		inactiveSources.add("Hellqqqo");
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	protected List getAktiveSources() {
		return activeSources;
	}
	protected List getInaktiveSources() {
		return inactiveSources;
	}
}
