package com.cn.flypay.pageModel.sys;

public class Role implements java.io.Serializable {


	private static final long serialVersionUID = -673942498756646122L;
	private String resourceIds;
	private String resourceNames;
	
	
	private String name; // 角色名称
	private Integer seq; // 排序号
	private Integer isDefault; // 是否默认
	private String description; // 备注
	private Long id;
	private Long version;
	private Long organizationId;
	private String organizationName;

	private  String roleIdStr;
	
	public String getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}
	public String getResourceNames() {
		return resourceNames;
	}
	public void setResourceNames(String resourceNames) {
		this.resourceNames = resourceNames;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Long getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getRoleIdStr() {
		return roleIdStr;
	}

	public void setRoleIdStr(String roleIdStr) {
		this.roleIdStr = roleIdStr;
	}
}
