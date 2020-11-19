package com.company.cellphone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.company.cellphone.exception.CSVReadFailureException;
import com.company.cellphone.exception.WriteFailureException;
import com.company.cellphone.model.CellPhone;
import com.company.cellphone.model.CellPhoneUsage;
import com.company.cellphone.util.DateFormatter;
import com.company.cellphone.util.PrintFile;

public class GenerateCellPhoneUsageReport {
	private static final String APPLE = "Apple";
	private static final String SAMSUNG = "Samsung";
	private static final Logger LOG = Logger.getLogger(GenerateCellPhoneUsageReport.class);
	private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.##");
	private static final String NUMBER_OF_PHONES = "NUMBER_OF_PHONES";
	private static final String TOTAL_MINS = "TOTAL_MINS";
	private static final String TOTAL_DATA = "TOTAL_DATA";

	public static void main(String[] args) {
		try {
			final Map<String, Object> metricsMap = new HashMap<>();
			final String cellPhoneCsvFile = "CellPhone.csv";
			final String cellPhoneUsuageCsvFile = "CellPhoneUsageByMonth.csv";
			final String outputFileName = "report.xlsx";
			final Map<Integer, CellPhone> cellPhonesmap = readCellPhoneCSVFile(cellPhoneCsvFile);
			final Map<Integer, List<CellPhoneUsage>> cellPhoneUsageMap = readCellPhoneUsageCSVFile(
					cellPhoneUsuageCsvFile, metricsMap);

			writeHeaderToExcel(outputFileName,metricsMap);
			writeHeaderToDetailsSection(cellPhonesmap, cellPhoneUsageMap,outputFileName);
			PrintFile.print(outputFileName);

		} catch (Exception e) {
			LOG.error("Error in GenerateCellPhoneUsageReport ", e);
		}
	}

	private static void writeHeaderToDetailsSection(final Map<Integer, CellPhone> cellPhonesmap,
			Map<Integer, List<CellPhoneUsage>> cellPhoneUsageMap, String fileName) throws IOException, WriteFailureException {
		Map<String, List<CellPhone>> companysCellPhoneReport = new HashMap<>();

		cellPhonesmap.forEach((empId, cellPhone) -> {
			List<CellPhone> cellPhonesList = null;
			String key = null;
			if (cellPhone != null && cellPhone.getModel().contains(SAMSUNG)) {
				cellPhonesList = companysCellPhoneReport.get(SAMSUNG);
				key = SAMSUNG;
			} else if (cellPhone != null && cellPhone.getModel().contains(APPLE)) {
				cellPhonesList = companysCellPhoneReport.get(APPLE);
				key = APPLE;
			}

			if (cellPhonesList != null && !cellPhonesList.isEmpty()) {
				cellPhonesList.add(cellPhone);
			} else {
				cellPhonesList = new ArrayList<>();
				cellPhonesList.add(cellPhone);
				companysCellPhoneReport.put(key, cellPhonesList);
			}
		});
		
		try (FileOutputStream fos = new FileOutputStream(fileName);) {

			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			int rowCount = sheet.getLastRowNum();
			int startRow = rowCount+2;

			Row row = sheet.createRow(startRow);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue("Employee Id");
			Cell cell2 = row.createCell(1);
			cell2.setCellValue("Employee Name");
			Cell cell3 = row.createCell(2);
			cell3.setCellValue("Model");
			Cell cell4 = row.createCell(3);
			cell4.setCellValue("Purchase Date");
			Cell cell5 = row.createCell(4);
			cell5.setCellValue("Minutes Usage");
			Cell cell6 = row.createCell(5);
			cell6.setCellValue("Data Usage");

			
			cellPhoneUsageMap.forEach((empId,usageList)->{
				CellPhoneUsage usage = usageList.stream().findFirst().orElse(null);
				CellPhone cellPhone = cellPhonesmap.get(empId);
				if(usage != null && cellPhone != null) {
					Row row1 = sheet.createRow(rowCount);
					row1.createCell(0).setCellValue(usage.getEmpId());
					row1.createCell(1).setCellValue(cellPhone.getEmpName());
					row1.createCell(2).setCellValue(cellPhone.getModel());
					row1.createCell(3).setCellValue(cellPhone.getPurchaseDate());
					row1.createCell(4).setCellValue(usageList.stream().mapToInt(i->i.getTotalMins()).sum());
					row1.createCell(5).setCellValue(usageList.stream().mapToDouble(i->i.getTotalData()).sum());
				}
			});
			
			int rowIndex = 1;
			workbook.write(fos);
		} catch (Exception e) {
			throw new WriteFailureException(e);
		}


	}

	public static void writeHeaderToExcel(String reportFileName, final Map<String, Object> metricsMap) throws WriteFailureException {
		File fileName = new File(reportFileName);
		Integer noOfPhones = (Integer) metricsMap.get(NUMBER_OF_PHONES);
		Integer totalmins = (Integer) metricsMap.get(TOTAL_MINS);
		Double totalData = (Double) metricsMap.get(TOTAL_DATA);
		Double avergaeData = totalData / noOfPhones;

		Integer averageMins = totalmins / noOfPhones;

		LOG.info(String.format(
				"ReportRundate - %s, No.Of Phones - %s, Total Mins - %s, Total Data - %s, Average Mins - %s, Average Data - %s",
				LocalDateTime.now(), noOfPhones, totalmins, metricsMap.get(TOTAL_DATA), averageMins, avergaeData));

		try (FileOutputStream fos = new FileOutputStream(fileName);) {

			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet();

			Row row = sheet.createRow(0);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue("Report Run Date");
			Cell cell2 = row.createCell(1);
			cell2.setCellValue("Number of Phones");
			Cell cell3 = row.createCell(2);
			cell3.setCellValue("Total Minutes");
			Cell cell4 = row.createCell(3);
			cell4.setCellValue("Total Data");
			Cell cell5 = row.createCell(4);
			cell5.setCellValue("Average Minutes");
			Cell cell6 = row.createCell(5);
			cell6.setCellValue("Average Data");

			int rowIndex = 1;
			row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue(LocalDateTime.now());
			row.createCell(1).setCellValue(noOfPhones);
			row.createCell(2).setCellValue(totalmins);
			row.createCell(3).setCellValue(totalData);
			row.createCell(4).setCellValue(averageMins);
			row.createCell(5).setCellValue(avergaeData);
			workbook.write(fos);
		} catch (Exception e) {
			throw new WriteFailureException(e);
		}
	}

	private static Map<Integer, List<CellPhoneUsage>> readCellPhoneUsageCSVFile(final String cellPhoneUsageCsvFile,
			final Map<String, Object> metricsMap) throws CSVReadFailureException {
		Map<Integer, List<CellPhoneUsage>> cellPhoneUsageMap = new HashMap<>();
		Integer totalMins = 0;
		Double totalData = 0d;
		Integer noOfPhones = 0;

		try (FileReader fileReader = new FileReader(cellPhoneUsageCsvFile);
				BufferedReader reader = new BufferedReader(fileReader);) {

			String line;
			boolean skipHeader = true;
			while ((line = reader.readLine()) != null) {
				if (skipHeader) {
					skipHeader = false;
					continue;
				}
				String[] arr = line.split(",");
				CellPhoneUsage cellPhoneUsage = new CellPhoneUsage();
				Integer empId = StringUtils.isNotBlank(arr[0]) && StringUtils.isNumeric(arr[0])
						? Integer.valueOf(arr[0])
						: 0;
				if (empId == 0) {
					LOG.warn("Found invalid employee detials - empid :  " + arr[0] + ", empname : " + arr[1]);
					continue;
				}
				Date date = DateFormatter.convertToDate(arr[1]);

				Integer mins = StringUtils.isNotBlank(arr[2]) && StringUtils.isNumeric(arr[2]) ? Integer.valueOf(arr[2])
						: 0;
				Double data = StringUtils.isNotBlank(arr[3]) && StringUtils.isNumeric(arr[3])
						? Double.valueOf(DOUBLE_FORMAT.format(arr[3]))
						: 0;
				cellPhoneUsage.setEmpId(empId);
				cellPhoneUsage.setDate(date);
				cellPhoneUsage.setTotalMins(mins);
				cellPhoneUsage.setTotalData(data);

				List<CellPhoneUsage> exisitngCellPhoneUsage = cellPhoneUsageMap.get(empId);
				if (exisitngCellPhoneUsage != null && !exisitngCellPhoneUsage.isEmpty()) {
					exisitngCellPhoneUsage.add(cellPhoneUsage);
				} else {
					exisitngCellPhoneUsage = new ArrayList<>();
					exisitngCellPhoneUsage.add(cellPhoneUsage);
					cellPhoneUsageMap.put(empId, exisitngCellPhoneUsage);
				}

				totalMins = totalMins + mins;
				totalData = totalData + data;
				noOfPhones++;
			}
			metricsMap.put(NUMBER_OF_PHONES, noOfPhones);
			metricsMap.put(TOTAL_DATA, totalData);
			metricsMap.put(TOTAL_MINS, totalMins);
			return cellPhoneUsageMap;
		} catch (IOException e) {
			LOG.error(e);
			throw new CSVReadFailureException(e);
		}
	}

	private static Map<Integer, CellPhone> readCellPhoneCSVFile(final String cellPhoneCsvFile)
			throws CSVReadFailureException {
		final Map<Integer, CellPhone> cellPhonesMap = new HashMap<>();
		try (FileReader fileReader = new FileReader(cellPhoneCsvFile);
				BufferedReader reader = new BufferedReader(fileReader);) {

			String line;
			boolean skipHeader = true;
			while ((line = reader.readLine()) != null) {
				if (skipHeader) {
					skipHeader = false;
					continue;
				}
				String[] arr = line.split(",");
				CellPhone cellPhone = new CellPhone();
				Integer empId = StringUtils.isNotBlank(arr[0]) && StringUtils.isNumeric(arr[0])
						? Integer.valueOf(arr[0])
						: 0;
				if (empId == 0) {
					LOG.warn("Found invalid employee detials - empid :  " + arr[0] + ", empname : " + arr[1]);
					continue;
				}

				cellPhone.setEmpId(empId);
				cellPhone.setEmpName(StringUtils.isNotBlank(arr[1]) ? arr[1] : null);
				Date purchaseDate = DateFormatter.convertToDate(arr[2]);
				cellPhone.setPurchaseDate(purchaseDate);
				cellPhone.setModel(StringUtils.isNotBlank(arr[3]) ? arr[3] : null);
				cellPhonesMap.put(empId, cellPhone);
			}
			return cellPhonesMap;
		} catch (IOException e) {
			LOG.error(e);
			throw new CSVReadFailureException(e);
		}
	}

}
