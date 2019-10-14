package ru.landar.spring;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ru.landar.spring.service.HelperService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringFdApplicationTests {
	@Autowired
	HelperService hs;
	@Test
	public void test() throws Exception {
		String s = hs.getDefaultObjectTemplate("RProperty_RProperty", true);
		if (s != null) hs.copyStream(new ByteArrayInputStream(s.getBytes("UTF-8")), new FileOutputStream("C:\\TEMP\\detailsRProperty_RPropertyPage.html"), true, true);
	}
}