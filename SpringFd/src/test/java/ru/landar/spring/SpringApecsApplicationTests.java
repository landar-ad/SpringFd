package ru.landar.spring;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import ru.landar.spring.model.Document;
import ru.landar.spring.model.Reestr;
import ru.landar.spring.service.DocxServiceImpl;
import ru.landar.spring.service.ObjService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringApecsApplicationTests {
	@Test
	public void testDocxService() throws IOException {
		DocxServiceImpl d = new DocxServiceImpl();
		Map<String, String> mapData = new HashMap<String, String>();
		try {
			XWPFDocument docx = d.openDocument("C:\\TEMP\\p1.docx");
			XWPFTable[] ts = d.tableContains(docx, new String[] {"list_doc"});
			if (ts != null) {
				for (XWPFTable table : ts) {
					mapData.clear();
					mapData.put("{#}", "1");
					mapData.put("{list_doc}", "");
					mapData.put("{doc_type__name}", "Счет");
					mapData.put("{doc_number}", "01-656");
					mapData.put("{doc_date}", "10.05.2019");
					d.addRow(table, "{list_doc}", mapData);
					mapData.clear();
					mapData.put("{#}", "2");
					mapData.put("{list_doc}", "");
					mapData.put("{doc_type__name}", "ЗКР");
					mapData.put("{doc_number}", "18-011");
					mapData.put("{doc_date}", "21.05.2019");
					d.addRow(table, "{list_doc}", mapData);
					mapData.clear();
					mapData.put("{#}", "3");
					mapData.put("{list_doc}", "");
					mapData.put("{doc_type__name}", "Выписка");
					mapData.put("{doc_number}", "18-431");
					mapData.put("{doc_date}", "24.05.2019");
					d.addRow(table, "{list_doc}", mapData);
					d.deleteRows(table, new String[] {"list_doc"});
				}
			}
			mapData.put("{act_number}", "1");
			mapData.put("{act_date}", "08.06.2019");
			mapData.put("{depart__name}", "Департамент бухгалтерского учета");
			mapData.put("{create_agent__post}", "Специалист");
			mapData.put("{create_agent__name}", "Пашенцев Н.В.");
			mapData.put("{create_agent__phone}", "8-499-930-5432");
			mapData.put("{create_agent__email}", "pashentsev-nv@mon.gov.ru");
			d.replace(docx, mapData);
			docx.write(new FileOutputStream("C:\\TEMP\\p2_result.docx"));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	@Autowired
	ObjService objService;
	@Test
	public void testFindService() throws IOException {
		Page<?> p = objService.findAll(Reestr.class, null, new String[] {"list_doc__rn"}, new Object[] {109});
		
	}
}