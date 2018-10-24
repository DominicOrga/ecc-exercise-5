package com.ecc.model;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class TableCell implements Comparable<TableCell> {
		private String leftCell;
		private String rightCell;

		public TableCell(String leftCell, String rightCell) {
			this.leftCell = leftCell;
			this.rightCell = rightCell;
		}

		public void setLeftCell(String leftCell) {
			this.leftCell = leftCell;
		}

		public void setRightCell(String rightCell) {
			this.rightCell = rightCell;
		}

		public String getLeftCell() {
			return this.leftCell;
		}

		public String getRightCell() {
			return this.rightCell;
		}

		@Override
		public String toString() {
			return leftCell + "," + rightCell;
		}

		@Override
		public int compareTo(TableCell other) {
			String o1 = this.getLeftCell().toLowerCase() + this.getRightCell().toLowerCase();
			String o2 = other.getLeftCell().toLowerCase() + other.getRightCell().toLowerCase();

			return new CompareToBuilder().append(o1, o2).toComparison(); // Test usage of apache commons
		}
	}