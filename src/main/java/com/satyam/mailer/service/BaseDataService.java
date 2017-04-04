package com.satyam.mailer.service;

import com.satyam.mailer.exception.IOClientException;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Satyam on 9/11/2016.
 */
public class BaseDataService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDataService.class);

    /**
     * Reads an excel file and generates a map.
     * Key of Map = Header name
     * Value = column value
     * List of map is equivalent to list of rows
     * Map of List is for list of rows per sheets
     * @param inputStream
     * @return
     */
    protected Map<String, List<Map<String, String>>> readExcelWithHeader (InputStream inputStream){
        LOG.info("Reading excel from location: [{}]");
        Map<String, List<Map<String, String>>> dataMap = null;
        ExecutorService service = null;
        try {
            long startTime = System.currentTimeMillis();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            int numberOfSheets = workbook.getNumberOfSheets();

            LOG.info("Preparing callable tasks for reading each sheet.");
            List<Callable<Map<String, List<Map<String, String>>>>> sheetReadTasks = new ArrayList<>();
            for (int index = 0; index < numberOfSheets; index++){
                XSSFSheet sheet = workbook.getSheetAt(index);
                sheetReadTasks.add(callable(sheet));
            }

            service =  Executors.newFixedThreadPool(3);
            List<Future<Map<String, List<Map<String, String>>>>> futureList = service.invokeAll(sheetReadTasks);

            dataMap = new HashedMap<>();
            for (Future<Map<String, List<Map<String, String>>>> future : futureList){
                Map<String, List<Map<String, String>>> sheetDataMap = future.get();
                if(sheetDataMap != null && !sheetDataMap.isEmpty()){
                    dataMap.putAll(sheetDataMap);
                }
            }

        } catch (Exception exception) {
            throw new IOClientException("Exception while reading the excel file", exception);
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (service != null) {
                service.shutdown();
            }
        }
        return dataMap;
    }

    /**
     * I am being optimistic here for now.. Will validate for null/row/cell conditions later
     * Returns callable classes
     *
     * @param sheet
     * @return
     */
    private Callable<Map<String, List<Map<String, String>>>> callable(XSSFSheet sheet) {
        return () -> {
            LOG.info("Processing sheet [{}] ", sheet.getSheetName());

            Map<String, List<Map<String, String>>> resultMap = new HashedMap<>();

            List<String> headerList = new LinkedList<String>();
            Row headerRow = sheet.getRow(0);
            Iterator<Cell> headerCellItr = headerRow.cellIterator();
            while (headerCellItr.hasNext()){
                headerList.add(getCellValue(headerCellItr.next()));
            }

            LOG.debug("Header prepared {} ", headerList);

            List<Map<String, String>> rowList = new ArrayList<>();
            boolean isDataRow = false;
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (!isDataRow) {
                    isDataRow = true;
                    continue;
                }

                Map<String, String> columnMap = new HashedMap<String, String>();
                int cellIndex = 0;
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();
                    columnMap.put(headerList.get(cellIndex), getCellValue(cell));
                    cellIndex++;
                }
                if (columnMap != null && !columnMap.isEmpty()) {
                    rowList.add(columnMap);
                }
            }

            if (rowList != null && !rowList.isEmpty()) {
                resultMap.put(sheet.getSheetName(), rowList);
            }

            return resultMap;
        };
    }

    /**
     * Reads the value from apache poi cell
     * @param cell
     * @return
     */
    private String getCellValue (Cell cell){
        String cellValue = null;
        switch (cell.getCellType())
        {
            case Cell.CELL_TYPE_NUMERIC:
                Double val = cell.getNumericCellValue();
                if (val != null) {
                    cellValue = String.valueOf(val);
                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
        }
        return cellValue;
    }

}
