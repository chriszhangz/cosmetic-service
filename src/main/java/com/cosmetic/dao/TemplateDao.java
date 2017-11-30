
package com.cosmetic.dao;
import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Template;

public interface TemplateDao {

	public Template selectTemplateById(@Param("template_id") int tid);    

}
