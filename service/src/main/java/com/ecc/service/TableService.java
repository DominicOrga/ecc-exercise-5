package com.ecc.service;

import com.ecc.model.TableCell;
import com.ecc.model.CellCoordinate;

import java.util.Optional;

public interface TableService {
	char OUTER_CELL_DELIMITER = '/';
	char INNER_CELL_DELIMITER = ',';

	boolean isCellNull(int row, int col);
	boolean isCellNull(CellCoordinate cellCoordinate);
	boolean isCellCoordinateOutOfBounds(int row, int col);
	boolean isCellCoordinateOutOfBounds(CellCoordinate cellCoordinate);

	int getRowCount();
	int getColumnCount();

	void addRow();
	void sortRow(int row, boolean isAscending);
	void editCell(int row, int col, boolean isRightPart, String newStr);
	void editCell(CellCoordinate cellCoordinate, boolean isRightPart, String newStr);
	void addCell(int row, int col, String leftStr, String rightStr);
	void addCell(CellCoordinate cellCoordinate, String leftStr, String rightStr);

	void searchTable(String searchString);
	void resetTable(int rowCount, int colCount);
	void displayTable();
}