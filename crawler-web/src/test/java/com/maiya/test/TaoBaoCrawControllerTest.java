package com.maiya.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.maiya.web.controller.TaoBaoCrawController;

@WebAppConfiguration
public class TaoBaoCrawControllerTest extends BaseTest {

	@Autowired
	private TaoBaoCrawController taoBaoCrawController;

	private MockMvc mock;

	@Before
	public void setup() {
		this.mock = MockMvcBuilders.standaloneSetup(taoBaoCrawController).build();
	}

	
	public void testTaoBaoCrawController() {

		try {
			ResultActions result = this.mock.perform(MockMvcRequestBuilders.get("/crawling/crawlingTaoBao")
					.param("userIdentity", "73fa419bc7064baf833a915e0433ea99").param("userChannel", "my"));
			System.out.println(result.andReturn().getResponse().getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testQueryTaoBaoAddressInfo() {

		try {
			MvcResult result = this.mock.perform(MockMvcRequestBuilders.get("/crawling/queryTaoBaoAddress")
					.param("userIdentity", "73fa419bc7064baf833a915e0433ea99").param("userChannel", "my")).andReturn();
			System.out.println(result.getResponse().getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
