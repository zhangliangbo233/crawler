package com.maiya.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.maiya.web.controller.JDCrawController;

@WebAppConfiguration
public class JDCrawControllerTest extends BaseTest {

	@Autowired
	private JDCrawController JDCrawController;

	private MockMvc mock;

	@Before
	public void setup() {
		this.mock = MockMvcBuilders.standaloneSetup(JDCrawController).build();
	}
	
	@Test
	public void testJdCrawController() {
		try {
			MvcResult result  = this.mock.perform(
					MockMvcRequestBuilders.get("/crawling/crawlingJD").param("userIdentity", "73fa419bc7064baf833a915e0433ea99")
							.param("userChannel", "my").param("city", "南京")).andReturn();
			System.out.println(result.getResponse().getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	public void testQueryJDAddress(){
		
		try {
			 MvcResult result = this.mock.perform(
					MockMvcRequestBuilders.get("/crawling/queryJDAddress").param("userIdentity", "73fa419bc7064baf833a915e0433ea99")
							.param("userChannel", "my")).andReturn();
			System.out.println(result.getResponse().getContentAsString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
