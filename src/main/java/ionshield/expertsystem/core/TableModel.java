package ionshield.expertsystem.core;

import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel {
    boolean[] editableColumns = {};
    public TableModel(Object[] columnNames, int rowCount, boolean[] editableColumns) {
        super(columnNames, rowCount);
        this.editableColumns = editableColumns;
        if (this.editableColumns == null) {
            this.editableColumns = new boolean[]{};
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column < editableColumns.length && editableColumns[column]) {
            return super.isCellEditable(row, column);
        }
        return false;
    }
}
