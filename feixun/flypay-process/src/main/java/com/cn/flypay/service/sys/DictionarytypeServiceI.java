package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Dictionarytype;

public interface DictionarytypeServiceI {


	public void add(Dictionarytype dictionarytype);

	public void delete(Long id);

	public void edit(Dictionarytype dictionarytype);

	public Dictionarytype get(Long id);

	public List<Tree> tree();

	List<Tree> tree(String code);


}
