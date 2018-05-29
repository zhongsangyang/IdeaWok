package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tdictionary;
import com.cn.flypay.model.sys.Tdictionarytype;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.Dictionarytype;
import com.cn.flypay.service.sys.DictionaryService;


@Service
public class DictionaryServiceImpl implements DictionaryService {

	@Autowired
	private BaseDao<Tdictionary> dictionaryDao;

	@Autowired
	private BaseDao<Tdictionarytype> dictionarytypeDao;

	@Override
	public void add(Dictionary r) {
		Tdictionary t = new Tdictionary();
		t.setIsdefault(GlobalConstant.NOT_DEFAULT);
		t.setState(GlobalConstant.ENABLE);
		t.setCode(r.getCode());
		t.setText(r.getText());
		t.setSeq(r.getSeq());
		t.setDictionarytype(dictionarytypeDao.get(Tdictionarytype.class, r.getDictionarytypeId()));
		dictionaryDao.save(t);
	}

	@Override
	public void delete(Long id) {
		Tdictionary t = dictionaryDao.get(Tdictionary.class, id);
		dictionaryDao.delete(t);
	}

	@Override
	public void edit(Dictionary r) {
		Tdictionary t = dictionaryDao.get(Tdictionary.class, r.getId());
		t.setText(r.getText());
		t.setSeq(r.getSeq());
		t.setCode(r.getCode());
		t.setState(r.getState());
		t.setDictionarytype(dictionarytypeDao.get(Tdictionarytype.class, r.getDictionarytypeId()));
		dictionaryDao.update(t);
	}

	@Override
	public Dictionary get(Long id) {
		Tdictionary t = dictionaryDao.get(Tdictionary.class, id);
		Dictionary r = new Dictionary();
		r.setId(t.getId());
		r.setIsdefault(t.getIsdefault());
		r.setText(t.getText());
		r.setSeq(t.getSeq());
		r.setCode(t.getCode());
		if (t.getDictionarytype() != null) {
			r.setDictionarytypeId(t.getDictionarytype().getId());
			r.setDictionarytypeName(t.getDictionarytype().getName());
		}
		r.setState(t.getState());
		return r;
	}

	@Override
	public List<Dictionary> dataGrid(Dictionary dictionary, PageFilter ph) {
		List<Dictionary> ul = new ArrayList<Dictionary>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Tdictionary t ";
		List<Tdictionary> l = dictionaryDao.find(hql + whereHql(dictionary, params) + orderHql(ph), params,
				ph.getPage(), ph.getRows());
		for (Tdictionary t : l) {
			Dictionary u = new Dictionary();
			BeanUtils.copyProperties(t, u);
			if (t.getDictionarytype() != null) {
				u.setDictionarytypeId(t.getDictionarytype().getId());
				u.setDictionarytypeName(t.getDictionarytype().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(Dictionary dictionary, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Tdictionary t ";
		return dictionaryDao.count("select count(*) " + hql + whereHql(dictionary, params), params);
	}

	private String whereHql(Dictionary dictionary, Map<String, Object> params) {
		String hql = "";
		if (dictionary != null) {
			hql += " where 1=1 ";
			if (dictionary.getText() != null) {
				hql += " and t.name like :name";
				params.put("name", "%%" + dictionary.getText() + "%%");
			}
			if (dictionary.getDictionarytypeId() != null) {
				hql += " and t.dictionarytype.id = :dictionarytypeId";
				params.put("dictionarytypeId", dictionary.getDictionarytypeId());
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public List<Dictionary> combox(String code) {
		List<Dictionary> ld = new ArrayList<Dictionary>();
		List<Tdictionary> lt = dictionaryDao.find("from Tdictionary t where t.state=0 and t.dictionarytype.code='"
				+ code + "' order by t.seq asc");
		if (lt != null && lt.size() > 0) {
			System.out.println("lt.size():"+lt.size());
			for (int i = 0; i < lt.size(); i++) {
				if (lt.get(i).getState() == 0) {
					Dictionary d = new Dictionary();
					d.setId(lt.get(i).getId());
					d.setCode(lt.get(i).getCode());
					d.setText(lt.get(i).getText());
					ld.add(d);
					
				}
			}

		}
		return ld;
	}

	@Override
	public List<Dictionary> comboxCodeAndTextByTypeCode(String code) {
		List<Dictionary> ld = new ArrayList<Dictionary>();
		List<Tdictionary> lt = dictionaryDao.find("from Tdictionary t where t.state=0 and t.dictionarytype.code='"
				+ code + "'");
		if (lt != null && lt.size() > 0) {
			for (int i = 0; i < lt.size(); i++) {
				if (lt.get(i).getState() == 0) {
					Dictionary d = new Dictionary();
					d.setCode(lt.get(i).getCode());
					d.setText(lt.get(i).getText());
					ld.add(d);
				}
			}
		}
		return ld;
	}

	@Override
	public List<Dictionary> comboxCodeAndTextByParentTypeCode(String code) {
		List<Dictionary> ld = new ArrayList<Dictionary>();
		List<Tdictionary> lt = dictionaryDao.find("from Tdictionary t where t.state=0 and t.dictionarytype.code in("
				+ "select tt.code from Tdictionarytype tt where tt.dictionarytype.code='" + code + "'" + ")");
		if (lt != null && lt.size() > 0) {
			for (int i = 0; i < lt.size(); i++) {
				if (lt.get(i).getState() == 0) {
					Dictionary d = new Dictionary();
					d.setCode(lt.get(i).getCode());
					d.setText(lt.get(i).getText());
					ld.add(d);
				}
			}
		}
		return ld;
	}
	@Cacheable(value = "dictionaryCache", key = "#code+'comboxMap'")
	@Override
	public Map<String, String> comboxMap(String code) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<Tdictionary> lt = dictionaryDao.find("select t from Tdictionary t where t.state=0 and t.dictionarytype.code='"
				+ code + "' order by t.seq");
		if (lt != null && lt.size() > 0) {
			for (int i = 0; i < lt.size(); i++) {
				if (lt.get(i).getState() == 0) {
					map.put(lt.get(i).getCode(), lt.get(i).getText());
				}
			}
		}
		return map;
	}

	@Override
	public Dictionary checkUnique(Dictionary dictionary) {
		Tdictionary t = dictionaryDao.get("from Tdictionary t where t.code = '" + dictionary.getCode()
				+ "' and t.dictionarytype.id=" + dictionary.getDictionarytypeId());
		if (t != null) {
			Dictionary r = new Dictionary();
			r.setId(t.getId());
			r.setIsdefault(t.getIsdefault());
			r.setText(t.getText());
			r.setSeq(t.getSeq());
			r.setCode(t.getCode());
			if (t.getDictionarytype() != null) {
				r.setDictionarytypeId(t.getDictionarytype().getId());
				r.setDictionarytypeName(t.getDictionarytype().getName());
			}
			r.setState(t.getState());
			return r;
		} else {
			return null;
		}
	}

	public List<Dictionarytype> getDictionarytypeByParentCode(String code) {
		List<Tdictionarytype> dtl = dictionarytypeDao
				.find("select  t from Tdictionarytype t  left join t.dictionarytype tt where tt.code='" + code + "'");
		List<Dictionarytype> dts = new ArrayList<Dictionarytype>();
		for (Tdictionarytype t : dtl) {
			Dictionarytype u = new Dictionarytype();
			BeanUtils.copyProperties(t, u);
			dts.add(u);
		}
		return dts;
	}

}
