package org.apache.poi.hssf.usermodel;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Tests to show that our documentation at
 *  http://poi.apache.org/hssf/eval.html
 * all actually works as we'd expect them to
 */
public class TestFormulaEvaluatorDocs extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * http://poi.apache.org/hssf/eval.html#EvaluateAll
	 */
	public void testEvaluateAll() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s1 = wb.createSheet();
		HSSFSheet s2 = wb.createSheet();
		wb.setSheetName(0, "S1");
		wb.setSheetName(1, "S2");
		
		HSSFRow s1r1 = s1.createRow(0);
		HSSFRow s1r2 = s1.createRow(1);
		HSSFRow s2r1 = s2.createRow(0);
		
		HSSFCell s1r1c1 = s1r1.createCell((short)0);
		HSSFCell s1r1c2 = s1r1.createCell((short)1);
		HSSFCell s1r1c3 = s1r1.createCell((short)2);
		s1r1c1.setCellValue(22.3);
		s1r1c2.setCellValue(33.4);
		s1r1c3.setCellFormula("SUM(A1:B1)");
		
		HSSFCell s1r2c1 = s1r2.createCell((short)0);
		HSSFCell s1r2c2 = s1r2.createCell((short)1);
		HSSFCell s1r2c3 = s1r2.createCell((short)2);
		s1r2c1.setCellValue(-1.2);
		s1r2c2.setCellValue(-3.4);
		s1r2c3.setCellFormula("SUM(A2:B2)");
		
		HSSFCell s2r1c1 = s2r1.createCell((short)0);
		s2r1c1.setCellFormula("S1!A1");
		
		// Not evaluated yet
		assertEquals(0.0, s1r1c3.getNumericCellValue(), 0);
		assertEquals(0.0, s1r2c3.getNumericCellValue(), 0);
		assertEquals(0.0, s2r1c1.getNumericCellValue(), 0);
		
		// Do a full evaluate, as per our docs
		// uses evaluateFormulaCell()
		for(int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
			HSSFSheet sheet = wb.getSheetAt(sheetNum);
			HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(sheet, wb);

			for(Iterator rit = sheet.rowIterator(); rit.hasNext();) {
				HSSFRow r = (HSSFRow)rit.next();
				evaluator.setCurrentRow(r);

				for(Iterator cit = r.cellIterator(); cit.hasNext();) {
					HSSFCell c = (HSSFCell)cit.next();
					if(c.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
						evaluator.evaluateFormulaCell(c);
						
						// For testing - all should be numeric
						assertEquals(HSSFCell.CELL_TYPE_NUMERIC, evaluator.evaluateFormulaCell(c));
					}
				}
			}
		}
		
		// Check now as expected
		assertEquals(55.7, wb.getSheetAt(0).getRow(0).getCell((short)2).getNumericCellValue(), 0);
		assertEquals("SUM(A1:B1)", wb.getSheetAt(0).getRow(0).getCell((short)2).getCellFormula());
		assertEquals(HSSFCell.CELL_TYPE_FORMULA, wb.getSheetAt(0).getRow(0).getCell((short)2).getCellType());
		
		assertEquals(-4.6, wb.getSheetAt(0).getRow(1).getCell((short)2).getNumericCellValue(), 0);
		assertEquals("SUM(A2:B2)", wb.getSheetAt(0).getRow(1).getCell((short)2).getCellFormula());
		assertEquals(HSSFCell.CELL_TYPE_FORMULA, wb.getSheetAt(0).getRow(1).getCell((short)2).getCellType());
		
		assertEquals(22.3, wb.getSheetAt(1).getRow(0).getCell((short)0).getNumericCellValue(), 0);
		assertEquals("S1!A1", wb.getSheetAt(1).getRow(0).getCell((short)0).getCellFormula());
		assertEquals(HSSFCell.CELL_TYPE_FORMULA, wb.getSheetAt(1).getRow(0).getCell((short)0).getCellType());
		
		
		// Now do the alternate call, which zaps the formulas
		// uses evaluateInCell()
		for(int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
			HSSFSheet sheet = wb.getSheetAt(sheetNum);
			HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(sheet, wb);

			for(Iterator rit = sheet.rowIterator(); rit.hasNext();) {
				HSSFRow r = (HSSFRow)rit.next();
				evaluator.setCurrentRow(r);

				for(Iterator cit = r.cellIterator(); cit.hasNext();) {
					HSSFCell c = (HSSFCell)cit.next();
					if(c.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
						evaluator.evaluateInCell(c);
					}
				}
			}
		}
		
		assertEquals(55.7, wb.getSheetAt(0).getRow(0).getCell((short)2).getNumericCellValue(), 0);
		assertEquals(HSSFCell.CELL_TYPE_NUMERIC, wb.getSheetAt(0).getRow(0).getCell((short)2).getCellType());
		
		assertEquals(-4.6, wb.getSheetAt(0).getRow(1).getCell((short)2).getNumericCellValue(), 0);
		assertEquals(HSSFCell.CELL_TYPE_NUMERIC, wb.getSheetAt(0).getRow(1).getCell((short)2).getCellType());
		
		assertEquals(22.3, wb.getSheetAt(1).getRow(0).getCell((short)0).getNumericCellValue(), 0);
		assertEquals(HSSFCell.CELL_TYPE_NUMERIC, wb.getSheetAt(1).getRow(0).getCell((short)0).getCellType());
	}
}
