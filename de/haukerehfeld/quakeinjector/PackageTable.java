package de.haukerehfeld.quakeinjector;

import javax.swing.JTable;
import javax.swing.table.TableRowSorter;
import java.awt.Dimension;

public class PackageTable extends JTable {

	public PackageTable(PackageList maplist) {
		super(maplist);
		
		final TableRowSorter<PackageList> sorter = new TableRowSorter<PackageList>(maplist);
		setRowSorter(sorter);
		
		setPreferredScrollableViewportSize(new Dimension(500, 500));
		setFillsViewportHeight(true);
		setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	}

	public TableRowSorter<PackageList> getRowSorter() {
		return (TableRowSorter<PackageList>) super.getRowSorter();
	}
}