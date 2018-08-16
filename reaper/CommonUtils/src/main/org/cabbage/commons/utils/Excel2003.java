package org.cabbage.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Excel操作类。
 * 
 * @author
 * 
 */
public class Excel2003 {
	HSSFWorkbook wb;
	private final static SimpleDateFormat fullTimeFmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public Excel2003(InputStream is) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(is);
		wb = new HSSFWorkbook(fs);
	}

	public Excel2003(String filePath) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filePath));
		wb = new HSSFWorkbook(fs);
	}

	class Point {
		public Point(String cellPositionStr) {
			char[] chars = cellPositionStr.toCharArray();
			int i = 0;
			for (; i < chars.length; i++) {
				if (Character.isDigit(chars[i])) {
					break;
				}
			}
			row = Integer.parseInt(cellPositionStr.substring(i));
			col = cellNumStr2Int(cellPositionStr.substring(0, i));
		}

		public Point(String colStr, int row) {
			col = cellNumStr2Int(colStr);
			this.row = row;
		}

		int row;
		int col;
	}

	/**
	 * 给表格的列写数据
	 * 
	 * @param row
	 * @param col
	 * @param sheetNo
	 * @param objs
	 * @param props
	 * @return
	 */
	public int writeCols(int row, int col, int sheetNo,
			Collection<Object> objs, String[] props) {

		return row;
	}

	public int writeCols(int row, String col, int sheetNo,
			Collection<Object> objs, String[] props) {

		return writeCols(row, cellNumStr2Int(col), sheetNo, objs, props);
	}

	public void writeCol(int row, String col, int sheetNo, Object obj,
			String[] props) {
		writeCol(row, cellNumStr2Int(col), sheetNo, obj, props);
	}

	public void writeCol(int row, int col, int sheetNo, Object obj,
			String[] props) {

	}

	/**
	 * 给表格的行写数据
	 * 
	 * @param row
	 *            int 从0开始
	 * @param col
	 *            int 从0开始
	 * @param sheetNo
	 *            sheet编号
	 * @param objs
	 *            结果集
	 * @param props
	 *            解析的字段
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public int writeRows(int row, int col, int sheetNo,
			Collection<Object> objs, String[] props)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		for (Object obj : objs) {
			this.copyAndInsertRow(row, row + 1);
			writeRow(row, col, sheetNo, obj, props);
			row += 1;
		}
		return row;
	}

	/**
	 * 创建表格的行
	 * 
	 * @param row
	 *            int 从0开始
	 * @param col
	 *            String 从"A"开始
	 * @param sheetNo
	 *            sheet编号
	 * @param objs
	 *            结果集
	 * @param props
	 *            解析的字段
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public int writeRows(int row, String col, int sheetNo,
			Collection<Object> objs, String[] props)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return writeRows(row, cellNumStr2Int(col), sheetNo, objs, props);
	}

	public void writeRow(int row, int col, int sheetNo, Object obj,
			String[] props) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		for (int i = 0; i < props.length; i++) {
			this.setCellValue(i + col, row, sheetNo, BeanUtils.getProperty(obj,
					props[i]));
		}
	}

	public void writeRow(int row, String col, int sheetNo, Object obj,
			String[] props) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		writeRow(row, cellNumStr2Int(col), sheetNo, obj, props);
	}

	/**
	 * 给Excel中的某个sheet的某个单元格赋值。
	 * 
	 * @param cellPositionStr
	 *            位置参数如A12表示A列，12行。
	 * @param sheetNo
	 * @param v
	 * @return
	 */
	public HSSFCell setCellValue(String cellPositionStr, int sheetNo, Object v) {
		Point p = new Point(cellPositionStr);
		return setCellValue(p, sheetNo, v);
	}

	public HSSFCell setCellValue(String cellPositionStr, Object v) {
		Point p = new Point(cellPositionStr);
		return setCellValue(p, 0, v);
	}

	/**
	 * 给Excel中的某个sheet的某个单元格赋值。
	 * 
	 * @param colNumStr
	 *            哪一列
	 * @param rowNum
	 * @param sheetNo
	 * @param v
	 * @return
	 */
	public HSSFCell setCellValue(String colNumStr, int rowNum, int sheetNo,
			Object v) {
		Point p = new Point(colNumStr, rowNum);
		return setCellValue(p, sheetNo, v);
	}

	public HSSFCell setCellValue(Point p, int sheetNo, Object v) {
		return setCellValue(p.col, p.row, sheetNo, v);
	}

	/**
	 * 给Excel中的某个sheet的某个单元格赋值。
	 * 
	 * @param colNum
	 * @param rowNum
	 *            从0开始。
	 * @param sheetNo
	 *            从0开始。
	 * @param v
	 * @return
	 */
	public HSSFCell setCellValue(int colNum, int rowNum, int sheetNo, Object v) {
		HSSFCell cell = this.getCell(colNum, rowNum, sheetNo);
		if (v == null) {
			cell.setCellValue(new HSSFRichTextString(""));
			// 添加的值是以单元格格式为准，还是以数据类型为准？
			return cell;
		}
		if (v.getClass() == Boolean.class) {
			cell.setCellValue((Boolean) v);
		} else if (v.getClass() == Integer.class) {
			cell.setCellValue((Integer) v);
		} else if (v.getClass() == Double.class) {
			cell.setCellValue((Double) v);
		} else if (v.getClass() == Float.class) {
			cell.setCellValue((Float) v);
		} else if (v.getClass() == BigDecimal.class) {
			cell.setCellValue(((BigDecimal) v).doubleValue());
		} else if (v.getClass() == Date.class) {
			cell.setCellValue(new HSSFRichTextString(fullTimeFmt
					.format((Date) v)));// 权益之计
		} else if (v.getClass() == String.class) {
			cell.setCellValue(new HSSFRichTextString((String) v));
		} else {
			cell.setCellValue(new HSSFRichTextString(v.toString()));
		}

		return cell;
	}

	public HSSFCell setCellFormula(String colNumStr, int rowNum, int sheetNo,
			Object v) {
		Point p = new Point(colNumStr, rowNum);
		HSSFCell cell = this.getCell(p.col, p.row, sheetNo);
		cell.setCellFormula((String) v);
		return cell;
	}

	/**
	 * 根据指定行列和sheet获取单元。
	 * 
	 * @param rowNum
	 * @param cellNum
	 * @param sheetNo
	 * @return
	 */
	public HSSFCell getCell(int colNum, int rowNum, int sheetNo) {
		HSSFRow row = getRow(rowNum, sheetNo);
		return row.getCell(colNum);
	}

	public HSSFCell getCell(String colNumStr, int rowNum, int sheetNo) {
		int colNum = cellNumStr2Int(colNumStr);
		return getCell(colNum, rowNum, sheetNo);
	}

	public HSSFCell getCell(String cellPositionStr, int sheetNo) {
		Point p = new Point(cellPositionStr);
		return getCell(p.col, p.row, sheetNo);
	}

	/**
	 * 获取某一行。
	 * 
	 * @param rowNum
	 * @param sheetNo
	 * @return
	 */
	public HSSFRow getRow(int rowNum, int sheetNo) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		return sheet.getRow(rowNum - 1);
	}

	/**
	 * 将列的名称转换为数字。
	 * 
	 * @param cellNumStr
	 * @return
	 */
	private static int cellNumStr2Int(String cellNumStr) {
		cellNumStr = cellNumStr.toLowerCase();
		int cellNum = 0;
		char[] chars = cellNumStr.toCharArray();
		int j = 0;
		for (int i = chars.length - 1; i >= 0; i--) {
			cellNum += (chars[i] - 'a' + 1) * Math.pow(26, j);
			j++;
		}
		return cellNum - 1;
	}

	/**
	 * 将excel写入到某个输出流中。
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException {
		wb.write(out);
	}

	public void save(String filePath) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(filePath));
			write(out);
			out.flush();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}

	/**
	 * 获取某个单元格的值，并做一定的类型判断。
	 * 
	 * @param cell
	 * @return
	 */
	private Object getCellValue(HSSFCell cell) {
		Object value = null;
		if (cell != null) {
			int cellType = cell.getCellType();
			HSSFCellStyle style = cell.getCellStyle();
			short format = style.getDataFormat();
			switch (cellType) {
			case HSSFCell.CELL_TYPE_NUMERIC:
				double numTxt = cell.getNumericCellValue();
				System.out.println("Excel.getCellValue()" + cell.getCellNum()
						+ " col format=" + format + " cellType=" + cellType);
				if (format == 22 || format == 14)
					value = HSSFDateUtil.getJavaDate(numTxt);
				else
					value = numTxt;
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				boolean booleanTxt = cell.getBooleanCellValue();
				value = booleanTxt;
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				value = null;
				break;
			case HSSFCell.CELL_TYPE_STRING:
				HSSFRichTextString rtxt = cell.getRichStringCellValue();
				if (rtxt == null) {
					System.out.print("null,");
					break;
				}
				String txt = rtxt.getString();
				value = txt;
				break;
			default:
				System.out.println(cell.getCellNum() + " col cellType="
						+ cellType);
			}
		}
		return value;

	}

	/**
	 * 读取某个excel，然后将其转化为List的List。
	 * 
	 * @param source
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<List<Object>> excelToListList(int sheetNo)
			throws FileNotFoundException, IOException {
		// 首先是讲excel的数据读入，然后根据导入到的数据库的结构和excel的结构来决定如何处理。
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();
		List<List<Object>> rows = new ArrayList<List<Object>>();
		for (int i = firstRowNum; i < lastRowNum; i++) {
			HSSFRow row = sheet.getRow(i);
			List<Object> cellList = new ArrayList<Object>();
			for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
				Object value = null;
				HSSFCell cell = row.getCell((short) j);
				value = getCellValue(cell);
				cellList.add(value);
			}
			rows.add(cellList);
		}
		return rows;
	}

	public List<Map<String, Object>> excelToMapList(int sheetNo)
			throws FileNotFoundException, IOException {
		// 首先是讲excel的数据读入，然后根据导入到的数据库的结构和excel的结构来决定如何处理。
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		HSSFRow firstRow = sheet.getRow(firstRowNum);
		for (int i = firstRowNum + 1; i < lastRowNum; i++) {
			HSSFRow row = sheet.getRow(i);
			if (row == null)
				continue;
			Map<String, Object> rowMap = new HashMap<String, Object>();
			for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
				HSSFCell col = firstRow.getCell((short) j);
				String key = col.getRichStringCellValue().getString();
				Object value = null;
				HSSFCell cell = row.getCell((short) j);
				value = getCellValue(cell);
				rowMap.put(key, value);
			}
			rows.add(rowMap);
		}
		return rows;
	}

	/**
	 * 新增一行，拷贝目标row的格式，新增至新行
	 * 
	 * @param targetRowNum
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HSSFRow createRow(int targetRowNum) {
		HSSFSheet sheet = wb.getSheetAt(0);
		int rowNum = sheet.getLastRowNum();
		HSSFRow targetRow = sheet.getRow(targetRowNum);
		HSSFRow newRow = sheet.createRow(++rowNum);
		newRow.setHeight(targetRow.getHeight());
		int i = 0;
		for (Iterator<HSSFCell> cit = (Iterator<HSSFCell>) targetRow
				.cellIterator(); cit.hasNext();) {
			HSSFCell hssfCell = cit.next();
			// HSSFCell中的一些属性转移到Cell中
			HSSFCell cell = newRow.createCell((short) i++);
			// HSSFCellStyle s = hssfCell.getCellStyle();
			cell.setCellStyle(hssfCell.getCellStyle());
		}
		return newRow;
	}

	public void deleteRow(int rowNum) {
		deleteRow(0, rowNum);
	}

	public void deleteRow(int sheetNo, int rowNum) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		sheet.shiftRows(rowNum, sheet.getLastRowNum(), -1);
	}

	/**
	 * 拷贝行粘帖到指定位置。
	 * 
	 * @param sheet
	 * @param srcRow
	 * @param rowNum
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HSSFRow copyAndInsertRow(HSSFSheet sheet, HSSFRow srcRow,
			int targetRowNum) {
		sheet.shiftRows(targetRowNum, sheet.getLastRowNum(), 1);
		HSSFRow newRow = sheet.getRow(targetRowNum);
		newRow.setHeight(srcRow.getHeight());
		int j = 0;
		for (Iterator<HSSFCell> cit = (Iterator<HSSFCell>) srcRow
				.cellIterator(); cit.hasNext();) {
			HSSFCell hssfCell = cit.next();
			// HSSFCell中的一些属性转移到Cell中
			HSSFCell cell = newRow.createCell((short) j++);
			cell.setCellStyle(hssfCell.getCellStyle());
		}
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			Region region = sheet.getMergedRegionAt(i);
			if (region.getRowFrom() == srcRow.getRowNum()
					&& region.getRowTo() == region.getRowFrom()) {
				sheet.addMergedRegion(new Region(targetRowNum, region
						.getColumnFrom(), targetRowNum, region.getColumnTo()));
			}
		}
		return newRow;
	}

	public HSSFRow copyAndInsertRow(int sheetNo, int fromRowNum,
			int targetRowNum) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		HSSFRow srcRow = sheet.getRow(fromRowNum);
		return copyAndInsertRow(sheet, srcRow, targetRowNum);
	}

	public HSSFRow copyAndInsertRow(int fromRowNum, int targetRowNum) {
		return copyAndInsertRow(0, fromRowNum, targetRowNum);
	}

	/**
	 * 添加一行（格式与最后一行相同）
	 * 
	 * @return
	 */
	public HSSFRow copyAndInsertRow() {
		return copyAndInsertRow(1, 2);
	}

	public HSSFWorkbook getWb() {
		return wb;
	}

	public void setWb(HSSFWorkbook wb) {
		this.wb = wb;
	}

	/**
	 * 合并单元格
	 * 
	 * @param rowFrom
	 * @param columnFrom
	 * @param rowTo
	 * @param columnTo
	 * @param sheetNo
	 */
	public void mergeCell(int rowFrom, String columnFrom, int rowTo,
			String columnTo, int sheetNo) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		Region region = new Region((short) rowFrom,
				(short) cellNumStr2Int(columnFrom), (short) rowTo,
				(short) cellNumStr2Int(columnTo));
		sheet.addMergedRegion(region);

	}

	/**
	 * 复制单元格格式
	 * 
	 * @param rowFrom
	 * @param columnFrom
	 * @param rowTo
	 * @param columnTo
	 * @param sheetNo
	 */
	public void copyCellStyle(int rowFrom, String columnFrom, int rowTo,
			String columnTo, int sheetNo) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		HSSFCell cellFrom = this.getCell(cellNumStr2Int(columnFrom), rowFrom,
				sheetNo);
		int realRow = rowTo - 1;
		HSSFRow row = sheet.getRow(realRow);
		if (row == null) {
			row = sheet.createRow(realRow);
		}
		HSSFCell cellTo = this
				.getCell(cellNumStr2Int(columnTo), rowTo, sheetNo);
		if (cellTo == null) {
			cellTo = row.createCell((short) cellNumStr2Int(columnTo));
		}
		cellTo.setCellStyle(cellFrom.getCellStyle());

	}

	public void copySheet(int fromSheetNo) {
		wb.cloneSheet(fromSheetNo);
	}

	public void setSheetName(int sheetNo, String sheetName) {
		wb.setSheetName(sheetNo, sheetName);
	}

	public int getRowCount(int sheetNo) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		return sheet.getPhysicalNumberOfRows();
	}

	public int getColCount(int sheetNo, int rowNo) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		return sheet.getRow(rowNo - 1).getPhysicalNumberOfCells();
	}

	/**
	 * 通过sheet名获得总记录数
	 * 
	 * @param sheetName
	 *            sheet名
	 * @return 总记录数 （-1的场合为sheet不存在的场合）
	 */
	public int getRowCount(String sheetName) {
		HSSFSheet sheet = wb.getSheet(sheetName);
		if (sheet != null) {
			return sheet.getPhysicalNumberOfRows();
		} else {
			return -1;
		}
	}

	/**
	 * 通过sheet名,行号,列号获得单元格值
	 * 
	 * @param sheetName
	 *            sheet名
	 * @param rowNo
	 *            行号
	 * @param colNo
	 *            列号
	 * @return 单元格值
	 */
	public String getCellValue(String sheetName, int rowNo, int colNo) {
		HSSFSheet sheet = wb.getSheet(sheetName);
		if (sheet != null) {
			HSSFRow row = sheet.getRow(rowNo - 1);
			if (row != null) {
				HSSFCell cell = row.getCell(colNo - 1);
				if (cell != null) {
					// 判断是否是整数，但是获取到数据为
					return cell.toString().trim();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 通过sheet编号,行号,列号获得单元格值
	 * 
	 * @param sheetNo
	 *            sheet编号 0:代表第一个sheet
	 * @param rowNo
	 *            行号
	 * @param colNo
	 *            列号
	 * @return 单元格值
	 */
	public String getCellValue(int sheetNo, int rowNo, int colNo) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		if (sheet != null) {
			HSSFRow row = sheet.getRow(rowNo - 1);
			if (row != null) {
				HSSFCell cell = row.getCell(colNo - 1);
				if (cell != null) {
					// 判断是否是整数，但是获取到数据为
					return cell.toString().trim();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 通过sheet名,行号,列号获得单元格值
	 * 
	 * @param sheetName
	 *            sheet名
	 * @param rowNo
	 *            行号
	 * @param colNo
	 *            列号
	 * @return 单元格值
	 */
	public Date getCellDateValue(String sheetName, int rowNo, int colNo) {
		HSSFSheet sheet = wb.getSheet(sheetName);
		if (sheet != null) {
			HSSFRow row = sheet.getRow(rowNo - 1);
			if (row != null) {
				HSSFCell cell = row.getCell(colNo - 1);
				if (cell != null) {
					try {
						return cell.getDateCellValue();
					} catch (Exception ex) {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 通过sheet编号,行号,列号获得单元格值
	 * 
	 * @param sheetNo
	 *            sheet编号 0:代表第一个sheet
	 * @param rowNo
	 *            行号
	 * @param colNo
	 *            列号
	 * @return 单元格值
	 */
	public Date getCellDateValue(int sheetNo, int rowNo, int colNo) {
		HSSFSheet sheet = wb.getSheetAt(sheetNo);
		if (sheet != null) {
			HSSFRow row = sheet.getRow(rowNo - 1);
			if (row != null) {
				HSSFCell cell = row.getCell(colNo - 1);
				if (cell != null) {
					try {
						return cell.getDateCellValue();
					} catch (Exception ex) {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 通过sheet名,行号,列号获得单元格值
	 * 
	 * @param sheetName
	 *            sheet名
	 * @param rowNo
	 *            行号
	 * @param colNo
	 *            列号
	 * @return 单元格值
	 */
	public String getCellFormulaValue(String sheetName, int rowNo, int colNo) {
		HSSFSheet sheet = wb.getSheet(sheetName);
		if (sheet != null) {
			HSSFRow row = sheet.getRow(rowNo - 1);
			if (row != null) {
				HSSFCell cell = row.getCell(colNo - 1);
				if (cell != null) {
					try {
						return String.valueOf(cell.getNumericCellValue());
					} catch (Exception ex) {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		
	}

}