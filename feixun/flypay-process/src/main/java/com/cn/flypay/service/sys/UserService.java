package com.cn.flypay.service.sys;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;

import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserBlackList;
import com.cn.flypay.pageModel.sys.UserImage;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;

public interface UserService {

	@Deprecated
	public void addShenfuD0Merch();

	public List<User> dataGrid(User user, PageFilter ph);

	public Long count(User user, PageFilter ph);

	public List<UserBlackList> dataGrid(UserBlackList userBlackList, PageFilter ph);

	public Long count(UserBlackList userBlackList, PageFilter ph);

	public void addUser(User user);

	public void addUserBlackList(String realName, String idNo, Integer status, String agentId);

	public void delete(Long id);

	public String editToAgent(Long id);

	public String editToAgent2(Long id);

	public void edit(User user) throws Exception;

	public void editBlackStatus(String idNo, long id);

	public void editAgent(User user);

	public User get(Long id);

	public User loginManagerSystem(User user);

	public List<String> listResource(Long id);

	public Set<Long> listUserResIds(Long userId);

	/**
	 * 修改登录密码
	 * 
	 * @param sessionInfo
	 * @param oldPwd
	 * @param pwd
	 * @return
	 */
	public boolean editUserPwd(SessionInfo sessionInfo, String oldPwd, String pwd);

	public boolean updateUserWeiXinCode(String openid, String nickname, String unionid, Long userid);

	/**
	 * 更新Ip
	 * 
	 * @param sessionInfo
	 * @return
	 */
	public boolean editUserIp(SessionInfo sessionInfo);

	public List<User> getByLoginName(User user);

	public List<User> getUserListByUserType();

	public String[] getUserListNameByUserType();

	public List<User> getByPhone(User user);

	public User getByCode(String user);

	public List<UserImage> findToCheckImagesByUserId(Long id);

	public void updateUserAuthStatus(Long id, Boolean status, String errorInfo);

	public void updateTuser(Tuser u);

	public String updateUserAuthMerchantStatus(Long id, Integer merchantType, String errorInfo);

	public User getUserByToken(String token);

	/**
	 * 该用户是否允许支付 ** 用户状态被锁定 无法支付； 用户为实名认证不允许提现和转账
	 * 
	 * @param userId
	 * @param payType
	 * @return
	 */
	public String isAllowUserPay(Long userId, String payType);

	/**
	 * 根据用户运营商以及用户的手机号登录系统
	 * 
	 * @param appName
	 * @param agentId
	 * @param loginPsw
	 * @return
	 */
	public User loginApp(String appName, String agentId, String loginPsw);

	/**
	 * app端注册用户
	 * 
	 * @param appLoginName
	 * @param loginPwd
	 * @param pCode
	 * @param agentId
	 * @return
	 */
	public User addUser(String appLoginName, String loginPwd, String pCode, String agentId);

	/**
	 * 为代理商添加root子用户
	 * 
	 * @param appLoginName
	 * @param loginPwd
	 * @param pCode
	 * @param orgId
	 * @return
	 * @throws Exception
	 */
	public User addRootUserForOrganization(String appLoginName, String loginPwd, String pCode, Long orgId) throws Exception;

	public User findUserByPhone(String phone, String agentId);

	/**
	 * 保存用户图片
	 * 
	 * @param userId
	 * @param imagePath
	 * @param imageName
	 */
	public void addUserImage(Long userId, String imagePath, Integer imageType, String imageName);

	/**
	 * 判断用户输入的身份证与原有的证件是否一致，若原来为空，返回ture，只有证件不一致时才返回false
	 * 
	 * @param parseLong
	 * @param realIdNo
	 * @return
	 */
	public boolean isOrgIdNo(long parseLong, String realIdNo);

	/**
	 * 更新用户的登录密码
	 * 
	 * @param appLoginName
	 *            手机号
	 * @param oldPwd
	 * @param newPwd
	 * @param isLoginPsw
	 *            true 登录密码 false 交易密码
	 * @return 若原始密码不正确，返回false；若密码存在，直接保存，返回true
	 */
	public Boolean updateUserPsw(String appLoginName, String oldPwd, String newPwd, boolean isLoginPsw, String agentId);

	public UserSettlementConfig getStmConfigByUserId(Long userId);

	/**
	 * 用户上传头像
	 * 
	 * @param userId
	 * @param attachPath
	 */
	public void updateUserHeadIcon(Long userId, String attachPath);

	boolean isOrgRealName(Long userId, String realName);

	/**
	 * 验证用户是否允许转账或提现
	 * 
	 * @param userId
	 * @param transPwd
	 *            交易密码经过MD5加密
	 * @return
	 */
	public String isTransAccount(Long userId, String transPwd, Double amt, Boolean isBrokerage);

	public User findUserByLoginName(String phone, String agentId);

	public User getOpen(String openid, String unionid, String agentId);

	public Tuser findUserByLoginNameT(String phone, String agentId);

	public Tuser getUserLoginName(String phone);

	/**
	 * 用户进入人工认证阶段
	 * 
	 * @param userId
	 * @param cardInfos
	 * @return
	 */
	public String updateUserAuthWhenManualAuth(Long userId, Map<String, String> cardInfos);

	/**
	 * 根据用户提现的额度判断用户是否超出预设范围
	 * 
	 * @param userId
	 * @param transType
	 * @param amt
	 * @return
	 */
	public String isOverLimit(Long userId, String transType, Double amt);

	/**
	 * 判断用户的交易密码是否正确
	 * 
	 * @param parseLong
	 * @param transPwd
	 * @return
	 */
	public Boolean isTransPsw(Long userId, String transPwd);

	/**
	 * 根据订单的金额更新用户的类型
	 * 
	 * @param userId
	 * @param orgAmt
	 */
	public void updateUserType(Long userId, BigDecimal orgAmt, Integer subPersonNum) throws Exception;

	/**
	 * 根据推广码获取推广者
	 * 
	 * @param pcode
	 * @param agentId
	 * @return
	 */
	public Tuser findTuserByUserCodeOrPhone(String pcode, String agentId);

	public void dealClearUserAuthErrorNum();

	public User getMemntUser(User user);

	Tuser getTuser(Long id);

	User findUserByUserCodeOrPhone(String userCodeOrPhone, String agentId);

	/**
	 * 判断用户是否可以消耗积分
	 * 
	 * @param userId
	 * @param transPwd
	 * @param point
	 * @return
	 */
	String isConsumePoint(Long userId, String transPwd, Long point);

	List<UserImage> findToCheckMerchantImagesByUserId(Long id);

	User getSimpleUser(Long id);

	String updateUserMerchantAuthWhenManualAuth(Long userId, Map<String, String> cardInfos);

	public void updateUserVoiceType(Long userId, Integer voiceType);

	void createSubAgent(Long userId, Long orgId) throws Exception;

	Boolean isSuperAdmin(Long userId);

	/**
	 * 根据用户的系统唯一标识识别用户
	 * 
	 * @param userCode
	 * @return
	 */
	Tuser findTuserByUserCode(String userCode);

	public Workbook exportUserList(User user);

	public boolean getUserRole();

	public String getUserRole(Long userId);

	/**
	 * 代理商登录
	 * 
	 * @param user
	 * @return
	 */
	public User loginAgentManagerSystem(User user);

	/**
	 * 更改用户的设置结算卡状态 1 有 0 无
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	public boolean editSettlementStauts(Long userId, int status);

	/**
	 * 修改隐私权限状态 1 开启 0 关闭
	 * 
	 * @param userId
	 * @param privacyType
	 * @return
	 */
	public boolean updatePrivacyType(long userId, int privacyType);

	/**
	 * 修改一键代还状态 1 开启 0 关闭
	 * 
	 * @param parseLong
	 * @param parseInt
	 * @return
	 */
	public boolean updateLoanType(long userId, int loanType);

	public boolean updateSpeechType(long userId, String speechType);

}
