package ru.landar.spring;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import ru.landar.spring.model.SpCommon;
import ru.landar.spring.model.assets.RClaim;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.HelperServiceImpl;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringFdApplicationTests {
	@Autowired
	HelperService hs;
	@Autowired
	UserService userService;
	@Autowired
	ObjService objService;
	@Test
	public void test() throws Exception {
		/*
		String clazz = "RCommission";
		String s = hs.getDefaultObjectTemplate(clazz, true);
		if (s != null) hs.copyStream(new ByteArrayInputStream(s.getBytes("UTF-8")), new FileOutputStream("C:\\TEMP\\details" + clazz + "Page.html"), true, true);
		*/
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("user", userService.getUser("admin"));
		//map.put("obj", objService.find(RClaim.class, 0)); ==obj.co_org.rn
		map.put("op1", null);
		map.put("op2", null);
		Object r = hs.evaluate("#op1==#op2", map);
		//Object r = hs.evaluate("{'delete','add','copy'}.?[#this=='*'||#this==#op].size()>0", map);
		//Object r = hs.evaluate("!{'delete','add','copy'}.contains(#op)", map);
		//Object r = hs.evaluate("#user.org?.rn==null", map);
		System.out.println(r);
		/*
		String v = hs.li("SpRCommission_RClaim", "za_type", "c_type", 726);
		*/
	}
}