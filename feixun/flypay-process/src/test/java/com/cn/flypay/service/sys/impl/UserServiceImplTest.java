package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class UserServiceImplTest {
	@Autowired
	private UserService userService;

	/*
	 * @Test public void testAdd() { userService.add(null); }
	 */
	public void testdataGrid() {
		User user = new User();
		user.setRoleIds("1");
		Organization org = new Organization();
		user.setOrganizationId(1l);
		user.setPassword("add");
		userService.addUser(user);

	}
	
	public void testupdateUserAuthStatus() {
		userService.updateUserAuthStatus(4l, true, "test");
	}

	public void testdealClearUserAuthErrorNum() {
		userService.dealClearUserAuthErrorNum();
	}

	public void testAddUser() {
		try {
			userService.addUser("1101122", "11111", "003M62", "F20160001");
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testLogin() {
		try {
			userService.loginApp("138171176441", "F20160", "123456");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testfindUserByPhone() {
		try {
			User u = userService.findUserByPhone("138171176441", "F20160001");
			System.out.println(u.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testupdateUserType() {
		try {
			userService.updateUserType(4243l, null, 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testgetByCode() {
		try {
			User user = userService.getByCode("100000");
			System.out.println(user.getOrganizationName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testget() {
		try {
			User user = userService.get(2l);
			System.out.println(user.getOrganizationAppName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testfindUserByLoginName() {
		try {
			User user = userService.findUserByLoginName("18068089860", "F20160003");
			System.out.println(user.getAgentId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testcreateSubAgent() {
		try {
			userService.createSubAgent(4l, 27l);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testisSuperAdmin() {
		boolean f = userService.isSuperAdmin(1l);
		System.out.println(f);
	}

	public void testfindToCheckMerchantImagesByUserId() {
		userService.findToCheckMerchantImagesByUserId(4l);
	}

	public void test() {

	}
}
