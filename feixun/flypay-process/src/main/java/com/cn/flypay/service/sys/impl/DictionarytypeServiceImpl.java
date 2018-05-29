package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tdictionarytype;
import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Dictionarytype;
import com.cn.flypay.service.sys.DictionarytypeServiceI;
import com.cn.flypay.utils.StringUtil;


@Service
public class DictionarytypeServiceImpl implements DictionarytypeServiceI {

	@Autowired
	private BaseDao<Tdictionarytype> dictionarytypeDao;

	@Override
	public void add(Dictionarytype r) {
		Tdictionarytype t = new Tdictionarytype();
		t.setName(r.getName());
		t.setSeq(r.getSeq());
		t.setDescription(r.getDescription());
		dictionarytypeDao.save(t);
	}

	@Override
	public void delete(Long id) {
		Tdictionarytype t = dictionarytypeDao.get(Tdictionarytype.class, id);
		dictionarytypeDao.delete(t);
	}

	@Override
	public void edit(Dictionarytype r) {
		Tdictionarytype t = dictionarytypeDao.get(Tdictionarytype.class, r.getId());
		t.setDescription(r.getDescription());
		t.setName(r.getName());
		t.setSeq(r.getSeq());
		dictionarytypeDao.update(t);
	}

	@Override
	public Dictionarytype get(Long id) {
		Tdictionarytype t = dictionarytypeDao.get(Tdictionarytype.class, id);
		Dictionarytype r = new Dictionarytype();
		r.setDescription(t.getDescription());
		r.setId(t.getId());
		r.setName(t.getName());
		r.setSeq(t.getSeq());
		return r;
	}

	@Override
	public List<Tree> tree() {
		List<Tdictionarytype> l = null;
		List<Tree> lt = new ArrayList<Tree>();

		l = dictionarytypeDao.find("select distinct t from Tdictionarytype t order by t.seq");

		if ((l != null) && (l.size() > 0)) {
			for (Tdictionarytype r : l) {
				Tree tree = new Tree();
				tree.setId(r.getId().toString());
				if (r.getDictionarytype() != null) {
					tree.setPid(r.getDictionarytype().getId().toString());
					tree.setIconCls("icon_folder");
				}else{
					tree.setIconCls("icon_company");
				}
				tree.setText(r.getName());
				lt.add(tree);
			}
		}
		return lt;
	}
	@Override
	public List<Tree> tree(String code) {
		List<Tdictionarytype> l = null;
		List<Tree> lt = new ArrayList<Tree>();
		if (StringUtil.isNullOrEmpty(code)) {
			return lt;
		}
		l = dictionarytypeDao.find("select distinct t from Tdictionarytype t where t.code='"+code+"'order by t.seq");
		if ((l != null) && (l.size() > 0)) {
			for (Tdictionarytype r : l) {
				Tree tree = new Tree();
				tree.setId(r.getId().toString());
				if (r.getDictionarytype() != null) {
					tree.setPid(r.getDictionarytype().getId().toString());
					tree.setIconCls("icon_folder");
				}else{
					tree.setIconCls("icon_company");
				}
				tree.setText(r.getName());
				lt.add(tree);
			}
		}
		return lt;
	}
}
