package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisSharededPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/users")
public class UserManageController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){
        ServerResponse<User> response = userService.login(username, password);

        if(response.isSuccess()){
            User user = response.getData();
            if(userService.checkAdminRole(user).isSuccess()){
//                session.setAttribute(Const.CURRENT_USER, user);
                CookieUtil.writeLoginToken(httpServletResponse, session.getId());
                RedisSharededPoolUtil.setEx(session.getId(), Const.REDIS_SESSION_EXTIME, JsonUtil.obj2String(user));

                return response;
            }else{
                return ServerResponse.createByErrorMsg("不是管理员");
            }
        }
        return response;
    }

}
