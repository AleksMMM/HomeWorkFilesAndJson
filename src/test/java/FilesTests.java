import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojos.Cat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesTests {

    ClassLoader classLoader = FilesTests.class.getClassLoader();
    String zipName = "files.zip";
    String pdfName = "sample.pdf";
    String cvcName = "file_example_CSV_5000.csv";
    String xlsName = "file_example_XLS_50.xls";

    private InputStream getFileFromArchive(String fileName) throws Exception {
        File zipFile = new File("src/test/resources/" + zipName);
        ZipFile zip = new ZipFile(zipFile);
        return zip.getInputStream(zip.getEntry(fileName));
    }

    @DisplayName("Check pdf from zip")
    @Test
    void parseZipPdfTest() throws Exception {
        try (InputStream pdfFileStream = getFileFromArchive(pdfName)) {
            PDF pdf = new PDF(pdfFileStream);
            assertThat(pdf.numberOfPages).isEqualTo(2);
            assertThat(pdf.text).containsAnyOf("A Simple PDF File");
        }
    }

    @DisplayName("Check csv from zip")
    @Test
    void parseZipCsvTest() throws Exception {

        try (InputStream csvFileStream = getFileFromArchive(cvcName)) {
            CSVReader csvReader = new CSVReader(new InputStreamReader(csvFileStream, UTF_8));
            List<String[]> csv = csvReader.readAll();
            assertThat(csv).contains(new String[]{"1", "Dulce", "Abril", "Female", "United States", "32", "15/10/2017", "1562"});
        }
    }

    @DisplayName("Check xls from zip")
    @Test
    void parseZipXlsTest() throws Exception {
        try (InputStream xlsFileStream = getFileFromArchive(xlsName)) {
            XLS xls = new XLS(xlsFileStream);
            assertThat(xls.excel.getSheetAt(0).getRow(13).getCell(6).getStringCellValue()).contains("16/08/2016");
            assertThat(xls.excel.getSheetAt(0).getRow(13).getCell(0).getNumericCellValue()).isEqualTo(13);
            assertThat(xls.excel.getSheetAt(0).getRow(13).getCell(1).getStringCellValue()).contains("Sherron");
            assertThat(xls.excel.getSheetAt(0).getRow(13).getCell(2).getStringCellValue()).contains("Ascencio");
            assertThat(xls.excel.getSheetAt(0).getRow(13).getCell(3).getStringCellValue()).contains("Female");
            assertThat(xls.excel.getSheetAt(0).getRow(13).getCell(4).getStringCellValue()).contains("Great Britain");
            assertThat((xls.excel.getSheetAt(0).getRow(13).getCell(5).getNumericCellValue())).isEqualTo(32.0);
            assertThat((xls.excel.getSheetAt(0).getRow(13).getCell(7).getNumericCellValue())).isEqualTo(3256.0);
            assertThat(xls.excel.getSheetAt(0).getPhysicalNumberOfRows()).isEqualTo(51);
        }
    }

    @DisplayName("Check json test")
    @Test
    void parseJsonTest() throws Exception {
        try(InputStream jsonFileStream = classLoader.getResourceAsStream("cat.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            Cat cat = objectMapper.readValue(jsonFileStream,Cat.class);
            assertThat(cat.getName()).isEqualTo("Ryzhik");
            assertThat(cat.getVaccination().date).isEqualTo("25.11.2021");
            assertThat(cat.getColors()).contains("white", "red");
        }
    }
}