package com.questioner.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.questioner.entity.Question;
import org.apache.commons.collections4.*;


public class QuestionModel {
	private HashMap<Long,List<String>> FieldTag;
	public QuestionModel(HashMap<Long,List<String>> fieldTag){
		this.FieldTag = fieldTag;
	}
	public Long JudgeModel(List<String> Tag,Long Field) {
		if(CollectionUtils.intersection(Tag, FieldTag.get(Field)).size()>0) {
			return -5l;//符合
		}
		else {
			int MaxSize=0;
			Long _field=-1l;
			for(Entry<Long,List<String>> e: FieldTag.entrySet()) {
				if(CollectionUtils.intersection(Tag, FieldTag.get(Field)).size()>MaxSize)
					_field=e.getKey();
			}
			return _field;
		}
	}

}
