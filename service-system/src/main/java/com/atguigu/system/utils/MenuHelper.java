package com.atguigu.system.utils;

import com.atguigu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: MenuHelper
 * Package: com.atguigu.system.utils
 * Description:
 *
 * @Author 邓瑶
 * @Create 2023/5/10 11:12
 * @Version 1.0
 */
public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        List<SysMenu> sysMenus = new ArrayList<>();
        sysMenuList.forEach(sysMenu -> {
            if (sysMenu.getParentId().longValue()==0){
                sysMenus.add(findChildren(sysMenu,sysMenuList));
            }
        });
        return sysMenus;
    }

    private static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<>());
        sysMenuList.forEach(items ->{
            if (sysMenu.getId().longValue()==items.getParentId().longValue()){
                if (sysMenu.getChildren()==null){
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(findChildren(items,sysMenuList));
            }
        });
        return sysMenu;
    }
}
