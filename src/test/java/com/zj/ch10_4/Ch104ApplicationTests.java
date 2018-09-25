package com.zj.ch10_4;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zj.ch10_4.dao.PersonRepository;
import com.zj.ch10_4.domain.Person;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Ch104Application.class)
@WebAppConfiguration
@Transactional  //确保每次测试后的数据将会被回滚
public class Ch104ApplicationTests {

	@Autowired
	PersonRepository personRepository;
	
	MockMvc mvc;
	
	@Autowired
	WebApplicationContext webApplicationContext;
	String expectedJson;
	
	@Before //在测试开始前进行一些初始化的工作
	public void setUp() throws JsonProcessingException{
		Person p1=new Person("wyf");
		Person p2=new Person("wisely");
		personRepository.save(p1);
		personRepository.save(p2);
		
		//获得期待返回的JSON字符串
		expectedJson=Obj2Json(personRepository.findAll());
		mvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	//将对象转换成JSON字符串
	private String Obj2Json(Object obj) throws JsonProcessingException {
		ObjectMapper mapper=new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	@Test
	public void testPersonController() throws Exception {
		String uri="/person";
		MvcResult result=mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
				.andReturn(); //获得一个request的执行结果
		int status=result.getResponse().getStatus(); //获得request执行结果的状态
		//获得request执行结果的内容
		String content=result.getResponse().getContentAsString();
		
		//将预期状态200和实际状态比较
		Assert.assertEquals("错误，正确的返回值为200",200, status); 
		//将预期字符串和返回字符串比较
		Assert.assertEquals("错误，返回值和预期返回值不一致", expectedJson,content);
	}

}
