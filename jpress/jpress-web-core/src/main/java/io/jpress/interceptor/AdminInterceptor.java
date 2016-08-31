/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.interceptor;

import io.jpress.Consts;
import io.jpress.menu.MenuManager;
import io.jpress.model.Content;
import io.jpress.model.Taxonomy;
import io.jpress.model.User;
import io.jpress.utils.EncryptUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class AdminInterceptor implements Interceptor {
	
	static Content null_content = new Content();
	static Taxonomy null_taxonomy = new Taxonomy();

	@Override
	public void intercept(Invocation inv) {

		Controller controller = inv.getController();
		
		String target = controller.getRequest().getRequestURI();
		String cpath = controller.getRequest().getContextPath();

		if (!target.startsWith(cpath + "/admin")) {
			inv.invoke();
			return;
		}

		controller.setAttr("c", controller.getPara("c"));
		controller.setAttr("p", controller.getPara("p"));
		controller.setAttr("m", controller.getPara("m"));
		controller.setAttr("t", controller.getPara("t"));
		controller.setAttr("s", controller.getPara("s"));
		controller.setAttr("k", controller.getPara("k"));
		controller.setAttr("page", controller.getPara("page"));

		User user = InterUtils.tryToGetUser(inv);
		
		if (user != null && user.isAdministrator()) {
			
			//content和taxonomy用于清空全局freemarker的全局标签设置，否则会相互冲突
			controller.setAttr("content", null_content);
			controller.setAttr("taxonomy", null_taxonomy);
			
			controller.setAttr(Consts.ATTR_USER, user);
			controller.setAttr("ucode", EncryptUtils.generateUcode(user.getId(),user.getSalt()));
			controller.setAttr("_menu_html", MenuManager.me().generateHtml());
			inv.invoke();
			return;
		}

		controller.redirect("/admin/login");
	}
	

}
