package com.ecc.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Comparator;
import java.util.Collections;
import java.util.Optional;
import java.util.Map;

import java.util.function.Supplier;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.TextStringBuilder;

import com.ecc.model.TableCell;
import com.ecc.model.CellCoordinate;
import com.ecc.util.Utility;

public class TableServiceImpl implements TableService {

	private String tableFilePath;
	private List<List<Optional<TableCell>>> rowCells;

	public TableServiceImpl(String tableFilePath) throws FileNotFoundException, IOException {
		this(new File(tableFilePath));
	}

	public TableServiceImpl(File tableFile) throws FileNotFoundException, IOException {
		this.tableFilePath = tableFile.getAbsolutePath();
		this.rowCells = parseTable(tableFile);
	}

	public boolean isCellNull(int row, int col) {
		if (isCellCoordinateOutOfBounds(row, col)) {
			return false;
		}

		return !this.rowCells.get(row).get(col).isPresent();
	}

	public boolean isCellNull(CellCoordinate cellCoordinate) {
		return isCellNull(cellCoordinate.getX(), cellCoordinate.getY());
	}

	
	public boolean isCellCoordinateOutOfBounds(int row, int col) {
		return row >= getRowCount() || col >= getColumnCount() || row < 0 || col < 0;
	}

	public boolean isCellCoordinateOutOfBounds(CellCoordinate cellCoordinate) {
		return isCellCoordinateOutOfBounds(cellCoordinate.getX(), cellCoordinate.getY());
	}

	public int getRowCount() {
		return this.rowCells.size();
	}

	public int getColumnCount() {

		if (getRowCount() == 0) {
			return 0;
		}

		return this.rowCells.get(0).size();
	}

	public void addRow() {
		List<Optional<TableCell>> columnCells = new ArrayList<>();

		for (int j = 0, s = getColumnCount(); j < s; j++) {
			columnCells.add((j == 0) ? 
				Optional.of(new TableCell(generateRandomString(), generateRandomString())) : 
				Optional.empty());
		}

		this.rowCells.add(columnCells);

		persistTable();
	}

	public void sortRow(int row, boolean isAscending) {
		if (row >= getRowCount() || row < 0) {
			return;
		}

		Map<Boolean, List<Optional<TableCell>>> presentCells = 
			this.rowCells.get(row).stream().collect(Collectors.partitioningBy(Optional::isPresent));

		List<Optional<TableCell>> sortedPresentCells = 
			presentCells.get(true)
			            .stream()
			            .map(Optional::get)
			            .sorted(isAscending ? Comparator.naturalOrder() : Comparator.reverseOrder())
	                    .map(innerCell -> Optional.ofNullable(innerCell))
	                    .collect(Collectors.toList());
   
       	this.rowCells.set(
       		row, 
       		Stream.concat(sortedPresentCells.stream(), presentCells.get(false).stream())
       		      .collect(Collectors.toList())
   		);

       	persistTable();
	}

	public void editCell(int row, int col, boolean isRightPart, String newStr) {
		if (isCellCoordinateOutOfBounds(row, col) || isCellNull(row, col)) {
			return;
		}

		Optional<TableCell> cell = this.rowCells.get(row).get(col);

		if (cell.isPresent()) {
			if (isRightPart) {
				cell.get().setRightCell(newStr);
			}
			else {
				cell.get().setLeftCell(newStr);
			}
		}

		persistTable();
	}

	public void editCell(CellCoordinate cellCoordinate, boolean isRightPart, String newStr) {
		editCell(cellCoordinate.getX(), cellCoordinate.getY(), isRightPart, newStr);
	}

	public void addCell(int row, int col, String leftStr, String rightStr) {
		if (!isCellNull(row, col)) {
			return;
		}

		this.rowCells.get(row).set(col, Optional.of(new TableCell(leftStr, rightStr)));

		persistTable();
	}

	public void addCell(CellCoordinate cellCoordinate, String leftStr, String rightStr) {
		addCell(cellCoordinate.getX(), cellCoordinate.getY(), leftStr, rightStr);
	}

	public void searchTable(String searchString) {
		for (int i = 0, s = this.rowCells.size(); i < s; i++) {
			List<Optional<TableCell>> columnCells = this.rowCells.get(i);

			for (int j = 0, t = columnCells.size(); j < t; j++) {

				Optional<TableCell> cell = columnCells.get(j);

				if (!cell.isPresent()) {
					continue;
				}

				int leftCellCount = 
					StringUtils.countMatches(cell.get().getLeftCell(), searchString);

				if (leftCellCount > 0) {
					System.out.printf(
							"@(%d,%d) Left Inner Cell, Found %d occurrences.\n", 
							i, j, leftCellCount);
				}

				int rightCellCount = 
					StringUtils.countMatches(cell.get().getRightCell(), searchString);

				if (rightCellCount > 0) {
					System.out.printf(
							"@(%d,%d) Right Inner Cell, Found %d occurrences.\n", 
							i, j, rightCellCount);
				}
			}
		}

		System.out.println();
	}

	public void resetTable(int rowCount, int colCount) {
		Supplier<List<Optional<TableCell>>> rowCellSupplier = 
		() -> {
			return Stream.generate(() -> 
						     Optional.of(new TableCell(generateRandomString(), generateRandomString())))
						 .limit(colCount)
						 .collect(Collectors.toList());
		};

		this.rowCells = Stream.generate(rowCellSupplier)
							  .limit(rowCount)
							  .collect(Collectors.toList());

		persistTable();
	}

	public void displayTable() {
		for (int i = 0, s = this.rowCells.size(); i < s; i++) {
			List<Optional<TableCell>> columnCells = this.rowCells.get(i);

			for (int j = 0, t = columnCells.size(); j < t; j++) {
				Optional<TableCell> cell = columnCells.get(j);

				if (cell.isPresent()) {
					System.out.printf("%s,%s", cell.get().getLeftCell(), cell.get().getRightCell());
				}
				else {
					System.out.print("NULL");
				}
				
				if (j < t - 1) {
					System.out.print(TableService.OUTER_CELL_DELIMITER);
				}
			}
			System.out.println();
		}

		System.out.println();
	}

	private void persistTable() {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.tableFilePath))) {
			TextStringBuilder textStringBuilder = new TextStringBuilder();

			this.rowCells.stream().forEach(
				(columnCells) -> {
					String columnCellsStr = 
						columnCells.stream()
						           .map(cell -> cell.isPresent() ? 
											cell.get().toString() : Utility.EMPTY_STRING)
							 	   .collect(
							 	   		Collectors.joining(
							 	   			TableService.OUTER_CELL_DELIMITER + Utility.EMPTY_STRING));

		 			textStringBuilder.append(columnCellsStr);
		 			textStringBuilder.append("\n");
				});

			bufferedWriter.write(textStringBuilder.toString());
			bufferedWriter.flush();

		} catch (IOException e) {
			System.out.println("Data persistence failed");
		}
	}

	private List<List<Optional<TableCell>>> parseTable(File tableFile) 
		throws FileNotFoundException, IOException {

		BufferedReader bufferedReader = new BufferedReader(new FileReader(tableFile));

		List<List<Optional<TableCell>>> rowCells = new ArrayList<>();

		String columnCellsStr;
		int columnSize = -1;

		while ((columnCellsStr = bufferedReader.readLine()) != null && 
			    !columnCellsStr.trim().isEmpty()) {

			List<Optional<TableCell>> columnCells = new ArrayList<>();

			String[] unparsedColumnCells = extractUnparsedColumnCells(columnCellsStr);
			
			if (columnSize == -1) {
				columnSize = unparsedColumnCells.length;
			}
			else if (columnSize != unparsedColumnCells.length) {
				System.out.println("Please ensure that all rows have the same number of columns");
				throw new IOException();
			}

			for (int i = 0, s = unparsedColumnCells.length; i < s; i++) {
	        	columnCells.add(parseCell(unparsedColumnCells[i]));
            }

            rowCells.add(columnCells);
		}

		if (rowCells.size() == 0) {
			rowCells.add(Arrays.asList(Optional.empty()));
		}

		return rowCells;
	}

	private String[] extractUnparsedColumnCells(String columnCellsStr) {
		String[] unparsedCells;

		if (columnCellsStr.charAt(columnCellsStr.length() - 1) == TableService.OUTER_CELL_DELIMITER) {
			columnCellsStr += " ";
			unparsedCells = 
				columnCellsStr.split(TableService.OUTER_CELL_DELIMITER + Utility.EMPTY_STRING);
			unparsedCells[unparsedCells.length - 1] = Utility.EMPTY_STRING;
		}
		else {
			unparsedCells = 
				columnCellsStr.split(TableService.OUTER_CELL_DELIMITER + Utility.EMPTY_STRING);
		}

		return unparsedCells;
	}

	private Optional<TableCell> parseCell(String unparsedCell) throws IOException {

		if (unparsedCell.length() > 0 && 
			unparsedCell.charAt(unparsedCell.length() - 1) == INNER_CELL_DELIMITER) {
			unparsedCell += " ";

			String[] innerCellArray;
        	innerCellArray = unparsedCell.split(INNER_CELL_DELIMITER + Utility.EMPTY_STRING);

        	return Optional.of(new TableCell(innerCellArray[0], Utility.EMPTY_STRING));
		}
		else {
			String[] innerCellArray;
        	innerCellArray = unparsedCell.split(INNER_CELL_DELIMITER + Utility.EMPTY_STRING);

        	if (innerCellArray.length > 2) {
				System.out.println("Each inner cells can only have 1 delimiter");
            	throw new IOException();
			}
			else if (innerCellArray.length == 2) {
				return Optional.of(new TableCell(innerCellArray[0], innerCellArray[1]));
			}
			else if (innerCellArray.length == 1) {
				if (innerCellArray[0].equals(Utility.EMPTY_STRING)) {
					return Optional.empty();
            	}
            	else {
            		System.out.println("Each non-empty cell should have a delimiter");
            		throw new IOException();
            	}
			}
			else {
				return Optional.of(new TableCell(innerCellArray[0], innerCellArray[1]));
			}
		}
	}	

	private String generateRandomString() {
		StringBuilder sb = new StringBuilder();

		Random rnd = new Random();

		for (int i = 0; i < 5; i++) {

			int ascii = -1;

			do {
				// ascii character between 0 ~ 31 represent symbols not found in the keyboard. Also, 
				// 127 ascii is an empty character. Hence, allowed ascii range is between 32 ~ 126 
				// only.
				ascii = rnd.nextInt(127 - 32) + 32; 
			} while (ascii == INNER_CELL_DELIMITER || ascii == OUTER_CELL_DELIMITER);
			

			sb.append((char) ascii);	
		}

		return sb.toString();
	}
}